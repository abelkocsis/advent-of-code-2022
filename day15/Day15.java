import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Day15 {

  /** List of all sensors */
  private static final List<Sensor> sensors = new ArrayList<>();

  /** Maximum distance between a sensor and the closest beacon */
  private static int maxDist = Integer.MIN_VALUE;

  /** Smallest possible X coordinate value */
  private static int minX = Integer.MAX_VALUE;

  /** Largest possible X coordinate value */
  private static int maxX = Integer.MIN_VALUE;

  /**
   * Class to represent a coordinate
   */
  public static class Coord extends Object {

    /** X coordinate */
    private final int x;

    /** Y coordinate */
    private final int y;

    /**
     *
     * @param x
     * @param y
     */
    private Coord(final int x, final int y) {
      super();
      this.x = x;
      this.y = y;
    }

    /**
     * Parses an input line to coordinate instance
     *
     * @param cords
     */
    private Coord(final String cords) {
      super();
      final String[] splittedCords = cords.split(", ");

      this.x = Integer.parseInt(splittedCords[0].substring("x=".length()).strip());
      this.y = Integer.parseInt(splittedCords[1].substring("y=".length()).strip());
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || this.getClass() != obj.getClass()) {
        return false;
      }
      final Coord coordObj = (Coord) obj;
      return this.x == coordObj.x && this.y == coordObj.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.x, this.y);
    }

    /**
     * Returns the Manhattan distance between coordinates
     *
     * @param b
     * @return
     */
    private int distance(final Coord b) {
      return Math.abs(this.x - b.x) + Math.abs(this.y - b.y);
    }
  }

  /**
   * Representation of a sensor
   */
  private static class Sensor {

    /** Coordiante of the sensor */
    private final Coord coordinate;

    /** Position of the closest beacon */
    private final Coord beaconCoord;

    /** Distance between sensor and closest beacon */
    private final int beaconDistance;

    /**
     * Parses a line into a sensor instance
     *
     * @param line
     */
    private Sensor(final String line) {
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

      this.beaconDistance = this.coordinate.distance(this.beaconCoord);

      if (this.beaconDistance > maxDist) {
        maxDist = this.beaconDistance;
      }
    }
  }


  public static void main(final String[] args) {
    String line;

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in15.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        sensors.add(new Sensor(line));
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // part 1
    final long p1Result = getRowCount(2_000_000);
    System.out.println("Solution for Part 1: " + p1Result);

    // part 2
    int distressX = -1;
    int distressY = -1;
    for (int y = 0; y <= 4_000_000; y++) {
      // 0 <= y <= 4 000 000 according to the description
      distressX = getEmptyX(y);
      if (distressX != -1) {
        // if distress position found, stop the finding
        distressY = y;
        break;
      }
    }
    final long p2Result = (long) distressX * 4_000_000 + distressY;
    System.out.println("Solution for Part 2: " + p2Result);

  }

  /**
   * Gets the X coordinate of the position where the distress beacon could be in the
   * given row
   *
   * @param y Y coordinate of row to examine
   * @return X coordinate where distress beacon could be. If distress beacon cannot
   *         be in current row, returns -1
   */
  private static int getEmptyX(final int y) {
    // 0 <= x <= 4 000 000 according to the description
    for (int x = 0; x <= 4_000_000; x++) {

      // current coordinate
      final Coord coord = new Coord(x, y);

      // we call a coordinate covered by a sensor, if the closest beacon to the
      // sensor is at most as close to the sensor as the currently examined position
      Sensor coveredBy; // sensor that covers the current coordinate
      try {
        coveredBy = sensors.stream()
            .filter(
                sensor -> coord.distance(sensor.coordinate) <= sensor.beaconDistance)
            .findAny().get();
      } catch (final NoSuchElementException e) {
        // if no sensor "covers" the coordinate, we found a place which could have
        // the distress signal
        return x;
      }

      // iterate x cleverly rather than 1 by 1, to speed up the finding proccess.
      if (coveredBy.coordinate.x > x) {
        x = coveredBy.coordinate.x + (coveredBy.coordinate.x - x);
      } else {
        x = coveredBy.coordinate.x
            + (coveredBy.beaconDistance - Math.abs(coveredBy.coordinate.y - y));
      }
    }

    return -1;
  }

  /**
   * Gets number of covered position counts in given row
   *
   * @param y
   * @return
   */
  private static long getRowCount(final int y) {
    int counter = 0;
    for (int x = minX - maxDist; x < maxX + maxDist; x++) {
      final Coord coord = new Coord(x, y);
      final boolean isOccupied = sensors.stream().anyMatch(
          sensor -> coord.distance(sensor.coordinate) <= sensor.beaconDistance);
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
