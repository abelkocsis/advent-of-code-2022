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

public class Sol21 {

  /** Monkey name - monkey object map to store all monkeys */
  private static final Map<String, Monkey> MONKEYS = new HashMap<>();

  /**
   * Map to store dependencies. MonkeyD is in the list belonging to key MonkeyK, if
   * MonkeyD's result can only be calculated after MonkeyK's result is known. Note
   * that it does not mean that MonkeyD is only dependent on MonkeyK's value
   */
  private static final Map<String, List<String>> DEPENDS_ON = new HashMap<>();

  /**
   * Representation of a monkey
   */
  private static final class Monkey {

    /** Whether result is given or has already been calculated */
    /* default */ transient boolean hasResult = false;

    /** Name of the monkey */
    /* default */ transient final String name;

    /** Result what is shouted by monkey */
    /* default */ transient Long result = null;

    /** First argument of calculation, if a calculation is needed */
    /* default */ transient final String param1Name;

    /** Second argument of calculation, if a calculation is needed */
    /* default */ transient final String param2Name;

    /** Function to calculate the result */
    private transient final BiFunction<Long, Long, Long> function;

    /**
     * Function to get the first parameter of the calculation, given the result and
     * the second parameter.
     */
    private transient final BiFunction<Long, Long, Long> revFunctionGet1;

    /**
     * Function to get the second parameter of the calculation, given the result and
     * the first parameter.
     */
    private transient final BiFunction<Long, Long, Long> revFunctionGet2;

    /**
     * Monkey constructor
     *
     * @param name
     * @param inOperation
     */
    protected Monkey(final String name, final String inOperation) {
      this.name = name;
      if (inOperation.contains("+")) {
        // operation is addition
        this.function = (param1, param2) -> param1 + param1;
        this.revFunctionGet1 = (result, param2) -> result - param2;
        this.revFunctionGet2 = (result, param1) -> result - param1;
        final String[] splittedOp = inOperation.split("[+]");
        this.param1Name = splittedOp[0].strip();
        this.param2Name = splittedOp[1].strip();
      } else if (inOperation.contains("-")) {
        // operation is subtraction
        this.function = (param1, param2) -> param1 - param2;
        this.revFunctionGet1 = (result, param2) -> result + param2;
        this.revFunctionGet2 = (result, param1) -> result - param1;
        final String[] splittedOp = inOperation.split("[-]");
        this.param1Name = splittedOp[0].strip();
        this.param2Name = splittedOp[1].strip();
      } else if (inOperation.contains("*")) {
        // operation is multiplication
        this.function = (param1, param2) -> param1 * param2;
        this.revFunctionGet1 = (result, param2) -> result / param2;
        this.revFunctionGet2 = (result, param1) -> result / param1;
        final String[] splittedOp = inOperation.split("[*]");
        this.param1Name = splittedOp[0].strip();
        this.param2Name = splittedOp[1].strip();
      } else if (inOperation.contains("/")) {
        // operation is division
        this.function = (param1, param2) -> param1 / param2;
        this.revFunctionGet1 = (result, param2) -> result * param2;
        this.revFunctionGet2 = (result, param1) -> result / param1;
        final String[] splittedOp = inOperation.split("[/]");
        this.param1Name = splittedOp[0].strip();
        this.param2Name = splittedOp[1].strip();
      } else {
        // monkey just shouts a number
        this.result = Long.parseLong(inOperation.strip());
        this.hasResult = true;
        this.param1Name = null;
        this.param2Name = null;
        this.function = null;
        this.revFunctionGet1 = null;
        this.revFunctionGet2 = null;
      }
    }

