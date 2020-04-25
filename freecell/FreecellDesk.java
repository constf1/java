package freecell;

import java.util.Arrays;
// import java.util.ArrayList;
// import java.util.Collections;

import common.Deck;
import common.IntStack;

public class FreecellDesk extends FreecellBasis {
  protected final IntStack _desk[];

  public FreecellDesk(final int PILE_NUM, final int CELL_NUM, final int BASE_NUM) {
    super(PILE_NUM, CELL_NUM, BASE_NUM);

    this._desk = new IntStack[DESK_SIZE];
    for (int i = 0; i < DESK_SIZE; i++) {
      this._desk[i] = new IntStack();
    }
  }

  /**
   * Returns a copy of the desk.
   */
  // public int[][] toArray() {
  //   final int arr[][] = new int[DESK_SIZE][];
  //   for (int i = 0; i < DESK_SIZE; i++) {
  //     arr[i] = this._desk[i].toArray​();
  //   }

  //   return arr;
  // }

  /**
   * Gets a card at [index, offset]
   * 
   * @param index  a line index
   * @param offset an offset in the line. A negative value can be used, indicating
   *               an offset from the end of the sequence.
   */
  // public int getCard(final int index, int offset) {
  //   final var line = this._desk[index];
  //   final int size = line.size();

  //   if (offset < 0) {
  //     offset = size + offset;
  //   }
  //   return offset >= 0 && offset < size ? line.get(offset) : -1;
  // }

  // public List<Integer> getLine(final int index) {
  // return Collections.unmodifiableList(this.desk[index]);
  // }

  // public int[] getTableauAt(final int index) {
  //   final var tableau = new ArrayList<Integer>();
  //   final var line = this._desk[index];

  //   int j = line.size();
  //   if (j > 0) {
  //     tableau.add(line.get(j - 1));
  //     while (--j > 0 && Deck.isTableau(line.get(j - 1), line.get(j)) && Deck.rankOf(line.get(j - 1)) > 0) {
  //       tableau.add(line.get(j - 1));
  //     }
  //   }
  //   Collections.reverse(tableau);
  //   return tableau.stream().mapToInt(i -> i).toArray();
  // }

  public int countEmptyCells() {
    int count = 0;
    for (int i = this.CELL_START; i < this.CELL_END; i++) {
      if (this._desk[i].size() == 0) {
        count++;
      }
    }
    return count;
  }

  public int countEmptyPiles() {
    int count = 0;
    for (int i = this.PILE_START; i < this.PILE_END; i++) {
      if (this._desk[i].size() == 0) {
        count++;
      }
    }
    return count;
  }

  public boolean isSolved() {
    for (int i = this.BASE_START; i < this.BASE_END; i++) {
      if (this._desk[i].size() < Deck.RANK_NUM) {
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
    return this.countEmptyCells() + this.countEmptyPiles();
  }

  private static final String RANKS = "_" + Deck.RANKS;

  public String baseToKey() {
    final var buf = new StringBuilder();
    for (int i = BASE_START; i < BASE_END; i++) {
      buf.append(RANKS.charAt(_desk[i].size()));
    }
    return buf.toString();
  }

  public String pileToKey() {
    final var arr = new String[this.PILE_NUM];
    for (int i = 0; i < this.PILE_NUM; i++) {
      arr[i] = lineToString(this._desk[i + this.PILE_START]);
    }
    Arrays.sort(arr);
    return String.join(",", arr);
  }

  public String toKey() {
    return this.baseToKey() + this.pileToKey();
  }

  int getEmptyCell() {
    for (int i = this.CELL_START; i < this.CELL_END; i++) {
      if (this._desk[i].size() == 0) {
        return i;
      }
    }
    return -1;
  }

  int getEmptyPile() {
    for (int i = this.PILE_START; i < this.PILE_END; i++) {
      if (this._desk[i].size() == 0) {
        return i;
      }
    }
    return -1;
  }

  int getBase(final int card) {
    final int suit = Deck.suitOf(card);
    final int rank = Deck.rankOf(card);

    for (int i = BASE_START + suit; i < BASE_END; i += Deck.SUIT_NUM) {
      if (_desk[i].size() == rank) {
        return i;
      }
    }
    return -1;
  }

  int getOppositeColorBaseMinRank(final int suit) {
    int rank = Deck.RANK_NUM;
    for (int i = BASE_START + ((suit + 1) % 2); i < BASE_END; i += Deck.SUIT_NUM / 2) {
      rank = Math.min(rank, _desk[i].size());
    }
    return rank;
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

  public static String lineToString(final IntStack line) {
    final var buf = new StringBuilder();
    for (int i = 0; i < line.size(); i++) {
      Deck.appendNameOf(buf, line.get(i));
    }
    return buf.toString();
  }
}
