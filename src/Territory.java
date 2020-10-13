import java.util.ArrayList;

public class Territory {

    private String name;
    private Continent continent;
    private ArrayList<Territory> neighbourTerritories;
    private Player currentPlayer;
    private Army currentArmies;

    public Territory(String name, Continent continent, ArrayList<Territory> neighbourTerritories, Player currentPlayer, Army currentArmies) {
        this.name = name;
        this.continent = continent;
        this.neighbourTerritories = neighbourTerritories;
        this.currentPlayer = currentPlayer;
        this.currentArmies = currentArmies;
    }
    public String getName() {
        return name;
    }

    public Continent getContinent() {
        return continent;
    }

    public ArrayList<Territory> getNeighbourTerritories() {
        return neighbourTerritories;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Army getCurrentArmies() {
        return currentArmies;
    }

    public void setCurrentArmies(Army currentArmies) {
        this.currentArmies = currentArmies;
    }
}
