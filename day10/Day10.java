import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day10 {

  /** Height of screen */
  private static final int SCREEN_HEIGHT = 6;

  /** Width of screen */
  private static final int SCREEN_WIDTH = 40;

  /**
   * Program command
   */
  private static final class Command {

    /** Length of command execution */
    int length;

    /** Increase of X value */
    int addX;

    /**
     * Parses an input line to a Command isntance
     *
     * @param line
     */
    Command(final String line) {
      final String[] splittedLine = line.split(" ");
      switch (splittedLine[0]) {
        case "noop":
          this.length = 1;
          this.addX = 0;
          return;
        case "addx":
          this.length = 2;
          this.addX = Integer.parseInt(splittedLine[1]);
          return;
        default:
          throw new RuntimeException("Invalid command");
      }
    }
  }

  public static void main(final String[] args) {

    // init variables
    String line;

    int cycle = 1;
    int xValue = 1;

    int nextImpCycle = 20; // next cycle that matters for Part 1
    int signStrengthSum = 0; // signal strength sums in important cycles for Part 1

    final char[][] screen = new char[SCREEN_HEIGHT][SCREEN_WIDTH];
    for (int i = 0; i < SCREEN_HEIGHT; i++) {
      for (int j = 0; j < SCREEN_WIDTH; j++) {
        screen[i][j] = ' ';
      }
    }

    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in10.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        final Command cmd = new Command(line);

        for (int len = 0; len < cmd.length; len++) {
          final int pixelY = (cycle - 1) / 40;
          final int pixelX = (cycle - 1) % 40;
          if (xValue - 1 <= pixelX && pixelX <= xValue + 1) {
            screen[pixelY][pixelX] = '#';
          }

          if (cycle == nextImpCycle) {
            signStrengthSum += cycle * xValue;
            nextImpCycle += 40;
          }
          cycle++;
        }
        xValue += cmd.addX;
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    System.out.println("Solution for Part 1: " + signStrengthSum);

    System.out.println("Solution for Part 2: ");

    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 40; j++) {
        System.out.print(screen[i][j]);
      }
      System.out.println("");
    }
  }

}
