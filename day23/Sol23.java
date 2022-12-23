import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Sol23 {

  static List<Elf> elves = new ArrayList<>();

  static int height = 1000;

  static int width = 1000;

  static char currentMap[][] = new char[height][width];

  static Map<Coords, Elf> proposeds = new HashMap<>();

  static List<Function<Coords, Coords>> getmovement;

  static {
    getmovement = new ArrayList<>();

    getmovement.add((current) -> {
      final boolean N = currentMap[current.y - 1][current.x] == '#';
      final boolean NE = currentMap[current.y - 1][current.x + 1] == '#';
      final boolean NW = currentMap[current.y - 1][current.x - 1] == '#';

      if ((!N && !NE && !NW)) {
        return new Coords(current.x, current.y - 1);
      }
      return null;
    });

    getmovement.add((current) -> {
      final boolean S = currentMap[current.y + 1][current.x] == '#';
      final boolean SE = currentMap[current.y + 1][current.x + 1] == '#';
      final boolean SW = currentMap[current.y + 1][current.x - 1] == '#';

      if ((!S && !SE && !SW)) {
        return new Coords(current.x, current.y + 1);
      }
      return null;
    });

    getmovement.add((current) -> {
      final boolean NW = currentMap[current.y - 1][current.x - 1] == '#';
      final boolean SW = currentMap[current.y + 1][current.x - 1] == '#';
      final boolean W = currentMap[current.y][current.x - 1] == '#';

      if ((!W && !NW && !SW)) {
        return new Coords(current.x - 1, current.y);
      }
      return null;
    });

    getmovement.add((current) -> {
      final boolean NE = currentMap[current.y - 1][current.x + 1] == '#';
      final boolean SE = currentMap[current.y + 1][current.x + 1] == '#';
      final boolean E = currentMap[current.y][current.x + 1] == '#';
      if ((!E && !NE && !SE)) {
        return new Coords(current.x + 1, current.y);
      }
      return null;
    });
  }

  private static class Coords {
    int x;
    int y;

    Coords(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(final Object o) {
      if (o == null) {
        return false;
      }
      if (o == this) {
        return true;
      }
      if (!(o instanceof Coords)) {
        return false;
      }
      final Coords coordsObj = (Coords) o;
      return this.x == coordsObj.x && this.y == coordsObj.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
      return "(" + this.x + "," + this.y + ")";
    }
  }


  private static class Elf {
    Coords current;
    Coords proposed;

    Elf(final int x, final int y) {
      this.current = new Coords(x, y);
    }

    void propose() {
      final boolean foundAtLeastOne = false;

      final boolean N = currentMap[this.current.y - 1][this.current.x] == '#';
      final boolean NE = currentMap[this.current.y - 1][this.current.x + 1] == '#';
      final boolean NW = currentMap[this.current.y - 1][this.current.x - 1] == '#';
      final boolean S = currentMap[this.current.y + 1][this.current.x] == '#';
      final boolean SE = currentMap[this.current.y + 1][this.current.x + 1] == '#';
      final boolean SW = currentMap[this.current.y + 1][this.current.x - 1] == '#';
      final boolean W = currentMap[this.current.y][this.current.x - 1] == '#';
      final boolean E = currentMap[this.current.y][this.current.x + 1] == '#';

      this.proposed = null;

      if ((!N && !NE && !NW && !S && !SE && !SW && !W && !E)) {
        return;
      }

      this.proposed =
          getmovement.stream().filter(func -> func.apply(this.current) != null)
              .findFirst().map(func -> func.apply(this.current)).orElse(null);

      if (proposeds.containsKey(this.proposed)) {
        proposeds.get(this.proposed).proposed = null;
        this.proposed = null;
      } else {
        proposeds.put(this.proposed, this);
      }
    }

    void step() {


      if (this.proposed != null) {
        currentMap[this.current.y][this.current.x] = '.';
        this.current = this.proposed;
        currentMap[this.current.y][this.current.x] = '#';
      }
    }
  }

  public static void main(final String[] args) throws IOException {
    String line;
    String[] splittedLine;

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in23.txt"), StandardCharsets.UTF_8)) {

      // read map
      int y = height / 2;
      while ((line = buffR.readLine()) != null) {
        splittedLine = line.split("");
        int x = width / 2;
        for (final String chr : splittedLine) {
          currentMap[y][x] = chr.charAt(0);
          if (chr.charAt(0) == '#') {
            final Elf elf = new Elf(x, y);
            elves.add(elf);
          }
          x++;
        }
        y++;
      }

      // System.out.println(
      // elves.stream().map(elf -> elf.current).collect(Collectors.toList()));


      for (int i = 0; i < 10; i++) {
        simulateRound();
      }

      // System.out.println(
      // elves.stream().map(elf -> elf.current).collect(Collectors.toList()));

      final int minX = elves.stream().mapToInt(elf -> elf.current.x).min()
          .orElse(Integer.MAX_VALUE);
      final int maxX = elves.stream().mapToInt(elf -> elf.current.x).max()
          .orElse(Integer.MIN_VALUE);
      final int minY = elves.stream().mapToInt(elf -> elf.current.y).min()
          .orElse(Integer.MAX_VALUE);
      final int maxY = elves.stream().mapToInt(elf -> elf.current.y).max()
          .orElse(Integer.MIN_VALUE);

      System.out.println(((maxX - minX + 1) * (maxY - minY + 1)) - elves.size());
    }
  }

  static void simulateRound() {
    elves.stream().forEach(elf -> elf.propose());
    elves.stream().forEach(elf -> elf.step());
    proposeds.clear();
    getmovement.add(getmovement.remove(0));
  }

}
