package freecell;

import common.Deck;
import common.IntStack;

public class FreecellGame extends FreecellDesk {
  protected IntStack _path = new IntStack();

  public FreecellGame(final int PILE_NUM, final int CELL_NUM, final int BASE_NUM) {
    super(PILE_NUM, CELL_NUM, BASE_NUM);
  }

  /**
   * Clears the game.
   */
  public void clear() {
    for (int i = DESK_SIZE; i-- > 0;) {
      _desk[i].clear();
    }
    _path.clear();
  }

  /**
   * Pops the last card from the `source` line and add it to the `destination`
   * 
   * @param giver source line
   * @param taker destination line
   */
  public void moveCard(final int giver, final int taker) {
    _path.push(toMove(giver, taker));
    _desk[taker].push(_desk[giver].pop());
  }

  public void moveCard(final int move) {
    _path.push(move);
    _desk[toTaker(move)].push(_desk[toGiver(move)].pop());
  }

  public void forward(final int[] path) {
    for (final int move : path) {
      _path.push(move);
      // move source => destination
      _desk[toTaker(move)].push(_desk[toGiver(move)].pop());
    }
  }

  public void backward(final int mark) {
    while (_path.size() > mark) {
      final int move = _path.pop();
      // move destination => source
      _desk[toGiver(move)].push(_desk[toTaker(move)].pop());
    }
  }

  public boolean isMoveForward(final int move) {
    return _path.isEmpty() || _path.peek() != toMove(toTaker(move), toGiver(move));
  }

  public int moveCardsToBases() {
    int count = 0;
    for (boolean next = true; next;) {
      next = false;
      for (int giver = 0; giver < DESK_SIZE; giver++) {
        if (isBase(giver) || _desk[giver].isEmpty()) {
          continue;
        }
        final int taker = getBase(_desk[giver].peek());
        if (taker >= 0) {
          moveCard(giver, taker);
          count++;
          next = true;
        }
      }
    }
    return count;
  }

  public int moveCardsAuto() {
    final int[] ranks = getBaseMinRanks(); 
    for (int giver = 0; giver < DESK_SIZE; giver++) {
      if (isBase(giver) || _desk[giver].isEmpty()) {
        continue;
      }
      final byte card = _desk[giver].peek();
      if (Deck.rankOf(card) <= ranks[Deck.colorOf(card)] + 1) {
        final int taker = getBase(card);
        if (taker >= 0 ) {
          moveCard(giver, taker);
          return 1 + moveCardsAuto();
        }
      }
    }
    return 0;
  }

  public int getBaseMinRank() {
    int rank = _desk[BASE_START].size();
    for (int i = 1; i < BASE_NUM; i++) {
      rank = Math.min(rank, _desk[BASE_START + i].size());
    }
    return rank;
  }

  public boolean canMoveToCell() {
    final int taker = getEmptyCell();
    if (taker >= 0) {
      for (int giver = PILE_START; giver < PILE_END; giver++) {
        if (!_desk[giver].isEmpty() && isMoveForward(toMove(giver, taker))) {
          return true;
        }
      }
    }
    return false;
  }

  public void getMovesToCell(final IntStack moves) {
    final int taker = getEmptyCell();
    if (taker >= 0) {
      for (int giver = PILE_START; giver < PILE_END; giver++) {
        if (!_desk[giver].isEmpty()) {
          final int move = toMove(giver, taker);
          if (isMoveForward(move)) {
            moves.push(move);
          }
        }
      }
    }
  }

  public boolean canMoveToPile() {
    final int taker = getEmptyPile();
    if (taker >= 0) {
      // 1. Test piles:
      for (int giver = PILE_START; giver < PILE_END; giver++) {
        if (_desk[giver].size() > 1 && isMoveForward(toMove(giver, taker))) {
          return true;
        }
      }
      // 2. Test cells:
      for (int giver = CELL_START; giver < CELL_END; giver++) {
        if (_desk[giver].size() > 0 && isMoveForward(toMove(giver, taker))) {
          return true;
        }
      }
    }
    return false;
  }

