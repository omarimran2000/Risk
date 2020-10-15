import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.FileReader;
import java.io.IOException;


public class Game {

    private Map theMap;

    public Game ()
    {
        theMap = new Map("Global");
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
    private void loadMap(String JSONfile) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(JSONfile);
        Object e = jsonParser.parse(fileReader);

        JSONObject mapObject = new JSONObject();
        mapObject = (JSONObject) e;

        JSONObject continents = (JSONObject) mapObject.get("continents");
        //System.out.println(continents.get("Europe"));

        for (int i=0;i<continents.keySet().size();i++)
        {
            String continentName = (String) continents.keySet().toArray()[i];
            JSONObject continentKeys = (JSONObject) continents.get(continentName);
            long longPoints = (long) continentKeys.get("points");
            int pointsInt = (int) longPoints;
            theMap.addContinents(new Continent(continentName,pointsInt));
        }

    }
    public static void main(String[] args) throws IOException, ParseException {

        Game game = new Game();
        game.loadMap("example.json");

    }
}
