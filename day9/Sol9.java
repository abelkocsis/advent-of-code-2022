import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Sol9 {

  /** Our map size, must be sufficiently enough to simulate all steps */
  private static final int SIZE = 1000;

  /** Length of the ropes */
  private static final int ROPES_LEN = 10;

  /** Whether second rope visited the position */
  private static final List<List<Boolean>> SECOND_ROPE_VISITED = new ArrayList<>();

  /** Whether last rope visited the position */
  private static final List<List<Boolean>> LAST_ROPE_VISITED = new ArrayList<>();

  /** List of ropes. Position 0: Head, Position ROPES_LEN - 1: tail rope */
  private static final List<Coord> ROPES = new ArrayList<>(ROPES_LEN);

  /** Coordinate of a rope */
  private static class Coord {

    /** x coordinate */
    protected transient int x = SIZE / 2;

    /** y coordinate */
    protected transient int y = SIZE / 2;

    /**
     * Gets necessary number of moves for R1 to follow R2 towards a particular
     * direction. In a given direction, never moves more than 1 position. Parameters
     * either correspond to x or y values.
     *
     * @param a x or y coordinate of rope R1
     * @param b x or y coordinate of rope R2
     * @return Number of moves towards x or y direction
     */
    protected static int getMove(final int a, final int b) {
      if (a > b) {
        return 1;
      }
      if (b > a) {
        return -1;
      }
      return 0;
    }

    /**
     * Changes rope's coordiantes to follow head parameter in accordance with the
     * task description
     *
     * @param ropeToFollow Rope to follow
     */
    protected void follow(final Coord ropeToFollow) {
      this.x += getMove(ropeToFollow.x, this.x);
      this.y += getMove(ropeToFollow.y, this.y);
    }

    /**
     * Gets whether rope touches parameter rope
     *
     * @param b
     * @return
     */
    protected boolean isTouching(final Coord b) {
      return Math.abs(b.x - this.x) < 2 && Math.abs(b.y - this.y) < 2;
    }

    /**
     * Steps rope based on input character;
     *
     * @param angle
     */
    protected void oneStep(final char angle) {
      switch (angle) {
        case 'R':
          this.x++;
          break;
        case 'L':
          this.x--;
          break;
        case 'U':
          this.y++;
          break;
        case 'D':
          this.y--;
          break;
        default:
          throw new RuntimeException("Invalid direction");
      }
    }

    /**
     * Sets element (x,y) to True in the parameter list
     *
     * @param list
     */
    protected void updateVisitedList(final List<List<Boolean>> list) {
      list.get(this.x).set(this.y, true);
    }

  }

  public static void main(final String[] args) {

    // initialise lists
    IntStream.range(0, SIZE).forEach(i -> SECOND_ROPE_VISITED
        .add(new ArrayList<>(Collections.nCopies(SIZE, false))));

    IntStream.range(0, SIZE).forEach(i -> LAST_ROPE_VISITED
        .add(new ArrayList<>(Collections.nCopies(SIZE, false))));

    IntStream.range(0, ROPES_LEN).forEach(i -> ROPES.add(new Coord()));

    String line;
    String[] splittedLine;

    // mark start position as visited
    ROPES.get(0).updateVisitedList(SECOND_ROPE_VISITED);
    ROPES.get(0).updateVisitedList(LAST_ROPE_VISITED);

    // read inputs
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in9.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        splittedLine = line.split(" ");
        final char direction = splittedLine[0].charAt(0);

        IntStream.range(0, Integer.parseInt(splittedLine[1]))
            // repeat step as specified in the input
            .forEach(i -> {
              // step HEAD rope
              ROPES.get(0).oneStep(direction);

              // for all other ropes
              for (int ropeIndex = 1; ropeIndex < ROPES_LEN; ropeIndex++) {
                if (ROPES.get(ropeIndex).isTouching(ROPES.get(ropeIndex - 1))) {
                  // if rope touches previous rope, no need to step it
                  // in addition, then no need to move the following ropes either
                  break;
                } else {
                  // otherwise rope should follow the previous rope
                  ROPES.get(ropeIndex).follow(ROPES.get(ropeIndex - 1));
                }
              }

              // update relevant lists
              ROPES.get(1).updateVisitedList(SECOND_ROPE_VISITED);
              ROPES.get(ROPES_LEN - 1).updateVisitedList(LAST_ROPE_VISITED);
            });
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    final long visitedBySecond = SECOND_ROPE_VISITED.stream()
        .map(row -> row.stream().filter(element -> element).count())
        .reduce(0L, (a, b) -> a + b);
    System.out.println("Solution for Part 1: " + visitedBySecond);

    final long visitedByTail = LAST_ROPE_VISITED.stream()
        .map(row -> row.stream().filter(element -> element).count())
        .reduce(0L, (a, b) -> a + b);
    System.out.println("Solution for Part 2: " + visitedByTail);
  }

}
