import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/**
 * The view class for RISK which is in charge of all GUI components
 *
 * @author Erica Oliver
 * @author Wintana Yosief
 * @author Santhosh Pradeepan
 * @author Omar Imran
 *
 * @version October 25 2020
 */
public class GameView extends JFrame {

    private GameModel model;
    private JList attackFromList;
    private JList attackToList;
    private JList deployToList;
    private JButton attackButton;
    private JButton passButton;
    private JButton deployButton;
    private JButton moveButton;
    private JButton startButton;
    private JButton quitButton;
    private JSpinner numDice;
    private JSpinner numTroops;
    private Container contentPane;
    private GameController controller;
    private JTextArea textArea;
    private JTextArea continentControl;
    private JPanel welcomePanel;
    private JScrollPane deployToScrollPane;
    private JScrollPane attackFromScrollPane;
    private JScrollPane attackScrollPane;
    private JPanel gameControl;
    private JPanel statusPanel;
    private JPanel numDicePanel;
    private JPanel numTroopsPanel;
    private int troopsDeployed;
    private final int frameSizeX = 1200;
    private final int frameSizeY = 750;
    private ArrayList<TerritoryButton> territoryButtons;

    /**
     * Constructor of class GameView
     *
     * @throws IOException
     * @throws ParseException
     */
    public GameView() throws IOException, ParseException {
        //ImageIcon icon = new ImageIcon("image_name.png");
        super("Risk Game");
        model = new GameModel();
        controller = new GameController(model, this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        territoryButtons = new ArrayList<TerritoryButton>();
        //setIconImage(icon.getImage());


        model.setView(this);
        contentPane = getContentPane();
        //setUpMap();

        contentPane.setLayout(new BorderLayout());
        welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.PAGE_AXIS));

        JLabel welcome = new JLabel("Welcome to RISK!");

        welcome.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        welcome.setFont(welcome.getFont().deriveFont(30.0f));

        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(welcome);

        textArea = new JTextArea();
        continentControl = new JTextArea();

        attackFromList = new JList();
        attackFromList.addListSelectionListener(controller);
        attackFromList.setEnabled(false);
        attackToList = new JList();
        attackToList.setEnabled(false);

        deployToList = new JList();

        attackButton = new JButton("ATTACK");
        attackButton.addActionListener(controller);
        attackButton.setEnabled(false);

        passButton = new JButton("PASS");
        passButton.addActionListener(controller);
        passButton.setEnabled(false);

        deployButton = new JButton("DEPLOY");
        deployButton.addActionListener(controller);
        deployButton.setEnabled(false);

        moveButton = new JButton("MOVE");
        moveButton.addActionListener(controller);
        moveButton.setEnabled(false);


        startButton = new JButton("START");
        startButton.addActionListener(controller);
        startButton.setEnabled(true);



        quitButton = new JButton("QUIT");
        quitButton.addActionListener(controller);
        quitButton.setVisible(false);
        quitButton.setEnabled(true);


        startButton.setAlignmentX(Box.CENTER_ALIGNMENT);
        welcomePanel.add(startButton);

        welcomePanel.add(Box.createVerticalGlue());

        //SpinnerNumberModel playersModel = new SpinnerNumberModel(2, 2, 6, 1);
        //numPlayers = new JSpinner(playersModel);
        //numOfPlayers = JOptionPane.showMessageDialog(null, numPlayers);

        numDicePanel = new JPanel();
        numDicePanel.setLayout(new BoxLayout(numDicePanel, BoxLayout.PAGE_AXIS));
        JLabel numDiceLabel = new JLabel("Number of Dice: ");
        numDicePanel.add(numDiceLabel);
        numDice = new JSpinner();
        numDicePanel.add(numDice);


