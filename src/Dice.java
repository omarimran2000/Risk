import java.util.Random;

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
            for (int i = 2; i < size; i++) {
                int temp = results[i];
                int j = i - 1;
                while (j > 0 && results[j] > temp) {
                    results[j + 1] = results[j];
                    j--;
                }
                results[j + 1] = temp;
            }
        }
        return results;
    }
}
