import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an AI player if the there is space for an AI player.
 *
 * @author Erica Oliver
 * @author Wintana Yosief
 * @author Santhosh Pradeepan
 * @author Omar Imran
 *
 * @version November 22 2020
 */
public class AIPlayer extends Player {
    public final static int AI_FORTIFY = 1;
    private final static int MAX_DICE = 3; //M4
    private GameModel model;

    /**
     * Constructor for class AI player
     *
     * @param name The player's name
     */
    public AIPlayer(String name, GameModel model) {
        super(name);
        this.model = model;
    }

    /**
     * Finds the territory with the lowest number of troops in it
     *
     * @param territories the list of territories
     * @return the territory with the lowest number of troops in it
     */
    public Territory findMinTroops(List<Territory> territories){
        Territory minTerritory = territories.get(0);
        for(Territory territory: territories){
            if(findTroops(territory) < findTroops(minTerritory)){
                minTerritory = territory;
            }
        }
        return minTerritory;
    }

    /**
     * Finds the territory with the largest number of troops in it
     *
     * @param territories the list of territories
     * @return the territory with the largest number of troops in it
     */
    public Territory findMaxTroops(List<Territory> territories) {
        Territory maxTerritory = territories.get(0);
        for(Territory territory: territories){
            if(findTroops(territory) > findTroops(maxTerritory) && !ownNeighbours(territory)){
                maxTerritory = territory;
            }
        }
        return maxTerritory;
    }

    /**
     * Checks if the AI player can attack
     *
     * @return true if player can attack, false otherwise
     */
    private boolean checkAvailableAttack() {
        for(Territory territory : territories) {
            if(findTroops(territory) > 1 && !ownNeighbours(territory)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the AI player can fortify
     *
     * @return true if player can fortify, false otherwise
     */
    private boolean checkAvailableFortify()
    {
        for(Territory t:territories)
        {
            if(findTroops(t)>1 && ownANeighbour(t))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * gets the territory that the player will fortify from
     *
     * @return a territory
     */
    public Territory getFortifyFromTerritory()
    {
        for(Territory t:territories)
        {
            if(findTroops(t)>1 && ownANeighbour(t))
            {
                return t;
            }
        }
        return null;
    }

    /**
     * gets the territory that the player will fortify to
     *
     * @param territory the territory the player is fortifying from
     * @return a territory
     */
    public Territory getFortifyToTerritory(Territory territory)
    {
        for(Territory t:territory.getNeighbourTerritories())
        {
            if(t.getCurrentPlayer().equals(this))
            {
                return t;
            }
        }
        return null;
    }

    /**
     * gets the territory that the player will attack
     *
     * @param attackFrom the territory that the player is attacking from
     * @return a territory
     */
    public Territory getAttackTo(Territory attackFrom) {
        ArrayList<Territory> neighbours = attackFrom.getAttackNeighbourTerritories(this);
        return findMinTroops(neighbours);
    }

    /**
     * gets the number of dice the player will use for attack
     *
     * @param attackFrom the territory that the player is attacking from
     * @return number of dice
     */
    public int getNumDice(Territory attackFrom) {
        int dice = findTroops(attackFrom) - 1;
        if (dice > MAX_DICE) {
            dice = MAX_DICE;
        }
        return dice;
    }

    /**
     *  sets up the view for the deploy phase
     */
    public void deployPhase() {
        Territory deployTerritory = findMinTroops(getTerritories());
        super.deploy(model.getNumberOfTroops(), deployTerritory);
        if (checkAvailableAttack()) {
            Territory attackFromTerritory = findMaxTroops(getTerritories());
            Territory attackToTerritory = getAttackTo(attackFromTerritory);
            int dice = getNumDice(attackFromTerritory);

            model.attack(attackFromTerritory, attackToTerritory, dice);
        }
        if (model.checkGameOver()) {
            for (GameModelListener l: listeners) {
                l.gameOver(model.getWinner());
            }
        }
        if(checkAvailableFortify())
        {
            model.fortify(AI_FORTIFY, getFortifyFromTerritory(), getFortifyToTerritory(getFortifyFromTerritory()));
        }
        model.passTurn();
    }
}
