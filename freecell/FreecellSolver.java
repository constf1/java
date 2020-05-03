package freecell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import common.Deck;
import common.IntStack;

public class FreecellSolver extends FreecellGame {
  public static final int
    TOTAL_MAX = 5000000,
    INPUT_MIN = 1000,
    INPUT_MAX = 100000;

  public boolean debug = false;
  
  public FreecellSolver() {
    super(8, 4, 4);
    setInputSize(8888);
  }

  public void setInputSize(int value) {
    _inputSize = Math.min(Math.max(value, INPUT_MIN), INPUT_MAX);
  }

  public int getInputSize() {
    return _inputSize;
  }

  protected List<int[]> _feed = new ArrayList<>();
  protected Map<String, Integer> _done = new HashMap<>();
  protected SortedMap<Integer, SortedSet<int[]>> _pool = new TreeMap<>();
  protected int[] _solution = null;
  protected int _iteration = 0;
  protected IntStack _nextMoves = new IntStack();
  protected int _inputSize = INPUT_MIN;

  protected Comparator<int[]> _comparator = new Comparator<>() {
    @Override
    public int compare(int[] a, int[] b) {
      if (a.length != b.length) {
        return a.length - b.length;
      }
      for (int i = 0; i < a.length; i++) {
        if (a[i] != b[i]) {
          // Actually we could also analyze which move is better.
          return a[i] - b[i];
        }
      }
      return 0;
    }
  };

  protected boolean _shouldSolve(int pathLength) {
    if (_solution != null) {
      int cards = Deck.CARD_NUM - countSolved();
      return _solution.length > pathLength + cards;
    }
    return true;
  }

  protected void _prepare() {
    _feed.clear();
    _done.clear();
    _pool.clear();

    _solution = null;
    _iteration = 0;

    rewind();
    moveCardsAuto();

    _done.put(toKey(), _path.size());
    _feed.add(_path.toArray​());
  }

  protected void _splitOutput() {
    final int feedSize = _feed.size();
    if (feedSize > _inputSize) {
      for (final int[] path : _feed) {
        rewind();
        forward(path);

        final int solved = countSolved();
        final int cells = countEmptyCells();
        final int piles = countEmptyPiles();
        final Integer KEY = Integer.valueOf(
          (int) -Math.round(90.00 * solved + 3.00 * cells + 4.25 * piles - 2.75 * path.length)
        );

        if (_pool.containsKey(KEY)) {
          _pool.get(KEY).add(path);
        } else {
          final SortedSet<int[]> set = new TreeSet<>(_comparator);
          set.add(path);
          _pool.put(KEY, set);
        }
      }
      _feed.clear();
    }
  }

  protected void _getNextInput() {
    if (_feed.isEmpty() && !_pool.isEmpty()) {
      final Integer KEY = _pool.firstKey();
      final var set = _pool.get(KEY);
      final int size = set.first().length;
      for (int[] path : set) {
        if (size != path.length) {
          break;
        } else {
          _feed.add(path);
        }
      }

      if (set.size() == _feed.size()) {
        _pool.remove(KEY);
      } else {
        set.removeAll(_feed);
      }
    }
  }

  protected boolean _nextIteration() {
    final int doneSize = _done.size();
    if (_solution != null && doneSize > TOTAL_MAX) {
      return false;
    }

    final int oldSize = _pool.size();

    _splitOutput();
    _getNextInput();

    if (_feed.isEmpty()) {
      return false;
    }

    final int newSize = _pool.size();
    if (debug) {
      if (oldSize < newSize) {
        System.out.print('+');
      } else if (oldSize > newSize) {
        System.out.print('-');
      } else {
        System.out.print('=');
      }
    }

    _iteration++;
    if (debug && _iteration % 100 == 0) {
      System.out.println("\n" + (_iteration / 100)
        + " [" + (doneSize * 100 / TOTAL_MAX)
        + "% " + newSize + ']');
    }

    final var input = _feed;
    _feed = new ArrayList<>();

    for (final int[] path : input) {
      rewind();
      forward(path);
      if (_shouldSolve(path.length + 1)) {
        getMoves(_nextMoves);
        for (int i = 0; i < _nextMoves.size(); i++) {
          backward(path.length);
          moveCard(_nextMoves.get(i));
          if (_shouldSolve(_path.size()) && hasNextMove()) {
            moveCardsAuto();
            String key = toKey();
            Integer value = _done.get(key);
            if (value == null || value.intValue() > _path.size()) {
              final int[] next = _path.toArray​();
              moveCardsToBases();
              if (isSolved()) {
                if (_solution == null || _solution.length > _path.size()) {
                  _solution = _path.toArray​();

                  if (debug) {
                    System.out.println();
                    System.out.println("*********");
                    System.out.println("Solution: " + _solution.length);
                    System.out.println(pathToString(_solution));
                    System.out.println("*********");
                  }
                }

              } else {
                _feed.add(next);
                _done.put(key, Integer.valueOf(next.length));
              }
            }
          }
        }
      }
    }

    return _feed.size() > 0 || _pool.size() > 0;
  }

  public static char toChar(int n) {
    if (n >= 0 && n < 10) {
      return (char)('0' + n);
    } else {
      return (char)('a' + n - 10);
    }
  }

  public String pathToString(int[] path) {
    var buf = new StringBuilder(path.length);
    for (var move : path) {
      buf.append(toChar(toGiver(move)));
      buf.append(toChar(toTaker(move)));
    }
    return buf.toString();
  }

  public int[] solve() {
    _prepare();
    while (_nextIteration());
    return _solution;
  }

  public static void main(String[] args) {
    final int deal = 8;
    var game = new FreecellSolver();
    game.debug = true;
    game.deal(deal);
    System.out.println("DESK: " + game.toString());
    System.out.println("KEY: " + game.toKey());

    var path = game.solve();
    if (path != null) {
      System.out.println("Solved!");
      System.out.println("" + deal
        + ',' + path.length
        + ',' + game.pathToString(path));
      // game.rewind();
      // game.forward(path);
    } else {
      System.out.println("Unsolved ;-(");
    }
  }
}
