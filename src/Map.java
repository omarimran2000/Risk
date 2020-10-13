import java.util.ArrayList;

public class Map {
    String name;
    ArrayList<Continent> continents;

    public Map(String name, ArrayList<Continent> continents) {
        this.name = name;
        this.continents = continents;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Continent> getContinents() {
        return continents;
    }
}
