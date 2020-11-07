import org.json.simple.parser.ParseException;
import org.junit.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test cases for GameModel
 *
 * @version Nov 4, 2020
 * @author Erica Oliver
 */
public class ModelTest{

    static GameModel model;
    static GameView  view;

    Player player;
    int minPlayers = 2;
    int maxPlayers = 6;
    int minDeployTroops = 3;
    static ArrayList<Player> players;

    @BeforeClass
    public static void setup() throws IOException, ParseException {

        try {
            view = new GameView();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        model = view.getModel();
        model.setView(view);
        model.loadMap("map.json");

        int numPlayers = 3;
        model.setNumberOfPlayers(numPlayers);

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add("Player 1");
        playerNames.add("Player 2");
        playerNames.add("Player 3");
        model.createPlayers(playerNames);
        /*
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player(playerNames.get(i));
            player.setActive(true);
            GameModel.addPlayer(player);
        }

        model.initializeDefaultArmy();
        model.setArmies(numPlayers);

        model.setFirstPlayer();

         */
       // player = model.getPlayer();
    }

    @Test
    /**
     * test the initialization of the game: setting up the map, adding players, placing troops
     */
    public void testInit(){
        assertNotNull (model);
        assertNotNull(view);

        assert (model.getNumberOfPlayers() >= minPlayers);
        assert (model.getNumberOfPlayers() <= maxPlayers);

        assertNotNull(model.getPlayer());

        for (Territory t : model.getPlayer().getTerritories()){
            assert (model.getPlayer().findTroops(t) >= 1);
        }
    }

    @Test
    public void testDeploy(){
        int maxTroops = model.getNumberOfTroops();
        assert (maxTroops >= minDeployTroops);

        assert(model.getPlayer().getTerritories().size() > 0);
        Territory t = model.getPlayer().getTerritories().get(0);
        assertNotNull (t);

        int numTroops = model.getPlayer().findTroops(t);
        assert (numTroops >= 1);

        model.deploy(t, maxTroops);
        assertEquals (maxTroops+ numTroops, model.getPlayer().findTroops(t));

    }

    @Test
    public void testAttack(){
        Player player = model.getPlayer();
        Territory attack = player.getTerritories().get(0);

        for(Territory t:player.getTerritories())
        {
            if(!model.ownNeighbours(t))
            {
                attack = t;
                break;
            }
        }

        assertEquals(player, attack.getCurrentPlayer());

        Territory defend = attack.getNeighbourTerritories().get(0);
        assertNotEquals(player, defend.getCurrentPlayer());

        int numDice = model.calculateDice(attack);
        assert(numDice <= 3 && numDice >= 1);

        model.attack(attack, defend, numDice);
    }

    @Test
    /**
     * make sure the turn is passed to the next available player in list
     */
    public void testPass(){
        Player player1 = model.getPlayer();
        model.passTurn();
        Player player2 = model.getPlayer();
        assertNotEquals(player1, player2);
        assertEquals(player2,model.getPlayers().get(1));
       // model.passTurn();
       // //Player player3 = model.getPlayer();
       // player1.setActive(false);
       // model.passTurn();
       // assertEquals(player2, model.getPlayer());
    }

    // OTHER TESTS:
    // moving troops after winning a territory after an attack
    // getting points for continents
    // adding continents when you obtain all territories
    // removing continent when you loose a territory
}
