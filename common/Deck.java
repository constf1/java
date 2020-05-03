package common;

public class Deck {
  // Standard 52-card deck
  public static final byte
    CARD_NUM = 52, // SUIT_NUM * RANK_NUM
    SUIT_NUM = 4,
    RANK_NUM = 13;

  public static final char
    SUITS[] = { 'S', 'D', 'C', 'H' },
    RANKS[] = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };

  public static final int
    COLOR_POS = 5,
    COLOR_FLAG = 1 << COLOR_POS,
    RANK_MASK = COLOR_FLAG - 1;

  public static byte cardOf(int index) {
    return (byte)('A' + ((index >>> 1) | ((index & 1) << COLOR_POS)));
  }

  // Returns 0 for blacks (spades and clubs) and 1 for reds (diamonds and hearts)
  public static int colorOf(byte card) {
    return ((card & COLOR_FLAG) >>> COLOR_POS);
  }

  public static boolean isBlack(byte card) {
    return colorOf(card) == 0;
  }

  public static boolean isRed(byte card) {
    return !isBlack(card);
  }

  // Card index is defined as: suit + rank * SUIT_NUM
  public static int indexOf(int s, int r) {
    return s + r * SUIT_NUM;
  }

  // Returns the rank of a card.
  // Cards are ranked, from 0 to 12: A, 2, 3, 4, 5, 6, 7, 8, 9, T, J, Q and K.
  public static int rankOf(byte card) {
    return (((card - 1) & RANK_MASK) >>> 1);
  }

  // Returns the suit of a card. The order of suits: Spades, Diamonds, Clubs and Hearts.
  public static int suitOf(byte card) {
    return (((card - 1) & 1) << 1) | colorOf(card);
  }

  public static boolean isTableau(byte cardA, byte cardB) {
    return (rankOf(cardA) == rankOf(cardB) + 1 && colorOf(cardA) != colorOf(cardB));
  }

  // Returns a set of optionally shuffled playing cards.
  public static byte[] deal(long seed) {
    byte cards[] = new byte[CARD_NUM];
    for (int i = 0; i < CARD_NUM; i++) {
      cards[i] = cardOf(i);
    }

    if (seed >= 0) {
      // System.out.println("SEED: " + seed);
      // use LCG algorithm to pick up cards from the deck
      // http://en.wikipedia.org/wiki/Linear_congruential_generator
      final double m = 0x80000000L;
      final double a = 1103515245L;
      final double c = 12345L;

      for (int i = 0; i < CARD_NUM; i++) {
        seed = (long) ((a * seed + c) % m);

        // swap cards
        final int j = (int)(seed % CARD_NUM);
        if (i != j) {
          final byte card = cards[i];
          cards[i] = cards[j];
          cards[j] = card;
        }
      }
    }

    return cards;
  }

  public static String toString(byte card) {
    int suit = suitOf(card);
    int rank = rankOf(card);
    boolean black = isBlack(card);

    return "Card #" + indexOf(suit, rank) + " " + RANKS[rank] + SUITS[suit] + (black ? " black" : " red");
  }

  public static String toString(byte[] cards) {
    var buf = new StringBuilder();
    for (byte card : cards) {
      buf.append(toString(card)).append('\n');
    }
    return buf.toString();
  }

  public static void main(String[] args) {
    var cards = deal(-1);
    System.out.println(toString(cards));

    var str = new String(cards);
    System.out.println("As string:");
    System.out.println(str);
  }
}
