import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.LongStream;

public class Day17 {

  /** Array of jets */
  private static char[] jets;

  /** Occupied x values for each row at the moment */
  private static List<Set<Integer>> occupation = new ArrayList<>();

  /** Number of removed lines from occupation list at the moment */
  private static long removed = 0L;

  /** Width of chamber */
  private static final int CHAMBER_WIDTH = 7;

  /** Highest value at given column */
  private static Long[] highestAtX = {0L, 0L, 0L, 0L, 0L, 0L, 0L};

  /** Previous Status given time value */
  private static Map<Long, Status> previousOccupations = new HashMap<>();

  /**
   * Represents a rock. <br>
   * We store the rock in a 4x4 array, where [0][0] is the bottom left point of the
   * rock.
   */
  private static class Rock {

    /** Rock elems. '#' for rock and '.' for empty. */
    private final char[][] rock = new char[4][4];

    /** X coordinate of rock */
    private int x;

    /** Y coordiante of rock */
    private long y;

    /** Real width of rock */
    private final int width;

    /**
     * Creates a rock based on the parameter
     *
     * @param i
     */
    private Rock(final int i) {
      switch (i % 5) {
        case 0:
          this.rock[0] = "####".toCharArray();
          this.rock[1] = "....".toCharArray();
          this.rock[2] = "....".toCharArray();
          this.rock[3] = "....".toCharArray();
          this.width = 4;
          break;
        case 1:
          this.rock[0] = ".#..".toCharArray();
          this.rock[1] = "###.".toCharArray();
          this.rock[2] = ".#..".toCharArray();
          this.rock[3] = "....".toCharArray();
          this.width = 3;
          break;
        case 2:
          this.rock[0] = "###.".toCharArray();
          this.rock[1] = "..#.".toCharArray();
          this.rock[2] = "..#.".toCharArray();
          this.rock[3] = "....".toCharArray();
          this.width = 3;
          break;
        case 3:
          this.rock[0] = "#...".toCharArray();
          this.rock[1] = "#...".toCharArray();
          this.rock[2] = "#...".toCharArray();
          this.rock[3] = "#...".toCharArray();
          this.width = 1;
          break;
        case 4:
          this.rock[0] = "##..".toCharArray();
          this.rock[1] = "##..".toCharArray();
          this.rock[2] = "....".toCharArray();
          this.rock[3] = "....".toCharArray();
          this.width = 2;
          break;
        default:
          throw new RuntimeException("Invalid modulus.");
      }
    }

    /**
     * Whether rock collides at the moment with the chamber wall or any other rock
     *
     * @return
     */
    private boolean collides() {
      if (this.x < 0 || this.x + this.width > CHAMBER_WIDTH || this.y < removed) {
        // collides with chamber wall
        return true;
      }
      for (int rockY = 0; rockY < 4; rockY++) {
        final long realY = this.y + rockY;
        if (realY >= getOccupiedHeight()) {
          // rock currently is higher than any other rock
          return false;
        }
        for (int rockX = 0; rockX < 4; rockX++) {
          if (this.rock[rockY][rockX] == '.') {
            continue;
          }
          if (getOccupations(realY).contains(this.x + rockX)) {
            // rock collides with another (settled) rock
            return true;
          }
        }
      }
      return false;
    }

    /**
     * Settles rock at current coordiante
     */
    private void settle() {
      for (int rockY = 0; rockY < 4; rockY++) {
        for (int rockX = 0; rockX < 4; rockX++) {
          if (this.rock[rockY][rockX] == '#') {
            // for each rock point, we write the occupation list
            final long realY = this.y + rockY;
            final int realX = rockX + this.x;
            if (realY >= getOccupiedHeight()) {
              addOccLine();
            }

            getOccupations(realY).add(realX);
            if (realY > highestAtX[realX]) {
              highestAtX[realX] = realY;
            }
          }
        }
      }
      clearLines();
    }

    /**
     * Simulates the moving ov a rock. It falls until can and then it settles.
     *
     * @param time Time when rock appears
     * @return Time after the rock is settled
     */
    private long simulate(long time) {
      // default coordinates
      this.x = 2;
      this.y = getOccupiedHeight() + 3;

      boolean isMoving = true;
      while (isMoving) {
        // apply jet, if possible
        final char jet = getJet(time);
        if (jet == '<') {
          this.x--;
          if (this.collides()) {
            // cannot apply jet
            this.x++;
          }

        } else {
          this.x++;
          if (this.collides()) {
            // cannot apply jet
            this.x--;
          }
        }

        // move down, if possible
        this.y--;
        time++;
        if (this.collides()) {
          // could not move down, should settle here
          this.y++;
          isMoving = false;
          this.settle();
        }
      }

      return time;
    }

  }

