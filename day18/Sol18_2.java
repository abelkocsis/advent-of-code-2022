import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sol18_2 {
  static List<Triple> lavaCubes = new ArrayList<>();
  static List<Triple> waterCubes = new ArrayList<>();

  static int width = 22;
  static int height = 25;
  static int depth = 25;

  static boolean[][][] isWater = new boolean[width][height][depth];

  static class Triple {
    int x;
    int y;
    int z;

    List<Triple> nonLavaAdjacents;

    Triple(final int x, final int y, final int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    Triple(final String line) {
      final String[] splittedLine = line.split(",");
      this.x = Integer.parseInt(splittedLine[0]);
      this.y = Integer.parseInt(splittedLine[1]);
      this.z = Integer.parseInt(splittedLine[2]);
    }

    @Override
    public boolean equals(final Object b) {

      if ((b == null) || !(b instanceof Triple)) {
        return false;
      }
      final Triple triple = (Triple) b;

      return this.x == triple.x && this.y == triple.y && this.z == triple.z;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.x, this.y, this.z);
    }

    public boolean isOutOfMap() {
      return this.x < 0 || this.y < 0 || this.z < 0 || this.x >= width
          || this.y >= height || this.z >= depth;
    }

    @Override
    public String toString() {
      return "(" + this.x + "," + this.y + "," + this.z + ")";
    }

    void addIfNotLava(final Triple tr) {
      if (!lavaCubes.contains(tr)) {
        this.nonLavaAdjacents.add(tr);
      }
    }

    void calcAdjacents() {
      this.nonLavaAdjacents = new ArrayList<>();
      for (int vx = -1; vx < 2; vx += 2) {
        final Triple tr = new Triple(this.x + vx, this.y, this.z);
        this.addIfNotLava(tr);
      }
      for (int vy = -1; vy < 2; vy += 2) {
        final Triple tr = new Triple(this.x, this.y + vy, this.z);
        this.addIfNotLava(tr);
      }
      for (int vz = -1; vz < 2; vz += 2) {
        final Triple tr = new Triple(this.x, this.y, this.z + vz);
        this.addIfNotLava(tr);
      }
    }
  }

  public static void main(final String[] args) {
    String line;
    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in18.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        lavaCubes.add(new Triple(line));
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    lavaCubes.forEach(cube -> cube.calcAdjacents());
    System.out.println(countAllFreeSurfaces());

    fillWithWater(0, 0, 0);

    System.out.println(countAllExternalSurfaces());


  }

  static int countAllExternalSurfaces() {
    return lavaCubes.stream()
        .mapToInt(lavaCube -> (int) lavaCube.nonLavaAdjacents.stream()
            .filter(adj -> adj.isOutOfMap() || isWater[adj.x][adj.y][adj.z]).count())
        .sum();
  }

  static int countAllFreeSurfaces() {
    return lavaCubes.stream().mapToInt(lavaCube -> lavaCube.nonLavaAdjacents.size())
        .sum();
  }

  static void fillWithWater(final int x, final int y, final int z) {
    if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth) {
      return;
    }
    if (isWater[x][y][z] || lavaCubes.stream()
        .anyMatch(cube -> cube.x == x && cube.y == y && cube.z == z)) {
      return;
    }
    isWater[x][y][z] = true;
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
