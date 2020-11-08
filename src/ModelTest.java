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
 * @author Omar Imran
 */
public class ModelTest{

    private static GameModel model;
    private static GameView  view;
    private static int minPlayers = 2;
    private int maxPlayers = 6;
    private int minDeployTroops = 3;
    private static int moveTroops = 1;
    private static ArrayList<Player> players;

    /**
     * Sets up the model before each test
     * @throws IOException
     * @throws ParseException
     */
    @Before
    public void setup() throws IOException, ParseException {

        try {
            view = new GameView();
            view.setVisibleFalse();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        model = view.getModel();
        model.setView(view);
        model.loadMap("map.json");

        int numPlayers = 2;
        model.setNumberOfPlayers(numPlayers);

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add("Player 1");
        playerNames.add("Player 2");
        playerNames.add("Player 3");
        model.createPlayers(playerNames);
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        view = null;
        model = null;
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
        assertEquals(model.getPlayer().getArmy().getTroops().size(),50);
    }

    /**
     * Tests the deploy method of model
     */
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

    /**
     * Tests the attack method of the model
     */
    @Test
    public void testAttack(){
        Player player = model.getPlayer();
        Territory attack = player.getTerritories().get(0);
        Territory defend = attack.getNeighbourTerritories().get(0);
        for(Territory t:player.getTerritories())
        {
            if(!model.ownNeighbours(t) && player.findTroops(t)>1)
            {
                attack = t;
                for(Territory neighbour:t.getNeighbourTerritories())
                {
                    if (! neighbour.getCurrentPlayer().equals(model.getPlayer()))
                    {
                        defend = neighbour;
                        break;
                    }
                }
                break;
            }
        }

        assertEquals(player, attack.getCurrentPlayer());
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
    }

    /**
     * Tests the move method of model
     */
    @Test
    public void testMove()
    {
        Territory empty = null;
        Territory attackFrom = null;
        int moveTroops =1;
        for(Territory t:model.getPlayer().getTerritories())
        {
            if(!model.ownNeighbours(t) && model.getPlayer().findTroops(t)>2)  //generates a scenario for move
            {
                attackFrom = t;
                for(Territory neighbour:t.getNeighbourTerritories())
                {
                    if (! neighbour.getCurrentPlayer().equals(model.getPlayer()))
                    {
                        empty = neighbour;
                        break;
                    }
                }
                break;
            }
        }
        Player occupant = empty.getCurrentPlayer();
        occupant.removeTroops(occupant.findTroops(empty),empty);
        occupant.removeTerritory(empty);
        empty.setCurrentPlayer(model.getPlayer());
        model.getPlayer().move(moveTroops,attackFrom,empty);

        assertEquals(model.getPlayer(),empty.getCurrentPlayer());
        assertNotEquals(occupant,empty.getCurrentPlayer());
        assertEquals(moveTroops,model.getPlayer().findTroops(empty));
        assertEquals(occupant.findTroops(empty),0);

    }

    /**
     * Tests to see if player can control a continent properly
     */
    @Test
    public void testContinent()
    {
        for(Territory t:model.getTheMap().getContinents().get(0).getTerritories()) //gives player all territories in South America
        {
            if(!(t.getCurrentPlayer().equals(model.getPlayer())))
            {
                Player occupant = t.getCurrentPlayer();
                occupant.removeTroops(occupant.findTroops(t),t);
                occupant.removeTerritory(t);

                t.setCurrentPlayer(model.getPlayer());
                model.getPlayer().getArmy().addTroop(new Troop());
                model.getPlayer().deploy(moveTroops,t);
            }
        }
        int numTroopInitial = model.getNumberOfTroops();
        int continentBonus = model.getTheMap().getContinents().get(0).getContinentPoint();

        model.getPlayer().addContinent(model.getTheMap().getContinents().get(0));

        assertTrue(model.getPlayer().getContinents().contains(model.getTheMap().getContinents().get(0)));
        assertEquals(model.getNumberOfTroops(),numTroopInitial+continentBonus);   //checks if continent bonus works

    }

    /**
     * Tests if player can attack
     */
    @Test
    public void testCanAttack()
    {

            for(Territory t:model.getPlayer().getTerritories()) //removing all troops except one for each territory so no attack possible
            {
                Player occupant = t.getCurrentPlayer();
                occupant.removeTroops(occupant.findTroops(t),t);

                occupant.getArmy().addTroop(new Troop());
                occupant.deploy(moveTroops,t);

            }

        assertEquals(model.canAttack(model.getPlayer()),false);

    }

    /**
     * Tests to see if check neighbours works
     */
    @Test
    public void testCheckNeighbours()
    {
        for(Territory t:model.getPlayer().getTerritories().get(0).getNeighbourTerritories()) //allows players to own all neighbouring territories
        {
            if(!t.getCurrentPlayer().equals(model.getPlayer())) {
                Player occupant = t.getCurrentPlayer();
                occupant.removeTroops(occupant.findTroops(t), t);
                occupant.removeTerritory(t);

                t.setCurrentPlayer(model.getPlayer());
                model.getPlayer().getArmy().addTroop(new Troop());
                model.getPlayer().deploy(moveTroops, t);
            }
        }
        assertEquals(model.ownNeighbours(model.getPlayer().getTerritories().get(0)),true);
    }

    /**
     * Tests to see if game can be ended
     */
    @Test
    public void testCheckGameOver()
    {
        Player tempOff = model.getPlayers().get(0);
        tempOff.setActive(false);
        assertEquals(model.getWinner(),model.getPlayers().get(1));
        assertEquals(model.checkGameOver(),true);
    }
}
