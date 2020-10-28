import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class GameView extends JFrame {

    private GameModel model;
    private JList attackFromList;
    private JList attackToList;
    private JButton attackButton;
    private JButton passButton;
    private JButton deployButton;
    private JSpinner numDice;
    private Container contentPane;

    public GameView() throws IOException, ParseException {
        //ImageIcon icon = new ImageIcon("image_name.png");
        super("Risk Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setIconImage(icon.getImage());
        model = new GameModel();
        model.setView(this);
        contentPane = getContentPane();
        setUpMap();

        attackFromList = new JList();
        attackToList = new JList();
        attackToList.addListSelectionListener(new GameController(model,this));

        attackButton = new JButton("ATTACK");
        attackButton.addActionListener(new GameController(model,this));

        passButton = new JButton("PASS");
        passButton.addActionListener(new GameController(model,this));

        deployButton = new JButton("DEPLOY");
        deployButton.addActionListener(new GameController(model,this));

        numDice = new JSpinner();

        setVisible(true);
        setSize(800,800);
    }

    public void setUpMap() throws IOException, ParseException {
        model.loadMap("map.JSON");
        contentPane.add(new JLabel(new ImageIcon(ImageIO.read(new File(model.getTheMap().getFilePath())))));
    }
    public void updateAttack()
    {
        //attackFromList.setModel(model.getPlayer().getTerritories());
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

    public JButton getPassButton() {
        return passButton;
    }

    public JButton getDeployButton() {
        return deployButton;
    }

    public JSpinner getNumDice() {
        return numDice;
    }

    public static void main(String[] args) throws IOException, ParseException {
        new GameView();
    }
}