  /**
   * Class to represent the status of the chamber at a given state
   */
  private static class Status {

    /** Occupation list */
    private final List<Set<Integer>> occupation;

    /** Form id that was lastly droppped */
    private final int lastForm;

    /** Number of removed items from current occupation list */
    private final long removed;

    /** After which step this status arised */
    private final long afterStep;

    /**
     * Creates status instance from parameters
     *
     * @param occupation
     * @param lastForm
     * @param removed
     * @param afterStep
     */
    private Status(final List<Set<Integer>> occupation, final int lastForm,
        final long removed, final long afterStep) {
      this.occupation = occupation;
      this.lastForm = lastForm;
      this.removed = removed;
      this.afterStep = afterStep;
    }
  }

  public static void main(final String[] args) {
    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in17.txt"), StandardCharsets.UTF_8)) {
      jets = buffR.readLine().toCharArray();
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    final long p1 = simulateThrowingOf(2022);
    System.out.println("Solution for Part 1: " + p1);

    final long p2 = simulateThrowingOf(1_000_000_000_000L);
    System.out.println("Solution for Part 2: " + p2);

  }

  /**
   * Adds new line to the end of occupation list
   */
  private static void addOccLine() {
    occupation.add(new HashSet<>());
  }

  /**
   * Clears all unnecessary lines from occupation list (lines that cannot be
   * reached). Increases removed variable with the number of removed lines so the
   * overall lines can be tracked
   */
  private static void clearLines() {

    final long clearFrom = Arrays.asList(highestAtX).stream().reduce(highestAtX[0],
        (a, b) -> a < b ? a : b);
    final long clearLines = clearFrom - removed - 1;
    if (clearLines > 0) {
      removed += clearLines;
      LongStream.range(0, clearLines).forEach(l -> occupation.remove(0));
    }
  }

  /**
   * Gets current jet value for given time
   *
   * @param time
   * @return
   */
  private static char getJet(final long time) {
    return jets[(int) (time % jets.length)];
  }

  /**
   * Gets occupation for given row
   *
   * @param y
   * @return
   */
  private static Set<Integer> getOccupations(final long y) {
    return occupation.get((int) (y - removed));
  }

  /**
   * Gets overall number of occupied rows
   *
   * @return
   */
  private static long getOccupiedHeight() {
    return removed + occupation.size();
  }

  /**
   * Simulates the falling of a single rock
   *
   * @param simulationNumber
   * @param time
   * @return
   */
  private static long simulate(final int simulationNumber, final long time) {
    return new Rock(simulationNumber).simulate(time);
  }

  /**
   * Simulates the throwing of number of rocks in parameter
   *
   * @param number
   * @return
   */
  private static long simulateThrowingOf(final long number) {
    long time = 0;

    long foundSameSecond = -1;
    long foundSameFirst = -1;
    long heightInc = 0;

    for (long i = 0; i < number; i++) {

      // simulate next rock
      time = simulate((int) (i % 5), time);

      // let's save current status to the previousOccupations list
      previousOccupations.put(time,
          new Status(List.copyOf(occupation), (int) (i % 5), removed, i));

      // try to find the same status, where the time equals to currentTime - k *
      // jets.length. We know that if two of these statuses are the same, they will
      // repeat again and again (as same jet + same rock form will repeat again and
      // again with the same height increase)
      for (long timeToCheck = time - jets.length; timeToCheck >= 0; timeToCheck -=
          jets.length) {
        if (previousOccupations.containsKey(timeToCheck)) {
          // found repeating one

          final Status st = previousOccupations.get(timeToCheck);
          if (st.lastForm == (int) (i % 5) && st.occupation.equals(occupation)) {
            foundSameFirst = st.afterStep;

            // height increase between same statuses
            heightInc = removed - st.removed;

            // finish for loop
            break;
          }

        }
      }
      if (foundSameFirst != -1) {
        foundSameSecond = i;
        break;
      }

    }

    // if couldn't find repeating ones, then we simulated all. just return the
    // height.
    if (foundSameSecond == -1) {
      return getOccupiedHeight();
    }


    // increase numbers until the last repeating moment
    final long stepInc = foundSameSecond - foundSameFirst;
    final long timesToDo = (number - foundSameSecond) / stepInc;
    removed += timesToDo * heightInc;

    // simulate the rest of the steps
    for (long i = foundSameSecond + timesToDo * stepInc + 1; i < number; i++) {
      time = simulate((int) (i % 5), time);
    }

    return getOccupiedHeight();
  }

}
