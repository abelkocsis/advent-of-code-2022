import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sol5 {

    private static class Command {
        int number;
        int from;
        int to;

        Command(String commandStr) {
            String[] splittedCommand = commandStr.split(" ");
            number = Integer.parseInt(splittedCommand[1]);
            from = Integer.parseInt(splittedCommand[3]);
            to = Integer.parseInt(splittedCommand[5]);
        }
    }

    private static final List<Command> commands = new ArrayList<>(100);

    public static void main(String[] str) {
        String line;

        boolean areStacksBeingParsed = true;

        final List<LinkedList<String>> stacksP1 = new ArrayList<>();
        final List<LinkedList<String>> stacksP2 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            stacksP1.add(new LinkedList<String>());
            stacksP2.add(new LinkedList<String>());
        }

        try (BufferedReader br = new BufferedReader(new FileReader("in5.txt"))) {
            while ((line = br.readLine()) != null) {

                if (areStacksBeingParsed) {
                    if (line.charAt(1) == '1') {
                        areStacksBeingParsed = false;
                        continue;
                    }

                    for (int i = 1; i < line.length(); i += 4) {
                        if (line.charAt(i) != ' ') {
                            stacksP1.get((i + 3) / 4).addFirst(line.substring(i, i + 1));
                            stacksP2.get((i + 3) / 4).addFirst(line.substring(i, i + 1));
                        }
                    }
                } else {
                    if (!line.isEmpty()) {
                        commands.add(new Command(line));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("IOException in try block =>" + e.getMessage());
        }

        runCommandsP1(stacksP1);
        System.out.println("Solution for Part 1: " + stacksP1.stream().filter(stack -> !stack.isEmpty())
                .map(stack -> stack.getLast()).collect(Collectors.toList()).toString().replaceAll("[,\\[\\]\\ ]", ""));

        runCommandsP2(stacksP2);
        System.out.println("Solution for Part 2: " + stacksP2.stream().filter(stack -> !stack.isEmpty())
                .map(stack -> stack.getLast()).collect(Collectors.toList()).toString().replaceAll("[,\\[\\]\\ ]", ""));
    }

    private static void runCommandsP1(final List<LinkedList<String>> stacks) {
        commands.forEach(command -> {
            IntStream.range(0, command.number).forEach(i -> {
                stacks.get(command.to).add(stacks.get(command.from).removeLast());
            });
        });
    }

    private static void runCommandsP2(final List<LinkedList<String>> stacks) {
        commands.forEach(command -> {
            final int fromSize = stacks.get(command.from).size();
            final int fromRemoveStart = stacks.get(command.from).size() - command.number;
            stacks.get(command.to).addAll(stacks.get(command.from).subList(fromRemoveStart, fromSize));

            IntStream.range(0, command.number).forEach(i -> stacks.get(command.from).removeLast());
        });
    }
}
