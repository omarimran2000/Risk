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
public class GameController implements ActionListener {

    private GameModel model;
    private GameView view;
    private Territory attackFromTerritory;
    private Territory attackToTerritory;
    private Territory deployTerritory;
    private Territory fortifyFromTerritory;
    private Territory fortifyToTerritory;
    private boolean deployPhase;
    private boolean attackPhase;
    private boolean fortifyPhase;
    private final static int AI_FORTIFY = 1;

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
        if (e.getSource() instanceof TerritoryButton) {
            TerritoryButton territoryButton = (TerritoryButton) e.getSource();
            Territory temp = territoryButton.getTerritory();
            if(view.isChooseDeploy() && deployPhase) {
                view.getDeployToList().clearSelection();
                deployTerritory = temp;
                view.disableAllButtons();
                view.getDeployToList().setSelectedValue(temp, true);
                view.getDeployButton().setEnabled(true);
            }
            else if(view.isChosenAttack() && attackPhase) {
                view.getAttackFromList().clearSelection();
                view.disableAllButtons();
                view.setAttackToButtons(temp);
                attackFromTerritory = temp;
                view.setChosenAttack(false);
                view.getAttackFromList().setSelectedValue(temp, true);

                view.getAttackToList().setModel(model.defaultListConversion(temp.getAttackNeighbourTerritories(model.getPlayer())));
                view.getAttackScrollPane().setVisible(true);
                view.promptChooseAttackTo();
            }
            else if(!view.isChosenAttack() && attackPhase) {
                view.getAttackToList().clearSelection();
                attackToTerritory = temp;
                view.getAttackButton().setEnabled(true);
                view.disableAllButtons();
                SpinnerNumberModel numDiceModel = new SpinnerNumberModel(1, 1, model.calculateDice(attackFromTerritory), 1);
                view.getNumDice().setModel(numDiceModel);
                view.getNumDicePanel().setVisible(true);
                view.getAttackToList().setSelectedValue(temp, true);
                view.getAttackButton().setEnabled(true);
                view.setTextArea("Choose number of dice to roll and \nclick attack button to execute the attack");
            }
            else if (!view.isChosenFortifyFrom() && fortifyPhase){
                view.setChosenFortifyFrom(true);
                view.setChosenFortifyTo(false);
                fortifyFromTerritory = temp;
                view.disableAllButtons();
                view.enableFortifyToButtons(fortifyFromTerritory);
                view.disableTerritory(fortifyFromTerritory);
                view.setTextArea("Choose a territory to fortify");
            }
            else if (!view.isChosenFortifyTo() && fortifyPhase){
                fortifyToTerritory = temp;
                view.getFortifyButton().setEnabled(true);
                view.disableAllButtons();
                view.setNumTroops(model.getPlayer().findTroops(fortifyFromTerritory)-1);
                view.getNumTroops().setEnabled(true);
                view.getNumTroopsPanel().setVisible(true);
                view.setTextArea("Choose a number of troops to send to " + fortifyToTerritory + " from " + fortifyFromTerritory);
            }
        }
        else if (e.getSource() instanceof JButton) {
            JButton buttonPressed = (JButton) e.getSource();

            if (buttonPressed.equals(view.getAttackButton())) {
                try {
                    view.resetAttackText();
                    view.getAttackButton().setEnabled(false);

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

                    if(!(model.getPlayer() instanceof AIPlayer)) {
                        view.pass();
                    }else {
                        while (model.getPlayer() instanceof AIPlayer) {
                            AIPlayer ai = (AIPlayer) model.getPlayer();
                            deployTerritory = ai.findMinTroops(ai.getTerritories());
                            model.deploy(deployTerritory, model.getNumberOfTroops());
                            if (ai.checkAvailableAttack()) {
                                attackFromTerritory = ai.findMaxTroops(ai.getTerritories());
                                attackToTerritory = ai.getAttackTo(attackFromTerritory);
                                int dice = ai.getNumDice(attackFromTerritory);
                                //view.getNumDice().setValue(dice);
                                model.attack(attackFromTerritory, attackToTerritory, dice);
                            }

                            if (model.checkGameOver()) {
                                view.gameOver(model.getWinner());
                            }
                            if(ai.checkAvailableFortify())
                            {
                                model.fortify(AI_FORTIFY,ai.getFortifyFromTerritory(), ai.getFortifyToTerritory(ai.getFortifyFromTerritory()));
                            }

                            model.passTurn();
                            view.pass();
                        }
                    }
                    view.pass();

                    deployPhase = true;
                    attackPhase = false;
                    fortifyPhase = false;
                } else //no players active i.e. game is done
                {
                    JOptionPane.showMessageDialog(null,"Game is complete. The winner is  "+model.getPlayer().getName());
                    view.setVisible(false);
                    view.dispose();
                }

            } else if (buttonPressed.equals(view.getDeployButton())) {
                    try {
                        view.resetAIText();
                        int numTroops = (int) view.getNumTroops().getValue();
                        model.deploy(deployTerritory, numTroops);
                        deployPhase = false;
                        attackPhase = true;
                    }
                    catch(Exception ex) {
                        JOptionPane.showMessageDialog(null,"Error with deploy. Error: " + ex);
                    }

            } else if (buttonPressed.equals(view.getStartButton())) {
                try {
                    ArrayList<String> names = new ArrayList<>();
                    String name = "";
                    int numOfPlayers = 0;
                    SpinnerNumberModel playersModel = new SpinnerNumberModel(2, 2, 6, 1);
                    JSpinner numPlayers = new JSpinner(playersModel);
                    JOptionPane.showMessageDialog(null, numPlayers, "Enter the number of players", JOptionPane.QUESTION_MESSAGE);
                    try {
                        numOfPlayers = (int) numPlayers.getValue();
                        model.setNumberOfPlayers(numOfPlayers);
                    }
                    catch (Exception ex) {
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

                    deployPhase = true;
                    attackPhase = false;
                    fortifyPhase = false;

                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }

            } else if (buttonPressed.equals(view.getMoveButton())) {
                try {
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
                view.getAttackButton().setEnabled(false);


            } else if (buttonPressed.equals(view.getQuitButton())) {
                view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
            }

            else if (buttonPressed.equals(view.getFortifyButton())){
                try {
                    view.getFortifyButton().setEnabled(false);
                    int numTroops = (int) view.getNumTroops().getValue();
                    model.fortify(numTroops, fortifyFromTerritory, fortifyToTerritory);
                    view.getPassButton().doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Fortify is producing an error. Error: " + ex);
                }
            }

            else if (buttonPressed.equals(view.getPassAttackButton())){
                try{
                    view.getFortifyButton().setEnabled(true);
                    view.getPassAttackButton().setEnabled(false);
                    view.passAttack();
                    attackPhase = false;
                    fortifyPhase = true;
                    view.setChosenFortifyFrom(false);
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Pass attack is producing an error. Error: " + ex);
                }
            }
        }
    }
}
