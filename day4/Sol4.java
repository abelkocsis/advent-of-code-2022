import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Sol4 {
    public static void main(String[] str) {
        String line;
        String[] splittedLine;
        String[] e1S;
        int e1Start;
        int e1End;
        String[] e2S;
        int e2Start;
        int e2End;

        int counterP1 = 0;
        int counterP2 = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("in4.txt"))) {
            while ((line = br.readLine()) != null) {
                splittedLine = line.split(",");
                e1S = splittedLine[0].split("-");
                e1Start = Integer.parseInt(e1S[0]);
                e1End = Integer.parseInt(e1S[1]);
                e2S = splittedLine[1].split("-");
                e2Start = Integer.parseInt(e2S[0]);
                e2End = Integer.parseInt(e2S[1]);

                // part 1
                if (isContains(e1Start, e1End, e2Start, e2End)) {
                    counterP1++;
                    counterP2++;
                } else if (isOverlap(e1Start, e1End, e2Start, e2End)) {
                    counterP2++;
                }
            }
        } catch (IOException e) {
            System.out.println("IOException in try block =>" + e.getMessage());
        }

        System.out.println("Solution for Part 1: " + counterP1);
        System.out.println("Solution for Part 2: " + counterP2);
    }

    public static boolean isContains(final int e1Start, final int e1End, final int e2Start, final int e2End) {
        if (e1Start <= e2Start && e1End >= e2End) {
            return true;
        } else if (e2Start <= e1Start && e2End >= e1End) {
            return true;
        }
        return false;
    }

    public static boolean isOverlap(final int e1Start, final int e1End, final int e2Start, final int e2End) {
        return (e1Start <= e2Start && e2Start <= e1End) || (e1Start <= e2End && e2End <= e1End);
    }
}
