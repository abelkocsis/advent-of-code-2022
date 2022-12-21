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

  static Map<String, Monkey> monkeys = new HashMap<>();
  static Map<String, List<String>> dependentOn = new HashMap<>();

  static class Monkey {
    boolean hasResult = false;
    String name;
    Long result = null;
    String in1 = null;
    String in2 = null;
    BiFunction<Long, Long, Long> function;
    BiFunction<Long, Long, Long> revFunctionGet1;
    BiFunction<Long, Long, Long> revFunctionGet2;

    Monkey(final String name, final String inOperation) {
      this.name = name;
      if (inOperation.contains("+")) {
        this.function = (a, b) -> a + b;
        this.revFunctionGet1 = (a, b) -> a - b;
        this.revFunctionGet2 = (a, b) -> a - b;
        final String[] splittedOp = inOperation.split("[+]");
        this.in1 = splittedOp[0].strip();
        this.in2 = splittedOp[1].strip();
      } else if (inOperation.contains("-")) {
        this.function = (a, b) -> a - b;
        this.revFunctionGet1 = (a, b) -> a + b;
        this.revFunctionGet2 = (a, b) -> b - a;
        final String[] splittedOp = inOperation.split("[-]");
        this.in1 = splittedOp[0].strip();
        this.in2 = splittedOp[1].strip();
      } else if (inOperation.contains("*")) {
        this.function = (a, b) -> a * b;
        this.revFunctionGet1 = (a, b) -> a / b;
        this.revFunctionGet2 = (a, b) -> a / b;
        final String[] splittedOp = inOperation.split("[*]");
        this.in1 = splittedOp[0].strip();
        this.in2 = splittedOp[1].strip();
      } else if (inOperation.contains("/")) {
        this.function = (a, b) -> a / b;
        this.revFunctionGet1 = (a, b) -> a * b;
        this.revFunctionGet2 = (a, b) -> b / a;
        final String[] splittedOp = inOperation.split("[/]");
        this.in1 = splittedOp[0].strip();
        this.in2 = splittedOp[1].strip();
      } else {
        this.result = Long.parseLong(inOperation.strip());
        this.hasResult = true;
      }
    }

    void tryToCalculate() {
      // for part 2 only
      if ("humn".equals(this.name)) {
        return;
      }


      if (this.hasResult) {
        // nothing to do
      } else {
        final Monkey in1M = monkeys.get(this.in1);
        if (in1M == null || !in1M.hasResult) {
          if (!dependentOn.containsKey(this.in1)) {
            dependentOn.put(this.in1, new ArrayList<>());
          }
          dependentOn.get(this.in1).add(this.name);
        }
        final Monkey in2M = monkeys.get(this.in2);
        if (in2M == null || !in2M.hasResult) {
          if (!dependentOn.containsKey(this.in2)) {
            dependentOn.put(this.in2, new ArrayList<>());
          }
          dependentOn.get(this.in2).add(this.name);
        }
        if (in1M != null && in1M.hasResult && in2M != null && in2M.hasResult) {
          this.result = this.function.apply(in1M.result, in2M.result);
          this.hasResult = true;
        }

      }

      if (this.hasResult) {
        if (dependentOn.containsKey(this.name)) {
          dependentOn.get(this.name)
              .forEach(mName -> monkeys.get(mName).tryToCalculate());
          dependentOn.remove(this.name);
        }
      }
    }

    void tryToRevCalculate() {
      if (this.in1 == null && this.in2 == null) {
        return;
      }
      final Monkey in1M = monkeys.get(this.in1);
      final Monkey in2M = monkeys.get(this.in2);

      System.out.println(this.in1);
      System.out.println(in1M);
      if (in1M.result == null && in2M.result != null) {
        in1M.result = this.revFunctionGet1.apply(this.result, in2M.result);
        in1M.tryToRevCalculate();
      } else if (in2M.result == null && in1M.result != null) {
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
      if (m.name.equals("humn")) {
        System.out.println(m.result);
        m.result = null;
        m.hasResult = false;
      }
      monkeys.put(m.name, m);
      m.tryToCalculate();
    });
    System.out.println(monkeys.get("humn").result);
    System.out.println(monkeys.get("root").result);

    monkeys.get("ztbt").result = monkeys.get("jzqh").result;
    monkeys.get("ztbt").tryToRevCalculate();

    System.out.println(monkeys.get("humn").result);

    // System.out.println(monkeys.get("humn").result);

    // humn = -3887609740495
  }

}
