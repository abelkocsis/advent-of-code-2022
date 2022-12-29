import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 {

  /** List of all monkeys */
  private final static List<Monkey> MONKEYS = new ArrayList<>(8);

  /** Common multiple of all divisors. Used to normalise worry level */
  private static long commonMultiple = 1L;

  /**
   * Class for a Monkey
   */
  private static final class Monkey {

    /** Worry levels of currently stored items */
    private transient final List<Long> items;

    /**
     * Array of elements of {@code val1} and {@code val2}, where the Operation
     * defined for the monkey is {@code old = <val1> <opCommand> <val2>}
     */
    private transient final String[] operationElements = new String[2];

    /**
     * Opeartion command {@code <opCommand>} where the Operation defined for the
     * monkey is {@code old = <val1> <opCommand> <val2>}
     */
    private transient final String opCommand;

    /**
     * Test function to run to decide to which monkey to throw an item based on its
     * worry level.
     */
    private transient final Function<Long, Integer> testAction;

    /** Number of times the monkey inspected an item */
    private transient int inspItemCount = 0;

    /**
     * Constructor
     *
     * @param buffR
     * @throws IOException
     */
    protected Monkey(final BufferedReader buffR) throws IOException {
      String line;

      // parse starting items
      line = buffR.readLine();
      final List<String> splittedLine = Arrays.asList(line.split(","));
      final String[] firstPartSplitted = splittedLine.get(0).split(" ");
      this.items = new ArrayList<>();
      this.items
          .add(Long.parseLong(firstPartSplitted[firstPartSplitted.length - 1]));
      for (int i = 1; i < splittedLine.size(); i++) {
        this.items.add(Long.parseLong(splittedLine.get(i).strip()));
      }

      // parse operation
      line = buffR.readLine();
      String[] splittedLineA = line.split("Operation: new = ");
      splittedLineA = splittedLineA[1].split(" ");
      this.operationElements[0] = splittedLineA[0];
      this.opCommand = splittedLineA[1];
      this.operationElements[1] = splittedLineA[2];

      // parse test action
      line = buffR.readLine();
      splittedLineA = line.split("Test: divisible by ");
      final long tesDivisor = Long.parseLong(splittedLineA[1]);
      commonMultiple *= tesDivisor;

      line = buffR.readLine();
      splittedLineA = line.split("If true: throw to monkey ");
      final int ifTrue = Integer.parseInt(splittedLineA[1]);

      line = buffR.readLine();
      splittedLineA = line.split("If false: throw to monkey ");
      final int ifFalse = Integer.parseInt(splittedLineA[1]);

      this.testAction =
          worryLevel -> worryLevel % tesDivisor == 0 ? ifTrue : ifFalse;
    }

    /**
     * Deals with all items in a single round. <br>
     * Only assumption is that an item is definitely thrown to a <i>different</i>
     * monkey after the operation. <br>
     * In order to avoid huge numbers, the result is always divided by a
     * commonMultiple variable, which is the multiplication of all the possible
     * divisors.
     */
    private void dealWithAllItems() {
      while (!this.items.isEmpty()) {
        this.inspItemCount++;
        long worryLevel = this.items.remove(0);
        worryLevel = this.operate(worryLevel);

        // For solution for Part 1, uncomment this line:
        // worryLevel = worryLevel / 3;

        final int nextMonkey = this.testAction.apply(worryLevel);
        worryLevel = worryLevel % commonMultiple;
        MONKEYS.get(nextMonkey).items.add(worryLevel);
      }
    }

    /**
     * Runs the operation when item is being inspected
     *
     * @param old
     * @return
     */
    private long operate(final long old) {
      final long elem1 = "old".equals(this.operationElements[0]) ? old
          : Long.parseLong(this.operationElements[0]);
      final long elem2 = "old".equals(this.operationElements[1]) ? old
          : Long.parseLong(this.operationElements[1]);

      switch (this.opCommand) {
        case "+":
          return elem1 + elem2;
        case "*":
          return elem1 * elem2;
        default:
          throw new RuntimeException("Invalid opretaion in command.");
      }
    }

  }

  public static void main(final String[] str) {
    String line;

    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in11.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        if (line.isBlank()) {
          continue;
        }
        MONKEYS.add(new Monkey(buffR));
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // For solution for Part 1, change number of rounds
    final int rounds = 10_000;
    IntStream.range(0, rounds)
        .forEach(i -> MONKEYS.forEach(monkey -> monkey.dealWithAllItems()));

    // get inspectedItemCount for each monkey and sorts them
    final List<Integer> handledItems = MONKEYS.stream()
        .map(monkey -> monkey.inspItemCount).sorted().collect(Collectors.toList());
    Collections.reverse(handledItems);

    // Gets result
    System.out.println((long) handledItems.get(0) * handledItems.get(1));

  }

}
