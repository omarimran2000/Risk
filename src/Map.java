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
     * @param continents the continents on the map
     */
    public Map(String name, ArrayList<Continent> continents) {
        this.name = name;
        this.continents = continents;
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
}