    /**
     * Tries to calculate the result of the operation. If the result is calculated
     * successfully, calculates all which was dependent on this calculation.
     */
    public void tryToCalculate() {
      if (!this.hasResult) {
        // if has no result yet, try to calculate it

        final Monkey param1Monkey = MONKEYS.get(this.param1Name);

        // if param 1 monkey is unknown or has no calculated value, current monkey
        // depends on it
        if (param1Monkey == null || !param1Monkey.hasResult) {
          if (!DEPENDS_ON.containsKey(this.param1Name)) {
            DEPENDS_ON.put(this.param1Name, new ArrayList<>());
          }
          DEPENDS_ON.get(this.param1Name).add(this.name);
        }

        final Monkey param2Monkey = MONKEYS.get(this.param2Name);
        // if param 1 monkey is unknown or has no calculated value, current monkey
        // depends on it
        if (param2Monkey == null || !param2Monkey.hasResult) {
          if (!DEPENDS_ON.containsKey(this.param2Name)) {
            DEPENDS_ON.put(this.param2Name, new ArrayList<>());
          }
          DEPENDS_ON.get(this.param2Name).add(this.name);
        }

        // if both parameter is known and have value, current result can be
        // calculated
        if (param1Monkey != null && param1Monkey.hasResult && param2Monkey != null
            && param2Monkey.hasResult) {
          this.result =
              this.function.apply(param1Monkey.result, param2Monkey.result);
          this.hasResult = true;
        }

      }

      // if current result is already calculated
      if (this.hasResult && DEPENDS_ON.containsKey(this.name)) {
        // for each monkey that was dependent on this calculation, try to calculate
        // their value
        DEPENDS_ON.get(this.name)
            .forEach(mName -> MONKEYS.get(mName).tryToCalculate());
        DEPENDS_ON.remove(this.name);
      }
    }


    /**
     * Tries to calculate a parameter value given an input and the other parameter.
     * Used for Part 2
     */
    protected void tryToRevCalculate() {
      // If monkey has no operation, nothing to do
      if (this.param1Name == null && this.param2Name == null) {
        return;
      }

      // otherwise, get the parameter monkeys
      final Monkey parameter1Monkey = MONKEYS.get(this.param1Name);
      final Monkey parameter2Monkey = MONKEYS.get(this.param2Name);


      if (parameter1Monkey.result == null && parameter2Monkey.result != null) {
        // if param 1 result is unknown, param 2 result is known, calculate param 1
        // value
        parameter1Monkey.result =
            this.revFunctionGet1.apply(this.result, parameter2Monkey.result);
        // try to go back from newly known monkey value
        parameter1Monkey.tryToRevCalculate();
      } else if (parameter2Monkey.result == null
          && parameter1Monkey.result != null) {
        // if param 2 result is unknown, param 3 result is known, calculate param 2
        // value
        parameter2Monkey.result =
            this.revFunctionGet2.apply(this.result, parameter1Monkey.result);
        // try to go back from newly known monkey value
        parameter2Monkey.tryToRevCalculate();
      }
    }

  }

  public static void main(final String[] str) throws IOException {
    String line;
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
      // create monkey
      final Monkey monkey = new Monkey(splL[0], splL[1]);
      // put them to Map
      MONKEYS.put(monkey.name, monkey);
      // try to calculate value
      monkey.tryToCalculate();
    });
    System.out.println("Result for Part 1: " + MONKEYS.get("root").result);

    // reset everything
    MONKEYS.clear();

    // part 2
    lines.forEach(l -> {
      final String[] splL = l.split(":");
      final Monkey monkey = new Monkey(splL[0], splL[1]);

      if ("humn".equals(monkey.name)) {
        // treat human monkey with unknown value
        monkey.result = null;
        monkey.hasResult = false;
      }
      MONKEYS.put(monkey.name, monkey);

      // still try to calculate cvalues
      monkey.tryToCalculate();
    });

    // after trying to calculate all values but with an empty humn value, many values
    // will be unknown even after running all calculations
    final Monkey root = MONKEYS.get("root");

    // we realised that at least one of the parameters has a value, thanks to the
    // kind advent-of-code staff
    // so we just make the other parameter's value to equal to it, and then try to
    // reverse back everything

    final Monkey rootIn1 = MONKEYS.get(root.param1Name);
    final Monkey rootIn2 = MONKEYS.get(root.param2Name);

    if (rootIn1.result == null) {
      rootIn1.result = rootIn2.result;
    }
    if (rootIn2.result == null) {
      rootIn2.result = rootIn1.result;
    }

    rootIn1.tryToRevCalculate();
    rootIn2.tryToRevCalculate();

    System.out.println("Solution for Part 2: " + MONKEYS.get("humn").result);
  }

}
