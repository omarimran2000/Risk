import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.Scanner;


public class Game {

    private Map theMap;
    private static List<Player> players;
    private static int numberOfPlayers;
    private static Scanner scanner = new Scanner(System.in);

    public Game ()
    {
        theMap = new Map("Global");
        players = new ArrayList<>();
    }

    /**
     * Add a Player to players list
     * @param player
     */
    public static void addPlayer(Player player){
        players.add(player);
    }


    public int getNumberOfPlayers(){
        return numberOfPlayers;
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

            offence += attacker.getDice()[i]; //access the die number saved at position i
            defence += defender.getDice()[i];
        }
        if (offence > defence) return attacker;
        return defender;
    }

    /**
     * Loading a map from a JSON file given the specific keys where structure is
     * {continent:{territory:[adjacent territories],points:int}}
     * @param JSONfile the filepath
     * @throws IOException
     * @throws ParseException
     */
    private void loadMap(String JSONfile) throws IOException, ParseException
    {
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(JSONfile);
        Object e = jsonParser.parse(fileReader);

        JSONObject mapObject = new JSONObject();
        mapObject = (JSONObject) e;

        JSONObject continents = (JSONObject) mapObject.get("continents"); //getting all the continent keys

        for (int i = 0; i < continents.keySet().size(); i++)  //adding all continents and associated territories
        {
            //adding continents to map
            String continentName = (String) continents.keySet().toArray()[i];   //gets continent name from JSON file
            JSONObject continentKeys = (JSONObject) continents.get(continentName); //gets all the objects for continent (territories/points_
            long longPoints = (long) continentKeys.get("points");
            int pointsInt = (int) longPoints;
            theMap.addContinents(new Continent(continentName, pointsInt));

            //adding territories to continents
            JSONObject territories = (JSONObject) continentKeys.get("territories");

            for (int j = 0; j < territories.keySet().size(); j++)  //iterating through all territories for this continent
            {
                String territoryName = (String) territories.keySet().toArray()[j];
                Territory temp = new Territory(territoryName, theMap.getContinents().get(i));
                theMap.getContinents().get(i).addTerritories(temp);
            }
        }
        /*
        Now that all territories and continents are created, the adjacent territories can be added by doing a similar
        process
         */
        for (int i = 0; i < continents.keySet().size(); i++)   //adding adjacent territories
        {
            String continentName = (String) continents.keySet().toArray()[i];  //gets current continent name
            JSONObject continentKeys = (JSONObject) continents.get(continentName);
            JSONObject territories = (JSONObject) continentKeys.get("territories"); //gets all territories for current continent

            for (int j = 0; j < territories.keySet().size(); j++)  //iterates through all the territories in JSON file again
            {
                String originalTerritoryName = (String) territories.keySet().toArray()[j]; //gets name of territory that needs adjacent territories
                Territory originalTerritory = theMap.findTerritory(originalTerritoryName);

                JSONObject territoriesKeys = (JSONObject) territories.get(originalTerritoryName); //gets all the keys from territories file
                JSONArray adjacentTerritories = (JSONArray) territoriesKeys.get("adjacent");

                for (int k=0;k<adjacentTerritories.size();k++)  //iterates through JSON array of adjacent territories
                {
                    String adjacentTerritoryName = (String) adjacentTerritories.get(k); //gets adjacent territory name
                    Territory adjacentTerritory = theMap.findTerritory(adjacentTerritoryName);
                    originalTerritory.addNeighbour(adjacentTerritory);  //adds adjacent territory to ArrayList
                }
            }
        }

    }

    /**
     * This method is used to initialize which player possess which territory.
     */
    public void initializeDefaultArmy() {
        List<Territory>territories = new ArrayList<>();
        Random random = new Random();
        for(Continent c : theMap.continents) {
            for(Territory t : c.getTerritories()) {
                territories.add(t);
            }
        }
        for (int i = 0; !territories.isEmpty(); i++) {
            Territory tempTerritory = territories.remove(random.nextInt(territories.size()));
            Player tempPlayer = players.get(i%numberOfPlayers);

            tempPlayer.addTerritory(tempTerritory);
            tempTerritory.setCurrentPlayer(tempPlayer);

        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Game game = new Game();
        game.loadMap("map.json");
        System.out.println("Map done loading");

        System.out.println("How many players are playing? Enter a number between 2-6");
        numberOfPlayers = scanner.nextInt();

        while (!(numberOfPlayers>=2 && numberOfPlayers<=6)){
            System.out.println("Please enter a number between 2-6");
            scanner.next();

        }
        // if a valid number of player is inputted
        // get and print every player's name and adds them to players
        {
            for (int i = 0; i < numberOfPlayers; i++)
            {
                System.out.print("Player name: ");
                String name = scanner.next();
                Player player = new Player(name);
                player.setActive(true);
                System.out.println(player.getName() + " added.");
                addPlayer(player);
            }
        }

        // setup the army placements
        game.initializeDefaultArmy();
        System.out.println(2);
        // ready to begin playing
        game.play();

    }


    public void play(){
        for (int i = 0; i < numberOfPlayers-1; i++)
        {
            Player player = players.get(i);
            if (player.isActive()) {
                System.out.println("It is now " + player.getName() + "'s turn");
                deploy(player);


                // return to first player
                if (i == numberOfPlayers - 1) {
                    i = 0;
                }
            }
        }
    }

    public int getNumberOfTroops(Player player){
        int numberOfTerritories = player.territories.size();
        int continentBonusPoints = 0; // find out if player has any continents and how much each is worth

        for (Continent continent : player.getContinents()) {
            continentBonusPoints += continent.getContinentPoint();
        }
        return numberOfTerritories/3 + continentBonusPoints;
    }

    public void deploy(Player player){

        int deployTroops = getNumberOfTroops(player);
        while (deployTroops > 0) {
            System.out.println("You have " + deployTroops + " troops to deploy. Where would you like to deploy them?");

            String t = scanner.next();
            Territory territory = theMap.findTerritory(t);
            if (!(player.getTerritories().contains(territory))) {
                System.out.println("Cannot deploy here. Pick another territory. ");
            } else {
                System.out.println("How many would you like to deploy? ");
                int x = scanner.nextInt();
                if (x <= deployTroops && (deployTroops - x) >= 0) {
                    player.deploy(x, territory);
                } else {
                    System.out.println("Please choose a number between " + 1 + "-" + deployTroops);
                    deployTroops -= x;
                }
            }
        }
    }

}
