import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sol24 {

  /** Width of map including walls */
  private static final int WIDTH = 122; // 122;

  /** Height of map including walls */
  private static final int HEIGHT = 27;

  /** Map */
  private static char[][] map = new char[HEIGHT][WIDTH];

  /** Lines of map with some additional information */
  private static List<Line> lines = new ArrayList<>();

  /** List of all Blizzards */
  private static List<Blizzard> blizzards = new ArrayList<>();

  /**
   * Maximum time to simulate the movement of blizzards, after which the overall
   * movement repeats itself. In general, it's the LCM of width and height (excluding
   * the walls).
   */
  private static final int REPEATS_AFTER = 600;

  /** Minimum distance the goal can be reached */
  private static int minValue = Integer.MAX_VALUE;

  /**
   * Maximum value we expect the operation to succeed. It's a rough estimation in
   * order to make the simulation faster.
   */
  private static final int ABS_MAX_VALUE = 1506;

  /**
   * Blizzard
   */
  private static class Blizzard {
    /** Current position, x coordinate */
    private int x;

    /** Current position, y coordinate */
    private int y;

    /** The movement of the Blizzard towards x direction (-1, 0, or 1) */
    private final int moveX;

    /** The movement of the Blizzard towards x direction (-1, 0, or 1) */
    private final int moveY;

    /**
     * Creates a Blizzard
     *
     * @param x Starting coordinate, x value
     * @param y Starting coordinate, y value
     * @param chr Character which represents the movement
     */
    /* default */ Blizzard(final int x, final int y, final char chr) {
      this.x = x;
      this.y = y;
      switch (chr) {
        case '>':
          this.moveX = 1;
          this.moveY = 0;
          break;
        case '<':
          this.moveX = -1;
          this.moveY = 0;
          break;
        case '^':
          this.moveX = 0;
          this.moveY = -1;
          break;
        case 'v':
          this.moveX = 0;
          this.moveY = 1;
          break;
        default:
          throw new RuntimeException("Invalid movement character");
      }
      lines.get(this.y).occupiedAtTime.get(0).add(this.x);

    }

    @Override
    public String toString() {
      return "(" + this.x + "," + this.y + ")";
    }

    /**
     * Take a step towards the predefined direction
     *
     * @param time Current time
     */
    /* default */void step(final int time) {
      final int nextX = this.x + this.moveX;
      final int nextY = this.y + this.moveY;
      final boolean changeX = this.x != nextX;
      if (map[nextY][nextX] == '#') {
        // if would bump into a wall, appear on the other side
        if (changeX) {
          this.y = nextY;
          if (nextX > this.x) {
            this.x = 1;
          } else {
            this.x = WIDTH - 2;
          }
        } else {
          this.x = nextX;
          if (nextY > this.y) {
            this.y = 1;
          } else {
            this.y = HEIGHT - 2;
          }
        }
      } else {
        this.x = nextX;
        this.y = nextY;
      }

      lines.get(this.y).occupiedAtTime.get(time).add(this.x);

    }
  }

  /**
   * Represents a line of the map
   */
  private static class Line {
    /**
     * Assigns to every time integer a set of positions which are occupied at the
     * given time by blizzards. time can be at most REPEATS_AFTER as after that, the
     * whole map repeats
     */
    /* default */Map<Integer, Set<Integer>> occupiedAtTime = new HashMap<>();

    /**
     * Assigns to every time integer a set of positions, where we have already tried
     * to build a path from at a given time. Time can be any huge. It prevents us
     * from trying to build the same path if we have been there at an earlier time
     * but with the same map state.
     */
    /* default */Map<Integer, Set<Integer>> hasBeenHereAtTime = new HashMap<>();

    /**
     * Creates and initialises a line
     */
    /* default */ Line() {
      for (int i = 0; i < REPEATS_AFTER + 1; i++) {
        this.occupiedAtTime.put(i, new HashSet<>());
        this.hasBeenHereAtTime.put(i, new HashSet<>());
      }
    }
  }

  public static void main(final String[] args) throws IOException {
    String line;
    String[] splittedLine;

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in24.txt"), StandardCharsets.UTF_8)) {

      int y = 0;
      while ((line = buffR.readLine()) != null) {
        splittedLine = line.split("");
        int x = 0;
        lines.add(new Line());
        for (final String chr : splittedLine) {
          // write map array
          map[y][x] = chr.charAt(0);
          if (chr.charAt(0) != '#' && chr.charAt(0) != '.') {
            // read blizzard
            blizzards.add(new Blizzard(x, y, chr.charAt(0)));
            if (!lines.get(y).occupiedAtTime.containsKey(0)) {
              lines.get(y).occupiedAtTime.put(0, new HashSet<>());
            }
            lines.get(y).occupiedAtTime.get(0).add(x);
          }
          x++;
        }
        y++;
      }

      // simulate the movement of Blizzards
      for (int i = 1; i < REPEATS_AFTER + 1; i++) {
        simulateBlizzs(i);
      }

      // simulate part 1
      final int p1 = simulateSteps(1, 0, 0, true);
      System.out.println("Solution for Part 1: " + p1);

      // reset values, to be able to start a new root finding
      minValue = Integer.MAX_VALUE;
      lines.forEach(l -> {
        l.hasBeenHereAtTime.clear();
      });

      // go back
      final int reachingBackTime = simulateSteps(WIDTH - 2, HEIGHT - 1, p1, false);

      // reset again
      minValue = Integer.MAX_VALUE;
      lines.forEach(l -> {
        l.hasBeenHereAtTime.clear();
      });

      // go to the end again
      final int part2 = simulateSteps(1, 0, reachingBackTime, true);
      System.out.println("Solution for Part 2: " + part2);

    }
  }

  /**
   * Simulates all blizzards
   *
   * @param time
   */
  private static void simulateBlizzs(final int time) {
    blizzards.forEach(b -> b.step(time));
  }

  /**
   * Finds smallest route to destination
   *
   * @param x Current coordinate x
   * @param y Current coordiante y
   * @param time Current time
   * @param isGoalDown True if we're going from top to bottom, false if going from
   *        down to top
   * @return Smallest possible steps to reach the goal
   */
  private static int simulateSteps(final int x, final int y, final int time,
      final boolean isGoalDown) {

    // if any of these conditions met, we don't care about the result. Return max
    // value
    if (y < 0 || y >= HEIGHT || time >= minValue || time > ABS_MAX_VALUE
        || map[y][x] == '#'
        || lines.get(y).occupiedAtTime.get(time % REPEATS_AFTER).contains(x)) {
      return Integer.MAX_VALUE;
    }

    // if we found the goal, return the current time as a result
    if (isGoalDown ? y == HEIGHT - 1 : y == 0) {
      if (time < minValue) {
        minValue = time;
        return time;
      }
      return Integer.MAX_VALUE;
    }

    // if we have been here at any (t - maxTimeToSimulate * k) time, the positions of
    // the blizzards were exactly the same, but we would have resulted in a small
    // value. thus, kill this fork of the simulation
    for (int t = time; t >= 0; t -= REPEATS_AFTER) {
      if (lines.get(y).hasBeenHereAtTime.containsKey(t)
          && lines.get(y).hasBeenHereAtTime.get(t).contains(x)) {
        return Integer.MAX_VALUE;
      }
    }

    // if haven't been here yet, add this time-position pair to the list
    if (!lines.get(y).hasBeenHereAtTime.containsKey(time)) {
      lines.get(y).hasBeenHereAtTime.put(time, new HashSet<>());
    }
    lines.get(y).hasBeenHereAtTime.get(time).add(x);

    // try to simulate all scenarios
    final int val1 = simulateSteps(x + 1, y, time + 1, isGoalDown);
    final int val2 = simulateSteps(x - 1, y, time + 1, isGoalDown);
    final int val3 = simulateSteps(x, y + 1, time + 1, isGoalDown);
    final int val4 = simulateSteps(x, y - 1, time + 1, isGoalDown);
    final int val5 = simulateSteps(x, y, time + 1, isGoalDown);

    // return the minimum of them
    return Math.min(Math.min(val1, val2), Math.min(val3, Math.min(val4, val5)));
  }
}
