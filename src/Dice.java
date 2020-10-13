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
        return sortDice(answer, n);
    }

    private int[] sortDice(int[] dice, int size) {
        if (size == 0) {

        }
        else if (size > 1) {

        }
        return dice;
    }
}
