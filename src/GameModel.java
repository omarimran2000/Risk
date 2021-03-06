import org.json.simple.*;
import org.json.simple.parser.*;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
public class GameModel implements Serializable {

    private final Map theMap;
    private List<Player> players;
    private int numberOfPlayers;
    private final static int LOSE_TROOP = 1;
    public final static int DEPLOY_SINGLE_TROOP = 1;
    public final static int[] DICE = {1, 2, 3};
    private final static int MAX_PLAYERS = 6;
    private ArrayList<GameModelListener> listeners;
    protected Player currentPlayer;
    private String status;
    private final int MIN_DEPLOY_TROOPS = 3;
    private final int DEPLOY_TERRITORY_DIVISOR = 3;
    private final static int MIN_PLAYERS = 2;
    private final static int MIN_TROOPS = 1;
    private final static int MIN_DICE = 1;
    public enum Phase {DEPLOY, ATTACK, FORTIFY}
    private Phase phase;

    public GameModel() {
        theMap = new Map("Global");
        listeners = new ArrayList<>();
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Add a GameModelListener
     *
     * @param l the GameModelListener
     */
    public void addListener(GameModelListener l)
    {
        listeners.add(l);
    }

    /**
     * Set the number of players in a game
     * @param num the number of players
     */
    public void setNumberOfPlayers(int num){ numberOfPlayers = num;}

    /**
     * Getter for number of players
     * @return number of players
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Getter for the map
     * @return gets the map
     */
    public Map getTheMap() {
        return theMap;
    }

    /**
     * Sets the status of what is happening to inform view
     * @param status the updated status
     */
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
                setStatus(defender.getName() + " lost one troop.");
            } else {
                attacker.removeTroops(LOSE_TROOP, attackFrom);
                setStatus(attacker.getName() + " lost one troop");
            }
            attacker.attackPhase(status);
        }
        if (defender.findTroops(territory) == 0) {
            defenderLost(territory, defender, attacker);
            return true;
        }
        return false;
    }
    /**
     * Actions for when a territory is conquered in an attack
     *
     * @param territory The attacked territory
     * @param defender The player defending the territory
     * @param attacker The player attacking the territory
     */
    private void defenderLost(Territory territory, Player defender, Player attacker){
        defender.removeTerritory(territory);
        territory.setCurrentPlayer(attacker);
        if(defender.getTerritories().size() == 0){
            setStatus(defender.getName() + " has no more territories and is now out of the game.");

            for(GameModelListener l:listeners)
            {
                l.attack(status);
            }
            defender.setActive(false);
        }
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
        Object e;
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

        setTerritories(continents);
        setAdjacentTerritories(continents);

        if(! theMap.checkValidMap())
        {
            throw new IOException("Invalid input");
        }
    }

    /**
     * Helper method to set territories of the map
     * @param continents the JSON object representing continents
     */
    private void setTerritories(JSONObject continents)  //M4
    {
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
                //view.addButtons(temp,x,y);
                for(GameModelListener l:listeners)
                {
                    l.addButtons(temp,x,y);
                }
            }
        }
    }
    /**
     * Helper method to set adjacent territories of the map
     * @param continents the JSON object representing continents
     *
     */
    private void setAdjacentTerritories(JSONObject continents)  //M4
    {
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
        int num_armies = arr_num_Armies[numberOfPlayers - MIN_PLAYERS];
        Random random = new Random();

        for (Player p : players) {
            int armiesCount = num_armies;
            for (Territory t : p.getTerritories()) //puts one army in every territory owned by player
            {
                p.deploy(DEPLOY_SINGLE_TROOP, t);
                armiesCount--;
            }
            while(armiesCount!=0)
            {
                int index = random.nextInt(p.getTerritories().size());

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
                currentPlayer.deployPhase();
                return;
            }
            if(i==players.size()-1)
            {
                i = 0;
            }
        }
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

        for (GameModelListener l : listeners) {
            l.setTroopsDeployed(numTroops);
            setStatus(currentPlayer.getName()+" deployed "+numTroops+" troops to "+territory.getName());
            l.deploy(status);
        }
        currentPlayer.deploy(numTroops, territory);

    }


    /**
     * Attacking phase
     *
     * @param attackFrom the Territory the player is attacking from
     * @param attack the Territory the player is attacking
     * @param numDice the number of dice the attacker is using
     */
    public boolean attack(Territory attackFrom, Territory attack, int numDice) {
        setStatus(currentPlayer.getName() + " attacked " + attack.getName() + " from " + attackFrom.getName());

        currentPlayer.attackPhase(status);
        int numDefendDice;
        currentPlayer.rollDice(numDice);
        Player defender = attack.getCurrentPlayer();
        if (defender.findTroops(attack) == MIN_TROOPS || numDice == MIN_DICE) {
            numDefendDice = DICE[0];
        } else {
            numDefendDice = DICE[1];
        }
        defender.rollDice(numDefendDice);

        if (checkWinner(currentPlayer, defender, numDefendDice, attack, attackFrom)) {
            for (GameModelListener l : listeners) {
                l.attackWon(attack, currentPlayer.findTroops(attackFrom));
            }
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
            if (player.findTroops(t) > MIN_TROOPS){
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
            for(GameModelListener l:listeners)
            {
                player.addListener(l);
            }
            player.setActive(true);
            addPlayer(player);
        }
        if(numberOfPlayers < MAX_PLAYERS) {
            addAIPlayer();
        }

        gameInit();
    }
    /**
     * Adds one AI Player to the game
     */
    private void addAIPlayer(){
        AIPlayer ai = new AIPlayer("AI X", this);
        ai.setActive(true);
        for(GameModelListener l:listeners)
        {
            ai.addListener(l);
        }
        addPlayer(ai);
        setNumberOfPlayers(numberOfPlayers + 1);
    }
    /**
     * Initializes the players with troops
     */
    private void gameInit()
    {
        initializeDefaultArmy();
        setArmies(numberOfPlayers);
        currentPlayer = players.get(0);
        for(GameModelListener l:listeners)
        {
            l.start();
            l.turn(currentPlayer, getNumberOfTroops());
        }
    }
    /**
     * Calculates the number of dice given a territory
     * @param attackFrom territory to attack from
     * @return number of dice
     */
    public int calculateDice(Territory attackFrom) {
        int legalArmies = currentPlayer.findTroops(attackFrom) - MIN_TROOPS;
        int numDice;
        if (legalArmies == 1) {
            numDice = DICE[0];
        } else if (legalArmies == 2) {
            numDice = DICE[1];
        }
        else {
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
     * Getter function for players
     * @return all the players
     */
    public List<Player> getPlayers(){
        return players;
    }

    /**
     * Fortifies a chosen territory by moving troops from a connected territory
     * @param numTroops Number of troops being moved
     * @param fortifyFrom Territory donating troops
     * @param fortifyTo Territory receiving troops
     */
    public void fortify(int numTroops, Territory fortifyFrom, Territory fortifyTo){
        currentPlayer.move(numTroops, fortifyFrom, fortifyTo);
        setStatus(currentPlayer.getName() + " fortified " + fortifyTo.getName() + " with " + numTroops + " troop(s)");
        for(GameModelListener l : listeners){
            l.fortify(status);
        }
    }




    /**
     * Checks to see if a player is able to fortify
     * @return true or false
     */
    public boolean canFortify()
    {
        for(Territory t:currentPlayer.getTerritories())
        {
            if(currentPlayer.ownANeighbour(t))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the phase
     * The phases of a turn are Deploy, Attack and Fortify
     * @param phase The current phase of the game
     */
    public void setPhase(Phase phase){
        this.phase = phase;
    }

    /**
     * Getter method for phase
     * @return phase The current phase of the game
     */
    public Phase getPhase(){
        return phase;
    }


    /**
     * Saves the game model to a file
     *
     * @param filename The name of the file
     */
    public void saveGame(String filename){
        if(filename.isEmpty()){
            return;
        }
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
            outputStream.writeObject(this);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a game model from a serialized file
     *
     * @param filename The name of the serialized file
     */
    public void loadGame(String filename){
        if(filename.isEmpty()){
            return;
        }
        try{
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
            GameModel model = (GameModel) inputStream.readObject();


            this.players = model.players;
            this.currentPlayer = model.currentPlayer;
            this.numberOfPlayers = model.numberOfPlayers;
            this.phase = model.phase;
            this.status = model.status;

            for(GameModelListener ml: model.listeners){
                 ml.restoreView(this.phase, this.status);
            }
             inputStream.close();



        } catch (ClassNotFoundException | IOException e) {

            e.printStackTrace();
        }

    }

}
