import javax.swing.*;

public class TerritoryButton extends JButton {

    private Territory territory;

    public TerritoryButton(Territory t) {
        super();
        territory = t;
    }

    public TerritoryButton(String text, Territory t) {
        super(text);
        territory = t;
    }

    public Territory getTerritory() { return territory; }
}
