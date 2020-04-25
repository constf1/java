package freecell;

public class FreecellBasis {
  public final int
    DESK_SIZE,
    PILE_START, PILE_NUM, PILE_END,
    BASE_START, BASE_NUM, BASE_END,
    CELL_START, CELL_NUM, CELL_END;

  FreecellBasis(
    final int PILE_NUM, // cascades
    final int CELL_NUM, // open cells
    final int BASE_NUM // foundation piles
  ) {
    this.PILE_NUM = PILE_NUM;
    this.CELL_NUM = CELL_NUM;
    this.BASE_NUM = BASE_NUM;
    this.DESK_SIZE = PILE_NUM + CELL_NUM + BASE_NUM;

    this.PILE_START = 0;
    this.PILE_END = this.BASE_START = this.PILE_START + this.PILE_NUM;
    this.BASE_END = this.CELL_START = this.BASE_START + this.BASE_NUM;
    this.CELL_END = this.CELL_START + this.CELL_NUM;
  }

  boolean isPile(final int index) {
    return index >= PILE_START && index < PILE_END;
  }

  boolean isBase(final int index) {
    return index >= BASE_START && index < BASE_END;
  }

  boolean isCell(final int index) {
    return index >= CELL_START && index < CELL_END;
  }

  int toMove(final int giver, final int taker) {
    return giver + taker * DESK_SIZE;
  }

  int toGiver(final int move) {
    return move % DESK_SIZE;
  }

  int toTaker(final int move) {
    return move / DESK_SIZE;
  }

  String getSpotName(final int index) {
    if (isBase(index)) {
      return "base " + (index - BASE_START);
    }
    if (isPile(index)) {
      return "pile " + (index - PILE_START);
    }
    if (isCell(index)) {
      return "cell " + (index - CELL_START);
    }
    return "unknown " + index;
  }

  public static void main(String[] args) {
    FreecellBasis basis = new FreecellBasis(8, 4, 4);

    System.out.println("Basis Test");
    System.out.println("DESK_SIZE: " + basis.DESK_SIZE);
    System.out.println("BASE_NUM: " + basis.BASE_NUM);
    System.out.println("CELL_NUM: " + basis.CELL_NUM);
    System.out.println("PILE_NUM: " + basis.PILE_NUM);

    for (int i = 0; i < basis.DESK_SIZE; i++) {
      System.out.println("SPOT #" + i + "\n\t" + basis.getSpotName(i));
    }
  }
}
