import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sol18 {

  /** List of lava cubes */
  private static List<Cube> lavaCubes = new ArrayList<>();

  /** Overall width */
  private static final int WIDTH = 22;

  /** Overall height */
  private static final int HEIGHT = 25;

  /** Overall depth */
  private static final int DEPTH = 25;

  /** Whether coordinate is water after filling it in from outside */
  private static boolean[][][] isWater = new boolean[WIDTH][HEIGHT][DEPTH];

  /**
   * Representation of a 1x1x1 cube
   */
  private static class Cube {

    /** X coordinate */
    private final int x;

    /** Y coordinate */
    private final int y;

    /** Z coordinate */
    private final int z;

    /** Non lava adjacents cubes */
    private List<Cube> nonLavaAdjacents;

    /**
     * Creates cube from coordinates
     *
     * @param x
     * @param y
     * @param z
     */
    private Cube(final int x, final int y, final int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    /**
     * Parses cube from line input
     *
     * @param line
     */
    private Cube(final String line) {
      final String[] splittedLine = line.split(",");
      this.x = Integer.parseInt(splittedLine[0]);
      this.y = Integer.parseInt(splittedLine[1]);
      this.z = Integer.parseInt(splittedLine[2]);
    }

    @Override
    public boolean equals(final Object b) {
      if ((b == null) || !(b instanceof Cube)) {
        return false;
      }
      final Cube triple = (Cube) b;

      return this.x == triple.x && this.y == triple.y && this.z == triple.z;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.x, this.y, this.z);
    }

    @Override
    public String toString() {
      return "(" + this.x + "," + this.y + "," + this.z + ")";
    }

    /**
     * Adds adjacent cube to nonLavaAdjacents list if adjacent is not a lava cube
     *
     * @param adjacent
     */
    private void addIfNotLava(final Cube adjacent) {
      if (!lavaCubes.contains(adjacent)) {
        this.nonLavaAdjacents.add(adjacent);
      }
    }

    /**
     * Calculates adjacent cubes, tries to add them to nonLavaAdjacents list
     */
    private void calcAdjacents() {
      this.nonLavaAdjacents = new ArrayList<>();
      for (int vx = -1; vx < 2; vx += 2) {
        final Cube tr = new Cube(this.x + vx, this.y, this.z);
        this.addIfNotLava(tr);
      }
      for (int vy = -1; vy < 2; vy += 2) {
        final Cube tr = new Cube(this.x, this.y + vy, this.z);
        this.addIfNotLava(tr);
      }
      for (int vz = -1; vz < 2; vz += 2) {
        final Cube tr = new Cube(this.x, this.y, this.z + vz);
        this.addIfNotLava(tr);
      }
    }

    /**
     * Whether current coordinate is on a map
     *
     * @return
     */
    private boolean isOutOfMap() {
      return this.x < 0 || this.y < 0 || this.z < 0 || this.x >= WIDTH
          || this.y >= HEIGHT || this.z >= DEPTH;
    }
  }

  public static void main(final String[] args) {

    // read input
    String line;
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in18.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        lavaCubes.add(new Cube(line));
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // for each cube, calculate adjacents
    lavaCubes.forEach(cube -> cube.calcAdjacents());

    // counts free surfaces for part 1
    System.out.println("Solution for Part 1: " + countAllFreeSurfaces());

    // fills map with water from outside
    fillWithWater(0, 0, 0);

    // counts surfaces adjacent with water
    System.out.println("Solution for Part 2: " + countAllExternalSurfaces());


  }

  /**
   * Counts all external surfaces (next to water)
   *
   * @return
   */
  private static int countAllExternalSurfaces() {
    return
    // each lava cube
    lavaCubes.stream()
        // get number of water adjacents
        .mapToInt(lavaCube -> (int)
        // each non-lava adjacents
        lavaCube.nonLavaAdjacents.stream()
            // filter for ones which is water (or out of map)
            .filter(adj -> adj.isOutOfMap() || isWater[adj.x][adj.y][adj.z])
            // count them
            .count())
        // summarise them
        .sum();
  }

  /**
   * Counts all free surfaces
   *
   * @return
   */
  private static int countAllFreeSurfaces() {
    return
    // for each lava cube
    lavaCubes.stream()
        // get non-lava adjacents
        .mapToInt(lavaCube -> lavaCube.nonLavaAdjacents.size())
        // summarise them
        .sum();
  }

  /**
   * Fills map with water. Recursively writes water to all directions which aren't
   * occupied by little lava cubes.
   *
   * @param x
   * @param y
   * @param z
   */
  private static void fillWithWater(final int x, final int y, final int z) {
    if (x < 0 || y < 0 || z < 0 || x >= WIDTH || y >= HEIGHT || z >= DEPTH) {
      // out of map, nothing to do
      return;
    }
    if (isWater[x][y][z] || lavaCubes.stream()
        .anyMatch(cube -> cube.x == x && cube.y == y && cube.z == z)) {
      // if already water or position of lava cube, nothing to do
      return;
    }

    // write water
    isWater[x][y][z] = true;

    // call fillWithWater for all adjacents (even diagonally)

    for (int vx = -1; vx < 2; vx += 2) {
      fillWithWater(x + vx, y, z);
    }
    for (int vy = -1; vy < 2; vy += 2) {
      fillWithWater(x, y + vy, z);
    }
    for (int vz = -1; vz < 2; vz += 2) {
      fillWithWater(x, y, z + vz);
    }
  }

}
