import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sol22 {

  /** Width of all map */
  private static final int WIDTH = 150;

  /** Height of all map */
  private static final int HEIGHT = 200;

  /** Map as a 2D array */
  private static char[][] map = new char[HEIGHT][WIDTH];

  /** Minimum X coordinate on the map in given row y */
  private static int[] minXinRow = new int[HEIGHT];

  /** Maximum X coordinate on the map in given row y */
  private static int[] maxXinRow = new int[HEIGHT];

  /** Minimum Y coordinate on the map in given column x */
  private static int[] minYinCol = new int[WIDTH];

  /** Maximum Y coordinate on the map in given column x */
  private static int[] maxYinCol = new int[WIDTH];

  /** Array of instructions */
  private static char[] instructions;

  /** Divisor for different cube surfaces in map */
  private static final int Y_DIV_1 = 50;

  /** Divisor for different cube surfaces in map */
  private static final int Y_DIV_2 = 100;

  /** Divisor for different cube surfaces in map */
  private static final int Y_DIV_3 = 150;

  /** Divisor for different cube surfaces in map */
  private static final int X_DIV_1 = 50;

  /** Divisor for different cube surfaces in map */
  private static final int X_DIV_2 = 100;

  /** Movement map to help calculate the wrapping rule. */
  private static Map<Position, Position> movements = new HashMap<>();

  /**
   * Represents a position and way we're facing at the given moment
   */
  private static final class Position {
    /**
     * Represents the way we're facing as in the task description: 0 for right (>), 1
     * for down (v), 2 for left (<), and 3 for up (^).
     */
    private int facing;

    /** Position x coordinate */
    private int x;

    /** Position y coordinate */
    private int y;

    /**
     * Creates a starting position
     */
    private Position() {
      this.y = 0;
      this.x = minXinRow[0];
      this.facing = 0;
    }

    /**
     * Creates a position from parameters
     *
     * @param x
     * @param y
     * @param facing
     */
    private Position(final int x, final int y, final int facing) {
      this.x = x;
      this.y = y;
      this.facing = facing;
    }

    /**
     * Copies a position
     *
     * @param m
     */
    private Position(final Position m) {
      this.x = m.x;
      this.y = m.y;
      this.facing = m.facing;
    }

    @Override
    public boolean equals(final Object o) {
      final Position moveObj = (Position) o;
      return this.x == moveObj.x && this.y == moveObj.y
          && this.facing == moveObj.facing;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.x, this.y, this.facing);
    }

    @Override
    public String toString() {
      return "(" + this.x + "," + this.y + "," + this.facing + ")";
    }

    /**
     * Calculates the next position based on the wrapping rule for Part 1
     *
     * @param nextX
     * @param nextY
     * @param nextFace
     * @return
     */
    private Position calcOutOfMapMovePart1(int nextX, int nextY,
        final int nextFace) {
      final Position nextReal = new Position();
      if (this.x != nextX) {
        // x changes
        if (nextX < minXinRow[nextY]) {
          nextX = maxXinRow[nextY];
        } else if (nextX > maxXinRow[nextY]) {
          nextX = minXinRow[nextY];
        }
      } else if (this.y != nextY) {
        if (nextY < minYinCol[nextX]) {
          nextY = maxYinCol[nextX];
        } else if (nextY > maxYinCol[nextX]) {
          nextY = minYinCol[nextX];
        }
      }

      nextReal.x = nextX;
      nextReal.y = nextY;
      nextReal.facing = nextFace;
      return nextReal;
    }

    /**
     * Calculates the next position based on the wrapping rule for Part 2. Instead of
     * returning the value, we fill up the movements list and use that later as a
     * reference. <br>
     * Unfortunately this part isn't too clever, the if-then-else statements are
     * based on my input format. Would require more thoughts on how to solve the
     * problem more generally.
     *
     * @param nextX
     * @param nextY
     * @param nextFace
     */
    private void calcOutOfMapMovePart2(final int nextX, final int nextY,
        final int nextFace) {
      final Position origMove = new Position(nextX, nextY, nextFace);

      // if movement is already in list, there is nothing to do
      if (movements.containsKey(origMove)) {
        return;
      }

      // based on what cube surface we're currently standing at, the movement should
      // be different. As stated above, this part only works to our input.
      // However, we only add movement once: if from position (x1, y1, facing1) we
      // need to move to (x2, y2, facing2), we also know that from (x2, y2,
      // reversed-facing2) we need to move to (x1, y1, reversed-facing1). This makes
      // our life slightly easier and our code less error-prone

      final Position nextReal = new Position();
      if (this.x != nextX) {
        if (nextX < minXinRow[nextY]) {
          if (this.y < Y_DIV_1) {
            nextReal.y = Y_DIV_3 - (this.y + 1);
            nextReal.x = 0;
            nextReal.facing = 0;
          } else if (this.y < Y_DIV_2) {
            nextReal.y = Y_DIV_2;
            nextReal.x = this.x - (Y_DIV_2 - this.y);
            nextReal.facing = 1;
          } else if (this.y >= Y_DIV_3) {
            nextReal.y = 0;
            nextReal.x = X_DIV_2 - (HEIGHT - this.y);
            nextReal.facing = 1;
          } else {
            return;
          }
        } else if (nextX > maxXinRow[nextY]) {
          if (this.y < Y_DIV_1) {
            nextReal.y = Y_DIV_3 - (this.y + 1);
            nextReal.x = X_DIV_2 - 1;
            nextReal.facing = 2;
          } else if (this.y < Y_DIV_2) {
            nextReal.y = Y_DIV_1 - 1;
            nextReal.x = this.x + (this.y - Y_DIV_1 + 1);
            nextReal.facing = 3;
          } else if (this.y >= Y_DIV_3 && this.y < HEIGHT) {
            nextReal.y = Y_DIV_3 - 1;
            nextReal.x = this.x + (this.y - Y_DIV_3 + 1);
            nextReal.facing = 3;
          } else {
            return;
          }
        }
      } else if (this.y != nextY && nextX < X_DIV_1 && this.facing == 1) {
        nextReal.y = 0;
        nextReal.x = X_DIV_2 + this.x;
        nextReal.facing = 1;
      } else {
        return;
      }

      movements.put(new Position(this), new Position(nextReal));
      nextReal.facing = (nextReal.facing + 2) % 4;
      this.facing = (origMove.facing + 2) % 4;
      movements.put(new Position(nextReal), new Position(this));
    }

    /**
     * Returns the number of steps we need to step on the x coordinate, based on the
     * current facing. Either -1, 0, or 1.
     *
     * @return
     */
    private int getPlusX() {
      return this.facing == 0 ? 1 : this.facing == 2 ? -1 : 0;
    }

    /**
     * Returns the number of steps we need to step on the y coordinate, based on the
     * current facing. Either -1, 0, or 1.
     *
     * @return
     */
    private int getPlusY() {
      return this.facing == 1 ? 1 : this.facing == 3 ? -1 : 0;
    }

    /**
     * Rotates left by changing the facing value.
     */
    private void rotateLeft() {
      this.facing = (this.facing - 1 + 4) % 4;
    }

    /**
     * Rotates left by changing the facing value.
     */
    private void rotateRight() {
      this.facing = (this.facing + 1) % 4;
    }

    /**
     * Steps one if possible
     *
     * @param isPart1
     * @return Whether a step was taken
     */
    private boolean step(final boolean isPart1) {

      // next coordinates, if they are on the map
      int nextX = this.x + this.getPlusX();
      int nextY = this.y + this.getPlusY();
      int nextFacing = this.facing;

      // whether they are actually on the map
      final boolean inMap = nextX >= 0 && nextX < WIDTH && nextY >= 0
          && nextY < HEIGHT && nextX >= minXinRow[nextY] && nextX <= maxXinRow[nextY]
          && nextY >= minYinCol[nextX] && nextY <= maxYinCol[nextX]

          && map[nextY][nextX] != ' ';

      if (!inMap) {
        // if nextX, nextY coordinates are out of the map, should apply the wrappign
        // rule
        Position nextReal;

        if (isPart1) {
          nextReal = this.calcOutOfMapMovePart1(nextX, nextY, this.facing);
        } else {
          nextReal = movements.get(this);
        }

        nextX = nextReal.x;
        nextY = nextReal.y;
        nextFacing = nextReal.facing;
      }

      // at this point, we know we're on the map, but should check for walls

      if (map[nextY][nextX] == '#') {
        // do not update current position as there is a wall ahead
        return false;
      }

      // if no wall is at the position, actually change current parameters
      this.x = nextX;
      this.y = nextY;
      this.facing = nextFacing;
      return true;
    }

  }

  public static void main(final String[] args) throws IOException {
    // init arrays
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        map[y][x] = ' ';
      }
    }
    for (int y = 0; y < HEIGHT; y++) {
      minXinRow[y] = Integer.MAX_VALUE;
      maxXinRow[y] = Integer.MIN_VALUE;
    }
    for (int x = 0; x < WIDTH; x++) {
      minYinCol[x] = Integer.MAX_VALUE;
      maxYinCol[x] = Integer.MIN_VALUE;
    }

    // read input
    String line;
    String[] splittedLine;

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in22.txt"), StandardCharsets.UTF_8)) {

      // read map
      int y = 0;
      while ((line = buffR.readLine()) != null) {
        if (line.isEmpty()) {
          break;
        }
        splittedLine = line.split("");
        int x = 0;

        for (final String c : splittedLine) {
          map[y][x] = c.charAt(0);

          // update minimum and maximum values
          if (map[y][x] != ' ') {
            if (y < minYinCol[x]) {
              minYinCol[x] = y;
            }
            if (y > maxYinCol[x]) {
              maxYinCol[x] = y;
            }
            if (x < minXinRow[y]) {
              minXinRow[y] = x;
            }
            if (x > maxXinRow[y]) {
              maxXinRow[y] = x;
            }
          }
          x++;
        }
        y++;
      }

      // read instructions
      line = buffR.readLine();
      instructions = line.toCharArray();

      // for part 2, calculate all out of map moves (fill up movements list)
      for (int x = 0; x < WIDTH; x++) {
        new Position(x, maxYinCol[x], 1).calcOutOfMapMovePart2(x, maxYinCol[x] + 1,
            1);
        new Position(x, minYinCol[x], 3).calcOutOfMapMovePart2(x, minYinCol[x] - 1,
            3);
      }
      for (int yi = 0; yi < HEIGHT; yi++) {
        new Position(minXinRow[yi], yi, 2).calcOutOfMapMovePart2(minXinRow[yi] - 1,
            yi, 2);
        new Position(maxXinRow[yi], yi, 0).calcOutOfMapMovePart2(maxXinRow[yi] + 1,
            yi, 0);
      }

      // calculate solutions
      final int p1 = simulate(true);
      System.out.println("Solution for Part 1: " + p1);

      final int p2 = simulate(false);
      System.out.println("Solution for Part 2: " + p2);

    }
  }

  /**
   * Simulates steps
   *
   * @param isPart1 whether Part 1 wrapping rule should be used
   * @return Result based on task description
   */
  private static int simulate(final boolean isPart1) {
    // string buffer to store number of steps since last rotating
    StringBuffer sb = new StringBuffer();
    final Position mv = new Position();

    // for each moves
    for (final char c : instructions) {
      final boolean isRight = c == 'R';
      final boolean isLeft = c == 'L';

      if (isRight || isLeft) {
        // if rotating, check number of steps should be taken before rotating
        final String numOfStepsStr = sb.toString();
        if (!numOfStepsStr.isEmpty()) {
          final int numOfStepsI = Integer.parseInt(numOfStepsStr);
          boolean stepWasTaken = true;
          // repeat steps until numOfStepsI or until a wall is found
          for (int i = 0; i < numOfStepsI && stepWasTaken; i++) {
            stepWasTaken = mv.step(isPart1);
          }
          sb = new StringBuffer();
        }
        if (isRight) {
          mv.rotateRight();
        } else if (isLeft) {
          mv.rotateLeft();
        }
      } else {
        // if not rotating, just add movement character to string builder
        sb.append(c);
      }
    }

    // even if we got to the end of the list, there might be some steps we need to
    // take
    final String numOfStepsStr = sb.toString();
    if (!numOfStepsStr.isEmpty()) {
      final int numOfStepsI = Integer.parseInt(numOfStepsStr);
      boolean stepWasTaken = true;
      for (int i = 0; i < numOfStepsI && stepWasTaken; i++) {
        stepWasTaken = mv.step(isPart1);
      }
    }

    // return result as in the task description
    return (mv.y + 1) * 1000 + (mv.x + 1) * 4 + mv.facing;

  }
}
