import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
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

        if (e.getSource() instanceof JButton) {
            JButton buttonPressed = (JButton) e.getSource();

            if (buttonPressed.equals(view.getAttackButton())) {
                try {

                    Territory attackFromTerritory = (Territory) view.getAttackFromList().getSelectedValue();

                    if (model.getPlayer().findTroops(attackFromTerritory) == 1) {

                        view.invalidAttackFrom();
                    } else {
                        Territory attackToTerritory = (Territory) view.getAttackToList().getSelectedValue();
                        int numDice = (int) view.getNumDice().getValue();

                        if (!(model.attack(attackFromTerritory, attackToTerritory, numDice))) {
                            view.clearAttackFromSelection();
                        }
                        view.getNumDice().setVisible(false);
                        view.getAttackScrollPane().setVisible(false);

                        if (model.checkGameOver()) {
                            view.gameOver(model.getWinner());
                        }

                    }


                } catch (Exception ex) {

                }
             }
            else if (buttonPressed.equals(view.getPassButton()))
            {
                if(model.playersActive())
                {
                    model.passTurn();
                    view.pass();
                } else    //no players active i.e. game is done
                {

                }

            } else if (buttonPressed.equals(view.getDeployButton())) {

                    Territory t = (Territory) (view.getDeployToList().getSelectedValue());
                    int numTroops = (int) view.getNumTroops().getValue();
                    model.deploy(t, numTroops);
                    // update the numTroops spinner in view
                /*
                if (allTroops - numTroops == 0){
                    view.getDeployButton().setEnabled(false);
                    view.getDeployToList().setVisible(false);
                    view.getNumTroops().setVisible(false);
                }

                 */
                // go to next phase
            } else if (buttonPressed.equals(view.getStartButton())) {

                try {
                    ArrayList<String> names = new ArrayList<>();
                    String name = "";
                    int numOfPlayers = 0;
                    SpinnerNumberModel playersModel = new SpinnerNumberModel(2, 2, 6, 1);
                    JSpinner numPlayers = new JSpinner(playersModel);
                    JOptionPane.showMessageDialog(null, numPlayers);
                    try {
                        numOfPlayers = (int) numPlayers.getValue();

                        model.setNumberOfPlayers(numOfPlayers);
                    }catch (Exception ex)
                    {
                        System.out.println("Spinner is not returning an integer. Error: " + ex);
                    }


                    for (int i = 0; i < numOfPlayers; i++) {
                        while(name == null || name.equals("")) {
                            name = JOptionPane.showInputDialog("Player #" + (i+1) + ": What is your name?");
                        }
                        names.add(name);
                        name = "";
                    }
                    view.setUpMap();
                    model.createPlayers(names);

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }


                //int option = JOptionPane.showOptionDialog(null, view.getNumPlayers(), "Enter the number of players", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                /*JOptionPane.showMessageDialog(null, view.getNumPlayers());
                try {
                    numPlayers = (int) view.getNumPlayers().getValue();
                }catch (Exception ex)
                {

                }
                view.getNumPlayers().setEnabled(false);*/
            } else if (buttonPressed.equals(view.getMoveButton())) {
                Territory attackFrom = (Territory) view.getAttackFromList().getSelectedValue();
                Territory attack = (Territory) view.getAttackToList().getSelectedValue();
                int numTroops = (int) view.getNumTroops().getValue();
                model.getPlayer().attackWin(numTroops, attackFrom, attack);
                view.getDeployToScrollPane().setVisible(false);
                view.getAttackFromScrollPane().setVisible(true);
                view.move(numTroops, attack);
                view.getNumTroops().setVisible(false);
                view.getAttackFromList().setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
                view.clearAttackFromSelection();

            } else if (buttonPressed.equals(view.getQuitButton())) {
                view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));

            }
        }
    }



    @Override
    public void valueChanged(ListSelectionEvent e) {

            if(e.getValueIsAdjusting()) {
                Territory attackFromTerritory = (Territory) view.getAttackFromList().getSelectedValue();
                view.getAttackToList().setModel(model.defaultListConversion(attackFromTerritory.getNeighbourTerritories(model.getPlayer())));
                view.getAttackToList().setEnabled(true);
                view.getAttackScrollPane().setVisible(true);
                view.resetAttackText();

                SpinnerNumberModel numDiceModel = new SpinnerNumberModel(1, 1, model.calculateDice(attackFromTerritory), 1);
                view.getNumDice().setModel(numDiceModel);
                view.getNumDice().setVisible(true);
            }

    }

}
