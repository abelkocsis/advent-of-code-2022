import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sol12 {

  /** Height of map */
  private static final int HEIGHT = 41;

  /** Width of map */
  private static final int WIDTH = 161;

  /** Map */
  private static char[][] map = new char[HEIGHT][WIDTH];

  /** Distance from start position */
  private static int[][] distance = new int[HEIGHT][WIDTH];

  /** Minimum distance from start */
  private static int minADistance = 0;

  public static void main(final String[] str) {
    String line;
    String[] strippedLine;
    int startX = -1; // x coordinate of start point
    int startY = -1; // y coordinate of start point
    int endX = -1; // x coordinate of end point
    int endY = -1; // y coordinate of end point

    // read map, find start and end points
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in12.txt"), StandardCharsets.UTF_8)) {
      int y = 0;
      while ((line = buffR.readLine()) != null) {
        strippedLine = line.split("");
        for (int x = 0; x < WIDTH; x++) {
          map[y][x] = strippedLine[x].charAt(0);
          distance[y][x] = Integer.MAX_VALUE;
          if (map[y][x] == 'S') {
            startY = y;
            startX = x;
          } else if (map[y][x] == 'E') {
            endY = y;
            endX = x;
          }
        }

        y++;
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // calculate distance from start point
    calculateDistances(startY, startX, 0);
    final int minDistanceToGoal = distance[endY][endX];
    System.out.println("Solution for Part 1: " + minDistanceToGoal);

    // reset distance array
    for (int i = 0; i < HEIGHT; i++) {
      for (int j = 0; j < WIDTH; j++) {
        distance[i][j] = Integer.MAX_VALUE;
      }
    }

    // calculate part 2
    minADistance = Integer.MAX_VALUE; // minimum value while a height 'A' could be
                                      // found from end point
    calculatePart2(endY, endX, 0);
    System.out.println("Solution for Part 2: " + minADistance);

  }

  /**
   * Recursively calculates reachable distances from start point
   *
   * @param y Current coordinate y value
   * @param x Current coordinate x value
   * @param dist Current distance from start point
   */
  private static void calculateDistances(final int y, final int x, final int dist) {

    // assign current distance to current point
    distance[y][x] = dist;

    // get current height
    final int currentHeight = getHeight(map[y][x]);
    final int nextDistance = dist + 1;

    // for each direction, if next height would be at most one step higher, and the
    // best distance is smaller than the next distance would be, take a recursive
    // step

    if (y + 1 < HEIGHT && currentHeight + 1 >= getHeight(map[y + 1][x])
        && nextDistance < distance[y + 1][x]) {
      calculateDistances(y + 1, x, nextDistance);
    }
    if (y - 1 >= 0 && currentHeight + 1 >= getHeight(map[y - 1][x])
        && nextDistance < distance[y - 1][x]) {
      calculateDistances(y - 1, x, nextDistance);
    }
    if (x + 1 < WIDTH && currentHeight + 1 >= getHeight(map[y][x + 1])
        && nextDistance < distance[y][x + 1]) {
      calculateDistances(y, x + 1, nextDistance);
    }
    if (x - 1 >= 0 && currentHeight + 1 >= getHeight(map[y][x - 1])
        && nextDistance < distance[y][x - 1]) {
      calculateDistances(y, x - 1, nextDistance);
    }
  }

  /**
   * Calculate parts 2 by finding possible starting points, and saving these starting
   * point distances to static variable minADistance
   *
   * @param y
   * @param x
   * @param dist
   */
  private static void calculatePart2(final int y, final int x, final int dist) {

    final int currentHeight = getHeight(map[y][x]);
    final int nextDistance = dist + 1;
    distance[y][x] = dist;
    if (currentHeight == 'a' && dist < minADistance) {
      // if 'a' height is found, end of recursion
      minADistance = dist;
    }

    // otherwise, try all directions recursively, if step is allowed

    if (y + 1 < HEIGHT && currentHeight - 1 <= getHeight(map[y + 1][x])
        && nextDistance < distance[y + 1][x]) {
      calculatePart2(y + 1, x, nextDistance);
    }
    if (y - 1 >= 0 && currentHeight - 1 <= getHeight(map[y - 1][x])
        && nextDistance < distance[y - 1][x]) {
      calculatePart2(y - 1, x, nextDistance);
    }
    if (x + 1 < WIDTH && currentHeight - 1 <= getHeight(map[y][x + 1])
        && nextDistance < distance[y][x + 1]) {
      calculatePart2(y, x + 1, nextDistance);
    }
    if (x - 1 >= 0 && currentHeight - 1 <= getHeight(map[y][x - 1])
        && nextDistance < distance[y][x - 1]) {
      calculatePart2(y, x - 1, nextDistance);
    }
  }

  /**
   * Get the height for a character
   *
   * @param c
   * @return
   */
  private static int getHeight(final char c) {
    if (c == 'E') {
      return 'z';
    }
    if (c == 'S') {
      return 'a';
    }
    return c;
  }

}
