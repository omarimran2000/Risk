import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;


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
    private JPanel welcomePanel;
    private JScrollPane deployToScrollPane;
    private JScrollPane attackFromScrollPane;
    private JScrollPane attackScrollPane;
    JPanel gameControl;
    JPanel statusPanel;
    private int troopsDeployed;

    public GameView() throws IOException, ParseException {
        //ImageIcon icon = new ImageIcon("image_name.png");
        super("Risk Game");
        model = new GameModel();
        controller = new GameController(model, this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        numDice = new JSpinner();
        numTroops = new JSpinner();

        contentPane.add(welcomePanel, BorderLayout.CENTER);
        setVisible(true);
        setSize(800,800);

        troopsDeployed = 0;
    }

    public void setUpMap() throws IOException, ParseException {
        model.loadMap("example.JSON");
        contentPane.add(new JLabel(new ImageIcon(ImageIO.read(new File(model.getTheMap().getFilePath())))));
    }

    public JList getAttackFromList() {
        return attackFromList;
    }

    public JList getAttackToList() {
        return attackToList;
    }

    public JButton getAttackButton() {
        return attackButton;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getPassButton() {
        return passButton;
    }

    public JButton getDeployButton() {
        return deployButton;
    }

    public JButton getMoveButton(){
        return moveButton;
    }

    public JButton getQuitButton(){
        return quitButton;
    }


    public JSpinner getNumDice() {
        return numDice;
    }

    public JList getDeployToList() {
        return deployToList;
    }

    public void setTextArea(String message){
        textArea.setText(message);
    }

    public JSpinner getNumTroops() {
        return numTroops;
    }

    public JScrollPane getAttackFromScrollPane(){
        return attackFromScrollPane;
    }

    public JScrollPane getAttackScrollPane(){
        return attackScrollPane;
    }

    public JScrollPane getDeployToScrollPane(){
        return deployToScrollPane;
    }

    public void start()  {



        welcomePanel.setVisible(false);





        startButton.setEnabled(false);
        deployButton.setEnabled(true);
        deployToList.setVisible(true);
        numTroops.setVisible(true);
        troopsDeployed = 0;
        deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));

    }

    public void turn(Player curr, int numDeployTroops){


        textArea.setText("It is " + curr.getName() + "'s turn.\n You have " + numDeployTroops + " troops to deploy");
        textArea.setEditable(false);
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
        gameControl.add(numTroops);
        gameControl.add(numDice);
        numDice.setVisible(false);
        statusPanel = new JPanel();
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
    public void pass()
    {
        deployButton.setEnabled(true);
        deployToScrollPane.setVisible(true);
        deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
        numTroops.setVisible(true);

        passButton.setVisible(false);
        attackButton.setEnabled(false);
        attackFromScrollPane.setVisible(false);
        attackScrollPane.setVisible(false);
        numDice.setVisible(false);

        setNumTroops(model.getNumberOfTroops());
        troopsDeployed= 0;

        textArea.setText("It is " + model.getPlayer().getName() + " 's turn");
        textArea.append("\n You have " + model.getNumberOfTroops() + " troops to deploy");
        textArea.setVisible(true);


    }
    public void deploy()
    {
        if(troopsDeployed == model.getNumberOfTroops())
        {
            deployButton.setEnabled(false);
            deployToScrollPane.setVisible(false);
            //deployToList.setVisible(false);
            numTroops.setVisible(false);

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
        }

    }
    public void setTroopsDeployed(int newTroops)
    {
        troopsDeployed += newTroops;
    }
    public void attack(String status)
    {
        textArea.append(status + "\n");
        textArea.setVisible(true);




    }

    public void clearAttackFromSelection(){

        attackFromList.clearSelection();

    }

    public void resetAttackText(){
        textArea.setText("");
        textArea.setVisible(false);

    }



    public void attackWon(Territory newTerritory, int numAttackTroops){
        textArea.append("\nSelect the number of troops to move to " + newTerritory.getName());
        setNumTroops(numAttackTroops - 1);
        numTroops.setVisible(true);
        moveButton.setVisible(true);
        moveButton.setEnabled(true);
        attackButton.setEnabled(false);
        passButton.setEnabled(false);


    }

    public void move(int numTroops, Territory attack){
        textArea.setText(model.getPlayer().getName() + " moved " + numTroops + " troop(s) to " + attack.getName());
        moveButton.setVisible(false);
        attackButton.setEnabled(true);
        passButton.setEnabled(true);
    }

    public void invalidAttackFrom(){
        textArea.setVisible(true);
        textArea.setText("This territory does not have enough troops to attack");
    }

    public void gameOver(Player winner){
        JOptionPane.showMessageDialog(contentPane, "GAME OVER!\n" + winner.getName() + " is the winner!");
        JOptionPane.showMessageDialog(contentPane, "Click QUIT to exit");

        attackButton.setVisible(false);
        deployButton.setVisible(false);
        passButton.setVisible(false);
        moveButton.setVisible(false);
        numTroops.setVisible(false);
        attackFromScrollPane.setVisible(false);
        startButton.setVisible(true);
        startButton.setEnabled(true);
        textArea.setVisible(false);
        quitButton.setVisible(true);


    }









    public void setNumTroops(int max) {
        SpinnerNumberModel troopsModel = new SpinnerNumberModel(1, 1, max, 1);
        numTroops.setModel(troopsModel);
    }

    public static void main(String[] args) throws IOException, ParseException {
        new GameView();
    }


}
