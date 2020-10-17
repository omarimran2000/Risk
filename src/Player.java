import java.util.List;
import java.util.ArrayList;

/**
 * Player class that includes the player's army,
 * dice rolls, continents, and territories.
 *
 * @author Wintana Yosief
 * @version October 15, 2020
 *
 */
public class Player {

    String name;
    boolean active;
    Dice die;
    int [] diceRolls;
    Army army;
    List<Continent> continents;
    List<Territory> territories;


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
     * Gets the territories the player owns
     *
     * @return An ArrayList of territories owned by the player
     */
    public List<Territory> getTerritories()
    {
        return territories;
    }

    /**
     * Gets the player's dice rolls sorted greatest to smallest in value
     * @return An array of sorted integers representing the dice rolls
     */
    public int [] getDice()
    {
        return diceRolls;
    }

    /**
     * Player attacks another territory
     *
     * @param numOfDice The number of dice rolled by the attacker
     * @param numOfTroops The number of attacking troops
     * @param oldTerritory The territory you are attacking from
     * @param attackingTerritory The territory you are attacking
     */
    public void attack(int numOfDice, int numOfTroops, Territory oldTerritory, Territory attackingTerritory)
    {
        move(numOfTroops, oldTerritory, attackingTerritory);
        diceRolls = die.rollDice(numOfDice);
    }

    /**
     * Player defends against an attack
     *
     * @param numOfDice The number of dice rolled by the defender
     *
     */
    public void defend(int numOfDice)
    {
        diceRolls = die.rollDice(numOfDice);
    }

    /**
     * Moves troop(s) from their old territory into a new one
     * @param numberOfTroops The number of troops being moved
     * @param newTerritory The territory where the troops are being moved to
     */
    public void move(int numberOfTroops, Territory oldTerritory, Territory newTerritory) {
        List<Troop> troops = army.getTroops();
        int count = 0;
        while (count < numberOfTroops) {
            for (Troop troop : troops) {
                if (troop.getLocation() == oldTerritory) {
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
        int count = 0;
        while(count < numOfTroops){
            for(Troop troop: troops) {
                Territory loc = troop.getLocation();
                if (loc == removedTerritory) {
                    troop.setDeployed(false);
                    army.removeTroop(troop);
                    count += 1;
                }
            }

        }
    }

}


