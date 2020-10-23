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

    /**
     *
     * @param numberOfPlayers
     */
    public void setArmies(int numberOfPlayers)
    {
        int[] arr_num_Armies = new int[]{50,35,30,25,20};
        int num_armies = arr_num_Armies[numberOfPlayers-2];
        Random random = new Random();

        for(Player p:players)
        {
            int armiesCount = num_armies;
            for (Territory t:p.getTerritories()) //puts one army in every territory owned by player
            {
                p.getArmy().addTroop(new Troop());
                p.deploy(1,t);
                armiesCount--;
            }

            for(int i=0;(i<p.getTerritories().size());i++)  //distributes rest of the troops
            {
                if(armiesCount!=0)
                {
                    int tempTroopCount = random.nextInt(armiesCount) + 1;

                    for (int j = 1; j < (tempTroopCount + 1); j++) {
                        p.getArmy().addTroop(new Troop());
                    }
                    p.deploy(tempTroopCount, p.territories.get(i));
                    armiesCount = armiesCount - tempTroopCount;
                }

            }


        }
    }
    /**
     *
     * @param args
     * @throws IOException
     * @throws ParseException
     */

    public static void main(String[] args) throws IOException, ParseException {
        Game game = new Game();
        game.loadMap("map.json");
        System.out.println("Map done loading"); //remember to remove this

        System.out.println("How many players are playing? Enter a number between 2-6");
        numberOfPlayers = scanner.nextInt();

        while (!(numberOfPlayers>=2 && numberOfPlayers<=6)){
            System.out.println("Please enter a number between 2-6");
            numberOfPlayers = scanner.nextInt();

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
        game.setArmies(numberOfPlayers);

        // ready to begin playing
        game.play();

    }


    public void play() {
        String response;
        for (int i = 0; playersActive(); i++) { // infinite loop that will end once all players are inactive
            Player player = players.get(i % numberOfPlayers);
            if (player.isActive()) {
                System.out.println("It is now " + player.getName() + "'s turn.");
                printPlayer(player);

                deploy(player);

                // after deploy phase and before attack phase
                System.out.println("Would you like to move to attack phase to pass your turn to the next player?");
                System.out.println("Type 'attack' or 'pass'.");
                response = scanner.next();
                while (!(response.equals("attack") | response.equals("pass"))) {
                    System.out.println("Type 'attack' or 'pass'.");
                    response = scanner.next();
                }
                if (response.equals("attack")) {
                    attack(player);
                }
                else if (response.equals("pass")) {
                    //add a statement confirming they don't want to attack
                }
            }
        }
    }

    /**
     * Gets the number of troops to be deployed in the deploy phase at the beginning of each turn
     * You get a minimum of 3 troops or the number of territories owned/3 + any continents bonus points
     *
     * @param player The current player
     * @return The number of troops to be deployed
     */
    private int getNumberOfTroops(Player player){
        int numberOfTerritories = player.territories.size();
        int continentBonusPoints = 0; // find out if player has any continents and how much each is worth

        for (Continent continent : player.getContinents()) {
            continentBonusPoints += continent.getContinentPoint();
        }
        return Math.max(numberOfTerritories / 3 + continentBonusPoints, 3);
    }

    /**
     * The deploy phase
     * @param player The current player
     */
    private void deploy(Player player){

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
                    for (int i = 0; i < deployTroops; i++) {
                        player.getArmy().addTroop(new Troop());
                    }
                    deployTroops -= x;
                    player.deploy(x, territory);
                } else {
                    System.out.println("Please choose a number between " + 1 + "-" + deployTroops);
                }
            }
        }
    }

    /**
     * Attacking phase
     *
     * @param player The attacking player
     *
     */
    private void attack(Player player){
        printPlayer(player);
        List<Territory> territories = player.getTerritories();

        System.out.println("From which of your territories would you like to attack from?");
        String t = scanner.next();
        Territory attackFrom = theMap.findTerritory(t);

        while(!(territories.contains(attackFrom))) {
            System.out.println("You cannot attack from here. Please pick a territory you have armies in");
            t = scanner.next();
            attackFrom = theMap.findTerritory(t);
        }

        System.out.println("You can attack any of the following territories: ");
        attackFrom.printAdjacentTerritories();
        System.out.print("Which territory would you like to attack?");
        t = scanner.next();
        Territory attack = theMap.findTerritory(t);
        ArrayList<Territory> neighbours = attackFrom.getNeighbourTerritories();

        while(!(neighbours.contains(attack))) {
            System.out.println("Please enter a territory that neighbours " + attackFrom.getName());
            t = scanner.next();
            attack = theMap.findTerritory(t);
        }

        System.out.println("With how many armies would you like to attack?");
        int armies = scanner.nextInt();
        int legalArmies = player.findTroops(attackFrom) - 1;

        while(armies > legalArmies || armies <= 0) {
            System.out.println("You cannot attack with those many armies");
            System.out.println("Please enter a number between " + "1 - " + legalArmies);
            armies = scanner.nextInt();
        }

        int numDice;
        if(armies > 3){
            numDice = 3;
        } else {
            numDice = armies;
        }
        player.attack(numDice, armies, attackFrom, attack);
        Player defender = attack.getCurrentPlayer();
        // call defend()

        Player winner = checkWinner(player, defender, numDice);
        if(winner.getName().equals(player.getName())){

            defender.removeTroops(2, attack);
            if(defender.findTroops(attack) == 0){
                defender.removeTerritory(attack);
                player.addTerritory(attack);
                if(defender.getTerritories().size() == 0){
                    defender.setActive(false);
                }
                boolean control = attack.getContinent().getControl(player);
                if(control) {
                    player.addContinent(attack.getContinent());

                }
            }
        } else {
            player.removeTroops(2, attack);
        }
    }

    /**
     * Prints a smaller overview for a specific player at the beginning of their turn including
     * the territories and continents that they own
     */
    private void printPlayer(Player player){
        System.out.println("You are occupying the following territories:");
        for (Territory t : player.getTerritories()){
            int troops = player.findTroops(t);
            System.out.println(t.getName() + " with " + troops + " troops.");
        }
        if (!player.getContinents().isEmpty()) {
            int count = 0;
            System.out.println("You occupy the following continents: ");
            for (Continent c : player.getContinents()){
                if (count < player.getContinents().size()) {
                    System.out.print(c + ", ");
                }
                else {
                    System.out.println(c);
                }
                count ++;
            }
        }
    }

    /**
     * Prints a view of the whole board including all territories, who owns them and how many troops
     * plus any player that owns whole continents and any inactive players
     */
    private void printGame(){
        // print all territories, who owns them and how many troops are in each
        for (Continent c : theMap.getContinents()) {
            for (Territory t : c.getTerritories()) {
                Player player = t.getCurrentPlayer();
                int troops = player.findTroops(t);
                System.out.println(t + " is owned by " + player + " and there are " + troops + " troops.");
            }
        }
        // print all players that own continents
        for (Player p : players){
            if (!p.getContinents().isEmpty()){
                int count = 0;
                System.out.print(p + " is in possession of: ");
                for (Continent c : p.getContinents()){
                    if (count < p.getContinents().size()) {
                        System.out.print(c + ", ");
                    }
                    else {
                        System.out.println(c);
                    }
                    count ++;
                }
            }
            // print any inactive players
            if (!p.isActive()) {
                System.out.println(p + " is out of the game.");
            }
        }
    }

    /**
     * Checks if there are any active players - if there is only
     * 1 active player then they have conquered the entire map
     *
     * @return true if there is still at least 2 active players
     */
    private boolean playersActive(){
        int x = 0;
        for (Player p : players){
            if (p.isActive()){
                x++;
                if (x == 2) return true;
            }
        }
        return false;
    }
}
