import javax.swing.*;
/**
 * A class that represents a special kind of button that is placed on territories on the maps
 *
 * @author Santhosh Pradeepan
 * @version November 8th 2020
 */

public class TerritoryButton extends JButton {

    private Territory territory;

    /**
     * Constructor for the territory button
     * @param t the territory it is placed on
     */
    public TerritoryButton(Territory t) {
        super();
        territory = t;
    }

    /**
     * Enables or disables the button and makes them visual or not
     * @param enable to turn on or off the button
     */
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        super.setVisible(enable);
    }

    /**
     * Get the territory that the button represents
     * @return territory
     */
    public Territory getTerritory() { return territory; }
}
