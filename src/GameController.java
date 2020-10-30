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
                if(model.playersActive())
                {
                    model.passTurn();
                }
                else    //no players active i.e. game is done
                {

                }

            }
            else if (buttonPressed.equals(view.getDeployButton()))
            {
                model.deploy();
            }
            else if (buttonPressed.equals(view.getStartButton()))
            {
                view.start();

                //int option = JOptionPane.showOptionDialog(null, view.getNumPlayers(), "Enter the number of players", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                /*JOptionPane.showMessageDialog(null, view.getNumPlayers());
                try {
                    numPlayers = (int) view.getNumPlayers().getValue();
                }catch (Exception ex)
                {

                }
                view.getNumPlayers().setEnabled(false);*/
                view.getStartButton().setEnabled(false);
            }
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Territory attackFromTerritory = (Territory) view.getAttackFromList().getSelectedValue();
        view.getAttackToList().setModel(model.defaultListConversion(attackFromTerritory.getNeighbourTerritories(model.getPlayer())));
        view.getAttackToList().setEnabled(true);

        SpinnerNumberModel numDiceModel = new SpinnerNumberModel(1, 1, model.calculateDice(attackFromTerritory), 1);
        view.getNumDice().setModel(numDiceModel);
    }
}
