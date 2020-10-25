import java.util.ArrayList;
import java.util.List;

/**
 * Army class representing each player's
 * army
 *
 * @version October 23, 2020
 *
 * @author Wintana Yosief
 */
public class Army {

    private List<Troop> troops;

    /**
     * Creates an army
     */
    public Army()
    {
        troops = new ArrayList<>();
    }

    /**
     * Gets the list of troops
     * @return List of troops in army
     */
    public List<Troop> getTroops()
    {
        return troops;
    }

    /**
     * Adds a troop to the army
     * @param newTroop The troop to be added
     */
    public void addTroop(Troop newTroop)
    {
        if(newTroop != null){
            troops.add(newTroop);
        }

    }

    /**
     * Removes a troop from the army
     * @param removeTroop The troop to be removed
     */
    public void removeTroop(Troop removeTroop)
    {
        troops.remove(removeTroop);

    }


}
