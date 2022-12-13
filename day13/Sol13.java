import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Sol13 {

  static class Element {
    int integerValue;
    List<Element> listValue = new ArrayList<>();

    boolean isInteger;

    Element() {
      this.isInteger = false;
    }

    Element(final int integerValue) {
      this.integerValue = integerValue;
      this.isInteger = true;
    }

    Element(final String input) {

      try {
        this.integerValue = Integer.parseInt(input);
        this.isInteger = true;
        return;
      } catch (final NumberFormatException e) {
        this.isInteger = false;
      }
      final char[] convertedString =
          input.substring(1, input.length() - 1).toCharArray();

      int bracketCounter = 0;
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < convertedString.length; i++) {
        if (convertedString[i] == ']') {
          sb.append(convertedString[i]);
          bracketCounter--;
        } else if (convertedString[i] == '[') {
          sb.append(convertedString[i]);
          bracketCounter++;
        } else if (bracketCounter > 0) {
          sb.append(convertedString[i]);
          continue;
        } else if (convertedString[i] == ',') {
          if (!sb.toString().isBlank()) {
            this.listValue.add(new Element(sb.toString()));
          }
          sb = new StringBuffer();
        } else {
          sb.append(convertedString[i]);
        }

      }
      if (!sb.toString().isBlank()) {
        this.listValue.add(new Element(sb.toString()));
      }
    }

    @Override
    public String toString() {
      if (this.isInteger) {
        return Integer.toString(this.integerValue);
      } else {
        return this.listValue.toString();
      }
    }

    RES isSmallerThan(final Element b) {
      if (this.isInteger && b.isInteger) {
        if (this.integerValue < b.integerValue) {
          return RES.GOOD;
        } else if (this.integerValue == b.integerValue) {
          return RES.CANT_DECIDE;
        } else {
          return RES.BAD;
        }
      } else if (!this.isInteger && !b.isInteger) {
        int i = 0;
        int j = 0;
        while (true) {
          if (i == this.listValue.size() && j < b.listValue.size()) {
            return RES.GOOD;
          } else if (i < this.listValue.size() && j == b.listValue.size()) {
            return RES.BAD;
          } else if (i == this.listValue.size() && j == b.listValue.size()) {
            return RES.CANT_DECIDE;
          } else {
            final RES partRes =
                this.listValue.get(i).isSmallerThan(b.listValue.get(j));

            if (partRes == RES.CANT_DECIDE) {
              i++;
              j++;
              continue;
            } else {
              return partRes;
            }
          }
        }

      } else if (this.isInteger) {
        return this.toListElement().isSmallerThan(b);
      } else {
        return this.isSmallerThan(b.toListElement());
      }
    }

    Element toListElement() {
      final Element result = new Element();
      result.listValue.add(this);
      return result;
    }
  }

  enum RES {
    GOOD, BAD, CANT_DECIDE
  }

  public Sol13() {
    // TODO Auto-generated constructor stub
  }

  public static void main(final String[] args) {
    String line;
    Element elem1;
    Element elem2;

    final List<Element> elements = new ArrayList<>();

    int i = 1;
    int sum = 0;

    try (BufferedReader buffR =
        Files.newBufferedReader(Paths.get("in13.txt"), StandardCharsets.UTF_8)) {


      while ((line = buffR.readLine()) != null) {
        if (line.isBlank()) {
          continue;
        }

        elem1 = new Element(line);
        elements.add(elem1);

        line = buffR.readLine();
        elem2 = new Element(line);
        elements.add(elem2);

        if (elem1.isSmallerThan(elem2) == RES.GOOD) {
          sum += i;
        }
        i++;

      }



    } catch (final IOException e) {
      System.out.println("IOException in try block =>" + e.getMessage());
    }

    System.out.println(sum);

    final Element dividerPacket1 = new Element("[[2]]");
    final Element dividerPacket2 = new Element("[[6]]");
    elements.add(dividerPacket1);
    elements.add(dividerPacket2);

    elements.sort((a, b) -> a.isSmallerThan(b) == RES.GOOD ? -1 : 1);

    final int result = IntStream.range(0, elements.size())
        .filter(ind -> elements.get(ind) == dividerPacket1
            || elements.get(ind) == dividerPacket2)
        .reduce(1, (a, b) -> (a) * (b + 1));
    System.out.println(result);
  }
}
