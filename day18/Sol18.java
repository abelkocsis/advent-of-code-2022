import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sol18 {
  static int width = 22;
  static int height = 25;
  static int depth = 25;


  static boolean[][][] map = new boolean[width][height][depth];
  static boolean[][][] mapWater = new boolean[width][height][depth];
  static int[][][] freeSurfaces = new int[width][height][depth];

  public static void main(final String[] args) {
    // init
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        for (int z = 0; z < depth; z++) {
          map[x][y][z] = false;
          mapWater[x][y][z] = false;
        }
      }
    }


    String line;
    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in18.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        final String[] splittedLine = line.split(",");
        final int x = Integer.parseInt(splittedLine[0]);
        final int y = Integer.parseInt(splittedLine[1]);
        final int z = Integer.parseInt(splittedLine[2]);
        System.out.println(x + "," + y + "," + z);
        map[x][y][z] = true;
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    System.out.println(countAllFreeSurfaces());

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        for (int z = 0; z < depth; z++) {
          if (!map[x][y][z]) {
            expandLava(x, y, z);
          }
        }
      }
    }

    fillWithWater(0, 0, 0);

    System.out.println(countAllSurfacesWithWater());

  }

  static int countAllFreeSurfaces() {
    int counter = 0;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        for (int z = 0; z < depth; z++) {
          if (map[x][y][z]) {
            counter += countFreeSurfaces(x, y, z);
          }
        }
      }
    }
    return counter;
  }

  static int countAllSurfacesWithWater() {
    int counter = 0;
    int lava = 0;
    int water = 0;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        for (int z = 0; z < depth; z++) {
          if (map[x][y][z]) {
            lava++;
            counter += countSurfacesWithWater(x, y, z);
          }
          if (mapWater[x][y][z]) {
            water++;
          }
        }
      }
    }
    System.out.println("Lava: " + lava);
    System.out.println("water: " + water);
    return counter;
  }

  static int countFreeSurfaces(final int x, final int y, final int z) {
    int counter = 0;
    if (x - 1 < 0 || !map[x - 1][y][z]) {
      counter++;
    }
    if (x + 1 >= width || !map[x + 1][y][z]) {
      counter++;
    }
    if (y - 1 < 0 || !map[x][y - 1][z]) {
      counter++;
    }
    if (y + 1 >= height || !map[x][y + 1][z]) {
      counter++;
    }

    if (z - 1 < 0 || !map[x][y][z - 1]) {
      counter++;
    }
    if (z + 1 >= depth || !map[x][y][z + 1]) {
      counter++;
    }

    return counter;
  }

  static int countSurfacesWithWater(final int x, final int y, final int z) {
    int counter = 0;
    if (x - 1 < 0 || mapWater[x - 1][y][z]) {
      counter++;
    }
    if (x + 1 >= width || mapWater[x + 1][y][z]) {
      counter++;
    }
    if (y - 1 < 0 || mapWater[x][y - 1][z]) {
      counter++;
    }
    if (y + 1 >= height || mapWater[x][y + 1][z]) {
      counter++;
    }

    if (z - 1 < 0 || mapWater[x][y][z - 1]) {
      counter++;
    }
    if (z + 1 >= depth || mapWater[x][y][z + 1]) {
      counter++;
    }

    if (counter > 5) {
      System.out.println("(" + x + "," + y + "," + z + "):" + counter);
    }

    return counter;
  }

  static boolean expandLava(final int x, final int y, final int z) {
    if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth) {
      return false;
    }
    if (map[x][y][z]) {
      return true;
    }
    map[x][y][z] = true;
    // try further expanding
    if (expandLava(x + 1, y, z) && expandLava(x - 1, y, z) && expandLava(x, y + 1, z)
        && expandLava(x, y - 1, z) && expandLava(x, y, z + 1)
        && expandLava(x, y, z - 1)) {
      // System.out.println("Lava expanded: " + x + "," + y + ", " + z);

      return true;
    }
    map[x][y][z] = false;
    return false;
  }

  static void fillWithWater(final int x, final int y, final int z) {
    if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= depth) {
      return;
    }
    if (mapWater[x][y][z] || map[x][y][z]) {
      return;
    }
    mapWater[x][y][z] = true;
    fillWithWater(x + 1, y, z);
    fillWithWater(x - 1, y, z);
    fillWithWater(x, y + 1, z);
    fillWithWater(x, y - 1, z);
    fillWithWater(x, y, z + 1);
    fillWithWater(x, y, z - 1);
  }


}
