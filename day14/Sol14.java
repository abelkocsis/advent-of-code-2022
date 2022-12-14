import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Sol14 {

  static int WIDTH = 1000;
  static int HEIGTH = 1000;
  static char[][] map = new char[HEIGTH][WIDTH];

  static Integer[] lowestInCol = new Integer[WIDTH];

  static class Coord {
    int x;
    int y;

    Coord(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    Coord(final String cordS) {
      final String[] splittedS = cordS.split(",");
      this.x = Integer.parseInt(splittedS[0].trim());
      this.y = Integer.parseInt(splittedS[1].trim());
    }

    void drawStones(final Coord prev) {
      map[this.y][this.x] = '#';
      if (lowestInCol[this.x] < this.y) {
        lowestInCol[this.x] = this.y;
      }

      if (prev != null) {
        if (prev.x == this.x) {
          final int minY = Math.min(this.y, prev.y);
          final int maxY = Math.max(this.y, prev.y);
          for (int y = minY; y < maxY; y++) {
            map[y][this.x] = '#';
            if (lowestInCol[this.x] < y) {
              lowestInCol[this.x] = y;
            }
          }
        } else if (prev.y == this.y) {
          final int minX = Math.min(this.x, prev.x);
          final int maxX = Math.max(this.x, prev.x);
          for (int x = minX; x < maxX; x++) {
            map[this.y][x] = '#';
            if (lowestInCol[x] < this.y) {
              lowestInCol[x] = this.y;
            }
          }
        }
      }
    }
  }

  public static void main(final String[] args) {
    for (int i = 0; i < HEIGTH; i++) {
      for (int j = 0; j < WIDTH; j++) {
        map[i][j] = '.';
      }
    }
    for (int j = 0; j < WIDTH; j++) {
      lowestInCol[j] = -1;
    }

    String line;

    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in14.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        parseLine(line);
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    int rested = 0;
    while (dropSand(500, 0)) {
      rested++;
    }

    System.out.println(rested);



    for (int i = 0; i < HEIGTH; i++) {
      for (int j = 0; j < WIDTH; j++) {
        if (map[i][j] == 'o') {
          map[i][j] = '.';
        }
      }
    }

    final int maxHeight = Arrays.asList(lowestInCol).stream()
        .reduce((a, b) -> a > b ? a : b).orElse(0);

    final Coord cord = new Coord(0, maxHeight + 2);
    cord.drawStones(null);
    final Coord cord2 = new Coord(WIDTH - 1, maxHeight + 2);
    cord2.drawStones(cord);

    for (int j = 0; j < WIDTH; j++) {
      lowestInCol[j] = maxHeight + 2;
    }

    rested = 0;
    while (map[0][500] == '.' && dropSand(500, 0)) {
      rested++;
    }

    System.out.println(rested);

    for (int i = 0; i < 15; i++) {
      for (int j = 490; j < 510; j++) {
        System.out.print(map[i][j]);

      }
      System.out.println("");
    }

  }

  static boolean dropSand(final int x, final int y) {
    if (lowestInCol[x] < y) {
      return false;
    }
    if (map[y + 1][x] == '.') {
      return dropSand(x, y + 1);
    } else if (map[y + 1][x - 1] == '.') {
      return dropSand(x - 1, y + 1);
    } else if (map[y + 1][x + 1] == '.') {
      return dropSand(x + 1, y + 1);
    } else {
      map[y][x] = 'o';
      return true;
    }
  }

  static void parseLine(final String line) {
    final String[] splittedLine = line.split("->");
    Coord prevCord = null;
    Coord currCord = null;
    for (int i = 0; i < splittedLine.length; i++) {
      currCord = new Coord(splittedLine[i]);
      currCord.drawStones(prevCord);
      prevCord = currCord;
    }
  }

}
