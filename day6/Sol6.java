import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sol6 {
  public static void main(final String[] str) {

    // parse input
    String line = "";
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in6.txt"), StandardCharsets.UTF_8)) {
      line = buffR.readLine();
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // input as list of characters
    final List<String> input = Arrays.asList(line.split(""));

    // calculate results
    System.out.println("Solution for Part 1: " + findFirstNDifferent(input, 4));
    System.out.println("Solution for Part 1: " + findFirstNDifferent(input, 14));

  }

  /**
   * Find first number of inputs needs to be processed before the first *n* different
   * characters are found
   *
   * @param input Input list, must have a length of at least n
   * @param n Number of consecutive different characters to look for
   * @return Result od -1 if no result is found
   */
  private static int findFirstNDifferent(final List<String> input, final int n) {
    // to check last n elements
    final List<String> lastElements = new ArrayList<>(n);
    Set<String> lastElementsS;

    for (int i = 0; i < n; i++) {
      // add first n to the list
      lastElements.add(input.get(i));
    }

    // check if first n is good
    lastElementsS = new HashSet<>();
    lastElementsS.addAll(lastElements);
    if (lastElementsS.size() == n) {
      return n;
    }

    // go through the list until result is found
    for (int i = n; i < input.size(); i++) {

      // remove oldest element from lastElements list
      lastElements.remove(0);

      // add new element to end of lastElements list
      lastElements.add(input.get(i));

      // convert it to a set (== remove duplicates)
      lastElementsS = new HashSet<>();
      lastElementsS.addAll(lastElements);

      // if size equals to n, then result is found
      if (lastElementsS.size() == n) {
        return i + 1;
      }
    }
    return -1;
  }
}
