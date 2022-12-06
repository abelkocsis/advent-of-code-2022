import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sol2 {

  /**
   * A shape to be played, wither ROCK, PAPER or SCISSORS
   */
  enum Rps {
    ROCK(1), PAPER(2), SCISSORS(3);

    /** Array to help choosing winner/loser */
    private static Rps[] rpsArray = {SCISSORS, ROCK, PAPER, SCISSORS, ROCK};

    /** Score for the shape */
    private final int value;

    Rps(final int value) {
      this.value = value;
    }

    /** Gets shape which it beats */
    public Rps beats() {
      return rpsArray[this.value - 1];
    }

    /** Gets basic score for the shape */
    public int getValue() {
      return this.value;
    }

    /** Gets shape which it loses against */
    public Rps losesAgainst() {
      return rpsArray[this.value + 1];
    }

  }

  public static void main(final String[] args) {
    String line;
    Rps opponent;
    Rps own;
    int score1 = 0;
    int score2 = 0;

    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in2.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        final String[] splittedLine = line.split(" ");
        opponent = parse(splittedLine[0].charAt(0));
        own = parse(splittedLine[1].charAt(0));
        score1 += getScore(own, opponent);

        own = toPlay(opponent, splittedLine[1].charAt(0));
        score2 += getScore(own, opponent);
      }
    } catch (

    final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }
    System.out.println("Solution for Part 1: " + score1);
    System.out.println("Solution for part 2: " + score2);
  }

  /**
   * Parses a character to an {@link Rps Rps} shape
   *
   * @param chr
   * @return
   */
  public static Rps parse(final char chr) {
    switch (chr) {
      case 'A':
      case 'X':
        return Rps.ROCK;
      case 'B':
      case 'Y':
        return Rps.PAPER;
      case 'C':
      case 'Z':
        return Rps.SCISSORS;
      default:
        throw new RuntimeException("Invalid input");
    }
  }

  /**
   * Get overall score for
   *
   * @param own
   * @param opponent
   * @return
   */
  private static int getScore(final Rps own, final Rps opponent) {
    final int baseScore = own.getValue();
    int matchScore;
    if (own.beats() == opponent) {
      matchScore = 6;
    } else if (own.losesAgainst() == opponent) {
      matchScore = 0;
    } else {
      matchScore = 3;
    }
    return baseScore + matchScore;
  }

  /**
   * Decides what to play based on the instruction we receive
   *
   * @param opponent
   * @param instruction Either 'X', 'Y' or 'Z'
   * @return
   */
  private static Rps toPlay(final Rps opponent, final char instruction) {
    switch (instruction) {
      case 'X':
        return opponent.beats();
      case 'Y':
        return opponent;
      case 'Z':
        return opponent.losesAgainst();
      default:
        throw new RuntimeException("Invalid command");
    }
  }
}
