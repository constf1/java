package freecell;

import java.util.ArrayList;
import java.util.HashMap;
// import java.util.HashSet;
import java.util.List;
import java.util.Map;
// import java.util.Set;

import common.Deck;
import common.IntStack;

public class FreecellSolver extends FreecellGame {
  public FreecellSolver(int PILE_NUM, int CELL_NUM, int BASE_NUM) {
    super(PILE_NUM, CELL_NUM, BASE_NUM);
  }

  // private final Set<String> _done = new HashSet<>();
  private Map<String, int[]> _output = new HashMap<>();
  private final List<Map<String, int[]>> _stack = new ArrayList<>();
  
  private int _iteration = 0;
  private int _solution[];

  // private static class SolvedException extends RuntimeException {
  //   /**
  //    * Serial UID
  //    */
  //   private static final long serialVersionUID = 1L;

  //   public SolvedException() {
  //     super();
  //   }
  // }

  private static class DeskState implements Comparable<DeskState> {
    public final int free, solved;
    public final String key;

    public DeskState(String key, int free, int solved) {
      this.key = key;
      this.free = free;
      this.solved = solved;
    }

    @Override
    public int compareTo(DeskState other) {
      if (solved != other.solved) {
        return other.solved - solved;
      }
      if (free != other.free) {
        return other.free - free;
      }
      return key.compareTo(other.key);
    }
  }

  // private final FreecellGame.OnMove _onMove = new FreecellGame.OnMove() {
  //   @Override
  //   public void test(FreecellGame game) {
  //     final var key = game.toKey();
  //     if (!_shouldSolve()) {
  //       _done.remove(key);
  //     } else {
  //       if (!_done.contains(key)) {
  //         _done.add(key);
  //         _output.put(key, game.pathToArray());
          
  //         game.moveCardsToBases();
  //         if (game.isSolved()) {
  //           if (_solution == null || _solution.length > game.pathLength()) {
  //             _solution = game.pathToArray();
  //             System.out.println("*********\nSolution: " + _solution.length);
  //             System.out.println("Cleaning output: " + _output.size() +  "\n*********\n");
  //             _done.removeAll(_output.keySet());
  //             _output.clear();
  //           }
  //         }
  //       }
  //     }
  //   }
  // };

  public void prepare() {
    // _done.clear();
    _stack.clear();
    _output.clear();
    _iteration = 0;

    rewind();
    moveCardsAuto();
    final var key = toKey();
    // _done.add(key);
    _output.put(key, _path.toArray​());
  }

  private Map<String, int[]> _getNextInput() {
    final int INPUT_MAX = 50000;
    if (_output.size() > 0) {
      return _splitInput(_output, INPUT_MAX, INPUT_MAX / 10);
    }

    if (_stack.size() > 0) {
      // System.out.println("Step back:" + _stack.size());
      // return _splitInput(_stack.remove(_stack.size() - 1), INPUT_MAX, INPUT_MAX / 3);
      return _stack.remove(_stack.size() - 1);
    }

    return null;
  }

  private Map<String, int[]> _splitInput(Map<String, int[]> input, int inputMax, int splitSize) {
    final int inputSize = input.size();
    if (inputSize <= inputMax) {
      return input;
    }

    var list = new ArrayList<DeskState>(inputSize);
    // int countEmptyMax = 0;
    // int countSolvedMax = 0;
    for (final var key : input.keySet()) {
      rewind();
      forward(input.get(key));
      final int countEmpty = countEmpty();
      final int countSolved = countSolved();

      // countEmptyMax = Math.max(countEmptyMax, countEmpty);
      // countSolvedMax = Math.max(countSolvedMax, countSolved);

      list.add(new DeskState(key, countEmpty, countSolved));
    }
    
    // var a = new ArrayList<int[]>();
    // var b = new ArrayList<int[]>();
    // for (int i = 0; i < inputSize; i++) {
    //   final var state = list.get(i);
    //   if (state.free == countEmptyMax || state.solved == countSolvedMax) {
    //     a.add(input.get(state.index));
    //   } else {
    //     b.add(input.get(state.index));
    //   }
    // }
    // System.out.println("\tSplitting input: " + input.size() + " => " + splitSize + " + " + (inputSize - splitSize));
    list.sort(null);

    while (input.size() > splitSize) {
      final var buf = new HashMap<String, int[]>(splitSize);
      while (buf.size() < splitSize && input.size() > splitSize) {
        final var key = list.get(input.size() - 1).key;
        buf.put(key, input.remove(key));
      }
      _stack.add(buf);
    }
    
    // for (int i = 0; i < inputSize - splitSize; i++) {
    //   int index = list.get(i).index;
    //   b.add(input.get(index));
    // }

    

    // System.out.println("\tPath Length: " + input.get(0).length);
    return input;
  }

