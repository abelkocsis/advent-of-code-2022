import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Day23 {

  /** List of elves */
  private static final List<Elf> ELVES = new ArrayList<>();

  /** Maximum height of the map */
  private static final int HEIGHT = 1000;

  /** Maximum width of the map */
  private static final int WIDTH = 1000;

  /** Map of elves */
  private static char currentMap[][] = new char[HEIGHT][WIDTH];

  /** List of proposed coordinates together with the elf who proposed it */
  private static Map<Coords, Elf> proposeds = new HashMap<>();

  /** Functions to check which way to move */
  private static List<Function<Coords, Coords>> movementChecks;

  /** number of elves moved in current round */
  private static int elfMoves = 0;

  static {

    // add checks to list
    movementChecks = new ArrayList<>();

    // north move
    movementChecks.add((current) -> {
      final boolean elfNorth = currentMap[current.y - 1][current.x] == '#';
      final boolean elfNorthEast = currentMap[current.y - 1][current.x + 1] == '#';
      final boolean elfNorthWest = currentMap[current.y - 1][current.x - 1] == '#';

      if (!elfNorth && !elfNorthEast && !elfNorthWest) {
        return new Coords(current.x, current.y - 1);
      }
      return null;
    });

    // south move
    movementChecks.add((current) -> {
      final boolean elfSouth = currentMap[current.y + 1][current.x] == '#';
      final boolean elfSouthEast = currentMap[current.y + 1][current.x + 1] == '#';
      final boolean elfSouthWest = currentMap[current.y + 1][current.x - 1] == '#';

      if (!elfSouth && !elfSouthEast && !elfSouthWest) {
        return new Coords(current.x, current.y + 1);
      }
      return null;
    });

    // west move
    movementChecks.add((current) -> {
      final boolean elfNorthWest = currentMap[current.y - 1][current.x - 1] == '#';
      final boolean elfSouthWest = currentMap[current.y + 1][current.x - 1] == '#';
      final boolean elfWest = currentMap[current.y][current.x - 1] == '#';

      if (!elfWest && !elfNorthWest && !elfSouthWest) {
        return new Coords(current.x - 1, current.y);
      }
      return null;
    });

    // east move
    movementChecks.add((current) -> {
      final boolean elfNorthEast = currentMap[current.y - 1][current.x + 1] == '#';
      final boolean elfSouthEast = currentMap[current.y + 1][current.x + 1] == '#';
      final boolean elfEast = currentMap[current.y][current.x + 1] == '#';
      if (!elfEast && !elfNorthEast && !elfSouthEast) {
        return new Coords(current.x + 1, current.y);
      }
      return null;
    });
  }

  /**
   * Coordinate
   */
  private static class Coords {

    /** X coordiante */
    private transient final int x;

    /** Y coordinate */
    private transient final int y;

    /* default */ Coords(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(final Object o) {
      if (o == null) {
        return false;
      }
      if (o == this) {
        return true;
      }
      if (!(o instanceof Coords)) {
        return false;
      }
      final Coords coordsObj = (Coords) o;
      return this.x == coordsObj.x && this.y == coordsObj.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
      return "(" + this.x + "," + this.y + ")";
    }
  }

  /**
   * Elf
   */
  private static class Elf {

    /** Current coordinate */
    private transient Coords current;

    /** Proposed coordinate. Could be null if cannot propose a coordinate */
    private transient Coords proposed;

    /**
     * Creates an elf for parameter
     *
     * @param x
     * @param y
     */
    /* default */ Elf(final int x, final int y) {
      this.current = new Coords(x, y);
    }

    /**
     * Creates a proposed coordinate
     */
    /* default */void propose() {

      // check if empty around
      boolean emptyAround = true;
      for (int yv = -1; yv < 2 && emptyAround; yv++) {
        for (int xv = -1; xv < 2 && emptyAround; xv++) {
          if (yv == 0 && xv == 0) {
            continue;
          }

          if (currentMap[this.current.y + yv][this.current.x + xv] == '#') {
            emptyAround = false;
          }
        }
      }

      if (emptyAround) {
        // return if empty
        this.proposed = null;
        return;
      }

      elfMoves++;

      // get new coordinate
      this.proposed =
          movementChecks.stream().filter(func -> func.apply(this.current) != null)
              .findFirst().map(func -> func.apply(this.current)).orElse(null);

      // check if already proposed
      if (proposeds.containsKey(this.proposed)) {
        // if proposed, revert other elf as well
        proposeds.get(this.proposed).proposed = null;
        this.proposed = null;
      } else {
        // if not yet proposed, pu it to list
        proposeds.put(this.proposed, this);
      }
    }

    /**
     * Steps to the proposed coordinate
     */
    /* default */void step() {
      if (this.proposed != null) {
        currentMap[this.current.y][this.current.x] = '.';
        this.current = this.proposed;
        currentMap[this.current.y][this.current.x] = '#';
      }
    }
  }

  public static void main(final String[] args) throws IOException {
    String line;
    String[] splittedLine;

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in23.txt"), StandardCharsets.UTF_8)) {

      // read map
      int y = HEIGHT / 2;
      while ((line = buffR.readLine()) != null) {
        splittedLine = line.split("");
        int x = WIDTH / 2;
        for (final String chr : splittedLine) {
          currentMap[y][x] = chr.charAt(0);
          if (chr.charAt(0) == '#') {
            final Elf elf = new Elf(x, y);
            ELVES.add(elf);
          }
          x++;
        }
        y++;
      }

      // part 1
      for (int i = 0; i < 10; i++) {
        simulateRound();
      }

      // get minimum and maximum coordinates
      final int minX = ELVES.stream().mapToInt(elf -> elf.current.x).min()
          .orElse(Integer.MAX_VALUE);
      final int maxX = ELVES.stream().mapToInt(elf -> elf.current.x).max()
          .orElse(Integer.MIN_VALUE);
      final int minY = ELVES.stream().mapToInt(elf -> elf.current.y).min()
          .orElse(Integer.MAX_VALUE);
      final int maxY = ELVES.stream().mapToInt(elf -> elf.current.y).max()
          .orElse(Integer.MIN_VALUE);

      int part1Sol = (maxX - minX + 1) * (maxY - minY + 1) - ELVES.size();
      System.out.println("Solution for Part 1: " + part1Sol);

      // part 2
      int i = 11;
      while (simulateRound()) {
        i++;
      }

      System.out.println("Solution for Part 2: " + i);
    }
  }

  /**
   * Simulates a round
   *
   * @return Whether at least one elf moved
   */
  /* default */static boolean simulateRound() {
    elfMoves = 0;
    ELVES.stream().forEach(elf -> elf.propose());
    ELVES.stream().forEach(elf -> elf.step());
    proposeds.clear();
    movementChecks.add(movementChecks.remove(0));
    return elfMoves != 0;
  }

}
