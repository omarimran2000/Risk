/**
 * The territory class that represents a territory on the board
 *
 * @author Omar Imran
 * @date October 13 2020
 */

import java.util.ArrayList;

public class Territory {

    private String name;
    private Continent continent;
    private ArrayList<Territory> neighbourTerritories;
    private Player currentPlayer;
    private Army currentArmies;

    /**
     *
     * @param name name of the territory
     * @param continent the continent the territory is located
     * @param neighbourTerritories the adjacent territories
     * @param currentPlayer the player who controls the territory
     * @param currentArmies the army on the territory
     */
    public Territory(String name, Continent continent, ArrayList<Territory> neighbourTerritories, Player currentPlayer, Army currentArmies) {
        this.name = name;
        this.continent = continent;
        this.neighbourTerritories = neighbourTerritories;
        this.currentPlayer = currentPlayer;
        this.currentArmies = currentArmies;
    }

    /**
     * Getter function for name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter function for continent
     * @return continent
     */

    public Continent getContinent() {
        return continent;
    }

    /**
     *
     * Getter function for neighbouring territories
     * @return territories
     */
    public ArrayList<Territory> getNeighbourTerritories() {
        return neighbourTerritories;
    }

    /**
     * Getter function for current player
     * @return the player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Setter function for current player
     * @param currentPlayer the player who controls the territory
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Getter function for armies
     * @return the army
     */
    public Army getCurrentArmies() {
        return currentArmies;
    }

    /**
     * Setter function for the armies
     * @param currentArmies setting the new armies
     */
    public void setCurrentArmies(Army currentArmies) {
        this.currentArmies = currentArmies;
    }
}
