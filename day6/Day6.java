import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Day6 {
    public static void main(String[] str) {
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader("in6.txt"))) {
            line = br.readLine();
        } catch (IOException e) {
            System.out.println("IOException in try block =>" + e.getMessage());
        }

        List<String> input = Arrays.asList(line.split(""));
        List<String> lastElements = new ArrayList<>(3);
        Set<String> lastElementsS;

        boolean foundSol1 = false;
        boolean foundSol2 = false;

        for (int i =0; i < 4; i++){
            lastElements.add(input.get(i));
        }
        for (int i = 4; i < input.size(); i++){
            lastElements.remove(0);
            lastElements.add(input.get(i));
            lastElementsS = new HashSet<>();
            lastElementsS.addAll(lastElements);
            if (lastElementsS.size() == 4){
                System.out.println("Solution for Part 1: " + ((int)i+1));
                break;
            }
        }

        lastElements = new ArrayList<>(14);
        for (int i =0; i < 14; i++){
            lastElements.add(input.get(i));
        }
        for (int i = 14; i < input.size(); i++){
            lastElements.remove(0);
            lastElements.add(input.get(i));
            lastElementsS = new HashSet<>();
            lastElementsS.addAll(lastElements);
            if (lastElementsS.size() == 14){
                System.out.println("Solution for Part 2: " + ((int)i+1));
                break;
            }
        }

    }
}
