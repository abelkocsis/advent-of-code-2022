import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sol16 {

  static Map<String, Valve> valves = new HashMap<>();

  static Set<String> openedValves = new HashSet<>();

  static int ALL_MINS = 30;

  static Map<String, Map<String, Integer>> distances;

  private static class Valve {
    String name;
    int flowRate;
    List<String> tunnelsTo = new ArrayList<>();
    int index;

    Valve(final String name, final String line) {
      this.name = name;
      String[] splittedString;
      if (line.contains(",")) {
        splittedString = line.substring("has flow rate=".length())
            .split("; tunnels lead to valves ");
      } else {
        splittedString = line.substring("has flow rate=".length())
            .split("; tunnel leads to valve ");
      }


      this.flowRate = Integer.parseInt(splittedString[0]);

      for (final String valveName : splittedString[1].split(",")) {
        this.tunnelsTo.add(valveName.strip());
      }

      if (this.flowRate == 0) {
        openedValves.add(this.name);
      }
    }

    void calculateDistFrom(final String from, final int distance) {

      if (distances.get(from).get(this.name) > distance) {
        distances.get(from).put(this.name, distance);
        this.tunnelsTo.stream().forEach(
            valve -> valves.get(valve).calculateDistFrom(from, distance + 1));
      }
    }
  }

  public static void main(final String[] args) {
    String line;
    final int i = 0;
    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in16.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        parseLine(line, i);
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }
    distances = new HashMap<>();
    System.out.println(valves.size());

    valves.values().stream().forEach(valve1 -> {
      distances.put(valve1.name, new HashMap<>());
      valves.values().stream().forEach(valve2 -> {
        distances.get(valve1.name).put(valve2.name, Integer.MAX_VALUE);
      });
    });

    valves.values().stream()
        .forEach(valve -> valve.calculateDistFrom(valve.name, 0));

    valves.values().stream()
        .filter(valve -> valve.flowRate == 0 && !"AA".equals(valve.name))
        .forEach(valveToDelete -> {
          distances.remove(valveToDelete.name);
          valves.values().stream().filter(valve -> distances.containsKey(valve.name))
              .forEach(valve -> {
                distances.get(valve.name).remove(valveToDelete.name);
                valve.tunnelsTo.remove(valveToDelete.name);
              });
        });

    // System.out.println(simulate("AA", 0, 0));
    System.out.println(simulatePart2("AA", "AA", 4, 4, 0));

  }

  static void parseLine(final String line, final int i) {
    final String valveName =
        line.substring("Valve ".length(), "Valve ".length() + 2);
    valves.put(valveName,
        new Valve(valveName, line.substring("Valve ".length() + 3)));
  }

  static int simulate(final String cuurentValveName, final int mins,
      final int releasedPres) {

    if (mins >= ALL_MINS || openedValves.size() == valves.size()) {
      return releasedPres;
    }

    final Valve currentvalve = valves.get(cuurentValveName);

    int timeAfterOpened;
    int releasedPresSum;
    // open this valve if not AA
    if ("AA".equals(cuurentValveName)) {
      timeAfterOpened = mins;
      releasedPresSum = releasedPres;
    } else {
      timeAfterOpened = mins + 1;
      openedValves.add(cuurentValveName);
      releasedPresSum =
          releasedPres + (ALL_MINS - timeAfterOpened) * currentvalve.flowRate;
    }


    // TODO add another filter for advanced logic

    final int max = distances.get(cuurentValveName).keySet().stream()
        .filter(nextValve -> !openedValves.contains(nextValve))
        .mapToInt(nextValve -> {
          final int val = simulate(nextValve,
              timeAfterOpened + distances.get(cuurentValveName).get(nextValve),
              releasedPresSum);

          // System.out.println("After valve " + currentvalve.name + ", if valve "
          // + nextValve + " is chose, the overall released pressure is " + val);
          return val;
        }).max().orElse(releasedPresSum);

    openedValves.remove(cuurentValveName);
    return max;
  }

  static int simulatePart2(final String currVal1Name, final String currval2Name,
      final int mins1, final int mins2, final int releasedPres) {

    final boolean p1Running = mins1 < ALL_MINS;
    final boolean p2Running = mins2 < ALL_MINS;

    if ((!p1Running && !p2Running) || openedValves.size() == valves.size()) {
      return releasedPres;
    }

    final Valve currValv1 = valves.get(currVal1Name);
    final Valve currValv2 = valves.get(currval2Name);

    int timeAfterOpened1 = mins1;
    int timeAfterOpened2 = mins2;
    int releasedPresSum = releasedPres;
    // open this valve if not AA
    if ("AA".equals(currVal1Name)) {
      timeAfterOpened1 = mins1;
      timeAfterOpened2 = mins2;
      releasedPresSum = releasedPres;
    } else {

      if (p1Running) {
        timeAfterOpened1 = mins1 + 1;
        openedValves.add(currVal1Name);
        releasedPresSum += (ALL_MINS - timeAfterOpened1) * currValv1.flowRate;

      }
      if (p2Running) {
        timeAfterOpened2 = mins2 + 1;
        openedValves.add(currval2Name);
        releasedPresSum += (ALL_MINS - timeAfterOpened2) * currValv2.flowRate;
      }

    }


    // TODO add another filter for advanced logic

    int counter = releasedPresSum;
    final int releaseSumFin = releasedPresSum;
    final int timeAfterOpened1Fin = timeAfterOpened1;
    final int timeAfterOpened2Fin = timeAfterOpened2;

    if (p1Running) {
      counter = distances.get(currVal1Name).keySet().stream()
          .filter(nextValve1 -> !openedValves.contains(nextValve1))
          .mapToInt(nextValve1 -> {

            if (p2Running) {
              return distances.get(currval2Name).keySet().stream()
                  .filter(nextValve2 -> !nextValve2.equals(nextValve1)
                      && !openedValves.contains(nextValve2))
                  .mapToInt(nextValve2 -> simulatePart2(nextValve1, nextValve2,
                      timeAfterOpened1Fin
                          + distances.get(currVal1Name).get(nextValve1),
                      timeAfterOpened2Fin
                          + distances.get(currval2Name).get(nextValve2),
                      releaseSumFin))
                  .max().orElse(releaseSumFin);
            } else {
              return simulatePart2(nextValve1, currval2Name,
                  timeAfterOpened1Fin + distances.get(currVal1Name).get(nextValve1),
                  timeAfterOpened2Fin, releaseSumFin);
            }


            // System.out.println("After valve " + currentvalve.name + ", if valve
            // "
            // + nextValve + " is chose, the overall released pressure is " + val);

          }).max().orElse(releasedPresSum);
    } else if (p2Running) {
      counter = distances.get(currval2Name).keySet().stream()
          .filter(nextValve2 -> !openedValves.contains(nextValve2))
          .mapToInt(nextValve2 -> {
            return simulatePart2(currVal1Name, nextValve2, timeAfterOpened1Fin,
                timeAfterOpened2Fin + distances.get(currval2Name).get(nextValve2),
                releaseSumFin);
            // System.out.println("After valve " + currentvalve.name + ", if valve
            // "
            // + nextValve + " is chose, the overall released pressure is " + val);

          }).max().orElse(releaseSumFin);
    }


    if (p1Running) {
      openedValves.remove(currVal1Name);
    }
    if (p2Running) {
      openedValves.remove(currval2Name);
    }

    return counter;
  }

}
