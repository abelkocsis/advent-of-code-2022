import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Day21 {

  /** List of all monkeys */
  private static Map<String, Monkey> monkeys = new HashMap<>();

  /**
   * Dependent list. if MonkeyNameValue is in the list belonging to key
   * MonkeyNameKey, it means that monkey's value with name MonkeyNameValue can only
   * be calculated after MonkeyNameKey's value is calculated. Not that it does not
   * mean that MonkeyNameValue's value can be definitely calculated as it can be
   * dependent on more monkeys' values.
   */
  private static Map<String, List<String>> dependentOn = new HashMap<>();

  /**
   * Representing a monkey
   */
  private static class Monkey {

    /** Whether monkey has a result to shout */
    private boolean hasResult = false;

    /** Name of the monkey */
    private final String name;

    /** Result to shout */
    private Long result = null;

    /**
     * The first parameter of the operation, if the monkey has an operation not a
     * value.
     */
    private String param1 = null;

    /**
     * The second parameter of the operation, if the monkey has an operation not a
     * value.
     */
    private String param2 = null;

    /** Function to calculate the result of the monkey, if an operation is given. */
    private final BiFunction<Long, Long, Long> function;

    /**
     * Reversed function to get the first parameter value, if the result and param2
     * is given.
     */
    private final BiFunction<Long, Long, Long> revFunctionGet1;

    /**
     * Reversed function to get the first parameter value, if the result and param1
     * is given.
     */
    private final BiFunction<Long, Long, Long> revFunctionGet2;

    /**
     * Creates a monkey
     *
     * @param name
     * @param inOperation
     */
    private Monkey(final String name, final String inOperation) {
      this.name = name;
      if (inOperation.contains("+")) {
        this.function = (a, b) -> a + b;
        this.revFunctionGet1 = (a, b) -> a - b;
        this.revFunctionGet2 = (a, b) -> a - b;
        final String[] splittedOp = inOperation.split("[+]");
        this.param1 = splittedOp[0].strip();
        this.param2 = splittedOp[1].strip();
      } else if (inOperation.contains("-")) {
        this.function = (a, b) -> a - b;
        this.revFunctionGet1 = (a, b) -> a + b;
        this.revFunctionGet2 = (a, b) -> b - a;
        final String[] splittedOp = inOperation.split("[-]");
        this.param1 = splittedOp[0].strip();
        this.param2 = splittedOp[1].strip();
      } else if (inOperation.contains("*")) {
        this.function = (a, b) -> a * b;
        this.revFunctionGet1 = (a, b) -> a / b;
        this.revFunctionGet2 = (a, b) -> a / b;
        final String[] splittedOp = inOperation.split("[*]");
        this.param1 = splittedOp[0].strip();
        this.param2 = splittedOp[1].strip();
      } else if (inOperation.contains("/")) {
        this.function = (a, b) -> a / b;
        this.revFunctionGet1 = (a, b) -> a * b;
        this.revFunctionGet2 = (a, b) -> b / a;
        final String[] splittedOp = inOperation.split("[/]");
        this.param1 = splittedOp[0].strip();
        this.param2 = splittedOp[1].strip();
      } else {
        this.result = Long.parseLong(inOperation.strip());
        this.hasResult = true;
        this.function = null;
        this.revFunctionGet1 = null;
        this.revFunctionGet2 = null;
      }
    }

    private void tryToCalculate(final boolean isPart1) {
      // for part 2 only, do not calculate things from human
      if (!isPart1) {
        if ("humn".equals(this.name)) {
          return;
        }
      }

      if (!this.hasResult) {
        // if has no result, try to calculate it
        final Monkey in1M = monkeys.get(this.param1);
        if (in1M == null || !in1M.hasResult) {
          // if cannot calculate because waiting for param1, add to dependent list
          if (!dependentOn.containsKey(this.param1)) {
            dependentOn.put(this.param1, new ArrayList<>());
          }
          dependentOn.get(this.param1).add(this.name);
        }
        final Monkey in2M = monkeys.get(this.param2);
        if (in2M == null || !in2M.hasResult) {
          // if cannot calculate because waiting for param2, add to dependent list
          if (!dependentOn.containsKey(this.param2)) {
            dependentOn.put(this.param2, new ArrayList<>());
          }
          dependentOn.get(this.param2).add(this.name);
        }

        if (in1M != null && in1M.hasResult && in2M != null && in2M.hasResult) {
          // if can calculate, calculate it
          this.result = this.function.apply(in1M.result, in2M.result);
          this.hasResult = true;
        }

      }

      if (this.hasResult) {
        // if has result, try to calculate all which was dpeendent on this value
        if (dependentOn.containsKey(this.name)) {
          dependentOn.get(this.name)
              .forEach(mName -> monkeys.get(mName).tryToCalculate(isPart1));
          dependentOn.remove(this.name);
        }
      }
    }

    /**
     * Calculates parameters in a reversed order. this.value is already assigned,
     * wants to calculate parmeters.
     */
    private void tryToRevCalculate() {
      if (this.param1 == null && this.param2 == null) {
        // of params are not provided, then has a value, done.
        return;
      }

      // get param monkeys
      final Monkey in1M = monkeys.get(this.param1);
      final Monkey in2M = monkeys.get(this.param2);

      if (in1M.result == null && in2M.result != null) {
        // if only param1 is missing, calculate it
        in1M.result = this.revFunctionGet1.apply(this.result, in2M.result);
        in1M.tryToRevCalculate();
      } else if (in2M.result == null && in1M.result != null) {
        // if only param2 is missing
        in2M.result = this.revFunctionGet2.apply(this.result, in1M.result);
        in2M.tryToRevCalculate();
      }
    }
  }

  public static void main(final String[] str) throws IOException {
    String line;
    final String[] splittedLine;

    final List<String> lines = new ArrayList<>();

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in21.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        lines.add(line);
      }
    }

    // part 1
    lines.forEach(l -> {
      final String[] splL = l.split(":");

      final Monkey m = new Monkey(splL[0], splL[1]);
      monkeys.put(m.name, m);
      m.tryToCalculate(true);
    });

    final long rootVal = monkeys.get("root").result;
    System.out.println("Solution for Part 1: " + rootVal);

    // reset
    monkeys.clear();

    // part 2
    lines.forEach(l -> {
      final String[] splL = l.split(":");

      final Monkey m = new Monkey(splL[0], splL[1]);
      if ("humn".equals(m.name)) {
        // assign empty to humn value
        m.result = null;
        m.hasResult = false;
      }
      monkeys.put(m.name, m);
      // calculate all. It does not calculate values which are dependent on humn in
      // any way
      m.tryToCalculate(false);
    });

    // assumption: one of the values in root was calculated

    final Monkey rootParam1 = monkeys.get(monkeys.get("root").param1);
    final Monkey rootParam2 = monkeys.get(monkeys.get("root").param2);

    // root params should be equal, so make them equal. Then calculate all backwards
    if (rootParam1.hasResult) {
      rootParam2.result = rootParam1.result;
      rootParam2.tryToRevCalculate();
    } else if (rootParam2.hasResult) {
      rootParam1.result = rootParam2.result;
      rootParam1.tryToRevCalculate();
    }

    final long humnVal = monkeys.get("humn").result;
    System.out.println("Solution for part 2: " + humnVal);
  }

}
