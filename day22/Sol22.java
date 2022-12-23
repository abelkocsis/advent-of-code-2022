import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sol22 {

  static int width = 150;
  static int height = 200;

  static char[][] map = new char[height][width];

  static int[] minXinRow = new int[height];
  static int[] maxXinRow = new int[height];
  static int[] minYinCol = new int[width];
  static int[] maxYinCol = new int[width];

  static char[] instructions;

  static int yDiv1 = 50;
  static int yDiv2 = 100;
  static int yDiv3 = 150;
  static int xDiv1 = 50;
  static int xDiv2 = 100;

  static Map<Move, Move> movements = new HashMap<>();

  static final class Move {
    int facing;
    int x;
    int y;

    Move() {
      this.y = 0;
      this.x = minXinRow[0];
      this.facing = 0;
    }

    Move(final int x, final int y, final int facing) {
      this.x = x;
      this.y = y;
      this.facing = facing;
    }

    Move(final Move m) {
      this.x = m.x;
      this.y = m.y;
      this.facing = m.facing;
    }

    @Override
    public boolean equals(final Object o) {
      final Move moveObj = (Move) o;
      return this.x == moveObj.x && this.y == moveObj.y
          && this.facing == moveObj.facing;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.x, this.y, this.facing);
    }

    @Override
    public String toString() {
      return "(" + this.x + "," + this.y + "," + this.facing + ")";
    }

    Move calcOutOfMapMovePart1(int nextX, int nextY, final int nextFace) {
      final Move nextReal = new Move();
      if (this.x != nextX) {
        // x changes
        if (nextX < minXinRow[nextY]) {
          nextX = maxXinRow[nextY];
        } else if (nextX > maxXinRow[nextY]) {
          nextX = minXinRow[nextY];
        }
      } else if (this.y != nextY) {
        if (nextY < minYinCol[nextX]) {
          nextY = maxYinCol[nextX];
        } else if (nextY > maxYinCol[nextX]) {
          nextY = minYinCol[nextX];
        }
      }

      nextReal.x = nextX;
      nextReal.y = nextY;
      nextReal.facing = nextFace;
      return nextReal;
    }

    void calcOutOfMapMovePart2(final int nextX, final int nextY,
        final int nextFace) {
      final Move origMove = new Move(nextX, nextY, nextFace);
      if (movements.containsKey(origMove)) {
        return;
      }

      final Move nextReal = new Move();
      if (this.x != nextX) {
        // x changes

        if (nextX < minXinRow[nextY]) {
          if (this.y < yDiv1) {
            // new
            nextReal.y = yDiv3 - (this.y + 1);
            nextReal.x = 0;
            nextReal.facing = 0;
          } else if (this.y < yDiv2) {
            // new
            nextReal.y = yDiv2;
            nextReal.x = this.x - (yDiv2 - this.y);
            nextReal.facing = 1;
          } else if (this.y >= yDiv3) {
            // new
            nextReal.y = 0;
            nextReal.x = xDiv2 - (height - this.y);
            nextReal.facing = 1;
          } else {
            return;
          }
        } else if (nextX > maxXinRow[nextY]) {
          if (this.y < yDiv1) {
            // new
            nextReal.y = yDiv3 - (this.y + 1);
            nextReal.x = xDiv2 - 1;
            nextReal.facing = 2;
          } else if (this.y < yDiv2) {
            // new
            nextReal.y = yDiv1 - 1;
            nextReal.x = this.x + (this.y - yDiv1 + 1);
            nextReal.facing = 3;
          } else if (this.y >= yDiv3 && this.y < height) {
            // new
            nextReal.y = yDiv3 - 1;
            nextReal.x = this.x + (this.y - yDiv3 + 1);
            nextReal.facing = 3;
          } else {
            return;
          }
        }
      } else if (((this.y != nextY) && (nextX < xDiv1)) && (this.facing == 1)) {
        // new
        nextReal.y = 0;
        nextReal.x = xDiv2 + this.x;
        nextReal.facing = 1;
      } else {
        return;
      }

      movements.put(new Move(this), new Move(nextReal));
      nextReal.facing = (nextReal.facing + 2) % 4;
      this.facing = (origMove.facing + 2) % 4;
      movements.put(new Move(nextReal), new Move(this));
    }

    int getPlusX() {
      return this.facing == 0 ? 1 : this.facing == 2 ? -1 : 0;
    }

    int getPlusY() {
      return this.facing == 1 ? 1 : this.facing == 3 ? -1 : 0;
    }

    void rotateLeft() {
      this.facing = (this.facing - 1 + 4) % 4;
    }

    void rotateRight() {
      this.facing = (this.facing + 1) % 4;
    }

    void step() {
      int nextX = this.x + this.getPlusX();
      int nextY = this.y + this.getPlusY();
      int nextFacing = this.facing;

      final boolean inMap = nextX >= 0 && nextX < width && nextY >= 0
          && nextY < height && nextX >= minXinRow[nextY] && nextX <= maxXinRow[nextY]
          && nextY >= minYinCol[nextX] && nextY <= maxYinCol[nextX]

          && map[nextY][nextX] != ' ';

      System.out.println("Inmap? : " + inMap);

      if (!inMap) {
        // part 1
        // final Move nextReal = this.calcOutOfMapMovePart1(nextX, nextY,
        // this.facing);

        // part 2
        final Move nextReal = movements.get(this);

        if (nextReal == null) {
          System.out.println(this);
          System.out.println(nextX);
          System.out.println(nextY);
          System.out.println(this.facing);
        }

        nextX = nextReal.x;
        nextY = nextReal.y;
        nextFacing = nextReal.facing;
      }
      if (map[nextY][nextX] == '.') {
        this.x = nextX;
        this.y = nextY;
        this.facing = nextFacing;
        System.out.println(this);
        return;
      }

      if (map[nextY][nextX] == '#') {
        System.out.println("blocked.");
        System.out.println(this);
        return;
      }
      this.x = nextX;
      this.y = nextY;
      this.facing = nextFacing;
      System.out.println("After step: ");
      System.out.println(this);
    }

  }

  public static void main(final String[] args) throws IOException {
    // init
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        map[y][x] = ' ';
      }
    }
    for (int y = 0; y < height; y++) {
      minXinRow[y] = Integer.MAX_VALUE;
      maxXinRow[y] = Integer.MIN_VALUE;
    }
    for (int x = 0; x < width; x++) {
      minYinCol[x] = Integer.MAX_VALUE;
      maxYinCol[x] = Integer.MIN_VALUE;
    }

    // read input
    String line;
    String[] splittedLine;

    // read input
    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in22.txt"), StandardCharsets.UTF_8)) {

      // read map
      int y = 0;
      while ((line = buffR.readLine()) != null) {
        if (line.isEmpty()) {
          break;
        }
        splittedLine = line.split("");
        int x = 0;

        for (final String c : splittedLine) {
          map[y][x] = c.charAt(0);

          if (map[y][x] != ' ') {
            if (y < minYinCol[x]) {
              minYinCol[x] = y;
            }
            if (y > maxYinCol[x]) {
              maxYinCol[x] = y;
            }
            if (x < minXinRow[y]) {
              minXinRow[y] = x;
            }
            if (x > maxXinRow[y]) {
              maxXinRow[y] = x;
            }
          }
          x++;
        }
        y++;
      }

      // read instructions
      line = buffR.readLine();
      instructions = line.toCharArray();

      // new Move(2, minYinCol[2], 3).calcOutOfMapMovePart2(2, minYinCol[2] - 1,
      // 3);
      for (int x = 0; x < width; x++) {
        new Move(x, maxYinCol[x], 1).calcOutOfMapMovePart2(x, maxYinCol[x] + 1, 1);
        new Move(x, minYinCol[x], 3).calcOutOfMapMovePart2(x, minYinCol[x] - 1, 3);
      }

      for (int yi = 0; yi < height; yi++) {
        new Move(minXinRow[yi], yi, 2).calcOutOfMapMovePart2(minXinRow[yi] - 1, yi,
            2);
        new Move(maxXinRow[yi], yi, 0).calcOutOfMapMovePart2(maxXinRow[yi] + 1, yi,
            0);
      }

      // System.out.println(movements.get(new Move(37, 100, 3)));
      simulate();
      // System.out.println(movements);
    }
  }

  static void simulate() {
    StringBuffer sb = new StringBuffer();
    final Move mv = new Move();
    for (final char c : instructions) {
      final boolean isRight = c == 'R';
      final boolean isLeft = c == 'L';
      if (isRight || isLeft) {
        final String numOfStepsStr = sb.toString();
        if (!numOfStepsStr.isEmpty()) {
          final int numOfStepsI = Integer.parseInt(numOfStepsStr);
          for (int i = 0; i < numOfStepsI; i++) {
            // TODO could check whether step was taken and stop if wasn't
            mv.step();
          }

          // System.out.println("Row: " + mv.y);
          // System.out.println("Column: " + mv.x);
          // System.out.println("Facing: " + mv.facing);
          // System.out.println();

          sb = new StringBuffer();
        }
        if (isRight) {
          mv.rotateRight();
        } else if (isLeft) {
          mv.rotateLeft();
        }
      } else {
        sb.append(c);
      }
    }

    final String numOfStepsStr = sb.toString();
    if (!numOfStepsStr.isEmpty()) {
      final int numOfStepsI = Integer.parseInt(numOfStepsStr);
      for (int i = 0; i < numOfStepsI; i++) {
        // TODO could check whether step was taken and stop if wasn't
        mv.step();
      }
    }

    System.out.println("Row: " + mv.y);
    System.out.println("Column: " + mv.x);
    System.out.println("Facing: " + mv.facing);
    final int res = (mv.y + 1) * 1000 + (mv.x + 1) * 4 + mv.facing;
    System.out.println("Result: " + res);

  }



}
