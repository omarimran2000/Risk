/**
 * The map class that the current map on the board
 *
 * @author Omar Imran
 * @date October 13 2020
 */
import java.util.ArrayList;

public class Map {
    String name;
    ArrayList<Continent> continents;

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
     */
    public void addContinents(Continent c)
    {
        continents.add(c);
    }
}
