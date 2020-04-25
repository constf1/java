package freecell;

import common.Deck;
import common.IntStack;

public class FreecellGame extends FreecellDesk {
  // public interface OnMove {
  //   void test(FreecellGame game);
  // }
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

  // protected void _addCard(final int destination, final int card) {
  //   _desk[destination].push(card);
  // }

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

  // public void moveCard(final int move) {
  //   moveCard(toGiver(move), toTaker(move));
  // }

  public boolean isMoveForward(final int move) {
    return _path.isEmpty() || _path.peek() != toMove(toTaker(move), toGiver(move));
  }

  public void moveCardsToBases() {
    for (boolean next = true; next;) {
      next = false;
      for (int giver = 0; giver < DESK_SIZE; giver++) {
        if (!(isBase(giver) || _desk[giver].isEmpty())) {
          final int taker = getBase(_desk[giver].peek());
          if (taker >= 0) {
            moveCard(giver, taker);
            next = true;
          }
        }
      }
    }
  }

  public void moveCardsAuto() {
    for (boolean next = true; next;) {
      next = false;
      for (int giver = 0; giver < DESK_SIZE; giver++) {
        if (!(isBase(giver) || _desk[giver].isEmpty())) {
          final int card = _desk[giver].peek();
          final int taker = getBase(card);
          if (taker >= 0) {
            if (Deck.rankOf(card) <= getOppositeColorBaseMinRank(Deck.suitOf(card)) + 1) {
              moveCard(giver, taker);
              next = true;
            }
          }
        }
      }
    }
  }

  // private void _onMove(final OnMove onMove, final int giver, final int taker) {
  //   // Ignore reverse moves
  //   if (_path.isEmpty() || toTaker(_path.peek()) != giver || toGiver(_path.peek()) != taker) {
  //     final int mark = _path.size();
  //     moveCard(giver, taker);
  //     // moveCardsAuto();
  //     onMove.test(this);
  //     backward(mark);
  //   }
  // }

  public int getBaseMinRank() {
    int rank = _desk[BASE_START].size();
    for (int i = 1; i < BASE_NUM; i++) {
      rank = Math.min(rank, _desk[BASE_START + i].size());
    }
    return rank;
  }

  // public void findMoves(final OnMove onMove) {
  //   // First make all mandatory moves to the bases.
  //   int ranks[] = { _desk[BASE_START].size(), _desk[BASE_START + 1].size() };
  //   for (int i = 2; i < BASE_NUM; i++) {
  //     ranks[i & 1] = Math.min(ranks[i & 1], _desk[BASE_START + i].size());
  //   }

  //   for (int giver = 0; giver < DESK_SIZE; giver++) {
  //     if (!(_desk[giver].isEmpty() || isBase(giver))) {
  //       final int card = _desk[giver].peek();
  //       final int suit = Deck.suitOf(card);
  //       final int rank = Deck.rankOf(card);
  //       if (rank <= ranks[(suit + 1) & 1] + 1) {
  //         final int base = getBase(card);
  //         if (base >= 0) {
  //           _onMove(onMove, giver, base);
  //           return; // Only one mangatory move is allowed.
  //         }
  //       }
  //     }
  //   }

  //   final int emptyCell = getEmptyCell();
  //   final int emptyPile = getEmptyPile();

  //   for (int giver = 0; giver < DESK_SIZE; giver++) {
  //     if (!_desk[giver].isEmpty()) {
  //       final int card = _desk[giver].peek();
  //       final int suit = Deck.suitOf(card);
  //       final int rank = Deck.rankOf(card);

  //       if (isBase(giver)) {
  //         // We can take cards from bases only to form a tableau.
  //         if (rank > ranks[(suit + 1) & 1] + 1) {
  //           for (int pile = PILE_START; pile < PILE_END; pile++) {
  //             if (!_desk[pile].isEmpty() && Deck.isTableau(_desk[pile].peek(), card)) {
  //               _onMove(onMove, giver, pile);
  //             }
  //           }
  //         }
  //       } else {
  //         // Cells and piles:
  //         // 1. To the base.
  //         for (int base = BASE_START + suit; base < BASE_END; base += Deck.SUIT_NUM) {
  //           if (_desk[base].size() == rank) {
  //             _onMove(onMove, giver, base);
  //             break; // one base is enough.
  //           }
  //         }
  //         // 2. To a tableau.
  //         for (int pile = PILE_START; pile < PILE_END; pile++) {
  //           if (!_desk[pile].isEmpty() && Deck.isTableau(_desk[pile].peek(), card)) {
  //             _onMove(onMove, giver, pile);
  //           }
  //         }
          
