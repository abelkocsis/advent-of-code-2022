import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sol12 {

  static final int HEIGHT = 41;
  static final int WIDTH = 161;
  static char[][] map = new char[HEIGHT][WIDTH];
  static int[][] distance = new int[HEIGHT][WIDTH];
  static int minADistance = 0;


  public static void main(final String[] str) {


    String line;
    String[] strippedLine;
    int startI = -1;
    int startJ = -1;
    int endI = -1;
    int endJ = -1;


    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in12.txt"), StandardCharsets.UTF_8)) {
      int i = 0;
      while ((line = buffR.readLine()) != null) {
        strippedLine = line.split("");
        for (int j = 0; j < WIDTH; j++) {
          map[i][j] = strippedLine[j].charAt(0);
          distance[i][j] = Integer.MAX_VALUE;
          if (map[i][j] == 'S') {
            startI = i;
            startJ = j;
          } else if (map[i][j] == 'E') {
            endI = i;
            endJ = j;
          }
        }

        i++;
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    takeStep(startI, startJ, 0);
    System.out.println("Solution for Part 1: " + distance[endI][endJ]);

    for (int i = 0; i < HEIGHT; i++) {
      for (int j = 0; j < WIDTH; j++) {
        distance[i][j] = Integer.MAX_VALUE;
      }
    }

    minADistance = Integer.MAX_VALUE;
    takeStepBack(endI, endJ, 0);
    System.out.println("Solution for Part 2: " + minADistance);

  }

  static int getHeight(final char c) {
    if (c == 'E') {
      return 'z';
    }
    if (c == 'S') {
      return 'a';
    }
    return c;
  }

  static void takeStep(final int i, final int j, final int dist) {
    distance[i][j] = dist;
    final int currentHeight = getHeight(map[i][j]);
    final int nextDistance = dist + 1;

    if (i + 1 < HEIGHT && currentHeight + 1 >= getHeight(map[i + 1][j])
        && nextDistance < distance[i + 1][j]) {
      takeStep(i + 1, j, nextDistance);
    }
    if (i - 1 >= 0 && currentHeight + 1 >= getHeight(map[i - 1][j])
        && nextDistance < distance[i - 1][j]) {
      takeStep(i - 1, j, nextDistance);
    }
    if (j + 1 < WIDTH && currentHeight + 1 >= getHeight(map[i][j + 1])
        && nextDistance < distance[i][j + 1]) {
      takeStep(i, j + 1, nextDistance);
    }
    if (j - 1 >= 0 && currentHeight + 1 >= getHeight(map[i][j - 1])
        && nextDistance < distance[i][j - 1]) {
      takeStep(i, j - 1, nextDistance);
    }
  }

  static void takeStepBack(final int i, final int j, final int dist) {

    final int currentHeight = getHeight(map[i][j]);
    final int nextDistance = dist + 1;
    distance[i][j] = dist;
    if (currentHeight == 'a' && dist < minADistance) {

      minADistance = dist;
    }

    if (i + 1 < HEIGHT && currentHeight - 1 <= getHeight(map[i + 1][j])
        && nextDistance < distance[i + 1][j]) {
      takeStepBack(i + 1, j, nextDistance);
    }
    if (i - 1 >= 0 && currentHeight - 1 <= getHeight(map[i - 1][j])
        && nextDistance < distance[i - 1][j]) {
      takeStepBack(i - 1, j, nextDistance);
    }
    if (j + 1 < WIDTH && currentHeight - 1 <= getHeight(map[i][j + 1])
        && nextDistance < distance[i][j + 1]) {
      takeStepBack(i, j + 1, nextDistance);
    }
    if (j - 1 >= 0 && currentHeight - 1 <= getHeight(map[i][j - 1])
        && nextDistance < distance[i][j - 1]) {
      takeStepBack(i, j - 1, nextDistance);
    }
  }

}
