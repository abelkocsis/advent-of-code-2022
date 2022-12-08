import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Sol8 {

  /** List to store the current directory structure */
  private final static List<List<Integer>> HEIGHTS = new ArrayList<>();

  public static void main(final String[] str) {
    String line;

    // read inputs
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in8.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        HEIGHTS.add(new ArrayList<>());
        final int lastInList = HEIGHTS.size() - 1;
        Arrays.asList(line.split("")).forEach(heightS -> {
          HEIGHTS.get(lastInList).add(Integer.parseInt(heightS));
        });
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // part 1
    final long counter = IntStream
        // for all i
        .range(0, HEIGHTS.size())
        // for all j
        .mapToLong(i -> IntStream.range(0, HEIGHTS.get(i).size())
            // check if visible
            .filter(j -> isVisible(i, j))
            // count them
            .count())
        // add them all together
        .reduce(0L, (a, b) -> a + b);

    System.out.println("Solution for Part 1: " + counter);

    // part 2
    final long highestScore =
        // for all i
        IntStream.range(0, HEIGHTS.size())
            // for all j
            .mapToLong(i -> IntStream.range(0, HEIGHTS.get(i).size())
                // get scenic score
                .mapToLong(j -> getScenicScore(i, j))
                // find the highest one
                .reduce(0L, (a, b) -> a > b ? a : b))
            .reduce(0L, (a, b) -> a > b ? a : b);

    System.out.println("Solution for Part 2: " + highestScore);

  }

  private static int couldSeeEast(final int currentHeight, final int i,
      final int j) {
    final int firstTallerJ = IntStream.range(j + 1, HEIGHTS.get(i).size())
        .filter(indJ -> HEIGHTS.get(i).get(indJ) >= currentHeight).findFirst()
        .orElse(HEIGHTS.get(i).size() - 1);

    return firstTallerJ - j;
  }

  private static int couldSeeNorth(final int currentHeight, final int i,
      final int j) {
    final int firstTallerI = IntStream.range(1, i + 1).map(it -> i - it)
        .filter(indI -> HEIGHTS.get(indI).get(j) >= currentHeight).findFirst()
        .orElse(0);
    return i - firstTallerI;
  }

  private static int couldSeeSouth(final int currentHeight, final int i,
      final int j) {
    final int firstTallerI = IntStream.range(i + 1, HEIGHTS.size())
        .filter(indI -> HEIGHTS.get(indI).get(j) >= currentHeight).findFirst()
        .orElse(HEIGHTS.size() - 1);
    return firstTallerI - i;
  }


  private static int couldSeeWest(final int currentHeight, final int i,
      final int j) {
    final int firstTallerJ = IntStream.range(1, j + 1).map(it -> j - it)
        .filter(indJ -> HEIGHTS.get(i).get(indJ) >= currentHeight).findFirst()
        .orElse(0);
    return j - firstTallerJ;
  }

  private static long getScenicScore(final int i, final int j) {
    final int currentHeight = HEIGHTS.get(i).get(j);
    return (long) couldSeeEast(currentHeight, i, j)
        * (long) couldSeeNorth(currentHeight, i, j)
        * couldSeeSouth(currentHeight, i, j) * couldSeeWest(currentHeight, i, j);
  }

  private static boolean isVisibleFromEast(final int currentHeight, final int i,
      final int j) {
    return !IntStream.range(j + 1, HEIGHTS.get(i).size())
        .anyMatch(indJ -> HEIGHTS.get(i).get(indJ) >= currentHeight);
  }

  private static boolean isVisibleFromNorth(final int currentHeight, final int i,
      final int j) {
    return !IntStream.range(0, i)
        .anyMatch(indI -> HEIGHTS.get(indI).get(j) >= currentHeight);
  }

  private static boolean isVisibleFromSouth(final int currentHeight, final int i,
      final int j) {
    return !IntStream.range(i + 1, HEIGHTS.size())
        .anyMatch(indI -> HEIGHTS.get(indI).get(j) >= currentHeight);
  }

  private static boolean isVisibleFromWest(final int currentHeight, final int i,
      final int j) {
    return !IntStream.range(0, j)
        .anyMatch(indJ -> HEIGHTS.get(i).get(indJ) >= currentHeight);
  }

  static boolean isVisible(final int i, final int j) {
    final int currentHeight = HEIGHTS.get(i).get(j);
    return isVisibleFromWest(currentHeight, i, j)
        || isVisibleFromEast(currentHeight, i, j)
        || isVisibleFromNorth(currentHeight, i, j)
        || isVisibleFromSouth(currentHeight, i, j);
  }

}
