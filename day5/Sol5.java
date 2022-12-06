import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sol5 {

  /** List of commands */
  private static final List<Command> COMMANDS = new ArrayList<>(100);

  /**
   * Commands, parsed from the rearranging procedure
   */
  private static class Command {

    /** Number of crates to rearrange */
    /* default */ final transient int number;

    /** From stack id */
    /* default */ final transient int from;

    /** To stack id */
    /* default */ final transient int to;

    /**
     * Command constructor, parses commandStr
     *
     * @param commandStr As specified in the task description
     */
    /* default */ Command(final String commandStr) {
      final String[] splittedCommand = commandStr.split(" ");
      this.number = Integer.parseInt(splittedCommand[1]);
      this.from = Integer.parseInt(splittedCommand[3]);
      this.to = Integer.parseInt(splittedCommand[5]);
    }
  }

  public static void main(final String[] str) {
    String line;

    boolean stackParsing = true; // is stack parsing being in progress?

    // instead of deep copying the original stack, save input to two lists and do the
    // rearranging inside them
    final List<LinkedList<String>> stacksP1 = new ArrayList<>();
    final List<LinkedList<String>> stacksP2 = new ArrayList<>();

    // initialise lists
    for (int i = 0; i < 10; i++) {
      stacksP1.add(new LinkedList<String>());
      stacksP2.add(new LinkedList<String>());
    }

    // read inputs
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in5.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {

        if (stackParsing) {
          if (line.charAt(1) == '1') {
            stackParsing = false;
            continue;
          }

          for (int i = 1; i < line.length(); i += 4) {
            if (line.charAt(i) != ' ') {
              stacksP1.get((i + 3) / 4).addFirst(line.substring(i, i + 1));
              stacksP2.get((i + 3) / 4).addFirst(line.substring(i, i + 1));
            }
          }
        } else if (!line.isEmpty()) {
          COMMANDS.add(new Command(line));
        }
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    runCommandsP1(stacksP1);
    System.out.println("Solution for Part 1: " + stacksP1.stream()
        // filter empty stacks, we assume only stack 0 is empty, which we didn't use
        .filter(stack -> !stack.isEmpty())
        // get element from the top (last element)
        .map(stack -> stack.getLast())
        // parse it as a string
        .collect(Collectors.toList()).toString()
        // delete unnecessary characters
        .replaceAll("[,\\[\\]\\ ]", ""));

    runCommandsP2(stacksP2);
    System.out.println("Solution for Part 2: " + stacksP2.stream()
        // filter empty stacks, we assume only stack 0 is empty, which we didn't use
        .filter(stack -> !stack.isEmpty())
        // get element from the top (last element)
        .map(stack -> stack.getLast())
        // parse it as a string
        .collect(Collectors.toList()).toString()
        // delete unnecessary characters
        .replaceAll("[,\\[\\]\\ ]", ""));
  }

  /**
   * Runs commands according to Part 1. <br>
   * The result is saved into the original list
   *
   * @param stacks
   */
  private static void runCommandsP1(final List<LinkedList<String>> stacks) {
    // for each command
    COMMANDS.forEach(command -> {
      // for command.number times
      IntStream.range(0, command.number).forEach(
          // remove to crate from from stack, add it to to stack
          i -> stacks.get(command.to).add(stacks.get(command.from).removeLast()));
    });
  }

  /**
   * Runs commands according to Part 2 <br>
   * The result is saved into the original list
   *
   * @param stacks
   */
  private static void runCommandsP2(final List<LinkedList<String>> stacks) {
    COMMANDS.forEach(command -> {

      // size of from stack
      final int fromSize = stacks.get(command.from).size();

      // first crate id we need to move. Assumed it is greater or equal to zero.
      final int fromRemoveStart = stacks.get(command.from).size() - command.number;

      // add moved crates to to stack
      stacks.get(command.to)
          .addAll(stacks.get(command.from).subList(fromRemoveStart, fromSize));

      // removed moved crates from from stack
      IntStream.range(0, command.number)
          .forEach(i -> stacks.get(command.from).removeLast());
    });
  }
}
