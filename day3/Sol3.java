package day3;

import java.io.IOException;
import java.util.Arrays;

import java.io.FileReader;
import java.io.BufferedReader;

public class Sol3 {

    public static void main(String[] args) {
        String line;
        int overallPriority = 0;
        String[] allLines = new String[300];
        int i = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("in3.txt"))) {
            while ((line = br.readLine()) != null) {
                allLines[i++] = line;
                // part 1
                int partLen = line.length() / 2;
                String secondPart = line.substring(partLen);
                String common = Arrays.asList(line.substring(0, partLen).split("")).stream()
                        .filter(chr -> secondPart.contains(chr)).findAny().get();
                overallPriority += getPriority(common);
            }
        } catch (IOException e) {
            System.out.println("IOException in try block =>" + e.getMessage());
        }

        // part 2
        int overallPriorityP2 = 0;
        for (int j = 0; j < allLines.length; j += 3) {
            final int firstInGrp = j;
            final int secondInGrp = j + 1;
            final int thirdInGrp = j + 2;
            String common = Arrays.asList(allLines[firstInGrp].split("")).stream()
                    .filter(chrS -> allLines[secondInGrp].contains(chrS))
                    .filter(chrS -> allLines[thirdInGrp].contains(chrS))
                    .findAny().get();
            overallPriorityP2 += getPriority(common);
        }

        System.out.println("Solution for part 1: " + overallPriority);
        System.out.println("Solution for part 2: " + overallPriorityP2);
    }

    private static int getPriority(String commonCharacter) {
        char chr = commonCharacter.charAt(0);
        if (chr < 'a') {
            return (int) chr - (int) 'A' + 27;
        } else {
            return (int) chr - (int) 'a' + 1;
        }
    }
}