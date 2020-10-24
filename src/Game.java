/**
 * The main class for RISK where the map is loaded
 * and the players can perform actions through the
 * terminal
 *
 * @author Erica Oliver
 * @author Wintana Yosief
 * @author Santhosh Pradeepan
 * @author Omar Imran
 *
 * @version October 23 2020
 */

import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.Scanner;


public class Game {

    private Map theMap;
    private static List<Player> players;
    private static int numberOfPlayers;
    private static Scanner scanner = new Scanner(System.in);

    public Game() {
        theMap = new Map("Global");
        players = new ArrayList<>();
    }

    /**
     * Add a Player to players list
     *
     * @param player the player to be added
     */
    public static void addPlayer(Player player) {
        players.add(player);
    }


    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * This method is used to determine if a raid was successful or not. Each loser of a die roll will lose a troop and
     * if the defender no longer has any troops, return true
     *
     * @param attacker   the player that is initiating the raid
     * @param defender   the player that is defending
     * @param numOfDice  the number of dice that are being rolled
     * @param territory  the territory that is being attacked
     * @param attackFrom the territory the attacker is attacking from
     * @return false if defender survives the attack, true otherwise
     */
    private boolean checkWinner(Player attacker, Player defender, int numOfDice, Territory territory, Territory attackFrom) {
        int offence = 0;
        int defence = 0;
        for (int i = 0; i < numOfDice; i++) {

            offence = attacker.getDice()[i]; //access the die number saved at position i
            defence = defender.getDice()[i];
            if (offence > defence) {
                defender.removeTroops(1, territory);
                System.out.println(defender.getName() + " loses one troop.");
            } else {
                attacker.removeTroops(1, attackFrom);
                System.out.println(attacker.getName() + " loses one troop.");
            }
        }
        if (defender.findTroops(territory) == 0) {
            defender.removeTerritory(territory);
            territory.setCurrentPlayer(attacker);
            if(defender.getTerritories().size() == 0){
                System.out.println(defender.getName() + " has no more territories and is now out of the game.");
                defender.setActive(false);
            }
            return true;
        }
        return false;
    }

    /**
     * Loading a map from a JSON file given the specific keys where structure is
     * {continent:{territory:[adjacent territories],points:int}}
     *
     * @param JSONfile the filepath
     * @throws IOException
     * @throws ParseException
     */
    private void loadMap(String JSONfile) throws IOException, ParseException {
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

                for (int k = 0; k < adjacentTerritories.size(); k++)  //iterates through JSON array of adjacent territories
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
        List<Territory> territories = new ArrayList<>();
        Random random = new Random();
        for (Continent c : theMap.getContinents()) {
            for (Territory t : c.getTerritories()) {
                territories.add(t);
            }
        }
        for (int i = 0; !territories.isEmpty(); i++) {
            Territory tempTerritory = territories.remove(random.nextInt(territories.size()));
            Player tempPlayer = players.get(i % numberOfPlayers);

            tempPlayer.addTerritory(tempTerritory);
            tempTerritory.setCurrentPlayer(tempPlayer);

        }
    }

    /**
     * @param numberOfPlayers
     */
    public void setArmies(int numberOfPlayers) {
        int[] arr_num_Armies = new int[]{50, 35, 30, 25, 20};
        int num_armies = arr_num_Armies[numberOfPlayers - 2];
        Random random = new Random();

        for (Player p : players) {
            int armiesCount = num_armies;
            for (Territory t : p.getTerritories()) //puts one army in every territory owned by player
            {
                p.getArmy().addTroop(new Troop());
                p.deploy(1, t);
                armiesCount--;
            }
            while(armiesCount!=0)
            {
                int index = random.nextInt(p.getTerritories().size());
                p.getArmy().addTroop(new Troop());
                p.deploy(1, p.getTerritories().get(index));
                armiesCount--;
            }
            for(Continent c:theMap.getContinents())
            {
                if(c.getControl(p))
                {
                    p.addContinent(c);
                }
            }
        }
    }

