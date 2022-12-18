import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.LongStream;

public class Sol17 {

  static char[] jets;

  static List<Set<Integer>> occupation = new ArrayList<>();

  static long removed = 0L;
  static int chamberWidth = 7;


  static Long[] highestAtX = {0L, 0L, 0L, 0L, 0L, 0L, 0L};

  // static List<List<Set<Integer>>> previousOccupations = new ArrayList<>();
  static Map<Long, Status> previousOccupations = new HashMap<>();

  private static class Status {
    List<Set<Integer>> occupation;
    int lastForm;
    long removed;
    long afterStep;

    Status(final List<Set<Integer>> occupation, final int lastForm,
        final long removed, final long afterStep) {
      this.occupation = occupation;
      this.lastForm = lastForm;
      this.removed = removed;
      this.afterStep = afterStep;
    }
  }

  static class Rock {
    char[][] rock = new char[4][4];

    int x;
    long y;
    int width;

    Rock(final int i) {
      switch (i % 5) {
        case 0:
          this.rock[0] = "####".toCharArray();
          this.rock[1] = "....".toCharArray();
          this.rock[2] = "....".toCharArray();
          this.rock[3] = "....".toCharArray();
          this.width = 4;
          break;
        case 1:
          this.rock[0] = ".#..".toCharArray();
          this.rock[1] = "###.".toCharArray();
          this.rock[2] = ".#..".toCharArray();
          this.rock[3] = "....".toCharArray();
          this.width = 3;
          break;
        case 2:
          this.rock[0] = "###.".toCharArray();
          this.rock[1] = "..#.".toCharArray();
          this.rock[2] = "..#.".toCharArray();
          this.rock[3] = "....".toCharArray();
          this.width = 3;
          break;
        case 3:
          this.rock[0] = "#...".toCharArray();
          this.rock[1] = "#...".toCharArray();
          this.rock[2] = "#...".toCharArray();
          this.rock[3] = "#...".toCharArray();
          this.width = 1;
          break;
        case 4:
          this.rock[0] = "##..".toCharArray();
          this.rock[1] = "##..".toCharArray();
          this.rock[2] = "....".toCharArray();
          this.rock[3] = "....".toCharArray();
          this.width = 2;
          break;
      }
    }

    boolean collides() {
      if (this.x < 0 || this.x + this.width > chamberWidth || this.y < removed) {
        return true;
      }
      for (int rockY = 0; rockY < 4; rockY++) {

        final long realY = this.y + rockY;

        if (realY >= getOccupiedHeight()) {

          return false;
        }
        for (int rockX = 0; rockX < 4; rockX++) {
          if (this.rock[rockY][rockX] == '.') {
            continue;
          }

          if (getOccupations(realY).contains(this.x + rockX)) {

            return true;
          }
        }
      }
      return false;
    }

    void settle() {
      // System.out.println("Settles with: " + this.x + ", " + this.y);
      for (int rockY = 0; rockY < 4; rockY++) {
        for (int rockX = 0; rockX < 4; rockX++) {
          if (this.rock[rockY][rockX] == '#') {
            final long realY = this.y + rockY;
            final int realX = rockX + this.x;


            if (realY >= getOccupiedHeight()) {

              addOccLine();
            }

            // System.out.println("Put occ");
            getOccupations(realY).add(realX);

            if (realY > highestAtX[realX]) {
              highestAtX[realX] = realY;
            }
          }
        }
      }
      clearLines();
    }

    long simulate(long time) {
      this.x = 2;
      this.y = getOccupiedHeight() + 3;

      // push
      boolean isMoving = true;
      while (isMoving) {
        // apply jet, if possible
        final char jet = getJet(time);
        if (jet == '<') {
          this.x--;
          if (this.collides()) {
            this.x++;
            // System.out.println("Rock couldn't move to left.");
          } else {
            // System.out.println("Rock moved to left.");
          }

        } else {
          this.x++;
          if (this.collides()) {
            this.x--;
            // System.out.println("Rock couldn't move to right.");
          } else {
            // System.out.println("Rock moved to right.");
          }

        }

        // move down, if possible
        this.y--;
        time++;
        if (this.collides()) {
          this.y++;
          // should settle here!
          isMoving = false;
          this.settle();
        }
      }

      return time;
    }

  }

