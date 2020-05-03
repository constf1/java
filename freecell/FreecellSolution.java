package freecell;

public class FreecellSolution {
  public static class Commands {
    public static final String
      d = "-d",
      deal = "--deal",
      s = "-s",
      split = "--split";
  }

  public static void main(String[] args) {
    if (args.length < 1 || (args.length & 1) == 1) {
      usage();
      return;
    }
    
    final var game = new FreecellSolver();
    game.debug = false;
    int deal = -1;

    for (int i = 0; i < args.length; i+=2) {
      String command = args[i];
      try {
        int value = Integer.decode(args[i + 1]);

        if (command.equalsIgnoreCase(Commands.d)
          || command.equalsIgnoreCase(Commands.deal)) {
          deal = value;
          game.deal(deal);
        } else if (command.equalsIgnoreCase(Commands.s)
        || command.equalsIgnoreCase(Commands.split)) {
          game.setInputSize(value);
        }
      } catch (NumberFormatException ex) {
        System.err.println("Couldn't parse integer from '" + args[i + 1]
          + "' for command '" + command + "'");
        usage();
        return;
      }
    }

    if (deal < 0) {
      System.err.println("Deal number should be > 0.");
      usage();
      return;
    }

    var path = game.solve();
    if (path != null) {
      System.out.println("" + deal + ',' + path.length + ',' + game.pathToString(path));
      System.exit(0);
    } else {
      System.err.println("Unsolved ;-(");
      System.exit(1);
    }
  }

  public static void usage() {
    System.out.println("Usage:");
    System.out.println("\t"  + Commands.d + ", " + Commands.deal
      + "\t<number>\tdeal number (>= 0)");
    System.out.println("\t" + Commands.s + ", " + Commands.split
      + "\t<number>\tinput split size ["
      + FreecellSolver.INPUT_MIN + ", "
      + FreecellSolver.INPUT_MAX + "]"
    );
    System.exit(-1);
  }
}
