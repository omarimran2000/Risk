import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController implements ActionListener {

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

                }catch(Exception ex){

                }
             }
            else if (buttonPressed.equals(view.getPassButton()))
            {

            }
        }

    }

}
