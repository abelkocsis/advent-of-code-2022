import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Sol15 {
  final static List<Sensor> sensors = new ArrayList<>();

  static int maxDist = Integer.MIN_VALUE;
  static int minX = Integer.MAX_VALUE;
  static int maxX = Integer.MIN_VALUE;

  public static class Coord extends Object {
    int x;
    int y;

    Coord(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    Coord(final String cords) {
      final String[] splittedCords = cords.split(", ");

      this.x = Integer.parseInt(splittedCords[0].substring("x=".length()).strip());
      this.y = Integer.parseInt(splittedCords[1].substring("y=".length()).strip());
    }

    @Override
    public boolean equals(final Object o) {

      // self check
      if (this == o) {
        return true;
      }
      // null check
      // type check and cast
      if ((o == null) || (this.getClass() != o.getClass())) {
        return false;
      }
      final Coord coord = (Coord) o;
      // field comparison
      return this.x == coord.x && this.y == coord.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.x, this.y);
    }

    int distance(final Coord b) {
      return Math.abs(this.x - b.x) + Math.abs(this.y - b.y);
    }
  }

  private static class Sensor {
    Coord coordinate;
    Coord beaconCoord;
    int distance;

    Sensor(final String line) {
      final String[] splittedLine =
          line.substring("Sensor at ".length()).split(": closest beacon is at ");
      this.coordinate = new Coord(splittedLine[0]);

      if (this.coordinate.x < minX) {
        minX = this.coordinate.x;
      }
      if (this.coordinate.x > maxX) {
        maxX = this.coordinate.x;
      }

      this.beaconCoord = new Coord(splittedLine[1]);

      this.distance = this.coordinate.distance(this.beaconCoord);

      if (this.distance > maxDist) {
        maxDist = this.distance;
      }
    }
  }


  public static void main(final String[] args) {
    // Initialise variables

    String line; // to parse input

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in15.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        sensors.add(new Sensor(line));
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }
    // System.out.println(getRowCount(2000000));

    int distressX = -1;
    int distressY = -1;
    for (int i = 0; i <= 4000000; i++) {
      final int emptyPosition = getEmptyX(i);
      if (emptyPosition != -1) {
        distressY = i;
        distressX = emptyPosition;
        System.out.println(getEmptyX(i) + ", " + i);
        break;
      }
    }

    System.out.println((long) distressX * 4000000 + distressY);

  }

  static int getEmptyX(final int y) {
    for (int x = 0; x <= 4000000; x++) {
      final Coord coord = new Coord(x, y);

      Sensor coveredBy;
      try {
        coveredBy = sensors.stream()
            .filter(sensor -> coord.distance(sensor.coordinate) <= sensor.distance)
            .findAny().get();
      } catch (final NoSuchElementException e) {
        return x;
      }

      if (coveredBy.coordinate.x > x) {
        x = coveredBy.coordinate.x + (coveredBy.coordinate.x - x);
      } else {
        x = coveredBy.coordinate.x
            + (coveredBy.distance - Math.abs(coveredBy.coordinate.y - y));
      }
    }

    return -1;
  }

  static long getRowCount(final int y) {
    int counter = 0;
    for (int x = minX - maxDist; x < maxX + maxDist; x++) {
      final Coord coord = new Coord(x, y);
      final boolean isOccupied = sensors.stream()
          .anyMatch(sensor -> coord.distance(sensor.coordinate) <= sensor.distance);
      if (isOccupied) {
        counter++;
      }
    }

    final long beaconInRow =
        sensors.stream().filter(sensor -> sensor.beaconCoord.y == y)
            .map(sensor -> sensor.beaconCoord).distinct().count();

    return counter - beaconInRow;
  }
}
