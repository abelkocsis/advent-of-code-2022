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

  static int width = 122; // 8; // 122;
  static int height = 27; // 6; // 27;
  static char[][] map = new char[height][width];
  static List<Line> lines = new ArrayList<>();

  static List<Blizzard> blizzards = new ArrayList<>();

  static int maxTimeToSimulate = 600; // 12; // 600;

  static int minValue = Integer.MAX_VALUE;

  private static class Blizzard {
    int x;
    int y;
    int moveX;
    int moveY;

    Blizzard(final int x, final int y, final char chr) {
      this.x = x;
      this.y = y;
      switch (chr) {
        case '>':
          this.moveX = 1;
          this.moveY = 0;
          return;
        case '<':
          this.moveX = -1;
          this.moveY = 0;
          return;
        case '^':
          this.moveX = 0;
          this.moveY = -1;
          return;
        case 'v':
          this.moveX = 0;
          this.moveY = 1;
          return;
      }
      lines.get(this.y).occupiedAtTime.get(0).add(this.x);

    }

    @Override
    public String toString() {
      return "(" + this.x + "," + this.y + ")";
    }

    void step(final int time) {
      final int nextX = this.x + this.moveX;
      final int nextY = this.y + this.moveY;
      final boolean changeX = this.x != nextX;
      if (map[nextY][nextX] == '#') {
        if (changeX) {
          this.y = nextY;
          if (nextX > this.x) {
            this.x = 1;
          } else {
            this.x = width - 2;
          }
        } else {
          this.x = nextX;
          if (nextY > this.y) {
            this.y = 1;
          } else {
            this.y = height - 2;
          }
        }
      } else {
        this.x = nextX;
        this.y = nextY;
      }

      lines.get(this.y).occupiedAtTime.get(time).add(this.x);

    }
  }

  private static class Line {
    Map<Integer, Set<Integer>> occupiedAtTime = new HashMap<>();
    Map<Integer, Set<Integer>> hasBeenHereAtTime = new HashMap<>();

    Line() {
      for (int i = 0; i < maxTimeToSimulate + 1; i++) {
        this.occupiedAtTime.put(i, new HashSet<>());
        this.hasBeenHereAtTime.put(i, new HashSet<>());
      }
    }
  }

  public static void main(final String[] args) throws IOException {
    String line;
    String[] splittedLine;

    // read input
    try (final BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in24.txt"), StandardCharsets.UTF_8)) {

      int y = 0;
      while ((line = buffR.readLine()) != null) {
        splittedLine = line.split("");
        int x = 0;
        lines.add(new Line());
        for (final String chr : splittedLine) {
          map[y][x] = chr.charAt(0);
          if (chr.charAt(0) != '#' && chr.charAt(0) != '.') {
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

      for (int i = 1; i < maxTimeToSimulate + 1; i++) {
        simulateBlizzs(i);
      }
      // System.out.println(lines.get(1).occupiedAtTime);
      final int p1 = simulateSteps(1, 0, 0, true);
      System.out.println(p1);

      minValue = Integer.MAX_VALUE;
      lines.forEach(l -> {
        l.hasBeenHereAtTime.clear();
      });

      final int p2 = simulateSteps(width - 2, height - 1, p1, false);
      System.out.println(p2);

      minValue = Integer.MAX_VALUE;
      lines.forEach(l -> {
        l.hasBeenHereAtTime.clear();
      });

      System.out.println(simulateSteps(1, 0, p2, true));

    }
  }

  static void simulateBlizzs(final int i) {
    blizzards.forEach(b -> b.step(i));
  }

  static int simulateSteps(final int x, final int y, final int time,
      final boolean isGoalDown) {
    if ((y < 0) || y >= height || (time >= minValue) || time > 1506
        || (map[y][x] == '#')
        || lines.get(y).occupiedAtTime.get(time % maxTimeToSimulate).contains(x)) {
      return Integer.MAX_VALUE;
    }

    if ((isGoalDown ? y == height - 1 : y == 0)) {
      if (time < minValue) {
        minValue = time;
        return time;
      }
      return Integer.MAX_VALUE;
    }

    for (int t = time; t >= 0; t -= maxTimeToSimulate) {
      if ((lines.get(y).hasBeenHereAtTime.containsKey(t)
          && lines.get(y).hasBeenHereAtTime.get(t).contains(x))) {
        return Integer.MAX_VALUE;
      }
    }

    if (!lines.get(y).hasBeenHereAtTime.containsKey(time)) {
      lines.get(y).hasBeenHereAtTime.put(time, new HashSet<>());
    }
    lines.get(y).hasBeenHereAtTime.get(time).add(x);

    // try all directions
    final int val1 = simulateSteps(x + 1, y, time + 1, isGoalDown);
    final int val2 = simulateSteps(x - 1, y, time + 1, isGoalDown);
    final int val3 = simulateSteps(x, y + 1, time + 1, isGoalDown);
    final int val4 = simulateSteps(x, y - 1, time + 1, isGoalDown);
    final int val5 = simulateSteps(x, y, time + 1, isGoalDown);

    final int min =
        Math.min(Math.min(val1, val2), Math.min(val3, Math.min(val4, val5)));

    return min;
  }
}
