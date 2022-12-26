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
import java.util.stream.Collectors;

public class Sol16 {

  /** Map of name-Valve pairs */
  private final static Map<String, Valve> VALVES = new HashMap<>();

  /** Set of opened valves */
  private static Set<String> openedValves = new HashSet<>();

  /** All minutes to simulate until */
  private static final int ALL_MINS = 30;

  /**
   * Distances map. For each valveNameKey, store a map of valveNameKey1, distance
   * pairs, which stores how far the valves are from each other. We only care about
   * valves which has flow rate bigger than zero.
   */
  private static Map<String, Map<String, Integer>> distances;


  /**
   * Representation of a Valve
   */
  private static class Valve {
    /** Name of valve */
    private final String name;

    /** Flow rate of valve */
    private final int flowRate;

    /** List of valve names which the current valve has tunnels to */
    private final List<String> tunnelsTo = new ArrayList<>();

    /**
     * Creates a valve from input string
     *
     * @param name
     * @param line
     */
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
        // if valve has a flow rate 0, it could be considered as opened
        openedValves.add(this.name);
      }
    }

    /**
     * Calculates distance from valve in parameter to this valve. Saves it to
     * distances list if new distance is smaller than a previously known one
     *
     * @param from
     * @param distance
     */
    void calculateDistFrom(final String from, final int distance) {
      if (distances.get(from).get(this.name) > distance) {
        distances.get(from).put(this.name, distance);
        this.tunnelsTo.stream().forEach(
            valve -> VALVES.get(valve).calculateDistFrom(from, distance + 1));
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

    VALVES.values().stream().forEach(valve1 -> {
      distances.put(valve1.name, new HashMap<>());
      VALVES.values().stream().forEach(valve2 -> {
        distances.get(valve1.name).put(valve2.name, Integer.MAX_VALUE);
      });
    });

    VALVES.values().stream()
        .forEach(valve -> valve.calculateDistFrom(valve.name, 0));

    VALVES.values().stream()
        .filter(valve -> valve.flowRate == 0 && !"AA".equals(valve.name))
        .forEach(valveToDelete -> {
          distances.remove(valveToDelete.name);
          VALVES.values().stream().filter(valve -> distances.containsKey(valve.name))
              .forEach(valve -> {
                distances.get(valve.name).remove(valveToDelete.name);
                valve.tunnelsTo.remove(valveToDelete.name);
              });
        });

    final int p1 = simulateP1("AA", 0, 0);
    System.out.println("Solution for Part 1: " + p1);
    openedValves =
        openedValves.stream().filter(vName -> VALVES.get(vName).flowRate == 0)
            .collect(Collectors.toSet());
    System.gc();
    final int p2 = simulatePart2("AA", "AA", 4, 4, 0);
    System.out.println("Solution for Part 2: " + p2);

  }

  /**
   * Parses a line, saves valve to VALVE map
   *
   * @param line
   * @param i
   */
  private static void parseLine(final String line, final int i) {
    final String valveName =
        line.substring("Valve ".length(), "Valve ".length() + 2);
    VALVES.put(valveName,
        new Valve(valveName, line.substring("Valve ".length() + 3)));
  }

  /**
   * Simulates valve opening for Part 1. Note that we call the simulate function for
   * every scenario, but only once for every opening, thus, we do not call simulate
   * once each step, only when an opening takes place.
   *
   * @param cuurentValveName
   * @param mins
   * @param releasedPres
   * @return Maximum number of water could be released
   */
  private static int simulateP1(final String cuurentValveName, final int mins,
      final int releasedPres) {

    if (mins >= ALL_MINS || openedValves.size() == VALVES.size()) {
      // if time is out or we opened all valves, return the current released pressure
      return releasedPres;
    }

    final Valve currentvalve = VALVES.get(cuurentValveName);

    int timeAfterOpened;
    int releasedPresSum;

    // open this valve if not AA
    if ("AA".equals(cuurentValveName)) {
      // assumption: AA has flow rate 0, so we should never need to open it. We can
      // only be here when starting the simulation.
      timeAfterOpened = mins;
      releasedPresSum = releasedPres;
    } else {
      // otherwise, we came here to open this valve. So, spend 1 minute to open this
      timeAfterOpened = mins + 1;
      openedValves.add(cuurentValveName);
      // we count the overall pressure this valve will release, starting from the
      // next minute, until the end
      releasedPresSum =
          releasedPres + (ALL_MINS - timeAfterOpened) * currentvalve.flowRate;
    }

    final int max =
        // get all valves we could choose to go to open
        distances.get(cuurentValveName).keySet().stream()
            // filter for those which are not opened
            .filter(nextValve -> !openedValves.contains(nextValve))
            // get the maximum value we could get for that valve
            .mapToInt(nextValve -> simulateP1(nextValve,
                timeAfterOpened + distances.get(cuurentValveName).get(nextValve),
                releasedPresSum))
            // get that valve's value which provided the most
            .max().orElse(releasedPresSum);

    // remember to remove current valve from opened valves before stepping back in
    // the recursion
    openedValves.remove(cuurentValveName);
    return max;
  }

  /**
   * Simulates valve opening for Part 2. Note that we call the simulate function for
   * every scenario, but only once for every opening, thus, we do not call simulate
   * once each step, only when an opening takes place.
   *
   * @param currVal1Name Valve name the person is currently at
   * @param currval2Name Valve name the elephant is currently at
   * @param mins1 Mins the person arrived
   * @param mins2 Mins the elephant arrived
   * @param releasedPres
   * @return Maximum number of water could be released
   */
  static int simulatePart2(final String currVal1Name, final String currval2Name,
      final int mins1, final int mins2, final int releasedPres) {

    // note that it could be the case that one of them has finished.
    final boolean p1Running = mins1 < ALL_MINS;
    final boolean p2Running = mins2 < ALL_MINS;

    if ((!p1Running && !p2Running) || openedValves.size() == VALVES.size()) {
      // if both finished or all valves are open, return value
      return releasedPres;
    }

    final Valve currValv1 = VALVES.get(currVal1Name);
    final Valve currValv2 = VALVES.get(currval2Name);

    int timeAfterOpened1 = mins1;
    int timeAfterOpened2 = mins2;
    int releasedPresSum = releasedPres;
    // open this valve if not AA
    if ("AA".equals(currVal1Name)) {
      // assumption: AA has flow rate 0, so we should never need to open it. We can
      // only be here when starting the simulation.
      timeAfterOpened1 = mins1;
      timeAfterOpened2 = mins2;
      releasedPresSum = releasedPres;
    } else {
      // otherwise, open valves since that's why we're here
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
      // if p1 is still running

      counter =
          // get all possible destinations
          distances.get(currVal1Name).keySet().stream()
              // filter for those which are not opened yet
              .filter(nextValve1 -> !openedValves.contains(nextValve1))
              // calculate result for each
              .mapToInt(nextValve1 -> {
                // if p2 is still running
                if (p2Running) {
                  return
                  // for each next valve we could choose for p2
                  distances.get(currval2Name).keySet().stream()
                      // filter for those which are not opened and which are not
                      // chosen for p1
                      .filter(nextValve2 -> !nextValve2.equals(nextValve1)
                          && !openedValves.contains(nextValve2))
                      // for all of them, get simulation result
                      .mapToInt(nextValve2 -> simulatePart2(nextValve1, nextValve2,
                          timeAfterOpened1Fin
                              + distances.get(currVal1Name).get(nextValve1),
                          timeAfterOpened2Fin
                              + distances.get(currval2Name).get(nextValve2),
                          releaseSumFin))
                      // get max of them
                      .max().orElse(releaseSumFin);
                } else {
                  // if p2 not running, just simulate
                  return simulatePart2(nextValve1, currval2Name,
                      timeAfterOpened1Fin
                          + distances.get(currVal1Name).get(nextValve1),
                      timeAfterOpened2Fin, releaseSumFin);
                }
              })
              // then get maximum of them
              .max().orElse(releasedPresSum);
    } else if (p2Running) {
      // if p1 is not running, but p2 is
      // do the same as above, but only for p2
      counter = distances.get(currval2Name).keySet().stream()
          .filter(nextValve2 -> !openedValves.contains(nextValve2))
          .mapToInt(nextValve2 -> {
            return simulatePart2(currVal1Name, nextValve2, timeAfterOpened1Fin,
                timeAfterOpened2Fin + distances.get(currval2Name).get(nextValve2),
                releaseSumFin);
          }).max().orElse(releaseSumFin);
    }


    if (p1Running) {
      // if p1 opened a valve at this step, remove it for the recursion
      openedValves.remove(currVal1Name);
    }
    if (p2Running) {
      // if p2 opened a valve at this step, remove it for the recursion
      openedValves.remove(currval2Name);
    }

    return counter;
  }

}
