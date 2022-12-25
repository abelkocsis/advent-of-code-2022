import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sol19 {

  /** Temporary max values for a particular print, for any given time */
  static public final List<Integer> maxes =
      new ArrayList<>(Arrays.asList(new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

  /**
   * Print object
   */
  private static class Print {

    /** Index of print */
    private final int index;

    /** Required ore number for one ore robot */
    private final int oreForOreRobot;

    /** Required ore number for one clay robot */
    private final int oreForClayRobot;

    /** Required ore number for one obsidian robot */
    private final int oreForObsRobot;

    /** Required clay number for one obsidian robot */
    private final int clayForObsRobot;

    /** Required ore number for one geode robot robot */
    private final int oreForGeoRobot;

    /** Required obsidian number for one geode robot */
    private final int obsForGeoRobot;

    /**
     * Parses a print from line
     *
     * @param line
     */
    private Print(final String line) {
      final int firstPlace = line.indexOf(':');
      this.index =
          Integer.parseInt(line.substring("Blueprint ".length(), firstPlace));

      final String[] splitted = line.substring(firstPlace).split("[.]");

      this.oreForOreRobot =
          Integer.parseInt(splitted[0].substring(": Each ore robot costs ".length(),
              ": Each ore robot costs ".length() + 1));

      this.oreForClayRobot = Integer
          .parseInt(splitted[1].substring(" Each clay robot costs".length() + 1,
              " Each clay robot costs".length() + 2));

      final String obsData =
          splitted[2].substring(" Each obsidian robot costs ".length()).strip();
      final String[] obsDataSpl = obsData.split(" ore and ");
      this.oreForObsRobot = Integer.parseInt(obsDataSpl[0]);
      this.clayForObsRobot = Integer.parseInt(
          obsDataSpl[1].substring(0, obsDataSpl[1].length() - " clay".length()));

      final String geodData =
          splitted[3].substring(" Each geode robot costs ".length()).strip();
      final String[] geodDataSpl = geodData.split(" ore and ");
      this.oreForGeoRobot = Integer.parseInt(geodDataSpl[0]);
      this.obsForGeoRobot = Integer.parseInt(geodDataSpl[1].substring(0,
          geodDataSpl[1].length() - "obsidian.".length()));

    }

    @Override
    public String toString() {
      return "(" + this.index + "," + this.oreForOreRobot + ","
          + this.oreForClayRobot + "," + this.oreForObsRobot + ","
          + this.clayForObsRobot + "," + this.oreForGeoRobot + ","
          + this.obsForGeoRobot + ")";
    }
  }

  /**
   * Status of simulation
   */
  private static class Status {
    /** Number of ores */
    private int ores = 0;

    /** Number of clays */
    private int clays = 0;

    /** Number of obsidians */
    private int obsidians = 0;

    /** Number of geodes */
    private int geodes = 0;

    /** Number of ore robots */
    private int oreRobots = 1;

    /** Number of clay robots */
    private int clayRobots = 0;

    /** Number of obsidian robots */
    private int obsidianRobots = 0;

    /** Number of geode robots */
    private int geodeRobots = 0;

    /**
     * Gets next status
     *
     * @return
     */
    Status getNextStatus() {
      final Status next = new Status();
      next.ores = this.ores + this.oreRobots;
      next.clays = this.clays + this.clayRobots;
      next.obsidians = this.obsidians + this.obsidianRobots;
      next.geodes = this.geodes + this.geodeRobots;

      next.oreRobots = this.oreRobots;
      next.clayRobots = this.clayRobots;
      next.obsidianRobots = this.obsidianRobots;
      next.geodeRobots = this.geodeRobots;

      return next;
    }

  }

  public static void main(final String[] args) {

    String line;
    final List<String> lines = new ArrayList<>();

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in19.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        lines.add(line);
      }
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    // Part 1
    int count = 0;
    for (final String l : lines) {
      for (int i = 0; i < 32; i++) {
        maxes.set(i, -1);
      }
      final Print print = new Print(l);
      final Status status = new Status();
      final int simRes = simulate(24, print, 0, status);
      count += simRes * print.index;
    }
    System.out.println("Solution for Part 1: " + count);

    // Part 2
    count = 1;
    for (int lineI = 0; lineI < 3; lineI++) {
      for (int i = 0; i < 32; i++) {
        maxes.set(i, -1);
      }
      final String l = lines.get(lineI);
      final Print print = new Print(l);
      final Status status = new Status();
      final int simRes = simulate(32, print, 0, status);
      count *= simRes;
    }
    System.out.println("Solution for Part 2: " + count);
  }

  /**
   * Simulates rock collecting
   *
   * @param maxtime Maximum time to work
   * @param print Current print
   * @param time Current time
   * @param status Current status
   * @return Maximum number of geodes which can be collected
   */
  public static int simulate(final int maxtime, final Print print, final int time,
      final Status status) {
    if (time == maxtime - 1) {
      // if maxtime reached, return result
      final int res = status.getNextStatus().geodes;
      if (res > maxes.get(time - 1)) {
        maxes.set(time - 1, res);
      }
      return res;
    }

    // estimate maximum values for each rock type from position

    final int maxOresFromHere = getMaxOres(print, status, time, maxtime);
    final int firstEnougOresForClayRobot =
        getFirstEnoughOresForClayRobot(print, status, time, maxtime);

    final int maxClaysFromHere = getMaxClays(print, status, maxOresFromHere,
        firstEnougOresForClayRobot, time, maxtime);
    final int firstEnougClaysForObsRobotEst = getFirstEnoughClaysForObsRobots(print,
        status, maxOresFromHere, firstEnougOresForClayRobot, time, maxtime);

    final int maxObsidiansFromHere = getMaxObsidians(print, status, maxOresFromHere,
        maxClaysFromHere, firstEnougClaysForObsRobotEst, time, maxtime);
    final int firstEnougObsForGeod =
        getFirstEnoughObsForGeoRobots(print, status, maxOresFromHere,
            maxClaysFromHere, firstEnougClaysForObsRobotEst, time, maxtime);

    final int maxGeodesFromHere = getMaxGeodes(print, status, maxOresFromHere,
        maxObsidiansFromHere, firstEnougObsForGeod, time, maxtime);

    if (maxGeodesFromHere <= maxes.get(time)
        || maxGeodesFromHere <= maxes.get(maxtime)) {
      // if cannot get more from here, then kill fork
      return -1;
    }

    // if can buy a geode robot, buy one, that's all.
    if (status.ores >= print.oreForGeoRobot
        && status.obsidians >= print.obsForGeoRobot) {

      status.ores -= print.oreForGeoRobot;
      status.obsidians -= print.obsForGeoRobot;

      final Status nextStatus = status.getNextStatus();
      nextStatus.geodeRobots++;
      final int result = simulate(maxtime, print, time + 1, nextStatus);
      if (result > maxes.get(time)) {
        maxes.set(time, result);
      }
      return result;

    } else {
      // else, try every scenarios
      int max = -1;
      Status nextStatus;
      if (time < maxtime - print.oreForOreRobot
          && status.ores >= print.oreForObsRobot
          && status.clays >= print.clayForObsRobot) {
        // buy obsidian robot
        nextStatus = status.getNextStatus();
        nextStatus.ores -= print.oreForObsRobot;
        nextStatus.clays -= print.clayForObsRobot;
        nextStatus.obsidianRobots++;
        final int result = simulate(maxtime, print, time + 1, nextStatus);
        if (result > max) {
          max = result;
        }
      }
      if (status.ores >= print.oreForClayRobot) {
        // buy clay robot
        nextStatus = status.getNextStatus();
        nextStatus.ores -= print.oreForClayRobot;
        nextStatus.clayRobots++;
        final int result = simulate(maxtime, print, time + 1, nextStatus);
        if (result > max) {
          max = result;
        }
      }
      if (status.ores >= print.oreForOreRobot) {
        // buy clay robot
        nextStatus = status.getNextStatus();
        nextStatus.ores -= print.oreForOreRobot;
        nextStatus.oreRobots++;
        final int result = simulate(maxtime, print, time + 1, nextStatus);
        if (result > max) {
          max = result;
        }
      }
      // buy nothing
      final int result = simulate(maxtime, print, time + 1, status.getNextStatus());
      if (result > max) {
        max = result;
      }

      if (max > maxes.get(time)) {
        maxes.set(time, max);
      }
      return max;

    }
  }

  /**
   * Estimate when there would be enough clays for an obsidian robot. Doesn't care
   * about building robots, just tries to do a rough estimation.
   *
   * @param print
   * @param status
   * @param maxOres
   * @param firstEnougOresForClayRobot
   * @param time
   * @param maxtime
   * @return
   */
  private static int getFirstEnoughClaysForObsRobots(final Print print,
      final Status status, final int maxOres, final int firstEnougOresForClayRobot,
      final int time, final int maxtime) {
    int clayTemp = status.clays;
    int clayRobotsTemp = status.clayRobots;
    int oreTemp = maxOres;

    for (int t = time; t <= maxtime; t++) {
      if (clayTemp >= print.clayForObsRobot) {
        // we have enough clays for an obsidian robots
        return t;
      }

      if (t >= firstEnougOresForClayRobot && oreTemp >= print.oreForClayRobot) {
        // let's build a clay robot. Clay doesn't cost clay, so we'free to spend
        oreTemp -= print.oreForClayRobot;
        clayTemp += clayRobotsTemp;
        clayRobotsTemp++;
      } else {
        // add clays
        clayTemp += clayRobotsTemp;
      }

    }
    return Integer.MAX_VALUE;
  }

  /**
   * Estimate when there would be enough obsidians for a geode robot. Doesn't care
   * about building robots, just tries to do a rough estimation.
   *
   * @param print
   * @param status
   * @param maxOres
   * @param maxClays
   * @param firstEnougClaysForObsRobotEst
   * @param time
   * @param maxtime
   * @return
   */
  private static int getFirstEnoughObsForGeoRobots(final Print print,
      final Status status, final int maxOres, final int maxClays,
      final int firstEnougClaysForObsRobotEst, final int time, final int maxtime) {
    int oreTemp = maxOres;
    int clayTemp = maxClays;
    int obsRobotsTemp = status.obsidianRobots;
    int obsTemp = status.obsidians;
    for (int t = time; t <= maxtime; t++) {
      if (obsTemp >= print.obsForGeoRobot) {
        // we have enough obsidiands for a geode robot
        return t;
      }
      if (t >= firstEnougClaysForObsRobotEst && oreTemp >= print.oreForObsRobot
          && clayTemp >= print.clayForObsRobot) {
        // we can possibly build another obsidian robot
        oreTemp -= print.oreForObsRobot;
        clayTemp -= print.clayForObsRobot;
        obsTemp += obsRobotsTemp;
        obsRobotsTemp++;
      } else {
        // just add obsidians
        obsTemp += obsRobotsTemp;
      }

    }
    return Integer.MAX_VALUE;
  }

  /**
   * Estimate when there would be enough ores for a clay robot. Doesn't care about
   * building robots, just tries to do a rough estimation.
   *
   * @param print
   * @param status
   * @param time
   * @param maxtime
   * @return
   */
  private static int getFirstEnoughOresForClayRobot(final Print print,
      final Status status, final int time, final int maxtime) {
    int oreTemp = status.ores;
    for (int t = time; t <= maxtime; t++) {
      if (oreTemp >= print.oreForClayRobot) {
        // we have enough ores for a clay robot
        return t;
      }

      if (oreTemp >= print.oreForOreRobot) {
        // A new ore robot would cost us ore. So in order to estimate the first time
        // we could build a clay robot, we're not sure whether we should build
        // another
        // ore robot or not as building an ore robot would cost ore. For this
        // estimation, let's just say we could build a new clay robot in the next
        // round, even if it's not true. It's good for estimation.
        return t + 1;
      } else {
        oreTemp += status.oreRobots;
      }
    }
    return Integer.MAX_VALUE;
  }

  /**
   * Estimate maximum number of clays from a given time. Doesn't care about building
   * robots, just tries to do a rough estimation.
   *
   * @param print
   * @param status
   * @param maxOres
   * @param firstEnougOresForClayRobot
   * @param time
   * @param maxtime
   * @return
   */
  private static int getMaxClays(final Print print, final Status status,
      final int maxOres, final int firstEnougOresForClayRobot, final int time,
      final int maxtime) {
    int clayTemp = status.clays;
    int clayTobotsTemp = status.clayRobots;
    int oreTemp = maxOres;

    for (int t = time; t <= maxtime; t++) {
      if (t >= firstEnougOresForClayRobot && oreTemp >= print.oreForClayRobot) {
        // A new clay robot could be build
        oreTemp -= print.oreForClayRobot;
        clayTemp += clayTobotsTemp;
        clayTobotsTemp++;
      } else {
        clayTemp += clayTobotsTemp;
      }

    }
    return clayTemp;
  }

  /**
   * Estimate maximum number of geodes from a given time. Doesn't care about building
   * robots, just tries to do a rough estimation.
   *
   * @param print
   * @param status
   * @param maxOresFromHere
   * @param maxObsidiansFromHere
   * @param firstEnougObsForGeod
   * @param time
   * @param maxtime
   * @return
   */
  private static int getMaxGeodes(final Print print, final Status status,
      final int maxOresFromHere, final int maxObsidiansFromHere,
      final int firstEnougObsForGeod, final int time, final int maxtime) {

    // max geodes with currently available robots
    int maxGeodesFromHere =
        status.geodes + status.geodeRobots * (maxtime - time + 1);

    int oreTemp = maxOresFromHere;
    int obsTemp = maxObsidiansFromHere;
    for (int t = firstEnougObsForGeod; t < maxtime && oreTemp >= print.oreForGeoRobot
        && obsTemp >= print.obsForGeoRobot; t++) {
      // try to spend all maximum ores and obsidiands for new geode robots
      maxGeodesFromHere += maxtime - t;
      oreTemp -= print.oreForGeoRobot;
      obsTemp -= print.obsForGeoRobot;
    }
    return maxGeodesFromHere;
  }

  /**
   * Estimate maximum number of obsidians from a given time. Doesn't care about
   * building robots, just tries to do a rough estimation.
   *
   * @param print
   * @param status
   * @param maxOres
   * @param maxClays
   * @param firstEnougClaysForObsRobotEst
   * @param time
   * @param maxtime
   * @return
   */
  private static int getMaxObsidians(final Print print, final Status status,
      final int maxOres, final int maxClays, final int firstEnougClaysForObsRobotEst,
      final int time, final int maxtime) {
    int oreTemp = maxOres;
    int clayTemp = maxClays;
    int obsRobotsTemp = status.obsidianRobots;
    int obsTemp = status.obsidians;
    for (int t = time; t <= maxtime; t++) {
      if (t >= firstEnougClaysForObsRobotEst && oreTemp >= print.oreForObsRobot
          && clayTemp >= print.clayForObsRobot) {
        oreTemp -= print.oreForObsRobot;
        clayTemp -= print.clayForObsRobot;
        obsTemp += obsRobotsTemp;
        obsRobotsTemp++;
      } else {
        obsTemp += obsRobotsTemp;
      }

    }
    return obsTemp;
  }

  /**
   * Estimate maximum number of ores from a given time. Doesn't care about building
   * robots, just tries to do a rough estimation.
   *
   * @param print
   * @param status
   * @param time
   * @param maxtime
   * @return
   */
  private static int getMaxOres(final Print print, final Status status,
      final int time, final int maxtime) {
    int oreTemp = status.ores;
    int oreRobotsTemp = status.oreRobots;
    for (int t = time; t <= maxtime; t++) {
      if (t < maxtime - print.oreForOreRobot && oreTemp >= print.oreForOreRobot) {
        oreTemp -= print.oreForOreRobot;
        oreTemp += oreRobotsTemp;
        oreRobotsTemp++;
      } else {
        oreTemp += oreRobotsTemp;
      }
    }
    return oreTemp;
  }
}