  private final boolean _shouldSolve(int pathLength) {
    if (_solution != null) {
      var cards = Deck.CARD_NUM - countSolved();
      return _solution.length > pathLength + cards;
    }
    return true;
  }

  // private boolean _removeIfLong(String key, int pathLength) {
  //   if (_solution != null) {
  //     if (pathLength + (Deck.CARD_NUM - countSolved()) >= _solution.length) {
  //       // _done.remove(key);
  //       return true;
  //     }
  //   }
  //   return false;
  // }

  public boolean nextIteration() {
    var input = _getNextInput();
    if (_output.size() > 0) {
      _output = new HashMap<String, int[]>();
    }

    if (input == null) {
      return false;
    }
    
    final int inputSize = input.size();
    _iteration++;
    if (_iteration > 5000) {
      return false;
    }

    if (inputSize <= 0) {
      return false;
    }

    /*
    System.out.println("#" + _iteration
      + ": input=" + inputSize
      //  + ", done=" + _done.size()
       + ", stack=" + _stack.size());
    */
    System.out.print('-');
    if (_iteration % 100 == 0) {
      System.out.println("\n" + (_iteration / 100) + ":[" + _stack.size() + "]");
    }

    final IntStack moves = new IntStack();
    // for (final var entry : input.entrySet()) {
    for (final var path : input.values()) {
      // final var path = entry.getValue();
      final int mark = path.length;
      rewind();
      forward(path);
      // if (!_removeIfLong(entry.getKey(), mark + 1)) {
      if (_shouldSolve(mark + 1)) {
        getMoves(moves);
        while (moves.size() > 0) {
          int move = moves.pop();
          if (isMoveForward(move)) {
            moveCard(move);
            final var key = toKey();
            // if (!_removeIfLong(key, mark + 1)) {
            if (_shouldSolve(mark + 1)) {
              // if (!_done.contains(key)) {
              if (!input.containsKey(key) && !_output.containsKey(key)) {
                // _done.add(key);
                _output.put(key, _path.toArray​());
                
                moveCardsToBases();
                if (isSolved()) {
                  if (_solution == null || _solution.length > _path.size()) {
                    _solution = _path.toArray​();
                    System.out.println("\n*********\nSolution: "
                      + _solution.length
                      + '\n' + pathToString(_solution)
                      + "\n*********\n");
                  }
                }
              }
            }
            backward(mark);
          }
        }
      }
    }

    return _output.size() > 0 || _stack.size() > 0;
  }

  public int[] solve() {
    prepare();
    while (nextIteration());
    return _solution;
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

  public static void main(String[] args) {
    final int deal = 25;
    var game = new FreecellSolver(8, 4, 4);
    game.deal(deal);
    System.out.println("DESK: " + game.toString());
    System.out.println("KEY: " + game.toKey());

    var path = game.solve();
    if (path != null) {
      System.out.println("Solved!");
      System.out.println("" + deal
        + ',' + path.length
        + ',' + game.pathToString(path));
      game.rewind();
      game.forward(path);
    } else {
      System.out.println("Unsolved ;-(");
    }
  }
}
