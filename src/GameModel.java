import org.json.simple.*;
import org.json.simple.parser.*;

import javax.swing.DefaultListModel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.Scanner;

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
 * @version October 25 2020
 */
public class GameModel {

    private Map theMap;
    private static List<Player> players;
    private static int numberOfPlayers;
    private final static int LOSE_TROOP = 1;
    private final static int DEPLOY_SINGLE_TROOP = 1;
    private final static int[] DICE = {1, 2, 3};
    private static Scanner scanner = new Scanner(System.in);
    private GameView view;
    private Player currentPlayer;
    private String status;
    private final int MIN_DEPLOY_TROOPS = 3;
    private final int DEPLOY_TERRITORY_DIVISOR = 3;

    public GameModel() {
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

    public void setNumberOfPlayers(int num){ numberOfPlayers = num;}

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Map getTheMap() {
        return theMap;
    }

    public void setView(GameView view)
    {
        this.view = view;
    }

    public void setStatus(String status){
        this.status = status;
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
                defender.removeTroops(LOSE_TROOP, territory);
                setStatus(defender.getName() + " loses one troop.");
                view.attack(status);
              //  System.out.println(defender.getName() + " loses one troop.");
            } else {
                attacker.removeTroops(LOSE_TROOP, attackFrom);
                setStatus(attacker.getName() + " loses one troop");
                view.attack(status);
             //   System.out.println(attacker.getName() + " loses one troop.");
            }
        }
        if (defender.findTroops(territory) == 0) {
            defender.removeTerritory(territory);
            territory.setCurrentPlayer(attacker);
            if(defender.getTerritories().size() == 0){
                setStatus(defender.getName() + " has no more territories and is now out of the game.");
                view.attack(status);
             //   System.out.println(defender.getName() + " has no more territories and is now out of the game.");
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
    public void loadMap(String JSONfile) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        Object e =null;
        try  //for JAR file
        {
            InputStream in = getClass().getResourceAsStream("/"+JSONfile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            e = jsonParser.parse(reader);
        }
        catch(Exception ex) {  //for IDE
              FileReader fileReader = new FileReader(JSONfile);
              e = jsonParser.parse(fileReader);
        }

        JSONObject mapObject = new JSONObject();
        mapObject = (JSONObject) e;

        JSONObject continents = (JSONObject) mapObject.get("continents"); //getting all the continent keys
        theMap.setFilePath((String) mapObject.get("filepath"));

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

                JSONObject territoriesKeys = (JSONObject) territories.get(territoryName);
                JSONObject coordinates = (JSONObject) territoriesKeys.get("coordinates");
                int x = (int)((long) coordinates.get("x"));
                int y = (int)((long) coordinates.get("y"));
                view.addButtons(temp,x,y);
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
     * Randomly allocates armies into territories
     *
     * @param numberOfPlayers The number of players
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
                p.deploy(DEPLOY_SINGLE_TROOP, t);
                armiesCount--;
            }
            while(armiesCount!=0)
            {
                int index = random.nextInt(p.getTerritories().size());
                p.getArmy().addTroop(new Troop());
                p.deploy(DEPLOY_SINGLE_TROOP, p.getTerritories().get(index));
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
     * Function to pass turn
     */
    public void passTurn()
    {
        int temp = 0;
        for (int i=0;i<players.size();i++) //find index of current player
        {
            if(players.get(i).equals(currentPlayer))
            {
                temp = i;
                break;
            }
        }
        for(int i = (temp+1)%players.size();i<players.size();i++) //find next active player
        {
            if(players.get(i).isActive())
            {
                currentPlayer = players.get(i);
                return;
            }
            if(i==players.size()-1)
            {
                i = 0;
            }
        }
        //view.pass();
    }

    /**
     * Plays the game
     */
    public void play() {
        String response = "";
        for (int i = 0; playersActive(); i++) { // infinite loop that will end once all players are inactive
            currentPlayer = players.get(i % numberOfPlayers);
            if (currentPlayer.isActive()) {
                System.out.println("It is now " + currentPlayer.getName() + "'s turn.");
                printPlayer(currentPlayer);


                //deploy();


                // after deploy phase and before attack phase
                while (!response.equals("pass")) {

                        System.out.println("Would you like to attack or pass your turn to the next player?");
                        System.out.println("Type 'attack' or 'pass'.");
                        response = scanner.next();

                    while (!(response.equals("attack") | response.equals("pass") | response.equals("help"))) {
                            System.out.println("Type 'attack' or 'pass'.");
                            response = scanner.next();
                    }
                    if (response.equals("attack")) {
                        if(canAttack(currentPlayer)) {
                            //attack(player);
                        } else {
                            System.out.println("You do not have enough troops to attack.");
                            break;

                        }
                    } else if (response.equals("pass")) {
                        response = "";
                        break;
                        //add a statement confirming they don't want to attack
                    } else  {
                        printGame();
                        continue;
                    }
                }
            }
        }
        System.out.println("Congratulations! " + currentPlayer.getName() + " is the winner!");
    }

    /**
     * Gets the number of troops to be deployed by the current player in the deploy phase at the beginning of each turn
     * You get a minimum of 3 troops or the number of territories owned/3 + any continents bonus points
     *
     * @return The number of troops to be deployed
     */
    public int getNumberOfTroops() {
        Player player = currentPlayer;
        int numberOfTerritories = player.getTerritories().size();
        int continentBonusPoints = 0; // find out if player has any continents and how much each is worth

        for (Continent continent : player.getContinents()) {
            continentBonusPoints += continent.getContinentPoint();
        }
        return Math.max(numberOfTerritories / DEPLOY_TERRITORY_DIVISOR + continentBonusPoints, MIN_DEPLOY_TROOPS);
    }

    /**
     * The deploy phase
     *
     * @param territory The territory to receive troops
     * @param numTroops The number of troops to be deployed
     */
    public void deploy(Territory territory, int numTroops) {


            for (int i = 0; i < numTroops; i++) {
                currentPlayer.getArmy().addTroop(new Troop());
            }
            view.setTroopsDeployed(numTroops);
            view.deploy();
            currentPlayer.deploy(numTroops, territory);

            //view.setTextArea("You have " + deployTroops + " troops to deploy. Where would you like to deploy them?");
            //String t = String.valueOf(view.getDeployToList().getSelectedValue()) ;
            //Territory territory = theMap.findTerritory(t);

            //if (!(currentPlayer.getTerritories().contains(territory))) {
            //    view.setTextArea("Cannot deploy here. Pick another territory. ");
            //}
            //else if (deployTroops == 1) {
            //    currentPlayer.deploy(deployTroops, territory);
            //}
            //else {
                //view.setTextArea("How many would you like to deploy? ");
                //int x = (Integer) view.getNumTroops().getValue();
                //if (x <= deployTroops && (deployTroops - x) >= 0) {
                    /*for (int i = 0; i < numTroops; i++) {
                        currentPlayer.getArmy().addTroop(new Troop());
                    }
                    deployTroops -= numTroops;
                    currentPlayer.deploy(numTroops, territory);*/
                //} else {
                //    view.setTextArea("Please choose a number between " + 1 + "-" + deployTroops);
                //}
            //}
    }

    /**
     * Attacking phase
     *
     * @param attackFrom the Territory the player is attacking from
     * @param attack the Terrritory the player is attacking
     * @param numDice the number of dice the attacker is using
     */
    public boolean attack(Territory attackFrom,Territory attack,int numDice) {
        //printPlayer(currentPlayer);
        setStatus(currentPlayer.getName() + " is attacking " + attack.getName() + " from " + attackFrom.getName());
        view.attack(status);
        int legalArmies, numDefendDice;
        //List<Territory> territories = currentPlayer.getTerritories();
        //scanner.nextLine();
      //  String t;
   //     boolean attackFromChosen = false, attackChosen = false;
   //     Territory attackFrom = null, attack = null;
        /*
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

         */
        currentPlayer.rollDice(numDice);
        Player defender = attack.getCurrentPlayer();
        if (defender.findTroops(attack) == 1 || numDice == 1) {
            numDefendDice = DICE[0];
        } else {
            numDefendDice = DICE[1];
        }
        defender.rollDice(numDefendDice);

        if (checkWinner(currentPlayer, defender, numDefendDice, attack, attackFrom)) {
            int numMoveTroops = 0;
            //legalArmies = currentPlayer.findTroops(attackFrom);
            //view.attackWon(attack);
            /*
            while (numMoveTroops < 1 || numMoveTroops > (legalArmies - 1)) {
                System.out.println("How many troops would you like to move to " + attack.getName() + "? (1-" + (legalArmies - 1) + ")");
                numMoveTroops = scanner.nextInt();
            }
            */
           //currentPlayer.attackWin(numMoveTroops, attackFrom, attack);
           view.attackWon(attack, currentPlayer.findTroops(attackFrom));
           return true;
        }
        return false;


    }

    /**
     * method used to get the current player
     *
     * @return the current player
     */
    public Player getPlayer() { return currentPlayer; }

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
                System.out.print(p.getName() + " is in possession of: ");
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
                System.out.println(p.getName() + " is out of the game.");
            }
        }
    }

