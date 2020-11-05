import javax.swing.*;

public class territoryButton extends JButton {

    private Territory territory;

    public territoryButton(Territory t) {
        super();
        territory = t;
    }

    public territoryButton(String text, Territory t) {
        super(text);
        territory = t;
    }

    public Territory getTerritory() { return territory; }
}
