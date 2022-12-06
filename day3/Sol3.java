import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Sol3 {

  public static void main(final String[] args) {
    String line;
    int overallPriority = 0;
    final String[] allLines = new String[300];
    int i = 0;

    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in3.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        allLines[i++] = line;
        // part 1
        final int partLen = line.length() / 2;
        final String secondPart = line.substring(partLen);

        // note: to avoid conversions we treat characters as strings

        // find common character
        final String common =
            // create stream of characters of first part
            Arrays.asList(line.substring(0, partLen).split("")).stream()
                // filter for characters which are present in second part
                .filter(chr -> secondPart.contains(chr))
                // thers should be exactly one, so just get nay duplicates
                .findAny().get();

        // get priority for common character
        overallPriority += getPriority(common);
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // part 2
    int overallPriorityP2 = 0;
    for (int groupIndex = 0; groupIndex < allLines.length; groupIndex += 3) {
      final int firstInGrp = groupIndex;
      final int secondInGrp = groupIndex + 1;
      final int thirdInGrp = groupIndex + 2;

      // find common for all three
      final String common =
          // create stream of characters in first list
          Arrays.asList(allLines[firstInGrp].split("")).stream()
              // filter for duplicates with second list
              .filter(chrS -> allLines[secondInGrp].contains(chrS))
              // filter for duplicates with thrid list
              .filter(chrS -> allLines[thirdInGrp].contains(chrS))
              // there should be exactly one, get any
              .findAny().get();

      // get priority for common character
      overallPriorityP2 += getPriority(common);
    }

    System.out.println("Solution for part 1: " + overallPriority);
    System.out.println("Solution for part 2: " + overallPriorityP2);
  }

  /**
   * Gets priority for a common character
   *
   * @param commonCharacter Common character as String. Only first character matters
   * @return
   */
  private static int getPriority(final String commonCharacter) {
    final char chr = commonCharacter.charAt(0);
    if (chr < 'a') {
      return chr - 'A' + 27;
    } else {
      return chr - 'a' + 1;
    }
  }
}
