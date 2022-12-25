import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Sol20 {

  /** Original list */
  private static List<Elem> originalList = new LinkedList<>();

  /** Elements to decrypt for Part 1 */
  private static List<Elem> elemsForPart1 = new LinkedList<>();

  /** Elements to decrypt for Part 2 */
  private static List<Elem> elemsForPart2 = new LinkedList<>();

  /** Decrypt key */
  private static final long DECRYPT_KEY = 811_589_153;

  /**
   * Element
   */
  private static class Elem {

    /** Number, after of before decrption */
    private long number;

    /** Original index of element */
    private final int oriIndex;

    /**
     * Creates an element from its parameters
     *
     * @param number
     * @param oriIndex
     */
    private Elem(final long number, final int oriIndex) {
      this.number = number;
      this.oriIndex = oriIndex;
    }

    /**
     * Creates an element from input
     *
     * @param line
     * @param index
     */
    private Elem(final String line, final int index) {
      this.number = Long.parseLong(line);
      this.oriIndex = index;
    }

    @Override
    public boolean equals(final Object o) {
      if (o == null || !(o instanceof Elem)) {
        return false;
      }
      final Elem pairObj = (Elem) o;
      return this.oriIndex == pairObj.oriIndex && pairObj.number == this.number;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.number, this.oriIndex);
    }

    /**
     * Calculates decrypted value for Part 1
     *
     * @return
     */
    private int calculateP1() {
      final long index = elemsForPart1.indexOf(this);
      elemsForPart1.remove(this);
      final int added = (int) (index + this.number) % elemsForPart1.size();

      final int newIndex = added > 0 ? added : elemsForPart1.size() + added;
      elemsForPart1.add(newIndex, this);
      return newIndex;
    }

    /**
     * Calculates decrypted value for Part 2
     *
     * @return
     */
    private void calculateP2() {
      final long index = elemsForPart2.indexOf(this);
      elemsForPart2.remove((int) index);
      final long added = (this.number + index) % elemsForPart2.size();

      final int newIndex = (int) (added >= 0 ? added : elemsForPart2.size() + added);
      elemsForPart2.add(newIndex, this);
    }

    /**
     * Multiplies value with decryption key
     */
    private void multyByDecKey() {
      this.number *= DECRYPT_KEY;
    }
  }

  public static void main(final String[] args) throws IOException {
    String line;
    int in = 0;
    int oriIndexOfNull = -1;
    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in20.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        elemsForPart1.add(new Elem(line, in));
        elemsForPart2.add(new Elem(line, in));
        final Elem p = new Elem(line, in);
        originalList.add(p);
        if (p.number == 0) {
          oriIndexOfNull = in;
        }
        in++;
      }
    }


    // Part 1
    for (final Elem p : originalList) {
      p.calculateP1();
    }

    final int nullIndex = elemsForPart1.indexOf(new Elem(0, oriIndexOfNull));
    int count = 0;
    for (int i = 1000; i < 3001; i += 1000) {
      count += elemsForPart1.get((nullIndex + i) % elemsForPart1.size()).number;
    }

    System.out.println("Solution for Part 1: " + count);

    // Part 2
    originalList.stream().forEach(pair -> pair.multyByDecKey());
    elemsForPart2.stream().forEach(pair -> pair.multyByDecKey());
    for (int i = 0; i < 10; i++) {
      for (final Elem p : originalList) {
        p.calculateP2();
      }

    }

    final int nullIndex2 = elemsForPart2.indexOf(new Elem(0, oriIndexOfNull));

    long count2 = 0;
    for (int i = 1000; i < 3001; i += 1000) {
      count2 += elemsForPart2.get((nullIndex2 + i) % elemsForPart2.size()).number;
    }
    System.out.println("Solution for Part 2: " + count2);

  }

}
