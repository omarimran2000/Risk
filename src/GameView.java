import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Flow;

public class GameView extends JFrame {

    private GameModel model;
    private JList attackFromList;
    private JList attackToList;
    private JList deployToList;
    private JButton attackButton;
    private JButton passButton;
    private JButton deployButton;
    private JButton startButton;
    private JSpinner numDice;
    private JSpinner numTroops;
    private Container contentPane;
    private GameController controller;
    private JTextArea textArea;
    private JPanel welcomePanel;
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

        startButton = new JButton("START");
        startButton.addActionListener(controller);
        startButton.setEnabled(true);


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
        model.loadMap("map.JSON");
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

    public void start() throws IOException, ParseException {
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
        model.createPlayers(names);
        welcomePanel.setVisible(false);
        setUpMap();

        model.play();

        //gameStart("STATUS WILL GO HERE");

        startButton.setEnabled(false);
        deployButton.setEnabled(true);
        deployToList.setVisible(true);
        numTroops.setVisible(true);
        troopsDeployed = 0;
        deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));

    }

    public void gameStart(String status){
        JPanel gameControl = new JPanel();
        JPanel statusPanel = new JPanel();
        textArea.setText(status);
        textArea.setEditable(false);
        statusPanel.add(textArea);
        gameControl.setLayout(new FlowLayout());
        gameControl.add(attackButton);
        gameControl.add(deployButton);
        deployButton.setEnabled(true);



        gameControl.add(passButton);
        gameControl.add(numTroops);
        statusPanel.add(deployToList);
        gameControl.add(statusPanel);
        contentPane.add(gameControl, BorderLayout.SOUTH);




    }
    public void pass()
    {
        deployButton.setEnabled(true);
        deployToList.setVisible(true);
        deployToList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
        numTroops.setVisible(true);

        passButton.setVisible(false);
        attackButton.setEnabled(false);
        attackFromList.setEnabled(false);
        attackToList.setEnabled(false);

        setNumTroops(model.getNumberOfTroops());
        troopsDeployed= 0;
    }
    public void deploy()
    {
        if(troopsDeployed == model.getNumberOfTroops())
        {
            deployButton.setEnabled(false);
            deployToList.setVisible(false);
            numTroops.setVisible(false);

            attackButton.setVisible(true);
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
    public void attack()
    {

    }

    public void setNumTroops(int max) {
        SpinnerNumberModel troopsModel = new SpinnerNumberModel(1, 1, max, 1);
        numTroops.setModel(troopsModel);
    }





    public static void main(String[] args) throws IOException, ParseException {
        new GameView();
    }
}