    /**
     * plays the game
     */
    public void play() {
        String response = "";
        Player player = null;
        for (int i = 0; playersActive(); i++) { // infinite loop that will end once all players are inactive
            player = players.get(i % numberOfPlayers);
            if (player.isActive()) {
                System.out.println("It is now " + player.getName() + "'s turn.");
                printPlayer(player);

                deploy(player);

                // after deploy phase and before attack phase
                while (!response.equals("pass")) {
                    if (canAttack(player)) {
                        System.out.println("Would you like to attack or pass your turn to the next player?");
                        System.out.println("Type 'attack' or 'pass'.");
                        response = scanner.next();
                    } else {
                        System.out.println("You do not have enough troops to attack.");
                        break;
                    }

                    while (!(response.equals("attack") | response.equals("pass") | response.equals("help"))) {
                            System.out.println("Type 'attack' or 'pass'.");
                            response = scanner.next();
                    }
                    if (response.equals("attack")) {
                        if (canAttack(player)) {
                            attack(player);
                        } else {
                            System.out.println("You do not have enough troops to attack.");
                            break;
                        }
                    } else if (response.equals("pass")) {
                        response = "";
                        break;
                        //add a statement confirming they don't want to attack
                    } else if (response.equals("help")) {
                        printGame();
                        continue;
                    }
                }
            }
        }
        System.out.println("Congratulations! " + player.getName() + " is the winner!");
    }

    /**
     * Gets the number of troops to be deployed in the deploy phase at the beginning of each turn
     * You get a minimum of 3 troops or the number of territories owned/3 + any continents bonus points
     *
     * @param player The current player
     * @return The number of troops to be deployed
     */
    private int getNumberOfTroops(Player player) {
        int numberOfTerritories = player.getTerritories().size();
        int continentBonusPoints = 0; // find out if player has any continents and how much each is worth

        for (Continent continent : player.getContinents()) {
            continentBonusPoints += continent.getContinentPoint();
        }
        return Math.max(numberOfTerritories / 3 + continentBonusPoints, 3);
    }

