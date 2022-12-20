import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Sol20 {

  static List<Pair> originalList = new LinkedList<>();
  static List<Pair> messageForPart1 = new LinkedList<>();
  static List<Pair> messageForPart2 = new LinkedList<>();

  static long decryptKey = 811589153;

  static class Pair {
    long number;
    boolean isDecoded;
    final int oriIndex;

    Pair(final long number, final boolean isDecoded, final int oriIndex) {
      this.number = number;
      this.isDecoded = isDecoded;
      this.oriIndex = oriIndex;
    }

    Pair(final String line, final int index) {
      this.number = Long.parseLong(line);
      this.oriIndex = index;
      this.isDecoded = false;
    }

    @Override
    public boolean equals(final Object o) {
      if ((o == null) || !(o instanceof Pair)) {
        return false;
      }
      final Pair pairObj = (Pair) o;
      return this.oriIndex == pairObj.oriIndex && pairObj.number == this.number;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.number, this.oriIndex);
    }

    @Override
    public String toString() {
      return "(" + this.number + "," + this.isDecoded + ")";
    }

    int calculateP1(final int index) {
      messageForPart1.remove(index);
      final int added = (int) (index + this.number) % messageForPart1.size();

      final int newIndex = added > 0 ? added : messageForPart1.size() + added;
      messageForPart1.add(newIndex, this);
      this.isDecoded = true;
      return newIndex;
    }

    void calculateP2() {
      final long index = messageForPart2.indexOf(this);
      messageForPart2.remove((int) index);
      final long added = (this.number + index) % messageForPart2.size();

      final int newIndex =
          (int) (added >= 0 ? added : messageForPart2.size() + added);
      messageForPart2.add(newIndex, this);
    }

    void multyByDecKey() {
      this.number *= decryptKey;
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
        messageForPart1.add(new Pair(line, in));
        messageForPart2.add(new Pair(line, in));
        final Pair p = new Pair(line, in);
        originalList.add(p);
        if (p.number == 0) {
          oriIndexOfNull = in;
        }
        in++;
      }
    }


    // Part 1
    int index = 0;
    // System.out.println(message);
    while (index < messageForPart1.size()) {
      final Pair p = messageForPart1.get(index);
      if (p.isDecoded) {
        index++;
      } else {
        final int newIndex = p.calculateP1(index);
        if (newIndex < index) {
          index++;
        }
        // System.out.println(message);
      }
    }

    final int nullIndex = messageForPart1.indexOf(new Pair(0, true, oriIndexOfNull));
    // System.out.println(nullIndex);

    int count = 0;
    for (int i = 1000; i < 3001; i += 1000) {
      count += messageForPart1.get((nullIndex + i) % messageForPart1.size()).number;
    }

    System.out.println(count);


    originalList.stream().forEach(pair -> pair.multyByDecKey());
    // Part 2

    messageForPart2.stream().forEach(pair -> pair.multyByDecKey());
    // System.out.println(messageForPart2);
    for (int i = 0; i < 10; i++) {
      for (final Pair p : originalList) {
        p.calculateP2();
      }

    }

    final int nullIndex2 =
        messageForPart2.indexOf(new Pair(0, false, oriIndexOfNull));
    System.out.println(nullIndex2);

    long count2 = 0;
    for (int i = 1000; i < 3001; i += 1000) {
      System.out.println(
          messageForPart2.get((nullIndex2 + i) % messageForPart2.size()).number);
      count2 +=
          messageForPart2.get((nullIndex2 + i) % messageForPart2.size()).number;
    }
    System.out.println(count2);

  }

}
