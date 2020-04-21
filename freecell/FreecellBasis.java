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
    return index >= this.PILE_START && index < this.PILE_END;
  }

  boolean isBase(final int index) {
    return index >= this.BASE_START && index < this.BASE_END;
  }

  boolean isCell(final int index) {
    return index >= this.CELL_START && index < this.CELL_END;
  }

  String getSpotName(final int index) {
    if (this.isBase(index)) {
      return "base " + (index - this.BASE_START);
    }
    if (this.isPile(index)) {
      return "pile " + (index - this.PILE_START);
    }
    if (this.isCell(index)) {
      return "cell " + (index - this.CELL_START);
    }
    return "unknown " + index;
  }
}
