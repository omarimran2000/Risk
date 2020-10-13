import java.util.ArrayList;

public class Territory {

    private String name;
    private Continent continent;
    private ArrayList<Territory> neighbourTerritories;

    public Territory(String name, Continent continent, ArrayList<Territory> neighbourTerritories) {
        this.name = name;
        this.continent = continent;
        this.neighbourTerritories = neighbourTerritories;
    }
    public String getName() {
        return name;
    }

    public Continent getContinent() {
        return continent;
    }


}
