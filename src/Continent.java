/**
 * The continent class that represents a continent on the board
 *
 * @author Omar Imran
 * @date October 13 2020
 */

import java.util.ArrayList;

public class Continent {

    private String name;
    private ArrayList<Territory> territories;
    private int continentPoint;

    /**
     * Default constructor
     *
     * @param name the name of the continent
     * @param continentPoint the amount of extra soldiers a player gets for holding the continent
     */
    public Continent(String name, int continentPoint) {
        this.name = name;
        this.territories = new ArrayList<>();
        this.continentPoint = continentPoint;
    }

    /**
     * Getter function for name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter function for territories
     * @return territories
     */
    public ArrayList<Territory> getTerritories() {
        return territories;
    }

    /**
     * Getter function for extra points
     * @return the amount of extra soldiers
     */
    public int getContinentPoint() {
        return continentPoint;
    }

    /**
     * Add to territories
     * @param t the territory
     */
    public void addTerritories(Territory t){territories.add(t);}


    /**
     * Checks to see if a player has all the territories in the continent
     * @param p the player being compared
     * @return if player holds entire continent or not
     */
    public boolean getControl(Player p){

        for (Territory territory:territories)
        {
            if (!(territory.getCurrentPlayer().equals(p)))
            {
                return false;
            }
        }
        return true;
    }
}