    /**
     * Checks if there are any active players - if there is only
     * 1 active player then they have conquered the entire map
     *
     * @return true if there is still at least 2 active players
     */
    public boolean playersActive(){
        int x = 0;
        for (Player p : players){
            if (p.isActive()){
                x++;
                if (x == 2) return true;
            }
        }
        return false;
    }

    /**
     * Checks if a player has enough troops to attack
     *
     * @param player The player wanting to attack
     * @return True, if player can attack, false otherwise
     */
    public boolean canAttack(Player player) {
        List<Territory> territories = player.getTerritories();
        for (Territory t : territories) {
            if (player.findTroops(t) > 1){

                // iterates through neighbour's of t
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
     * Converts an ArrayList to a DefaultListModel
     *
     * @param list the ArrayList
     * @return the converted DefaultListModel
     */
    public DefaultListModel<Territory> defaultListConversion(ArrayList<Territory> list) {
        DefaultListModel<Territory> model = new DefaultListModel<>();
        for(Territory t : list) {
            model.addElement(t);
        }
        return model;
    }

    /**
     * method used to create players
     *
     * @param names the ArrayList filled with player names
     */
    public void createPlayers(ArrayList<String> names) {
        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = new Player(names.get(i));
            player.setActive(true);
            addPlayer(player);
        }
        initializeDefaultArmy();
        setArmies(numberOfPlayers);
        currentPlayer = players.get(0);
        view.start();
        view.turn(currentPlayer, getNumberOfTroops());




    }

    /**
     * Calculates the number of dice given a territory
     * @param attackFrom territory to attack from
     * @return number of dice
     */
    public int calculateDice(Territory attackFrom) {
        int legalArmies = currentPlayer.findTroops(attackFrom) - 1;
        int numDice = 0;
        if (legalArmies == 1) {
            numDice = DICE[0];
        } else if (legalArmies == 2) {
            numDice = DICE[1];
        }
        else
        {
            numDice = DICE[2];
        }
        return numDice;
    }

    /**
     * checked if the game is over
     *
     * @return true if game is over, false otherwise
     */
    public boolean checkGameOver(){
        if(!playersActive()){
            return true;

        }
        return false;
    }

    /**
     * gets the winner of the game
     * @return the winner
     */
    public Player getWinner(){
        for(int i = 0; i < numberOfPlayers; i++){
            if(players.get(i).isActive()){
                return players.get(i);
            }
        }
        return null;
    }

    /**
     * @param args
     * @throws IOException
     * @throws ParseException
     */
     /*
    public static void main(String[] args) throws IOException, ParseException {
        GameModel game = new GameModel();
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
    */
}