    /**
     * The deploy phase
     *
     * @param player The current player
     */
    private void deploy(Player player) {
        scanner.nextLine();
        int deployTroops = getNumberOfTroops(player);
        while (deployTroops > 0) {

            System.out.println("You have " + deployTroops + " troops to deploy. Where would you like to deploy them?");
            String t = scanner.nextLine();
            Territory territory = theMap.findTerritory(t);

            if (t.equals("help")){
                printGame();
            }
            else if (!(player.getTerritories().contains(territory))) {
                System.out.println("Cannot deploy here. Pick another territory. ");
            }
            else if (deployTroops == 1) {
                player.deploy(deployTroops, territory);
            }
            else {
                System.out.println("How many would you like to deploy? ");
                int x = scanner.nextInt();
                scanner.nextLine();
                if (x <= deployTroops && (deployTroops - x) >= 0) {
                    for (int i = 0; i < x; i++) {
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
     */
    private void attack(Player player) {
        printPlayer(player);
        int numDice = 0, legalArmies, numDefendDice;
        List<Territory> territories = player.getTerritories();
        scanner.nextLine();
        String t;
        boolean attackFromChosen = false, attackChosen = false;
        Territory attackFrom = null, attack = null;
        while(!attackFromChosen) {
            System.out.println("Which of your territories would you like to attack from?");
            t = scanner.nextLine();
            attackFrom = theMap.findTerritory(t);

            if (t.equals("help")) {
                printGame();
                continue;
            } if(attackFrom == null){
                System.out.println("This territory does not exist.");
                continue;
            } if (!(territories.contains(attackFrom))) {
                System.out.println("You cannot attack from here. Please pick a territory you have armies in");
                continue;
            } if (player.findTroops(attackFrom) < 2) {
                System.out.println("Territory must have more than 1 troop to attack from");
                continue;
            }

            int x = attackFrom.getNeighbourTerritories().size() - 1;
            for (Territory territory : attackFrom.getNeighbourTerritories()) {
                if (!(territory.getCurrentPlayer() == player)) {
                        attackFromChosen = true;
                        break;
                } else if (x == 0) {
                    System.out.println("You own all neighbouring territories. Please pick a different territory to attack from");
                }
                x--;
            }
        }

        System.out.println("You can attack any of the following territories: ");
        attackFrom.printAdjacentTerritories(player);
        while(!attackChosen) {
            System.out.println("Which territory would you like to attack?");
            t = scanner.nextLine();
            attack = theMap.findTerritory(t);
            if (t.equals("help")) {
                printGame();
                continue;
            }
            if(attack==null)
            {
                System.out.println("Please enter a valid territory");
                continue;
            }
            else if(attack.getCurrentPlayer().equals(player))
            {
                System.out.println("You own this territory!");
                continue;
            }
            ArrayList<Territory> neighbours = attackFrom.getNeighbourTerritories();

            if(!(neighbours.contains(attack))) {
                System.out.println("Please enter a territory that neighbours " + attackFrom.getName());
                continue;
            }
            attackChosen = true;
        }
        legalArmies = player.findTroops(attackFrom) - 1;
        if (legalArmies == 1) {
            numDice = 1;
        } else if (legalArmies == 2) {
            while (numDice < 1 || numDice > 2) {
                System.out.println("How many dice would you like to roll? (1-2)");
                numDice = scanner.nextInt();
            }
        } else {
            while (numDice < 1 || numDice > 3) {
                System.out.println("How many dice would you like to roll? (1-3)");
                numDice = scanner.nextInt();
            }
        }
        player.rollDice(numDice);
        Player defender = attack.getCurrentPlayer();
        if (defender.findTroops(attack) == 1) {
            numDefendDice = 1;
        } else {
            numDefendDice = 2;
        }
        defender.rollDice(numDefendDice);

        if (defender.findTroops(attack) == 1 || numDice == 1) numDefendDice = 1;
        if (checkWinner(player, defender, numDefendDice, attack, attackFrom)) {
            int numMoveTroops = 0;
            legalArmies = player.findTroops(attackFrom);
            while (numMoveTroops < 1 || numMoveTroops > (legalArmies - 1)) {
                System.out.println("How many troops would you like to move to " + attack.getName() + "? (1-" + (legalArmies - 1) + ")");
                numMoveTroops = scanner.nextInt();
            }
            player.attackWin(numMoveTroops, attackFrom, attack);
        }
    }

    /**
     * Prints a smaller overview for a specific player at the beginning of their turn including
     * the territories and continents that they own
     *
     * @param player the player to be printed
     */
    private void printPlayer(Player player){
        System.out.println("You are occupying the following territories:");
        for (Territory t : player.getTerritories()){
            int troops = player.findTroops(t);
            System.out.println(t.getName() + " with " + troops + " troops.");
        }
        if (!player.getContinents().isEmpty()) {
            System.out.println("You occupy the following continents: ");
            for (Continent c : player.getContinents()){
                System.out.println(c.getName());
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
                System.out.println(t.getName() + " is owned by " + player.getName() + " and there are " + troops + " troops.");
            }
        }
        // print all players that own continents
        for (Player p : players){
            if (!p.getContinents().isEmpty()){
                int count = 0;
                System.out.print(p + " is in possession of: ");
                for (Continent c : p.getContinents()){
                    if (count < p.getContinents().size()) {
                        System.out.print(c.getName() + ", ");
                    }
                    else {
                        System.out.println(c.getName());
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

    public boolean canAttack(Player player) {
        List<Territory> territories = player.getTerritories();
        for (Territory t : territories) {
            if (player.findTroops(t) > 1){
                List<Territory> neighbours = t.getNeighbourTerritories();
                for(Territory terr: neighbours){
                    if(!(territories.contains(terr))){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        Game game = new Game();
        game.loadMap("map.json");

        System.out.println("Welcome to RISK! Build your army, attack enemy territories, and take over the world");
        System.out.println("How many players are playing? Enter a number between 2-6");
        numberOfPlayers = scanner.nextInt();

        while (!(numberOfPlayers >= 2 && numberOfPlayers <= 6)) {
            System.out.println("Please enter a number between 2-6");
            numberOfPlayers = scanner.nextInt();

        }
        // if a valid number of player is inputted
        // get and print every player's name and adds them to players
        {
            for (int i = 0; i < numberOfPlayers; i++) {
                System.out.print("Player name: ");
                String name = scanner.next();
                Player player = new Player(name);
                player.setActive(true);
                System.out.println(player.getName() + " added");
                addPlayer(player);
            }
        }

        // setup the army placements
        game.initializeDefaultArmy();
        game.setArmies(numberOfPlayers);

        System.out.println("Ready to start the game");
        System.out.println("Type 'help' at any point to print an overview of the entire board");
        // ready to begin playing
        game.play();
    }
}
