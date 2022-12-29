import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Day14 {

  /** Maximum WIDTH of the map */
  private static final int WIDTH = 1000;

  /** Maximum height of the map */
  private static final int HEIGTH = 1000;

  /**
   * Map, where element {@code MAP[y][x]} represents the point with coordinate (x, y)
   */
  private static final char[][] MAP = new char[HEIGTH][WIDTH];

  /**
   * Used for Part 1 to save the lowest rock for each column. Not that the higher the
   * value is, the lower a rock is.
   */
  private static final Integer[] LOWEST_IN_COL = new Integer[WIDTH];

  /**
   * Coordinate class for an element
   */
  private static class Coord {
    /** X coordinate */
    /* default */ int x;

    /** Y coordinate */
    /* default */ int y;

    /**
     *
     * @param x
     * @param y
     */
    /* default */ Coord(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    /**
     * @param cordS String to parse coordinate from
     */
    /* default */ Coord(final String cordS) {
      final String[] splittedS = cordS.split(",");
      this.x = Integer.parseInt(splittedS[0].trim());
      this.y = Integer.parseInt(splittedS[1].trim());
    }

    private static void adjustLowestInCold(final int x, final int y) {
      if (LOWEST_IN_COL[x] < y) {
        // if current value is higher, that is the new value
        LOWEST_IN_COL[x] = y;
      }
    }


    /**
     * Draws rocks to {@link Sol14.MAP MAP} between this and parameter Coordinate
     *
     * @param prev
     */
    /* default */void drawStones(final Coord prev) {
      // Add rock for current coordinate
      MAP[this.y][this.x] = '#';
      adjustLowestInCold(this.x, this.y);

      if (prev != null) {
        // only draw rest if prev is not null
        // assumed: either x or y coordinates match

        if (prev.x == this.x) {
          // if x coordinates match, we need to iterate trough y coordinates
          final int minY = Math.min(this.y, prev.y);
          final int maxY = Math.max(this.y, prev.y);
          for (int y = minY; y < maxY; y++) {
            MAP[y][this.x] = '#';
            adjustLowestInCold(this.x, y);
          }
        } else if (prev.y == this.y) {
          // if y coordinates match, we need to iterate trough x coordinates
          final int minX = Math.min(this.x, prev.x);
          final int maxX = Math.max(this.x, prev.x);
          for (int x = minX; x < maxX; x++) {
            MAP[this.y][x] = '#';
            adjustLowestInCold(x, this.y);
          }
        }
      }
    }
  }

  public static void main(final String[] args) {
    // Initialise variables

    // reset map
    for (int i = 0; i < HEIGTH; i++) {
      for (int j = 0; j < WIDTH; j++) {
        MAP[i][j] = '.';
      }
    }
    // reset lowestInCol
    for (int j = 0; j < WIDTH; j++) {
      LOWEST_IN_COL[j] = -1;
    }

    String line; // to parse input

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in14.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        parseLine(line);
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // Part 1. Drop sand units until we can
    int rested = 0;
    while (dropSand(500, 0)) {
      rested++;
    }

    System.out.println("Solution for Part 1: " + rested);

    // Reset MAP for Part 2, delete sand pieces
    for (int i = 0; i < HEIGTH; i++) {
      for (int j = 0; j < WIDTH; j++) {
        if (MAP[i][j] == 'o') {
          MAP[i][j] = '.';
        }
      }
    }

    // Find maximum height for all rocks
    final int maxHeight = Arrays.asList(LOWEST_IN_COL).stream()
        .reduce((a, b) -> a > b ? a : b).orElse(0);

    // Create ground line
    final Coord cord = new Coord(0, maxHeight + 2);
    cord.drawStones(null);
    final Coord cord2 = new Coord(WIDTH - 1, maxHeight + 2);
    cord2.drawStones(cord);

    // Set up lowestInCol for ground line
    for (int j = 0; j < WIDTH; j++) {
      LOWEST_IN_COL[j] = maxHeight + 2;
    }

    // Now, simulate Part 2, similarly to Part 1
    // Note that if starting point is occupied, we stop
    rested = 0;
    while (MAP[0][500] == '.' && dropSand(500, 0)) {
      rested++;
    }

    System.out.println("Solution for Part 2: " + rested);
  }

  /**
   * Drops a single sand unit
   *
   * @param x Current x coordinate of sand unit
   * @param y Current y coordinate of sand unit
   * @return Whether sand unit came to rest in the end of its move
   */
  private static boolean dropSand(final int x, final int y) {
    if (LOWEST_IN_COL[x] < y) {
      // if it's under the last rock in the column, it will never come to rest
      return false;
    }

    if (MAP[y + 1][x] == '.') {
      // if there is space, move down
      return dropSand(x, y + 1);
    } else if (MAP[y + 1][x - 1] == '.') {
      // otherwise, try to move left-down
      return dropSand(x - 1, y + 1);
    } else if (MAP[y + 1][x + 1] == '.') {
      // otherwise, try to move right-down
      return dropSand(x + 1, y + 1);
    } else {
      // otherwise, come to rest
      // Note that MAP is only updated when it came to rest.
      MAP[y][x] = 'o';
      return true;
    }
  }

  /**
   * Parses a single line input, draws rocks to {@link Sol14.MAP MAP}
   *
   * @param line
   */
  private static void parseLine(final String line) {
    final String[] splittedLine = line.split("->");
    Coord prevCord = null;
    Coord currCord;
    for (final String linePart : splittedLine) {
      currCord = new Coord(linePart);
      currCord.drawStones(prevCord);
      prevCord = currCord;
    }
  }

}
