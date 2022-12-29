import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day04 {

  public static void main(final String[] str) {
    String line;
    String[] splittedLine;
    String[] e1S; // elf 1's interval as string array
    String[] e2S; // elf 2's interval as string array

    int e1Start; // elf 1's interval starts
    int e1End; // elf 1's interval ends
    int e2Start; // elf 2's interval starts
    int e2End; // elf 2's interval ends

    int counterP1 = 0;
    int counterP2 = 0;
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in4.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {

        // parse intervals
        splittedLine = line.split(",");
        e1S = splittedLine[0].split("-");
        e1Start = Integer.parseInt(e1S[0]);
        e1End = Integer.parseInt(e1S[1]);
        e2S = splittedLine[1].split("-");
        e2Start = Integer.parseInt(e2S[0]);
        e2End = Integer.parseInt(e2S[1]);

        if (contains(e1Start, e1End, e2Start, e2End)) {
          // if fully contains, then overlaps as well
          counterP1++;
          counterP2++;
        } else if (overlaps(e1Start, e1End, e2Start, e2End)) {
          // check if they overlap
          counterP2++;
        }
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    System.out.println("Solution for Part 1: " + counterP1);
    System.out.println("Solution for Part 2: " + counterP2);
  }

  /**
   * Returns true if any of the intervals fully contains the other one
   *
   * @param e1Start First interval start
   * @param e1End First interval end
   * @param e2Start Second interval start
   * @param e2End Second interval end
   * @return
   */
  private static boolean contains(final int e1Start, final int e1End,
      final int e2Start, final int e2End) {
    return e1Start <= e2Start && e1End >= e2End
        || e2Start <= e1Start && e2End >= e1End;
  }

  /**
   * Returns true if the two intervals overlap. Returns false if one interval fully
   * contains the other, so contains must be run for full overlaps check.
   *
   * @param e1Start First interval start
   * @param e1End First interval end
   * @param e2Start Second interval start
   * @param e2End Second interval end
   * @return
   */
  private static boolean overlaps(final int e1Start, final int e1End,
      final int e2Start, final int e2End) {
    return e1Start <= e2Start && e2Start <= e1End
        || e1Start <= e2End && e2End <= e1End;
  }
}
