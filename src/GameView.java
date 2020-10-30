import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
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
    private JButton startButton;
    private JSpinner numDice;
    private JSpinner numTroops;
    private Container contentPane;
    private GameController controller;
    private JTextArea textArea;
    public GameView() throws IOException, ParseException {
        //ImageIcon icon = new ImageIcon("image_name.png");
        super("Risk Game");
        model = new GameModel();
        controller = new GameController(model, this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setIconImage(icon.getImage());


        model.setView(this);
        contentPane = getContentPane();
        setUpMap();

        attackFromList = new JList();
        attackFromList.addListSelectionListener(controller);
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

        //SpinnerNumberModel playersModel = new SpinnerNumberModel(2, 2, 6, 1);
        //numPlayers = new JSpinner(playersModel);
        //numOfPlayers = JOptionPane.showMessageDialog(null, numPlayers);

        numDice = new JSpinner();
        numTroops = new JSpinner();

        setVisible(true);
        setSize(800,800);
    }

    public void setUpMap() throws IOException, ParseException {
        model.loadMap("map.JSON");
        contentPane.add(new JLabel(new ImageIcon(ImageIO.read(new File(model.getTheMap().getFilePath())))));
    }
    public void updateAttack()
    {
        attackFromList.setModel(model.defaultListConversion((ArrayList<Territory>) model.getPlayer().getTerritories()));
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

    public void start() {
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
        }
        model.createPlayers(names);
    }

    public void setNumTroops(int max) {
        SpinnerNumberModel troopsModel = new SpinnerNumberModel(2, 2, max, 1);
        numTroops = new JSpinner(troopsModel);
    }

    public static void main(String[] args) throws IOException, ParseException {
        new GameView();
    }
}
