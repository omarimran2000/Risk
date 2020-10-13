import java.util.ArrayList;

public class Continent {

    private String name;
    private ArrayList<Territory> territories;
    private int continentPoint;

    public Continent(String name, ArrayList<Territory> territories, int continentPoint) {
        this.name = name;
        this.territories = territories;
        this.continentPoint = continentPoint;
    }
    public String getName() {
        return name;
    }

    public ArrayList<Territory> getTerritories() {
        return territories;
    }

    public int getContinentPoint() {
        return continentPoint;
    }
    public boolean getControl(){

        Player firstPlayer = territories.get(0).getCurrentPlayer();

        for (Territory territory:territories)
        {
            if (!(territory.getCurrentPlayer().equals(firstPlayer)))
            {
                return false;
            }
        }
        return true;
    }
}
