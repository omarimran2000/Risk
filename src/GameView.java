import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameView extends JFrame {

    GameModel model;
    public GameView() throws IOException, ParseException {
        super("Risk Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new GameModel();
        model.setView(this);
        setUpMap();

        setVisible(true);
        setSize(800,800);
    }

    public void setUpMap() throws IOException, ParseException {
        model.loadMap("map.JSON");
        this.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File(model.getTheMap().getFilePath())))));
    }


    public static void main(String[] args) throws IOException, ParseException {
        new GameView();
    }
}
