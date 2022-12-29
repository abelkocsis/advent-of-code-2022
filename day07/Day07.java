import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day07 {

  /** List to store the current directory structure */
  private final static List<String> CURRENT_PATH = new ArrayList<>();

  /* Map to store the sizes of each subdirectory */
  private final static Map<String, Long> SIZES = new ConcurrentHashMap<>();


  public static void main(final String[] str) {
    String line;

    // read inputs
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in7.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        processLine(line);
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    final Long sum = SIZES.values().stream()
        // filer values than 100 000
        .filter(size -> size <= 100_000)
        // summarise them
        .reduce(0L, (a, b) -> a + b);

    System.out.println("Solution of Part 1: " + sum);

    final long fullSize = 70_000_000;
    final long minNeeded = 30_000_000;
    final long used = SIZES.get(" ");

    final long freeSize = fullSize - used;

    final long minimumToDelete = SIZES.values().stream()
        // filter for values which would free up enough space
        .filter(size -> freeSize + size >= minNeeded)
        // get smallest one
        .reduce((a, b) -> a < b ? a : b).get();

    System.out.println("Solution of Part 2: " + minimumToDelete);

  }

  /**
   * Adds file size to every parent directory
   *
   * @param size
   */
  private static void addSize(final long size) {
    IntStream.range(0, CURRENT_PATH.size())
        // for every parent directory
        .forEach(i -> {
          // get qualified name
          final String fullFoldName = getQuialifyingName(i);

          if (SIZES.containsKey(fullFoldName)) {
            // if already in map, add the size to the previous element
            SIZES.put(fullFoldName, SIZES.get(fullFoldName) + size);
          } else {
            // if not is map, add the size as new element
            SIZES.put(fullFoldName, size);
          }
        });
  }

  private static String getQuialifyingName(final int i) {
    return CURRENT_PATH.subList(0, i + 1).stream()
        .collect(Collectors.joining("", "", ""));
  }

  /**
   * Processes a line
   *
   * @param line
   */
  private static void processLine(final String line) {
    if ("$ ls".equals(line) || line.startsWith("dir")) {
      // we do not need this information
      return;
    }
    if ("$ cd /".equals(line)) {
      // clear path, add home directory as " "
      CURRENT_PATH.clear();
      CURRENT_PATH.add(" ");
      return;
    }
    if ("$ cd ..".equals(line)) {
      // remove last element from path
      CURRENT_PATH.remove(CURRENT_PATH.size() - 1);
      return;
    }
    if (line.startsWith("$ cd")) {
      // add new directory as last element to path
      final String[] splittedLine = line.split(" ");
      CURRENT_PATH.add("/" + splittedLine[2]);
      return;
    }

    // otherwise, get size of element and add size to each parent directory
    final String[] splittedLine = line.split(" ");
    final long size = Long.parseLong(splittedLine[0]);
    addSize(size);

  }
}