        numTroopsPanel = new JPanel();
        numTroopsPanel.setLayout(new BoxLayout(numTroopsPanel, BoxLayout.PAGE_AXIS));
        JLabel numTroopsLabel = new JLabel("Number of Troops: ");
        numTroopsPanel.add(numTroopsLabel);
        numTroops = new JSpinner();
        numTroopsPanel.add(numTroops);

        contentPane.add(welcomePanel, BorderLayout.CENTER);
        contentPane.addMouseListener(controller);
        setVisible(true);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);

        troopsDeployed = 0;

        this.setResizable(false);
        this.setSize(frameSizeX,frameSizeY);
    }

    /**
     * method used to set up the map
     *
     * @throws IOException
     * @throws ParseException
     */
    public void setUpMap() throws IOException, ParseException {
        model.loadMap("map.json");
        try {                                  //for IDE
            contentPane.add(new JLabel(new ImageIcon(ImageIO.read(new File(model.getTheMap().getFilePath())))));
        }catch (Exception ex)  //for JAR
        {
            InputStream in = getClass().getResourceAsStream("/"+model.getTheMap().getFilePath());
            contentPane.add(new JLabel(new ImageIcon(ImageIO.read(in))));
        }
        for(TerritoryButton tb:territoryButtons)
        {
            tb.setVisible(true);
            contentPane.add(tb);
        }
    }

    /**
     * getter method for attackFromList
     *
     * @return attackFromList
     */
    public JList getAttackFromList() {
        return attackFromList;
    }

    /**
     * getter method for attackToList
     *
     * @return attackToList
     */
    public JList getAttackToList() {
        return attackToList;
    }

    /**
     * getter method for attackButton
     *
     * @return attackButton
     */
    public JButton getAttackButton() {
        return attackButton;
    }

    /**
     * getter method for startButton
     *
     * @return startButton
     */
    public JButton getStartButton() {
        return startButton;
    }

    /**
     * getter method for passButton
     *
     * @return passButton
     */
    public JButton getPassButton() {
        return passButton;
    }

    /**
     * getter method for deployButton
     *
     * @return deployButton
     */
    public JButton getDeployButton() {
        return deployButton;
    }

    /**
     * getter method for moveButton
     *
     * @return moveButton
     */
    public JButton getMoveButton(){
        return moveButton;
    }

    /**
     * getter method for quitButton
     *
     * @return quitButton
     */
    public JButton getQuitButton(){
        return quitButton;
    }

    /**
     * getter method for numDice
     *
     * @return numDice
     */
    public JSpinner getNumDice() {
        return numDice;
    }

    /**
     * getter method for deployToList
     *
     * @return deployToList
     */
    public JList getDeployToList() {
        return deployToList;
    }

    public void setTextArea(String message){
        textArea.setText(message);
    }

    /**
     * getter method for numTroops
     *
     * @return numTroops
     */
    public JSpinner getNumTroops() {
        return numTroops;
    }

    /**
     * getter method for attackFromScrollPane
     *
     * @return attackFromScrollPane
     */
    public JScrollPane getAttackFromScrollPane(){
        return attackFromScrollPane;
    }

    /**
     * getter method for attackScrollPane
     *
     * @return attackScrollPane
     */
    public JScrollPane getAttackScrollPane(){
        return attackScrollPane;
    }

    /**
     * getter method for deployToScrollPane
     *
     * @return deployToScrollPane
     */
    public JScrollPane getDeployToScrollPane(){
        return deployToScrollPane;
    }

    /**
     * Gets the numDicePanel
     *
     * @return numDicePanel
     */
    public JPanel getNumDicePanel(){
        return numDicePanel;
    }

    /**
     * Gets the numTroopsPanel
     *
     * @return numTroopsPanel
     */
    public JPanel getNumTroopsPanel(){
        return numTroopsPanel;
    }

    /**
     * method invoked to show starting GUI components
     */
    public void start()  {

        welcomePanel.setVisible(false);

        startButton.setEnabled(false);
        deployButton.setEnabled(true);
        deployToList.setVisible(true);
        numTroopsPanel.setVisible(true);
        troopsDeployed = 0;
        deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));

    }
    /**
     * Method to update the continent area
     * @return string to put in JTextArea
     */
    public String updateContinent()
    {
        String text = "";
        text += "You control the following continents:\n";
        for (Continent c: model.getPlayer().getContinents())
        {
            text+= c.getName() + "\n";
        }
        return text;
    }

    /**
     * method used to show the current player and number of troops they can deploy
     *
     * @param curr the current player
     * @param numDeployTroops the number of troops to deploy
     */
    public void turn(Player curr, int numDeployTroops){


        textArea.setText("It is " + curr.getName() + "'s turn.\n You have " + numDeployTroops + " troops to deploy");
        textArea.setEditable(false);

        continentControl.setText(updateContinent());
        continentControl.setEditable(false);

        gameControl = new JPanel();
        gameControl.setLayout(new FlowLayout());
        gameControl.add(attackButton);
        gameControl.add(deployButton);
        deployButton.setEnabled(true);

        gameControl.add(passButton);
        gameControl.add(moveButton);
        moveButton.setVisible(false);


        startButton.setVisible(false);
        gameControl.add(quitButton);
        quitButton.setVisible(false);



        setNumTroops(numDeployTroops);
        gameControl.add(numTroopsPanel);
        gameControl.add(numDicePanel);
        numDicePanel.setVisible(false);
        statusPanel = new JPanel();
        statusPanel.add(continentControl);
        statusPanel.add(textArea);

        deployToScrollPane = new JScrollPane(deployToList);
        statusPanel.add(deployToScrollPane);

        attackFromScrollPane = new JScrollPane(attackFromList);
        attackFromScrollPane.setVisible(false);
        statusPanel.add(attackFromScrollPane);

        attackScrollPane = new JScrollPane(attackToList);
        attackScrollPane.setVisible(false);
        statusPanel.add(attackScrollPane);


        gameControl.add(statusPanel);
        contentPane.add(gameControl, BorderLayout.SOUTH);




    }

    /**
     *  method invoked when player ends their turn
     */
    public void pass()
    {
        deployButton.setEnabled(true);
        deployToScrollPane.setVisible(true);
        deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
        numTroopsPanel.setVisible(true);

        passButton.setVisible(false);
        attackButton.setEnabled(false);
        attackFromScrollPane.setVisible(false);
        attackScrollPane.setVisible(false);
        numDicePanel.setVisible(false);

        setNumTroops(model.getNumberOfTroops());
        troopsDeployed= 0;

        textArea.setText("It is " + model.getPlayer().getName() + " 's turn");
        textArea.append("\n You have " + model.getNumberOfTroops() + " troops to deploy");
        textArea.setVisible(true);

        continentControl.setText(updateContinent());


    }

    /**
     * method invoked when a player is in the deploy phase
     */
    public void deploy()
    {
        if(troopsDeployed == model.getNumberOfTroops())
        {
            deployButton.setEnabled(false);
            deployToScrollPane.setVisible(false);
            //deployToList.setVisible(false);
            numTroopsPanel.setVisible(false);

            textArea.setText("");
            textArea.setVisible(false);
            passButton.setVisible(true);
            passButton.setEnabled(true);
            attackButton.setVisible(true);
            attackButton.setEnabled(true);

            attackFromScrollPane.setVisible(true);
            attackFromScrollPane.setEnabled(true);
            attackFromList.setEnabled(true);
            attackFromList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
        }
        else
        {
            setNumTroops(model.getNumberOfTroops() - troopsDeployed);
            deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
        }

    }

    /**
     * set the number of deployable troops
     *
     * @param newTroops num of new troops
     */
    public void setTroopsDeployed(int newTroops)
    {
        troopsDeployed += newTroops;
    }

    /**
     * method invoked during attack phase
     *
     * @param status the status of an attack
     */
    public void attack(String status)
    {
        textArea.append(status + "\n");
        textArea.setVisible(true);
        continentControl.setText(updateContinent());

    }

    /**
     * method used to remove a selected territory
     */
    public void clearAttackFromSelection(){

        attackFromList.clearSelection();

    }

    /**
     * method used to reset the attack text+
     */
    public void resetAttackText(){
        textArea.setText("");
        textArea.setVisible(false);

    }


    /**
     * method used for to move troops into new territory after successful attacks
     *
     * @param newTerritory the new territory to move into
     * @param numAttackTroops number of troops to move
     */
    public void attackWon(Territory newTerritory, int numAttackTroops){
        textArea.append("\nSelect the number of troops to move to " + newTerritory.getName());
        setNumTroops(numAttackTroops - 1);
        attackToList.setEnabled(false);
        attackFromList.setEnabled(false);
        numTroopsPanel.setVisible(true);
        moveButton.setVisible(true);
        moveButton.setEnabled(true);
        attackButton.setEnabled(false);
        passButton.setEnabled(false);



    }

    /**
     * moving troops into a territory
     *
     * @param numTroops number of troops to move
     * @param attack territory where troops are being moved
     */
    public void move(int numTroops, Territory attack){
        textArea.setText(model.getPlayer().getName() + " moved " + numTroops + " troop(s) to " + attack.getName());
        continentControl.setText(updateContinent());
        moveButton.setVisible(false);
        attackButton.setEnabled(true);
        passButton.setEnabled(true);
    }

    /**
     * method is used when user chooses to attack from an invalid territory
     */
    public void invalidAttackFrom(){
        //textArea.setVisible(true);
        JOptionPane.showMessageDialog(null,"This territory does not have enough troops to attack");
    }

    /**
     * method is invoked when the game is over
     *
     * @param winner the winner of the game
     */
    public void gameOver(Player winner){
        JOptionPane.showMessageDialog(contentPane, "GAME OVER!\n" + winner.getName() + " is the winner!");
        JOptionPane.showMessageDialog(contentPane, "Click QUIT to exit");

        attackButton.setVisible(false);
        deployButton.setVisible(false);
        passButton.setVisible(false);
        moveButton.setVisible(false);
        numTroopsPanel.setVisible(false);
        attackFromScrollPane.setVisible(false);
        startButton.setVisible(true);
        startButton.setEnabled(true);
        textArea.setVisible(false);
        quitButton.setVisible(true);


    }


    /**
     * method user to set the spinner with a max number
     *
     * @param max the max number
     */

    public void setNumTroops(int max) {
        SpinnerNumberModel troopsModel = new SpinnerNumberModel(1, 1, max, 1);
        numTroops.setModel(troopsModel);
    }

    /**
     *
     * @param t
     * @param x
     * @param y
     */
    public void addButtons(Territory t,int x,int y)
    {
        TerritoryButton temp = new TerritoryButton(t);
      //  temp.setBackground(Color.GREEN);
        temp.setBounds(x,y,10,10);
        temp.setEnabled(false);
        territoryButtons.add(temp);
    }
    public void setNotEnabledButtons()
    {
        for(TerritoryButton tb:territoryButtons)
        {
            tb.setEnabled(false);
        }
    }
    public void setDeployButtons()
    {
        for(Territory t:model.getPlayer().getTerritories())
        {
            for(TerritoryButton tb:territoryButtons)
            {
                if (tb.getTerritory().equals(t))
                {
                    tb.setEnabled(true);
                }
            }
        }
    }
    public void setAttackFromButtons()
    {
        for(Territory t:model.getPlayer().getTerritories())
        {
            for(TerritoryButton tb:territoryButtons)
            {
                if (tb.getTerritory().equals(t) && model.getPlayer().findTroops(t) > 1)
                {
                    tb.setEnabled(true);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        new GameView();
    }
}
