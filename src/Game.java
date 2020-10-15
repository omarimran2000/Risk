//import org.json.simple.*;
//import org.json.simple.parser.*;
import java.io.FileReader;


public class Game {

    private Map theMap;

    public Game ()
    {

    }

    /**
     * This method is used to determine if a raid was successful or not. If the defender successfully fend off the attacker's attack,
     * this method returns the defender, otherwise it returns the attacker
     *
     * @param attacker the player that is initiating the raid
     * @param defender the player that is defending
     * @param numOfDice the number of dice that are being rolled
     * @return the player that survives the attack
     */
    private Player checkWinner(Player attacker, Player defender, int numOfDice) {
        int offence = 0;
        int defence = 0;
        for (int i = 0; i < numOfDice; i++) // sums the highest rolls together per player
        {
       //     offence += attacker.getDice()[i]; //access the die number saved at position i
         //   defence += defence.getDice().[i];
        }
        if (offence > defence) return attacker;
        return defender;
    }
    private void loadMap(String JSONfile)
    {

    }
    public static void main(String[] args){

        Game game = new Game();
        game.loadMap("../example.json");

    }
}
