import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameController implements ActionListener, ListSelectionListener {

    GameModel model;
    GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JButton)
        {
            JButton buttonPressed = (JButton) e.getSource();

            if(buttonPressed.equals(view.getAttackButton()))
            {
                try {

                    Territory attackFromTerritory = (Territory) view.getAttackFromList().getSelectedValue();
                    Territory attackToTerritory = (Territory) view.getAttackToList().getSelectedValue();
                    int numDice = (int) view.getNumDice().getValue();
                    model.attack(attackFromTerritory, attackToTerritory, numDice);

                }catch(Exception ex){

                }
             }
            else if (buttonPressed.equals(view.getPassButton()))
            {

            }
            else if (buttonPressed.equals(view.getDeployButton()))
            {

            }
            else if (buttonPressed.equals(view.getStartButton()))
            {
                int numPlayers = 0;
                ArrayList<String> names = new ArrayList<>();
                String name = "";
                while (numPlayers < 2 || numPlayers > 6) {
                    String num = JOptionPane.showInputDialog("How many players are playing? (2-6)");
                    numPlayers = Integer.parseInt(num);
                }
                model.setNumberOfPlayers(numPlayers);
                for (int i = 0; i < numPlayers; i++) {
                    while(name == null || name.equals("")) {
                        name = JOptionPane.showInputDialog("what is your name?");
                    }
                    names.add(name);
                }
                model.createPlayers(names);
            }
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Territory attackFromTerritory = (Territory) view.getAttackFromList().getSelectedValue();
        view.getAttackToList().setModel(model.defaultListConversion(attackFromTerritory.getNeighbourTerritories(model.getPlayer())));
    }
}