  public void getMovesToPile(final IntStack moves) {
    final int taker = getEmptyPile();
    if (taker >= 0) {
      // 1. Test piles:
      for (int giver = PILE_START; giver < PILE_END; giver++) {
        if (_desk[giver].size() > 1) {
          final int move = toMove(giver, taker);
          if (isMoveForward(move)) {
            moves.push(move);
          }
        }
      }
      // 2. Test cells:
      for (int giver = CELL_START; giver < CELL_END; giver++) {
        if (_desk[giver].size() > 0) {
          final int move = toMove(giver, taker);
          if (isMoveForward(move)) {
            moves.push(move);
          }
        }
      }
    }
  }

  public boolean canMoveToBase() {
    // 1. Test piles:
    for (int giver = PILE_START; giver < PILE_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        final int suit = Deck.suitOf(card);
        final int rank = Deck.rankOf(card);
        for (int taker = BASE_START + suit; taker < BASE_END; taker += Deck.SUIT_NUM) {
          if (_desk[taker].size() == rank) {
            if (isMoveForward(toMove(giver, taker))) {
              return true;
            }
          }
        }
      }
    }

    // 2. Test cells:
    for (int giver = CELL_START; giver < CELL_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        final int suit = Deck.suitOf(card);
        final int rank = Deck.rankOf(card);
        for (int taker = BASE_START + suit; taker < BASE_END; taker += Deck.SUIT_NUM) {
          if (_desk[taker].size() == rank) {
            if (isMoveForward(toMove(giver, taker))) {
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  public void getMovesToBase(IntStack moves) {
    // 1. Test piles:
    for (int giver = PILE_START; giver < PILE_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        final int suit = Deck.suitOf(card);
        final int rank = Deck.rankOf(card);
        for (int taker = BASE_START + suit; taker < BASE_END; taker += Deck.SUIT_NUM) {
          if (_desk[taker].size() == rank) {
            final int move = toMove(giver, taker);
            if (isMoveForward(move)) {
              moves.push(move);
            }
          }
        }
      }
    }

    // 2. Test cells:
    for (int giver = CELL_START; giver < CELL_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        final int suit = Deck.suitOf(card);
        final int rank = Deck.rankOf(card);
        for (int taker = BASE_START + suit; taker < BASE_END; taker += Deck.SUIT_NUM) {
          if (_desk[taker].size() == rank) {
            final int move = toMove(giver, taker);
            if (isMoveForward(move)) {
              moves.push(move);
            }
          }
        }
      }
    }
  }

  // Get opposite color bases minimal ranks.
  public int[] getBaseMinRanks() {
    final int ranks[] = { _desk[BASE_START + 1].size(), _desk[BASE_START].size() };
    for (int i = 2; i < BASE_NUM;) {
      ranks[1] = Math.min(ranks[1], _desk[BASE_START + i++].size());
      ranks[0] = Math.min(ranks[0], _desk[BASE_START + i++].size());
    }
    return ranks;
  }

  public boolean canMoveToTableau() {
    // 1. Test piles:
    for (int giver = PILE_START; giver < PILE_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        for (int taker = PILE_START; taker < PILE_END; taker++) {
          if (giver != taker && !_desk[taker].isEmpty() && Deck.isTableau(_desk[taker].peek(), card)) {
            if (isMoveForward(toMove(giver, taker))) {
              return true;
            }
          }
        }
      }
    }

    // 2. Test cells:
    for (int giver = CELL_START; giver < CELL_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        for (int taker = PILE_START; taker < PILE_END; taker++) {
          if (!_desk[taker].isEmpty() && Deck.isTableau(_desk[taker].peek(), card)) {
            if (isMoveForward(toMove(giver, taker))) {
              return true;
            }
          }
        }
      }
    }

    // 3. Test bases:
    // We can take cards from bases only to form a tableau.

    // Get opposite color bases minimal ranks.
    final int ranks[] = getBaseMinRanks();

    for (int giver = BASE_START; giver < BASE_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        final int rank = Deck.rankOf(card);
        final int color = Deck.colorOf(card);

        if (rank > ranks[color] + 1) {
          for (int taker = PILE_START; taker < PILE_END; taker++) {
            if (!_desk[taker].isEmpty() && Deck.isTableau(_desk[taker].peek(), card)) {
              if (isMoveForward(toMove(giver, taker))) {
                return true;
              }
            }
          }
        }
      }
    }

