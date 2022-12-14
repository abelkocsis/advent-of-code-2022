import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Sol13 {

  /**
   * Class for an element of packet data
   */
  /* deafult */static class Elem {

    /** Integer value. Set iff {@link Sol13.Elem.isInteger} is true */
    private int intVal;

    /** List of subelements */
    private final List<Elem> listVal = new ArrayList<>();

    /** True if element is an integer value, false if a list. */
    private boolean isInteger;

    /**
     * Creates a list element without any entries in the list.
     */
    private Elem() {
      this.isInteger = false;
    }

    /**
     * Creates an integer element with value
     *
     * @param integerValue
     */
    private Elem(final int integerValue) {
      this.intVal = integerValue;
      this.isInteger = true;
    }

    /**
     * Creates an element based on the input string. If list element, creates more
     * Elems recursively
     *
     * @param input String to parse
     */
    /* default */ Elem(final String input) {

      try {
        // if input can be parsed as integer, element is integer
        this.intVal = Integer.parseInt(input);
        this.isInteger = true;
        return;
      } catch (final NumberFormatException e) {
        // otherwise, element is list
        this.isInteger = false;
      }

      // let's get rid of opening and closing brackets
      final char[] convertedString =
          input.substring(1, input.length() - 1).toCharArray();

      // counts how many brackets deep we currently are
      int bracketCounter = 0;

      // temporarily saves string between brackets
      StringBuffer sb = new StringBuffer();

      // iterate trough the whole string
      for (final char c : convertedString) {
        if (c == '[') {
          // if opening bracket,
          // add data to temporary data saver
          sb.append(c);
          // increase bracket counter
          bracketCounter++;
        } else if (c == ']') {
          // if closing bracket,
          // add data to temporary data saver
          sb.append(c);
          // decrease bracket counter
          bracketCounter--;
        } else if (bracketCounter > 0 || c != ',') {
          // if bracket counter is greater than zero and character not ',', it means
          // we are currently
          // parsing a sublist, which this element should not care about.
          // Add character to temporary data saver
          sb.append(c);
        } else {
          // if bracketCounter == 0 and character == ','
          // if temporary data saver is not blank, parse this data as a new element
          // and add as a list element to the current element
          if (!sb.toString().isBlank()) {
            this.listVal.add(new Elem(sb.toString()));
          }
          // do not forget to reset the temporary data saver
          sb = new StringBuffer();
        }
        // note that we covered all cases

      }

      // when the process ends, add the lastly parsed data to the list
      if (!sb.toString().isBlank()) {
        this.listVal.add(new Elem(sb.toString()));
      }
    }

    private static RES compareInts(final Elem first, final Elem second) {
      if (first.intVal < second.intVal) {
        return RES.GOOD;
      } else if (first.intVal == second.intVal) {
        return RES.CANT_DECIDE;
      } else {
        return RES.BAD;
      }
    }

    private static RES compareLists(final Elem first, final Elem second) {
      int thisListInd = 0;
      int bListInd = 0;
      while (true) {
        if (thisListInd == first.listVal.size()
            && bListInd < second.listVal.size()) {
          // first list ran out of elements
          return RES.GOOD;
        } else if (thisListInd < first.listVal.size()
            && bListInd == second.listVal.size()) {
          // second list ran out of elements
          return RES.BAD;
        } else if (thisListInd == first.listVal.size()
            && bListInd == second.listVal.size()) {
          // they both ran out of elements
          return RES.CANT_DECIDE;
        } else {
          // result of comparison of current elements
          final RES partRes = first.listVal.get(thisListInd)
              .isSmallerThan(second.listVal.get(bListInd));

          if (partRes == RES.CANT_DECIDE) {
            // if cannot decide, continue the comparison with the next elements
            thisListInd++;
            bListInd++;
            continue;
          } else {
            // if can decide, return the result
            return partRes;
          }
        }
      }
    }

    @Override
    public String toString() {
      if (this.isInteger) {
        return Integer.toString(this.intVal);
      } else {
        return this.listVal.toString();
      }
    }

    /**
     * Convert element to a list element
     *
     * @return
     */
    private Elem toListElement() {
      final Elem result = new Elem();
      result.listVal.add(this);
      return result;
    }

    /**
     * Returns whether the called element is smaller than parameter 'b'
     *
     * @param second
     * @return {@code RES.GOOD} if smaller; {@code RES.BAD} if greater, and
     *         {@code RES.CANT_DECIDE} if they equal
     */
    /* default */RES isSmallerThan(final Elem second) {
      if (this.isInteger && second.isInteger) {
        // if both elements are integers
        return compareInts(this, second);
      } else if (!this.isInteger && !second.isInteger) {
        // if both elements are lists
        return compareLists(this, second);
      } else if (this.isInteger) {
        // if current element is integer, second elem is a list
        // convert current element to list, compare with b
        return this.toListElement().isSmallerThan(second);
      } else {
        // otherwise, when current element is list, second elem is an integer
        // convert b to a list, compare elements
        return this.isSmallerThan(second.toListElement());
      }
    }
  }

  /**
   * Enum to return a result of the comparison
   */
  enum RES {
    GOOD, BAD, CANT_DECIDE
  }

  public static void main(final String[] args) {

    // initialise variables

    String line; // to parse a single line
    Elem elem1; // to store first element of a pair
    Elem elem2; // to store second element of a pair

    // to store all elements
    final List<Elem> elements = new ArrayList<>();

    int ind = 1; // indices of the pair
    int sum = 0; // sum of correct pair indices


    // start processing

    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in13.txt"), StandardCharsets.UTF_8)) {

      while ((line = buffR.readLine()) != null) {
        if (line.isBlank()) {
          continue;
        }

        elem1 = new Elem(line);
        elements.add(elem1);

        line = buffR.readLine();
        elem2 = new Elem(line);
        elements.add(elem2);

        if (elem1.isSmallerThan(elem2) == RES.GOOD) {
          sum += ind;
        }
        ind++;

      }

    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    System.out.println("Solution for Part 1: " + sum);

    // Part 2

    // Add divider packets
    final Elem dividerPacket1 = new Elem("[[2]]");
    final Elem dividerPacket2 = new Elem("[[6]]");
    elements.add(dividerPacket1);
    elements.add(dividerPacket2);

    // Sort all elements
    elements.sort((a, b) -> a.isSmallerThan(b) == RES.GOOD ? -1 : 1);

    // Find divider packets and multiply their indices
    final int result = IntStream.range(0, elements.size()).filter(
        i -> elements.get(i) == dividerPacket1 || elements.get(i) == dividerPacket2)
        .reduce(1, (a, b) -> (a) * (b + 1));

    System.out.println("Solution for Part 2: " + result);
  }
}
