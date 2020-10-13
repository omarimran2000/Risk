import java.util.Random;

/**
 * This class represents the dice used by players.
 *
 *
 */
public class Dice {
    private Random random;

    public Dice() {
        random = new Random();
    }
    private int roll() {
        return random.nextInt(6) + 1;
    }

    public int[] rollDice(int n) {
        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            answer[i] = roll();
        }
        return sortDice(answer);
    }

    private int[] sortDice(int[] results) {
        int size = results.length;
        if (size == 0) {
            // either throw an error if this is an error or just ignore, not sure.
        }
        else if (size > 1) {
            for (int j = 1; j < size; j++) {
                int temp = results[j];
                int i = j - 1;
                while ((i > -1) && (results[i] < temp) ) {
                    results[i + 1] = results[i];
                    i--;
                }
                results[i + 1] = temp;
            }
        }
        return results;
    }
}
