import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sol25 {

  public static void main(final String[] args) throws IOException {
    String line;
    final String[] splittedLine;

    // read input
    try (final BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in25.txt"), StandardCharsets.UTF_8)) {

      long sum = 0;
      while ((line = buffR.readLine()) != null) {
        sum += fromSNAFU(line);
      }
      System.out.println(toSNAFU(sum));
    }



  }

  private static long fromSNAFU(final String snafu) {
    final StringBuilder sb = new StringBuilder(snafu);
    final char[] snafuRev = sb.reverse().toString().toCharArray();
    long result = 0;
    long place = 1;
    for (final char c : snafuRev) {
      result += getDigVal(c) * place;
      place *= 5;
    }

    return result;
  }

  private static long getDigVal(final char e) {
    switch (e) {
      case '2':
        return 2;
      case '1':
        return 1;
      case '0':
        return 0;
      case '-':
        return -1;
      case '=':
        return -2;
      default:
        throw new RuntimeException("Invalid digit");

    }
  }

  private static String toSNAFU(long value) {

    final StringBuilder sb = new StringBuilder();

    while (value != 0) {
      final int mod = (int) (value % 5);
      // System.out.println("VAlue " + value + ", mod: " + mod);
      switch (mod) {
        case 2:
          sb.append('2');
          value -= 2;
          break;
        case 1:
          sb.append('1');
          value -= 1;
          break;
        case 0:
          sb.append('0');
          break;
        case 3:
          sb.append('=');
          value += 2;
          break;
        case 4:
          sb.append('-');
          value += 1;
          break;
      }
      value /= 5;
    }

    return sb.reverse().toString();
  }

}
