import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import java.io.Serializable;
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
public class GameController implements ActionListener, Serializable {

    private GameModel model;
    private GameView view;
    private Territory attackFromTerritory;
    private Territory attackToTerritory;
    private Territory deployTerritory;
    private Territory fortifyFromTerritory;
    private Territory fortifyToTerritory;

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
            if(view.isChooseDeploy() && model.getPhase().equals(GameModel.Phase.DEPLOY)) {
                deployTerritoryAction(temp);
            }
            else if(view.isChosenAttack() && model.getPhase().equals(GameModel.Phase.ATTACK)) {
                attackFromTerritoryAction(temp);
            }
            else if(!view.isChosenAttack() && model.getPhase().equals(GameModel.Phase.ATTACK)) {
                attackToTerritoryAction(temp);
            }
            else if (!view.isChosenFortifyFrom() && model.getPhase().equals(GameModel.Phase.FORTIFY)){
                fortifyFromTerritoryAction(temp);
            }
            else if (!view.isChosenFortifyTo() && model.getPhase().equals(GameModel.Phase.FORTIFY)){
               fortifyToTerritoryAction(temp);
            }
        }
        else if (e.getSource() instanceof JButton) {
            JButton buttonPressed = (JButton) e.getSource();

            if (buttonPressed.equals(view.getAttackButton())) {
                attackButtonAction();
            }

            else if (buttonPressed.equals(view.getPassButton()))
            {
                passButtonAction();

            } else if (buttonPressed.equals(view.getDeployButton())) {
                deployButtonAction();

            } else if (buttonPressed.equals(view.getStartButton())) {
                startButtonAction();

            } else if (buttonPressed.equals(view.getMoveButton())) {
                moveButtonAction();

            } else if (buttonPressed.equals(view.getQuitButton())) {
                view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
            }

            else if (buttonPressed.equals(view.getFortifyButton())){
                fortifyButtonAction();
            }

            else if (buttonPressed.equals(view.getPassAttackButton())){
                passAttackButtonAction();
            }


            else if (buttonPressed.equals((view.getCustomMapButton()))){
                customMapButtonAction();
            } //m4
        }
        else if(e.getSource() instanceof JMenuItem){
            JMenuItem menuItem = (JMenuItem) e.getSource();
            if(menuItem.equals(view.getSaveMenuItem())){
                saveMenuItemAction();
            } else if(menuItem.equals(view.getLoadGameMenuItem())){
                loadMenuItemAction();
            }
        } // m4
    }


    /**
     * Action for when save menu item
     * is clicked
     * Saves a game to a file
     */
    private void saveMenuItemAction(){

        String filename = view.saveGame();
        model.saveGame(filename);



    } //m4



    /**
     * Action for when load menu item is clicked
     * Loads a saved game from a file
     */
    private void loadMenuItemAction() {


       String filename = view.loadGame();
       view.setVisible(false);
       model.loadGame(filename);






    } //m4

    /**
     * Action for when customMapButton is clicked
     * Loads a custom map
     */
    private void customMapButtonAction() {
        //getChosenMap();
       // model.loadMap(file);
    } //m4

    /**
     * Action for when the territory to deploy to is chosen
     * @param temp The territory chosen to deploy to
     */
    private void deployTerritoryAction(Territory temp)
    {
        view.getDeployToList().clearSelection();
        deployTerritory = temp;
        view.disableAllButtons();
        view.getDeployToList().setSelectedValue(temp, true);
        view.getDeployButton().setEnabled(true);
    }

    /**
     * Action for when the territory to attack from is chosen
     * @param temp The territory chosen to attack from
     */
    private void attackFromTerritoryAction(Territory temp)
    {
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

    /**
     * Action for when the territory to attack is chosen
     * @param temp The territory to attack
     */
    private void attackToTerritoryAction(Territory temp)
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
        view.setTextArea("Choose number of dice to roll and \nclick attack button to execute the attack");
    }

    /**
     * Action for when the territory to fortify from is chosen
     * @param temp The territory to fortify from
     */
    private void fortifyFromTerritoryAction(Territory temp)
    {
        view.setChosenFortifyFrom(true);
        view.setChosenFortifyTo(false);
        fortifyFromTerritory = temp;
        view.disableAllButtons();
        view.enableFortifyToButtons(fortifyFromTerritory);
        view.disableTerritory(fortifyFromTerritory);
        view.setTextArea("Choose a territory to fortify");
    }

    /**
     * Action for when the territory to fortify is chosen
     * @param temp The territory to fortify
     */
    private void fortifyToTerritoryAction(Territory temp)
    {
        fortifyToTerritory = temp;
        view.getFortifyButton().setEnabled(true);
        view.disableAllButtons();
        view.setNumTroops(model.getPlayer().findTroops(fortifyFromTerritory)-1);
        view.getNumTroops().setEnabled(true);
        view.getNumTroopsPanel().setVisible(true);
        view.setTextArea("Choose a number of troops to send to " + fortifyToTerritory.getName() + " from " + fortifyFromTerritory.getName());
    }

    /**
     * Action for when the attack button is clicked
     */
    private void attackButtonAction()
    {
        try {
            view.resetAttackText();
            view.getAttackButton().setEnabled(false);
            view.getPassAttackButton().setVisible(true);
            view.getPassButton().setVisible(true);

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

    /**
     * Action for when the pass button is clicked
     */
    private void passButtonAction()
    {
        if(model.playersActive())
        {
            model.passTurn();
            model.setPhase(GameModel.Phase.DEPLOY);
        } else //no players active i.e. game is done
        {
            JOptionPane.showMessageDialog(null,"Game is complete. The winner is  "+model.getPlayer().getName());
            view.setVisible(false);
            view.dispose();
        }
    }

    /**
     * Action for when the deploy button is clicked
     */
    private void deployButtonAction()
    {
        try {
            view.resetPlayerText();
            int numTroops = (int) view.getNumTroops().getValue();
            model.deploy(deployTerritory, numTroops);
            model.setPhase(GameModel.Phase.ATTACK);
        }
        catch(Exception ex) {
            JOptionPane.showMessageDialog(null,"Error with deploy. Error: " + ex);
        }
    }

    /**
     * Action for when the start button is clicked
     */
    private void startButtonAction()
    {
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
            model.setPhase(GameModel.Phase.DEPLOY);

        } catch (IOException | ParseException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Action for when the move button is clicked
     */
    private void moveButtonAction()
    {
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
    }

    /**
     * Action for when the fortify button is clicked
     */
    private void fortifyButtonAction()
    {
        try {
            view.getFortifyButton().setEnabled(false);
            int numTroops = (int) view.getNumTroops().getValue();
            model.fortify(numTroops, fortifyFromTerritory, fortifyToTerritory);

            view.getPassButton().doClick();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fortify is producing an error. Error: " + ex);
        }
    }

    /**
     * Action for when the passAttack button is clicked
     */
    private void passAttackButtonAction()
    {
        try{
            view.getFortifyButton().setEnabled(false);
            view.getPassAttackButton().setEnabled(false);
            view.passAttack();
            model.setPhase(GameModel.Phase.FORTIFY);
            view.setChosenFortifyFrom(false);
            if(model.canFortify()) {
                view.getPassAttackButton().setEnabled(false);
                view.passAttack();
                model.setPhase(GameModel.Phase.FORTIFY);
                view.setChosenFortifyFrom(false);
            }
            else
            {
                view.getPassButton().doClick();
            }
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Pass attack is producing an error. Error: " + ex);
        }
    }

}
