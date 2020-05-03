package freecell;

import java.util.Arrays;

import common.ByteStack;
import common.Deck;

public class FreecellDesk extends FreecellBasis {
  private ByteStack _key = new ByteStack();
  private final ByteStack _buffer[];
  protected final ByteStack _desk[];

  public FreecellDesk(final int PILE_NUM, final int CELL_NUM, final int BASE_NUM) {
    super(PILE_NUM, CELL_NUM, BASE_NUM);

    _buffer = new ByteStack[PILE_NUM];
    _desk = new ByteStack[DESK_SIZE];
    for (int i = 0; i < DESK_SIZE; i++) {
      _desk[i] = new ByteStack();
    }
  }

  public int countEmptyCells() {
    int count = 0;
    for (int i = CELL_START; i < CELL_END; i++) {
      if (_desk[i].isEmpty()) {
        count++;
      }
    }
    return count;
  }

  public int countEmptyPiles() {
    int count = 0;
    for (int i = PILE_START; i < PILE_END; i++) {
      if (_desk[i].isEmpty()) {
        count++;
      }
    }
    return count;
  }

  public boolean isSolved() {
    for (int i = BASE_START; i < BASE_END; i++) {
      if (_desk[i].size() < Deck.RANK_NUM) {
        return false;
      }
    }
    return true;
  }

  public int countSolved() {
    int count = 0;
    for (int i = BASE_START; i < BASE_END; i++) {
      count += _desk[i].size();
    }
    return count;
  }

  public int countEmpty() {
    return countEmptyCells() + countEmptyPiles();
  }

  private static final byte BASES[] = { '_', 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };

  public void baseToKey(ByteStack key) {
    for (int i = BASE_START; i < BASE_END; i++) {
      key.push(BASES[_desk[i].size()]);
    }
  }

  public void pileToKey(ByteStack key) {
    int size = 0;
    for (int i = PILE_START; i < PILE_END; i++) {
      if (!_desk[i].isEmpty()) {
        _buffer[size++] = _desk[i];
      }
    }
    if (size > 0) {
      Arrays.sort(_buffer, 0, size);
      for (int i = 0; i < size; i++) {
        key.push(_buffer[i]);
        key.push((byte)',');
      }
      key.pop();
    }
  }

  public void toKey(ByteStack key) {
    key.clear();
    baseToKey(key);
    pileToKey(key);
  }

  public String toKey() {
    toKey(_key);
    return _key.toString();
  }

  int getEmptyCell() {
    for (int i = CELL_START; i < CELL_END; i++) {
      if (_desk[i].isEmpty()) {
        return i;
      }
    }
    return -1;
  }

  int getEmptyPile() {
    for (int i = PILE_START; i < PILE_END; i++) {
      if (_desk[i].isEmpty()) {
        return i;
      }
    }
    return -1;
  }

  int getBase(final byte card) {
    final int suit = Deck.suitOf(card);
    final int rank = Deck.rankOf(card);

    for (int i = BASE_START + suit; i < BASE_END; i += Deck.SUIT_NUM) {
      if (_desk[i].size() == rank) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public String toString() {
    final var buf = new StringBuilder();
    var prefix = "";
    for (int i = 0; i < DESK_SIZE; i++) {
      buf.append(prefix);
      buf.append(lineToString(_desk[i]));
      prefix = ",";
    }
    return buf.toString();
  }

  public static String lineToString(final ByteStack line) {
    final var buf = new StringBuilder();
    for (int i = 0; i < line.size(); i++) {
      buf.append(Deck.RANKS[Deck.rankOf(line.get(i))]);
      buf.append(Deck.SUITS[Deck.suitOf(line.get(i))]);
    }
    return buf.toString();
  }
}