  public static void main(final String[] args) {
    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in17.txt"), StandardCharsets.UTF_8)) {
      jets = buffR.readLine().toCharArray();
    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }
    System.out.println(jets.length);

    long time = 0;
    final long allRocketsLen = 1000000000000L;

    long foundI = -1;
    long foundJ = -1;
    long heightInc = 0;
    for (long i = 0; i < allRocketsLen; i++) {
      time = simulate((int) (i % 5), time);

      /*
       * if (i % 1710 == 861) { System.out.println("After step " + i);
       * System.out.println("After time " + time);
       * System.out.println("Current height: " + getOccupiedHeight());
       * System.out.println("Jet index" + (time % jets.length)); for (int asd = 0;
       * asd < occupation.size(); asd++) { System.out.println(occupation.get(asd)); }
       * }
       */



      if (foundJ == -1) {
        previousOccupations.put(time,
            new Status(List.copyOf(occupation), (int) (i % 5), removed, i));

        for (long timeToCheck = time - jets.length; timeToCheck >= 0; timeToCheck -=
            jets.length) {
          if (previousOccupations.containsKey(timeToCheck)) {
            final Status st = previousOccupations.get(timeToCheck);
            if (st.lastForm == (int) (i % 5) && st.occupation.equals(occupation)) {
              foundJ = st.afterStep;
              heightInc = removed - st.removed;
              break;
            }

          }
        }
        if (foundJ != -1) {
          foundI = i;
          break;
        }
      }



    }

    if (foundI == -1) {
      System.out.println("Current height: " + getOccupiedHeight());
      System.out.println(removed);
      System.out.println(occupation.size());
      System.out.println(occupation.get(37));
      return;
    }

    System.out.println("foundI: " + foundI);
    System.out.println("foundJ: " + foundJ);
    System.out.println("heightInc: " + heightInc);

    final long stepInc = foundI - foundJ;
    System.out.println("stepInc: " + stepInc);
    System.out.println("REmoved: " + removed);

    final long timesToDo = ((allRocketsLen - foundI) / stepInc);

    removed += timesToDo * heightInc;

    System.out.println(timesToDo);
    System.out.println("REmoved: " + removed);
    System.out.println("timesToDo: " + timesToDo);
    System.out.println(foundI + timesToDo * stepInc);
    System.out.println("Current height: " + getOccupiedHeight());


    for (long i = foundI + timesToDo * stepInc + 1; i < allRocketsLen; i++) {
      time = simulate((int) (i % 5), time);
      // System.out.println("Step" + i);
    }
    // 1577184069411

    System.out.println("Current height: " + getOccupiedHeight());
    System.out.println("Good solution: " + (getOccupiedHeight() == 1514285714288L));
  }

  static void addOccLine() {
    occupation.add(new HashSet<>());
  }

  static void clearLines() {

    final long clearFrom = Arrays.asList(highestAtX).stream().reduce(highestAtX[0],
        (a, b) -> a < b ? a : b);
    final long clearLines = clearFrom - removed - 1;
    if (clearLines > 0) {
      removed += clearLines;
      LongStream.range(0, clearLines).forEach(l -> occupation.remove(0));
    }
  }

  static char getJet(final long time) {
    return jets[(int) (time % jets.length)];
  }

  static Set<Integer> getOccupations(final long y) {
    return occupation.get((int) (y - removed));
  }

  static long getOccupiedHeight() {
    return removed + occupation.size();
  }

  static long simulate(final int simulationNumber, final long time) {
    return new Rock(simulationNumber).simulate(time);
  }

}
