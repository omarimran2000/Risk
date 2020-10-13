import java.util.ArrayList;

public class Continent {

    private String name;
    private ArrayList<Territory> territories;

    public Continent(String name, ArrayList<Territory> territories) {
        this.name = name;
        this.territories = territories;
    }
}
