import java.io.Serializable;
import java.util.Random;

/**
 * This class represents the dice used by players.
 *
 * @author Santhosh Pradeepan
 * @version October 13, 2020
 */
public class Dice implements Serializable {
    private Random random;

    /**
     * Constructor for class Dice.
     */
    public Dice() {
        random = new Random();
    }

    /**
     * Method used to roll a dice.
     *
     * @return a random number between 1-6
     */
    private int roll() {
        return random.nextInt(6) + 1;
    }

    /**
     * Method used to roll multiple dice.
     * Calls sortDice method to sort the dice in descending priority (6 is most priority and 1 is least priority).
     *
     * @param n the number of dice
     * @return an array of ints containing the number each die landed on, ordered from greatest to least
     */
    public int[] rollDice(int n) {
        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            answer[i] = roll();
        }
        return sortDice(answer);
    }

    /**
     * Method used to sort an array of integers from greatest to least.
     * The array of integers symbolize the dices.
     *
     * @param results the array of integers
     * @return the sorted array of integers
     */
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
