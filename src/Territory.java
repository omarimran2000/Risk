/**
 * The territory class that represents a territory on the board
 *
 * @author Omar Imran
 * @date October 13 2020
 */

import javax.swing.*;
import java.util.ArrayList;

public class Territory {

    private String name;
    private Continent continent;
    private ArrayList<Territory> neighbourTerritories;
    private Player currentPlayer;

    /**
     * Constructor for Territory class
     *
     * @param name name of the territory
     * @param continent the continent the territory is located
     */
    public Territory(String name, Continent continent) {
        this.name = name;
        this.continent = continent;
        neighbourTerritories = new ArrayList<>();
        //this.territoryButton = new JButton(this.name);

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
     *
     * @return continent
     */
    public Continent getContinent() {
        return continent;
    }

    /**
     * Getter function for neighbouring territories
     *
     * @return territories
     */
    public ArrayList<Territory> getNeighbourTerritories() {
        return neighbourTerritories;
    }

    /**
     * gets all the neighbouring territories that aren't owned by a specific player
     *
     * @param player the specific player
     * @return the list of neighbouring territories
     */
    public ArrayList<Territory> getNeighbourTerritories(Player player) {
        ArrayList<Territory> territories = null;
        for(Territory t:neighbourTerritories)
        {
            if (t.currentPlayer != player) {
                territories.add(t);
            }
        }
        return territories;
    }

    /**
     * Add neighbouring territories
     *
     * @param t  the territory
     */
    public void addNeighbour(Territory t){neighbourTerritories.add(t); }

    /**
     * Getter function for current player
     *
     * @return the player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Setter function for current player
     *
     * @param currentPlayer the player who controls the territory
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * prints territory's name
     */
    public void printTerritory()
    {
        System.out.println("Territory: "+name);
    }

    /**
     * prints all adjacent territories
     */
    public void printAdjacentTerritories()
    {
        for(Territory t:neighbourTerritories)
        {
            t.printTerritory();
        }
    }

    /**
     * prints all adjacent territories that aren't owned by a specific player
     *
     * @param player the specific player
     */
    public void printAdjacentTerritories(Player player) {
        for(Territory t:neighbourTerritories)
        {
            if (t.currentPlayer != player) {
                t.printTerritory();
            }
        }
    }
}
