/**
 * The map class that the current map on the board
 *
 * @author Omar Imran
 * @date October 13 2020
 */
import java.util.ArrayList;

public class Map {
    private String name;
    private ArrayList<Continent> continents;
    private String filePath;

    /**
     *
     * @param name of the map
     */
    public Map(String name) {
        this.name = name;
        this.continents = new ArrayList<>();
    }

    /**
     * Getter function for name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter function for continents
     * @return the continents
     */
    public ArrayList<Continent> getContinents() {
        return continents;
    }

    /**
     * Add to continents
     *
     * @param c  the continent
     */
    public void addContinents(Continent c)
    {
        continents.add(c);
    }

    /**
     * Finds the territory on the map
     * specified by its name
     *
     * @param name The name of the territory
     * @return the territory
     *
     */
    public Territory findTerritory(String name)
    {
        for(Continent c:continents)
        {
            for (Territory t: c.getTerritories())
            {
                if((t.getName().toLowerCase().equals(name.toLowerCase())))
                {
                    return  t;
                }
            }
        }
        return null;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
