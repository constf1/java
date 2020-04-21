package common;

/**
 * Standard 52-card deck
 */

public class Deck {
  public static final String SUITS = "SDCH";
  public static final char SUIT_CHARACTERS[] = {'S', 'D', 'C', 'H'};
  public static final char SUIT_PLAY_NAMES[] = {'♠', '♦', '♣', '♥'};

  public static final String SUIT_FULL_NAMES[] = {
    "spades",
    "diamonds",
    "clubs",
    "hearts"
  };

  public static final String SUIT_HTML_CODES[] = {
    "&spades;",
    "&diams;",
    "&clubs;",
    "&hearts;"
  }; // Special Symbol Character Codes for HTML

  public static final String RANKS = "A23456789TJQK";
  public static final char RANK_CHARACTERS[] = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };

  public static final String RANK_PLAY_NAMES[] = {
    "A",
    "2",
    "3",
    "4",
    "5",
    "6",
    "7",
    "8",
    "9",
    "10",
    "J",
    "Q",
    "K"
  };

  public static final String RANK_FULL_NAMES[] = {
    "Ace",
    "Two",
    "Three",
    "Four",
    "Five",
    "Six",
    "Seven",
    "Eight",
    "Nine",
    "Ten",
    "Jack",
    "Queen",
    "King"
  };

  public static final int SUIT_NUM = SUITS.length();
  public static final int RANK_NUM = RANKS.length();

  // Standard 52-card deck
  public static final int CARD_NUM = SUIT_NUM * RANK_NUM;

  // Card index is defined as: suit + rank * SUIT_NUM
  public static int indexOf(int s, int r) {
    return s + r * SUIT_NUM;
  }

  public static int rankOf(int index) {
    return index / SUIT_NUM;
  }

  public static int suitOf(int index) {
    return index % SUIT_NUM;
  }

  // Card names:
  public static String nameOf(int index) {
    return "" + RANKS.charAt(rankOf(index)) + SUITS.charAt(suitOf(index));
  }

  public static String playNameOf(int index) {
    return RANK_PLAY_NAMES[rankOf(index)] + SUIT_PLAY_NAMES[suitOf(index)];
  }

  public static String fullNameOf(int index) {
    return (
      RANK_FULL_NAMES[rankOf(index)] + " of " + SUIT_FULL_NAMES[suitOf(index)]
    );
  }

  // Suit names:
  public static char suitNameOf(int index) {
    return SUITS.charAt(suitOf(index));
  }

  public static char suitPlayNameOf(int index) {
    return SUIT_PLAY_NAMES[suitOf(index)];
  }

  public static String suitFullNameOf(int index) {
    return SUIT_FULL_NAMES[suitOf(index)];
  }

  public static String suitHTMLCodeOf(int index) {
    return SUIT_HTML_CODES[suitOf(index)];
  }

  // Rank names:
  public static char rankNameOf(int index) {
    return RANKS.charAt(rankOf(index));
  }

  public static String rankPlayNameOf(int index) {
    return RANK_PLAY_NAMES[rankOf(index)];
  }

  public static String rankFullNameOf(int index) {
    return RANK_FULL_NAMES[rankOf(index)];
  }

  // A set of optionally shuffled playing cards.
  public static int[] deck(int seed) {
    int cards[] = new int[CARD_NUM];
    for (int i = 0; i < CARD_NUM; i++) {
      cards[i] = i;
    }

    if (seed >= 0) {
      // use LCG algorithm to pick up cards from the deck
      // http://en.wikipedia.org/wiki/Linear_congruential_generator
      final int m = 0x80000000;
      final int a = 1103515245;
      final int c = 12345;

      for (int i = 0; i < CARD_NUM; i++) {
        seed = (a * seed + c) % m;

        // swap cards
        final int j = seed % CARD_NUM;
        if (i != j) {
          final int card = cards[i];
          cards[i] = cards[j];
          cards[j] = card;
        }
      }
    }
    return cards;
  }

  public static boolean isTableau(int cardA, int cardB) {
    return (
      // rankOf(cardA) === (rankOf(cardB) + 1) % RANK_NUM &&
      rankOf(cardA) == rankOf(cardB) + 1 &&
      suitOf(cardA) % 2 != suitOf(cardB) % 2
    );
  }

  public static void main(String[] args) {
    System.out.println("Deck Test");

    final int cards[] = deck(-1);
    for (int card : cards) {
      System.out.println("\t" + card + "\t" + nameOf(card) + "\t" + fullNameOf(card));
    }
  }

}