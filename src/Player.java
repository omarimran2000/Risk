
import java.util.List;
import java.util.ArrayList;

/**
 * Player class that includes the player's army,
 * dice rolls, continents, and territories.
 *
 * @author Wintana Yosief
 * @version October 17, 2020
 *
 */
public class Player {

    private String name;
    private boolean active;
    private Dice die;
    private int [] diceRolls;
    private Army army;
    private List<Continent> continents;
    private List<Territory> territories;


    /**
     *
     * @param name The player's name
     */
    public Player(String name) {
        this.name = name;
        active = true;
        army = new Army();
        continents = new ArrayList<>();
        territories = new ArrayList<>();
        die = new Dice();
        diceRolls = new int[3];
    }

    /**
     * Gets the player's name
     * @return The player's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the player's current state,
     *A player becomes inactive when they
     * get eliminated.
     * @return True if playing, false if eliminated
     */
    public boolean isActive(){
        return active;
    }

    /**
     * Sets the player's active state
     * @param active The player's new active state
     */
    public void setActive(boolean active){
        this.active = active;

    }

    /**
     * Gets the player's army
     *
     * @return The player's army
     */
    public Army getArmy()
    {
        return army;
    }

    /**
     * Gets an ArrayList of continents owned by the player
     *
     * @return An ArrayList of continents owned by the player
     */
    public List<Continent> getContinents()
    {
        return continents;
    }

    /**
     * Adds a continent to a player's list
     *
     * @param c The continent to be added
     */
    public void addContinent(Continent c){
        continents.add(c);
    }

    /**
     * Removes a continent from the player's list
     *
     * @param c The continent that the player has lost
     */
    public void removeContinent(Continent c) {
        Continent removeContinent = null;
        for (Continent continent : continents) {
            if (continent == c) {
                removeContinent = c;
                break;
            }
        }
        continents.remove(removeContinent);
    }

    /**
     * Adds a territory to the list of continents that player possesses
     *
     * @param t The territory that the play now owns
     */
    public void addTerritory(Territory t) {
        territories.add(t);
    }

    /**
     * Removes a territory from the player's list
     *
     * @param t The territory the player has lost
     */
    public void removeTerritory(Territory t) {
        Territory deletedTerritory =null;
        for (Territory territory : territories) {
            if (t == territory) {
                if(territory.getContinent().getControl(this)) {
                    removeContinent(territory.getContinent());
                }
                deletedTerritory = t;
                break;
            }
        }
        territories.remove(deletedTerritory);
    }
    /**
     * Gets the territories the player owns
     *
     * @return An ArrayList of territories owned by the player
     */
    public List<Territory> getTerritories()
    {
        return territories;
    }

    /**
     * Rolls multiple dice
     *
     * @param numOfDice the number of dice to roll
     */
    public void rollDice(int numOfDice) {
        diceRolls = die.rollDice(numOfDice);
    }

    /**
     * Gets the player's dice rolls sorted greatest to smallest in value
     *
     * @return An array of sorted integers representing the dice rolls
     */
    public int [] getDice()
    {
        return diceRolls;
    }

    /**
     * Player successfully attacks another territory
     *
     * @param numOfTroops The number of attacking troops
     * @param oldTerritory The territory you are attacking from
     * @param attackingTerritory The territory you are attacking
     */
    public void attackWin(int numOfTroops, Territory oldTerritory, Territory attackingTerritory)
    {
        addTerritory(attackingTerritory);
        if(attackingTerritory.getContinent().getControl(this)) {
            addContinent(attackingTerritory.getContinent());
        }
        move(numOfTroops, oldTerritory, attackingTerritory);
    }

    /**
     * Moves troop(s) from their old territory into a new one
     *
     * @param numberOfTroops The number of troops being moved
     * @param newTerritory The territory where the troops are being moved to
     */
    public void move(int numberOfTroops, Territory oldTerritory, Territory newTerritory) {
        List<Troop> troops = army.getTroops();
        int count = 0;
        while (count < numberOfTroops) {
            for (Troop troop : troops) {
                if (troop.getLocation() == oldTerritory && count < numberOfTroops ) {
                    troop.setLocation(newTerritory);
                    count += 1;
                }
            }

        }
    }

    /**
     * Deploys a number of troops into a territory.
     * These troops do not have a previous deployment.
     *
     * @param numberOfTroops The number of troops
     * @param deployment The territory the troops go
     */
    public void deploy(int numberOfTroops, Territory deployment){
        List<Troop> troops = army.getTroops();
        int count = 0;
        while(count < numberOfTroops){
            for(Troop troop: troops){
                if(!troop.isDeployed()) {
                    troop.setDeployed(true);
                    troop.setLocation(deployment);
                    count += 1;
                }
            }

        }
    }

    /**
     * Removes troop(s) from a player's army
     *
     * @param numOfTroops The number of troops to be removed
     * @param removedTerritory The territory to remove them from
     */
    public void removeTroops(int numOfTroops, Territory removedTerritory)
    {
        List<Troop> troops = army.getTroops();
        Troop deletedTroop = null;
        int count = 0;
        while(count < numOfTroops) {
            for (Troop troop : troops) {
                Territory loc = troop.getLocation();
                if (loc == removedTerritory) {
                    troop.setDeployed(false);
                    deletedTroop = troop;
                    count += 1;
                    break;
                }
            }
            army.removeTroop(deletedTroop);
        }
    }

    public int findTroops(Territory t){
        List<Troop> troops = army.getTroops();
        int count = 0;
        for(Troop troop : troops){
            if(troop.getLocation() == t){
                count += 1;
            }
        }
        return count;

    }

}