  //         if (isCell(giver)) {
  //           // Cells only
  //           // 3. To an empty pile.
  //           if (emptyPile >= 0) {
  //             _onMove(onMove, giver, emptyPile);
  //           }
  //         } else {
  //           // It should be a pile then.
  //           // 3. To an empty cell.
  //           if (emptyCell >= 0) {
  //             _onMove(onMove, giver, emptyCell);
  //           }
  //           // 4. To an empty pile.
  //           if (emptyPile >= 0 && _desk[giver].size() > 1) {
  //             _onMove(onMove, giver, emptyPile);
  //           }
  //         }
  //       }
  //     }
  //   }
  // }

  public void getMoves(final IntStack moves) {
    moves.clear();

    // Get opposite color bases minimal ranks.
    int ranks[] = { _desk[BASE_START].size(), _desk[BASE_START + 1].size() };
    for (int i = 2; i < BASE_NUM; i++) {
      ranks[i & 1] = Math.min(ranks[i & 1], _desk[BASE_START + i].size());
    }

    final int emptyCell = getEmptyCell();
    final int emptyPile = getEmptyPile();

    for (int giver = DESK_SIZE; giver-- > 0;) {
      if (!_desk[giver].isEmpty()) {
        final int card = _desk[giver].peek();
        final int suit = Deck.suitOf(card);
        final int rank = Deck.rankOf(card);

        if (isBase(giver)) {
          // We can take cards from bases only to form a tableau.
          if (rank > ranks[(suit + 1) & 1] + 1) {
            for (int pile = PILE_START; pile < PILE_END; pile++) {
              if (!_desk[pile].isEmpty() && Deck.isTableau(_desk[pile].peek(), card)) {
                moves.push(toMove(giver, pile));
              }
            }
          }
        } else {
          if (isCell(giver)) {
            // Cells only
            // 1. To an empty pile.
            if (emptyPile >= 0) {
              moves.push(toMove(giver, emptyPile));
            }
          } else {
            // It should be a pile then.
            // 1. To an empty cell.
            if (emptyCell >= 0) {
              moves.push(toMove(giver, emptyCell));
            }
            // 2. To an empty pile.
            if (emptyPile >= 0 && _desk[giver].size() > 1) {
              moves.push(toMove(giver, emptyPile));
            }
          }

          // Cells and piles:
          // 1. To a tableau.
          for (int pile = PILE_START; pile < PILE_END; pile++) {
            if (!_desk[pile].isEmpty() && Deck.isTableau(_desk[pile].peek(), card)) {
              moves.push(toMove(giver, pile));
            }
          }
          // 2. To the base.
          for (int base = BASE_START + suit; base < BASE_END; base += Deck.SUIT_NUM) {
            if (_desk[base].size() == rank) {
              if (rank <= ranks[(suit + 1) & 1] + 1) {
                // It's a mandatory move to the base. Clear all other moves and return this move only.
                moves.clear();
                moves.push(toMove(giver, base));
                return;
              } else {
                moves.push(toMove(giver, base));
                break; // one base is enough.
              }
            }
          }
        }
      }
    }
  }

  public void rewind() {
    backward(0);
  }
  
  // public int pathLength() {
  //   return _path.size();
  // }

  // public int[] pathToArray() {
  //   return _path.toArray​();
  // }

  /**
   * Makes a new deal.
   * 
   * @param seed seed number
   */
  int[] deal(final int seed) {
    final var cards = Deck.deck(seed);

    clear();
    for (int i = 0; i < cards.length; i++) {
      _desk[PILE_START + (i % PILE_NUM)].push(cards[i]);
    }
    return cards;
  }

}