import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The controller class for RISK that connects the View class with the Model Class.
 * This class is in charge of the events that occur.
 *
 * @author Erica Oliver
 * @author Wintana Yosief
 * @author Santhosh Pradeepan
 * @author Omar Imran
 *
 * @version October 25 2020
 */
public class GameController implements ActionListener {//, ListSelectionListener, MouseListener {

    private GameModel model;
    private GameView view;
    Territory attackFromTerritory;
    Territory attackToTerritory;
    Territory deployTerritory;

    /**
     * Constructor for this class
     *
     * @param model the model for this game
     * @param view the visual representation of this game
     */
    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * method is invoked whenever a specific action is performed
     *
     * @param e the action event that invoked this method
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof TerritoryButton)
        {
            TerritoryButton territoryButton = (TerritoryButton) e.getSource();
            Territory temp = territoryButton.getTerritory();
            if(view.isChooseDeploy())
            {
                view.getDeployToList().clearSelection();
                deployTerritory = temp;
                view.disableAllButtons();
                view.getDeployToList().setSelectedValue(temp, true);

                view.getDeployButton().setEnabled(true);

            }

            else if(view.isChosenAttack())
            {
                view.getAttackFromList().clearSelection();
                view.disableAllButtons();
                view.setAttackToButtons(temp);
                attackFromTerritory = temp;
                view.setChosenAttack(false);
                view.getAttackFromList().setSelectedValue(temp, true);
                view.getAttackToList().setModel(model.defaultListConversion(temp.getNeighbourTerritories(model.getPlayer())));
                view.getAttackScrollPane().setVisible(true);

            }
            else if(!view.isChosenAttack())
            {
                view.getAttackToList().clearSelection();
                attackToTerritory = temp;
                view.getAttackButton().setEnabled(true);
                view.disableAllButtons();
                SpinnerNumberModel numDiceModel = new SpinnerNumberModel(1, 1, model.calculateDice(attackFromTerritory), 1);
                view.getNumDice().setModel(numDiceModel);
                view.getNumDicePanel().setVisible(true);
                view.getAttackToList().setSelectedValue(temp, true);
                view.getAttackButton().setEnabled(true);
            }
        }
        else if (e.getSource() instanceof JButton) {
            JButton buttonPressed = (JButton) e.getSource();

            if (buttonPressed.equals(view.getAttackButton())) {

                try {
                       view.resetAttackText();
                       view.getAttackButton().setEnabled(false);

           //         Territory attackFromTerritory = (Territory) view.getAttackFromList().getSelectedValue();

        //            if (model.getPlayer().findTroops(attackFromTerritory) == 1) {

          //              view.invalidAttackFrom();
                     {
                  //      Territory attackToTerritory = (Territory) view.getAttackToList().getSelectedValue();
                        int numDice = (int) view.getNumDice().getValue();

                        if (!(model.attack(attackFromTerritory, attackToTerritory, numDice))) {
                            view.disableAllButtons();
                            view.clearAttackFromSelection();


                        }
                        view.getNumDicePanel().setVisible(false);
                        view.getAttackScrollPane().setVisible(false);

                        if (model.checkGameOver()) {
                            view.gameOver(model.getWinner());
                        }

                    }


                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,"Not enough parameters to attack with. Error: " + ex);
                }
                if (!model.canAttack(model.getPlayer())){
                    model.passTurn();
                    view.pass();
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
                    JOptionPane.showMessageDialog(null,"Game is complete. The winner is  "+model.getPlayer().getName());
                    view.setVisible(false);
                    view.dispose();
                }

            } else if (buttonPressed.equals(view.getDeployButton())) {
                    try {
                      //  Territory t = (Territory) (view.getDeployToList().getSelectedValue());
                        //if (t==null){
                          //  JOptionPane.showMessageDialog(null, "Pick a territory.");
                     //   }
                       // else {
                            int numTroops = (int) view.getNumTroops().getValue();

                            model.deploy(deployTerritory, numTroops);


                      //      if (deployTerritory != null) {
                        //        model.deploy(deployTerritory, numTroops);
                          //  }
                      //      else {
                        //        view.setTextArea("Choose a territory to deploy troops to");
                          //  }

                      //  }
                    }catch(Exception ex)
                    {
                        JOptionPane.showMessageDialog(null,"Error with deploy. Error: " + ex);
                    }
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
                    //JOptionPane.showMessageDialog(null, numPlayers);
                    JOptionPane.showMessageDialog(null, numPlayers, "Enter the number of players", JOptionPane.QUESTION_MESSAGE);
                    try {
                        numOfPlayers = (int) numPlayers.getValue();

                        model.setNumberOfPlayers(numOfPlayers);
                    }catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(null,"Spinner is not returning an integer. Error: " + ex);
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
                try {
                  //  Territory attackFrom = (Territory) view.getAttackFromList().getSelectedValue();
                  //  Territory attack = (Territory) view.getAttackToList().getSelectedValue();
                    int numTroops = (int) view.getNumTroops().getValue();
                    model.getPlayer().attackWin(numTroops, attackFromTerritory, attackToTerritory);
                    view.getDeployToScrollPane().setVisible(false);
                    view.getAttackFromScrollPane().setVisible(true);
                    view.move(numTroops, attackToTerritory);
                }catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null,"Move is producing an error. Error: " + ex);
                }
                view.getNumTroopsPanel().setVisible(false);
                view.getAttackFromList().setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
                view.clearAttackFromSelection();
               // view.getAttackToList().setEnabled(true);
               // view.getAttackFromList().setEnabled(true);
                view.getAttackButton().setEnabled(false);

            } else if (buttonPressed.equals(view.getQuitButton())) {
                view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));

            }
        }
    }

    /**
     * Method is invoked when a list value is selected
     *
     * @param e the listSelectionEvent that invoked this method
     */
    /*@Override
    public void valueChanged(ListSelectionEvent e) {

            if(e.getValueIsAdjusting()) {
                Territory attackFromTerritory = (Territory) view.getAttackFromList().getSelectedValue();
                view.getAttackToList().setModel(model.defaultListConversion(attackFromTerritory.getNeighbourTerritories(model.getPlayer())));
                view.getAttackToList().setEnabled(true);
                view.getAttackScrollPane().setVisible(true);
                view.resetAttackText();

                SpinnerNumberModel numDiceModel = new SpinnerNumberModel(1, 1, model.calculateDice(attackFromTerritory), 1);
                view.getNumDice().setModel(numDiceModel);
                view.getNumDicePanel().setVisible(true);
            }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("x: "+e.getX()+" y:"+e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }*/
}
