import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Sol19 {

  static int maxtime = 32;

  static AtomicInteger threadCount = new AtomicInteger(1);

  static int maxThreads = 100;


  static public final List<Integer> maxes =
      new ArrayList<>(Arrays.asList(new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

  // new ArrayList<>(Arrays.asList(new Integer[] {-1}));

  private static class Print {
    int index;
    int oreForOreRobot;
    int oreForClayRobot;


    int oreForObsRobot;
    int clayForObsRobot;

    int oreForGeoRobot;
    int obsForGeoRobot;

    Print(final String line) {
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

  private static class Status {
    int ores = 0;
    int clays = 0;
    int obsidians = 0;
    int geodes = 0;


    int oreRobots = 1;
    int clayRobots = 0;
    int obsidianRobots = 0;
    int geodeRobots = 0;

    Status getStatusIn(final int timeDelta) {
      final Status next = new Status();
      next.ores = this.ores + this.oreRobots * timeDelta;
      next.clays = this.clays + this.clayRobots * timeDelta;
      next.obsidians = this.obsidians + this.obsidianRobots * timeDelta;
      next.geodes = this.geodes + this.geodeRobots * timeDelta;

      next.oreRobots = this.oreRobots;
      next.clayRobots = this.clayRobots;
      next.obsidianRobots = this.obsidianRobots;
      next.geodeRobots = this.geodeRobots;

      return next;
    }

  }

  public static void main(final String[] args) {
    String line;
    int count = 0;
    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in19.txt"), StandardCharsets.UTF_8)) {
      while ((line = buffR.readLine()) != null) {
        for (int i = 0; i < maxtime; i++) {
          maxes.set(i, -1);
        }
        final Print p = new Print(line);
        final Status s = new Status();
        final int sim = simulate(p, 0, s);
        System.out.println(p.index + ": " + sim);

        count += sim * p.index;
      }
      System.out.println(count);

    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }
  }

  public static int simulate(final Print print, final int time,
      final Status status) {
    if (time == maxtime - 1) {
      final int res = status.getStatusIn(1).geodes;
      if (res > maxes.get(time - 1)) {
        maxes.set(time - 1, res);
      }
      return res;
    }

    int maxOresFromHere = status.ores + status.oreRobots * (maxtime - time + 1);
    int oreTemp = status.ores;
    int oreRobotsTemp = status.oreRobots;
    int firstEnougOresForClayRobotEst = Integer.MAX_VALUE;
    for (int t = time; t <= maxtime; t++) {
      if (firstEnougOresForClayRobotEst == Integer.MAX_VALUE
          && oreTemp >= print.oreForClayRobot) {
        firstEnougOresForClayRobotEst = t;
      }

      if (t < maxtime - print.oreForOreRobot && oreTemp >= print.oreForOreRobot) {
        oreTemp -= print.oreForOreRobot;
        oreTemp += oreRobotsTemp;
        oreRobotsTemp++;

        if (firstEnougOresForClayRobotEst == Integer.MAX_VALUE) {
          firstEnougOresForClayRobotEst = t + 1;
        }
      } else {
        oreTemp += oreRobotsTemp;
      }
    }
    maxOresFromHere = oreTemp;


    int maxClaysFromHere = status.clays + status.clayRobots * (maxtime - time + 1);
    int clayTemp = status.clays;
    int clayTobotsTemp = status.clayRobots;
    int firstEnougClaysForObsRobotEst = Integer.MAX_VALUE;
    oreTemp = maxOresFromHere;

    for (int t = time; t <= maxtime; t++) {
      if (firstEnougClaysForObsRobotEst == Integer.MAX_VALUE
          && clayTemp >= print.clayForObsRobot) {
        firstEnougClaysForObsRobotEst = t;
      }

      if (t >= firstEnougOresForClayRobotEst && oreTemp >= print.oreForClayRobot) {
        oreTemp -= print.oreForClayRobot;
        clayTemp += clayTobotsTemp;
        clayTobotsTemp++;
      } else {
        clayTemp += clayTobotsTemp;
      }
      // System.out.println(clayTemp);

    }
    maxClaysFromHere = clayTemp;

    // System.out.println(maxClaysFromHere);


    int maxObsidiansFromHere =
        status.obsidians + status.obsidianRobots * (maxtime - time + 1);
    oreTemp = maxOresFromHere;
    clayTemp = maxClaysFromHere;
    int obsRobotsTemp = status.obsidianRobots;
    int obsTemp = status.obsidians;
    int firstEnougObsForGeod = Integer.MAX_VALUE;
    for (int t = time; t <= maxtime; t++) {
      if (firstEnougObsForGeod == Integer.MAX_VALUE
          && obsTemp >= print.obsForGeoRobot) {
        firstEnougObsForGeod = t;
      }
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
    maxObsidiansFromHere = obsTemp;

    int maxGeodesFromHere =
        status.geodes + status.geodeRobots * (maxtime - time + 1);
    oreTemp = maxOresFromHere;
    obsTemp = maxObsidiansFromHere;
    for (int t = firstEnougObsForGeod; t < maxtime && oreTemp >= print.oreForGeoRobot
        && obsTemp >= print.obsForGeoRobot; t++) {
      maxGeodesFromHere += maxtime - t;
      oreTemp -= print.oreForGeoRobot;
      obsTemp -= print.obsForGeoRobot;
    }
    // System.out.println(maxGeodesFromHere);
    if (maxGeodesFromHere <= maxes.get(time)
        || maxGeodesFromHere <= maxes.get(maxtime)) {
      return -1;
    }

    if (status.ores >= print.oreForGeoRobot
        && status.obsidians >= print.obsForGeoRobot) {
      // if can buy a geode robot, buy one, that's all.
      status.ores -= print.oreForGeoRobot;
      status.obsidians -= print.obsForGeoRobot;

      final Status nextStatus = status.getStatusIn(1);
      nextStatus.geodeRobots++;
      final int result = simulate(print, time + 1, nextStatus);
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
        nextStatus = status.getStatusIn(1);
        nextStatus.ores -= print.oreForObsRobot;
        nextStatus.clays -= print.clayForObsRobot;
        nextStatus.obsidianRobots++;
        final int result = simulate(print, time + 1, nextStatus);
        if (result > max) {
          max = result;
        }
      }
      if (status.ores >= print.oreForClayRobot) {
        // buy clay robot
        nextStatus = status.getStatusIn(1);
        nextStatus.ores -= print.oreForClayRobot;
        nextStatus.clayRobots++;
        final int result = simulate(print, time + 1, nextStatus);
        if (result > max) {
          max = result;
        }
      }
      if (status.ores >= print.oreForOreRobot) {
        // buy clas robot
        nextStatus = status.getStatusIn(1);
        nextStatus.ores -= print.oreForOreRobot;
        nextStatus.oreRobots++;
        final int result = simulate(print, time + 1, nextStatus);
        if (result > max) {
          max = result;
        }
      }
      final int result = simulate(print, time + 1, status.getStatusIn(1));
      if (result > max) {
        max = result;
      }

      if (max > maxes.get(time)) {
        maxes.set(time, max);
      }
      return max;

    }
  }
}
