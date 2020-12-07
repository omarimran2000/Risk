import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
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
public class GameView extends JFrame implements GameModelListener, Serializable {

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
    private JButton fortifyButton;
    private JButton passAttackButton;
    private JButton customMapButton; //m4
    private ArrayList<TerritoryButton> territoryButtons;

    private JMenuItem saveGame; //m4
    private JMenuItem loadGame; //m4


    private JSpinner numDice;
    private JSpinner numTroops;
    private Container contentPane;
    private GameController controller;
    private JTextArea textArea;
    private JTextArea playerTextArea; // m3
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

    private boolean chosenAttack;
    private boolean chooseDeploy;
    private boolean chosenFortifyTo; // m3
    private boolean chosenFortifyFrom; // m3

    /**
     * Constructor of class GameView
     *
     * @throws IOException
     * @throws ParseException
     */
    public GameView()  {
        super("Risk Game");
        model = new GameModel();
        controller = new GameController(model, this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        territoryButtons = new ArrayList<TerritoryButton>();
        chosenAttack = false;
        chooseDeploy = false;
        chosenFortifyFrom = false;
        chosenFortifyTo = false;

        model.addListener(this);
        contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());
        welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.PAGE_AXIS));

        JLabel welcome = new JLabel("Welcome to RISK!");

        welcome.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        welcome.setFont(welcome.getFont().deriveFont(30.0f));

        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(welcome);

        textArea = new JTextArea();
        playerTextArea = new JTextArea();
        continentControl = new JTextArea();

        attackFromList = new JList();
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

        fortifyButton = new JButton("FORTIFY");
        fortifyButton.addActionListener(controller);
        fortifyButton.setEnabled(false);

        startButton = new JButton("START");
        startButton.addActionListener(controller);
        startButton.setEnabled(true);

        quitButton = new JButton("QUIT");
        quitButton.addActionListener(controller);
        quitButton.setVisible(false);
        quitButton.setEnabled(true);

        passAttackButton = new JButton("PASS ATTACK");
        passAttackButton.addActionListener(controller);
        passAttackButton.setEnabled(false);

        JMenuBar menubar = new JMenuBar();
        saveGame = new JMenuItem("Save Game");
        loadGame = new JMenuItem("Load Game");
        saveGame.addActionListener(controller);
        loadGame.addActionListener(controller);

        //m4
        JMenu menu = new JMenu("Risk");
        menu.add(saveGame);
        menu.add(loadGame);
        menubar.add(menu);
        this.setJMenuBar(menubar);

        startButton.setAlignmentX(Box.CENTER_ALIGNMENT);
        welcomePanel.add(startButton);

        welcomePanel.add(Box.createVerticalGlue());

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
        setVisible(true);

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
        JOptionPane.showMessageDialog(null,"Please choose a JSON map now");
        JFileChooser fileChooser = new JFileChooser();
        int approve = 1;
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        FileFilter fileFilter = new FileNameExtensionFilter("Text files", "json"); // only JSON files
        fileChooser.addChoosableFileFilter(fileFilter);
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setMultiSelectionEnabled(false); // only one file
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // no folders
        while (approve != fileChooser.APPROVE_OPTION)
        {
            File currentDir = new File(System.getProperty("user.dir"));
            fileChooser.setCurrentDirectory(currentDir);
            approve = fileChooser.showOpenDialog(null);
        }
        try {
            model.loadMap(fileChooser.getSelectedFile().getAbsolutePath());
        }catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null,ex);
            this.dispose();
        }
        for(TerritoryButton tb:territoryButtons) {
            tb.setEnabled(false);
            contentPane.add(tb);
        }
        try { //for IDE
            contentPane.add(new JLabel(new ImageIcon(ImageIO.read(new File(model.getTheMap().getFilePath())))));
        } catch (Exception ex) { //for JAR
            InputStream in = getClass().getResourceAsStream("/"+model.getTheMap().getFilePath());
            contentPane.add(new JLabel(new ImageIcon(ImageIO.read(in))));
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
     * getter method for fortifyButton
     *
     * @return fortifyButton
     */
    public JButton getFortifyButton(){
        return fortifyButton;
    } // m3

    /**
     * getter method for passFortifyButton
     *
     * @return passFortifyButton
     */
    public JButton getPassAttackButton(){
        return passAttackButton;
    }






    /**
     * getter method for customMapButton
     *
     * @return customMapButton
     */
    public JButton getCustomMapButton() {
        return customMapButton;
    } //m4

    public JMenuItem getSaveMenuItem() {
        return saveGame;
    }

    public JMenuItem getLoadGameMenuItem(){
        return loadGame;
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

    /**
     * set the text area
     *
     * @param message the message to display
     */
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
        setDeployButtons();
        chooseDeploy = true;
        numTroopsPanel.setVisible(true);
        troopsDeployed = 0;
        deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
        deployToList.setEnabled(false);
        fortifyButton.setVisible(true);
        passAttackButton.setVisible(true);
    }

    /**
     * Method to update the continent area
     * @return string to put in JTextArea
     */
    public String updateContinent()
    {
        String text = "";
        text += "It is " + model.getPlayer().getName() + "'s turn\n";
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
        textArea.setText("You have " + numDeployTroops + " troops to deploy\n");
        textArea.setEditable(false);

        continentControl.setText(updateContinent());
        continentControl.setEditable(false);

        playerTextArea.setVisible(false);
        playerTextArea.setEditable(false);

        gameControl = new JPanel();
        gameControl.setLayout(new FlowLayout());
        gameControl.add(attackButton);
        gameControl.add(deployButton);
        deployButton.setEnabled(false);

        gameControl.add(passButton);
        gameControl.add(passAttackButton);
        gameControl.add(fortifyButton);
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
        statusPanel.add(playerTextArea);

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
            deployButton.setEnabled(false);
            deployToScrollPane.setVisible(true);
            deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
            deployToList.setEnabled(false);
            disableAllButtons();
            setDeployButtons();
            chooseDeploy = true;
            numTroopsPanel.setVisible(true);

            resetAttackText();
            passButton.setVisible(false);
            attackButton.setEnabled(false);
            attackFromScrollPane.setVisible(false);
            attackScrollPane.setVisible(false);
            numDicePanel.setVisible(false);
            passAttackButton.setEnabled(false);

            setNumTroops(model.getNumberOfTroops());
            troopsDeployed = 0;

            textArea.append("It is " + model.getPlayer().getName() + " 's turn");
            textArea.append("\n You have " + model.getNumberOfTroops() + " troops to deploy");
            textArea.setVisible(true);

            continentControl.setText(updateContinent());
        }


    /**
     * method invoked when a player is in the deploy phase
     */
    public void deploy(String status) {

        if(troopsDeployed == model.getNumberOfTroops()) {
            deployButton.setEnabled(false);
            deployToScrollPane.setVisible(false);
            numTroopsPanel.setVisible(false);

            // set up attack phase
            resetAttackText();
            promptChooseAttackFrom();
            textArea.setVisible(true);
            passButton.setVisible(true);
            passButton.setEnabled(true);
            attackButton.setVisible(true);
            attackButton.setEnabled(false);
            passAttackButton.setVisible(true);
            passAttackButton.setEnabled(true);

            attackFromScrollPane.setVisible(true);
            attackFromScrollPane.setEnabled(true);
            attackFromList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
            attackFromList.setEnabled(false);
            setAttackFromButtons();
            chooseDeploy = false;
            chosenAttack = true;
        } else {
            int troopsLeft = model.getNumberOfTroops() - troopsDeployed;
            setNumTroops(troopsLeft);
            enableAllPlayerButtons();
            deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
            deployToList.setEnabled(false);
            setTextArea("You have " + troopsLeft + " troops left to deploy");
        }
    }

    /**
     * deploy phase for an AI player
     *
     * @param territory Territory to deploy to
     * @param numTroops number of troops to deploy
     */
    public void aiDeploy(Territory territory, int numTroops){
        disableAllButtons();
        passButton.setVisible(false);
        attackFromScrollPane.setVisible(false);
        continentControl.setVisible(false);
        textArea.setVisible(false);
        playerTextArea.setVisible(true);
        playerTextArea.append(model.getPlayer().getName() + " deployed " + numTroops + " troops to "
        + territory.getName() + "\n");
    }

    /**
     * Updates the player text area when the AI
     * attacks
     * @param status The new status of game
     */
    public void aiAttack(String status){
        playerTextArea.append(status + "\n");

    }


    /**
     * set the number of deployable troops
     *
     * @param newTroops num of new troops
     */
    public void setTroopsDeployed(int newTroops) {
        troopsDeployed += newTroops;
    }

    /**
     * method invoked during attack phase
     *
     * @param status the status of an attack
     */
    public void attack(String status) {
        textArea.append(status + "\n");
        textArea.setVisible(true);
        continentControl.setText(updateContinent());
        enableAllPlayerButtons();
    }

    /**
     * method used to remove a selected territory
     */
    public void clearAttackFromSelection(){
        attackFromList.clearSelection();
        setAttackFromButtons();
        setChosenAttack(true);
    }

    /**
     * method used to reset the attack text+
     */
    public void resetAttackText(){
        textArea.setText("");
        textArea.setVisible(false);
    }

    /**
     * Resets the player text
     */
    public void resetPlayerText(){
        playerTextArea.setText("");
        playerTextArea.setVisible(false);
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
        disableAllButtons();
        passButton.setEnabled(false);
        passAttackButton.setEnabled(false);
    }

    /**
     * moving troops into a territory
     *
     * @param numTroops number of troops to move
     * @param attack territory where troops are being moved
     */
    public void move(int numTroops, Territory attack){
        textArea.setText(model.getPlayer().getName() + " moved " + numTroops + " troop(s) to " + attack.getName() + "\n");
        promptChooseAttackFrom();
        continentControl.setText(updateContinent());
        moveButton.setVisible(false);
        attackButton.setEnabled(true);
        passButton.setEnabled(true);
        passAttackButton.setEnabled(true);
    }

    /**
     *
     * Updates the player text area when a player fortifies
     * @param status The new status
     */
    public void fortify(String status){
        playerTextArea.append(status + "\n");
        playerTextArea.setVisible(true);
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
     * add a territory button on the map
     *
     * @param t the territory it represents
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void addButtons(Territory t, int x, int y) {
        TerritoryButton temp = new TerritoryButton(t);
        temp.setBounds(x,y,10,10);
        temp.addActionListener(controller);
        temp.setEnabled(false);
        territoryButtons.add(temp);
        temp.setSize(15, 15);
    }

    /**
     * Enables all buttons that relate to the current player's territories
     */
    public void setDeployButtons() {
        for(Territory t:model.getPlayer().getTerritories()) {
            for(TerritoryButton tb:territoryButtons) {
                if (tb.getTerritory().equals(t)) {
                    tb.setEnabled(true);
                }
            }
        }
    }

    /**
     * Enables all buttons related to territories that the current player can attack from
     */
    public void setAttackFromButtons() {
        Player player = model.getPlayer();
        disableAllButtons();
        for(Territory t:player.getTerritories()) {
            for(TerritoryButton tb:territoryButtons) {
                if (tb.getTerritory().equals(t) && player.findTroops(t) > 1 && !player.ownNeighbours(t)) {
                    tb.setEnabled(true);
                }
            }
        }
    }

    /**
     * Enables all buttons related to neighbouring enemy territories of attackFrom
     * @param attackFrom The territory chosen to attack from
     */
    public void setAttackToButtons(Territory attackFrom) {
        for(TerritoryButton tb:territoryButtons) {
            if((attackFrom.getNeighbourTerritories().contains(tb.getTerritory())) && !tb.getTerritory().getCurrentPlayer().equals(model.getPlayer())) {
                tb.setEnabled(true);
            }
        }
    }

    /**
     * Disables all TerritoryButtons
     */
    public void disableAllButtons() {
        for (TerritoryButton tb:territoryButtons) {
            tb.setEnabled(false);
        }
    }

    /**
     * Enables all buttons related to the current player's territories
     */
    public void enableAllPlayerButtons() {
        for (TerritoryButton tb:territoryButtons) {
            if(tb.getTerritory().getCurrentPlayer().equals(model.getPlayer())) {
                tb.setEnabled(true);
            }
        }
    }

    /**
     * Enables all buttons related to territories that player can fortify from
     */
    public void enableAllFortifyFromButtons()
    {
        disableAllButtons();
        for (Territory t:model.getPlayer().getTerritories())
        {
            for(TerritoryButton tb:territoryButtons)
            {
                if (tb.getTerritory().equals(t) && model.getPlayer().ownANeighbour(t))
                {
                    tb.setEnabled(true);
                }
            }
        }
    }

    /**
     * Enables all buttons related to territories that the player can fortify to
     *
     * @param fortifyFrom the selected territory that the player is fortifying from
     */
    public void enableFortifyToButtons(Territory fortifyFrom)
    {
        for(Territory t:fortifyFrom.getNeighbourTerritories())
        {
            for(TerritoryButton tb:territoryButtons)
            {
                if(tb.getTerritory().equals(t) && t.getCurrentPlayer().equals(model.getPlayer()) && !tb.isEnabled())
                {
                    tb.setEnabled(true);
                    enableFortifyToButtons(t);
                }
            }
        }
    }

    /**
     * Set function to update if player is in attack phase or not
     * @param chosenAttack
     */
    public void setChosenAttack(boolean chosenAttack) {
        this.chosenAttack = chosenAttack;
    }

    /**
     * Getter for attack chosen
     * @return chosen attack
     */
    public boolean isChosenAttack() {
        return chosenAttack;
    }

    /**
     * Getter for deploy chosen
     * @return deploy chosen
     */
    public boolean isChooseDeploy() {
        return chooseDeploy;
    }

    /**
     * Getter for fortify from chosen
     * @return fortify from chosen
     */
    public boolean isChosenFortifyFrom(){
        return chosenFortifyFrom;
    } // m3

    /**
     * Getter for fortify to chosen
     * @return fortify to chosen
     */
    public boolean isChosenFortifyTo(){
        return chosenFortifyTo;
    } // m3

    /**
     * Prompts the user to select a territory to attack from via the text area
     */
    public void promptChooseAttackFrom(){
        textArea.append("Choose a territory to attack from\nor pass your turn to the next player");
    }

    /**
     * Prompts the user to select a territory to attack bia the text area
     */
    public void promptChooseAttackTo(){
        textArea.setText("Choose a territory to attack");
    }

    /**
     * Passes the attack phase and goes into the Fortify phase
     */
    public void passAttack(){
        disableAllButtons();
        attackButton.setEnabled(false);
        passAttackButton.setEnabled(false);
        passButton.setVisible(false);

        enableAllFortifyFromButtons();
        setTextArea("You are now in the fortify phase \nChoose a territory to move troops from");
    }

    /**
     * setter for chosenFortifyTo
     *
     * @param chosenFortifyTo the boolean value
     */
    public void setChosenFortifyTo(boolean chosenFortifyTo) {
        this.chosenFortifyTo = chosenFortifyTo;
    }

    /**
     * setter for chosenFortifyFrom
     *
     * @param chosenFortifyFrom the boolean value
     */
    public void setChosenFortifyFrom(boolean chosenFortifyFrom) {
        this.chosenFortifyFrom = chosenFortifyFrom;
    }

    /**
     * Disables a specific territory button
     * @param territory the territory the button represents
     */
    public void disableTerritory(Territory territory){
        for (TerritoryButton tb : territoryButtons){
            if (tb.getTerritory().equals(territory)){
                tb.setEnabled(false);
            }
        }
    }

    /**
     * Generates input dialog to select a filename
     *
     * @return The file path
     */
    public String saveGame(){
        boolean approve = false;
        String filename = "";
        while(!approve){
            filename = JOptionPane.showInputDialog(this, "Enter a filename with extension .ser");
            if(filename.endsWith(".ser")){
                approve = true;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid filename");
            }

        }
        JOptionPane.showMessageDialog(this, "Game saved.");
        return filename;


    }

    /**
     * Generates a file chooser for
     * selecting a file
     *
     * @return The chosen filepath
     */
    public String loadGame() {



        JOptionPane.showMessageDialog(this, "Select a serialized game file");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        FileFilter fileFilter = new FileNameExtensionFilter("Text files", "ser"); // only serialized files
        fileChooser.addChoosableFileFilter(fileFilter);
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setMultiSelectionEnabled(false); // only one file
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // no folders
        fileChooser.setControlButtonsAreShown(false);
        File currentDir = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(currentDir);


        fileChooser.showOpenDialog(this);
        File gameFile = fileChooser.getSelectedFile();
        return gameFile.getAbsolutePath();

    }

    /**
     * Restores the view from the serialized file
     *
     * @param phase The saved phase
     * @param status The saved status
     */
    public void restoreView(GameModel.Phase phase, String status){


        if(phase.equals(GameModel.Phase.ATTACK)){
            attack(status);


        } else if(phase.equals(GameModel.Phase.DEPLOY)){
            deploy(status);


        } else if(phase.equals(GameModel.Phase.FORTIFY)){
            fortify(status);

        }
        setVisible(true);

    }







    /*public GameModel getModel() {
        return model;
    }*/

    /**
     * Main function
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        new GameView();
    }
}