    return false;
  }

  public void getMovesToTableau(IntStack moves) {
    // 1. Test piles:
    for (int giver = PILE_START; giver < PILE_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        for (int taker = PILE_START; taker < PILE_END; taker++) {
          if (giver != taker && !_desk[taker].isEmpty() && Deck.isTableau(_desk[taker].peek(), card)) {
            final int move = toMove(giver, taker);
            if (isMoveForward(move)) {
              moves.push(move);
            }
          }
        }
      }
    }

    // 2. Test cells:
    for (int giver = CELL_START; giver < CELL_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        for (int taker = PILE_START; taker < PILE_END; taker++) {
          if (!_desk[taker].isEmpty() && Deck.isTableau(_desk[taker].peek(), card)) {
            final int move = toMove(giver, taker);
            if (isMoveForward(move)) {
              moves.push(move);
            }
          }
        }
      }
    }

    // 3. Test bases:
    // We can take cards from bases only to form a tableau.

    // Get opposite color bases minimal ranks.
    final int ranks[] = getBaseMinRanks();

    for (int giver = BASE_START; giver < BASE_END; giver++) {
      if (!_desk[giver].isEmpty()) {
        final byte card = _desk[giver].peek();
        final int rank = Deck.rankOf(card);
        final int color = Deck.colorOf(card);

        if (rank > ranks[color] + 1) {
          for (int taker = PILE_START; taker < PILE_END; taker++) {
            if (!_desk[taker].isEmpty() && Deck.isTableau(_desk[taker].peek(), card)) {
              final int move = toMove(giver, taker);
              if (isMoveForward(move)) {
                moves.push(move);
              }
            }
          }
        }
      }
    }
  }

  public boolean hasNextMove() {
    return canMoveToCell() || canMoveToPile() || canMoveToBase() || canMoveToTableau();
  }

  public void getMoves(final IntStack moves) {
    moves.clear();
    getMovesToBase(moves);
    getMovesToTableau(moves);
    getMovesToCell(moves);
    getMovesToPile(moves);
  }

  public void rewind() {
    backward(0);
  }

  /**
   * Makes a new deal.
   * 
   * @param seed seed number
   */
  byte[] deal(final int seed) {
    final var cards = Deck.deal(seed);

    clear();
    for (int i = 0; i < cards.length; i++) {
      _desk[PILE_START + (i % PILE_NUM)].push(cards[i]);
    }
    return cards;
  }

  public static void main(String[] args) {
    FreecellGame game = new FreecellGame(8, 4, 4);
    final int seed = 417;
    game.deal(seed);

    System.out.println("Deal: " + seed);
    System.out.println("Game: " + game);
    int count = game.moveCardsAuto();
    System.out.println("Auto: " + count);
    System.out.println("Game: " + game);
    System.out.println("Key: " + game.toKey());
    
    System.out.println("Rewinding...");
    game.rewind();
    System.out.println("Game: " + game);
    IntStack moves = new IntStack();
    count = 0;
    while (true) {
      moves.clear();
      game.getMovesToBase(moves);
      if (moves.isEmpty()) {
        break;
      } else {
        game.moveCard(moves.get(0));
        count++;
        System.out.println("Move: " + count);
        System.out.println("Key: " + game.toKey());
      }
    }
    System.out.println("Game: " + game);
  }
}
