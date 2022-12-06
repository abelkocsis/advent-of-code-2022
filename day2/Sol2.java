import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class Sol2 {

    enum Rps {
        ROCK(1), PAPER(2), SCISSORS(3);

        private static Rps[] rpsArray = { SCISSORS, ROCK, PAPER, SCISSORS, ROCK };

        private final int value;

        private Rps(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public Rps beats() {
            return rpsArray[this.value - 1];
        }

        public Rps losesAgainst() {
            return rpsArray[this.value + 1];
        }

    }

    public static Rps parse(final char chr) {
        switch (chr) {
            case 'A':
            case 'X':
                return Rps.ROCK;
            case 'B':
            case 'Y':
                return Rps.PAPER;
            case 'C':
            case 'Z':
                return Rps.SCISSORS;
            default:
                throw new RuntimeException("Invalid input");
        }
    }

    public static void main(final String[] args) {
        String line;
        Rps opponent;
        Rps own;
        int score1 = 0;
        int score2 = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("in2.txt"))) {
            while ((line = br.readLine()) != null) {
                final String[] splittedLine = line.split(" ");
                opponent = parse(splittedLine[0].charAt(0));
                own = parse(splittedLine[1].charAt(0));
                score1 += getScore(own, opponent);

                own = shouldPlay(opponent, splittedLine[1].charAt(0));
                score2 += getScore(own, opponent);
            }
        } catch (IOException e) {
            System.out.println("IOException in try block =>" + e.getMessage());
        }
        System.out.println("Solution for Part 1: " + score1);
        System.out.println("Solution for part 2: " + score2);

    }

    private static int getScore(final Rps own, final Rps opponent) {
        final int baseScore = own.getValue();
        int matchScore;
        if (own.beats() == opponent) {
            matchScore = 6;
        } else if (own.losesAgainst() == opponent) {
            matchScore = 0;
        } else {
            matchScore = 3;
        }
        return baseScore + matchScore;
    }

    private static Rps shouldPlay(final Rps opponent, final char command) {
        switch (command) {
            case 'X':
                return opponent.beats();
            case 'Y':
                return opponent;
            case 'Z':
                return opponent.losesAgainst();
            default:
                throw new RuntimeException("Invalid command");
        }
    }
}