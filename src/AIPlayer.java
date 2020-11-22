import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends Player {
    /**
     * @param name The player's name
     */
    public AIPlayer(String name) {
        super(name);
    }

    public Territory findMinTroops(List<Territory> territories){
        Territory minTerritory = territories.get(0);
        for(Territory territory: territories){
            if(findTroops(territory) < findTroops(minTerritory)){
                minTerritory = territory;
            }
        }
        return minTerritory;
    }

    public Territory findMaxTroops(List<Territory> territories) {
        Territory maxTerritory = territories.get(0);
        for(Territory territory: territories){
            if(findTroops(territory) > findTroops(maxTerritory) && !ownNeighbours(territory)){
                maxTerritory = territory;
            }
        }
        return maxTerritory;
    }

    public boolean checkAvailableAttack() {
        for(Territory territory : territories) {
            if(findTroops(territory) > 1 && !ownNeighbours(territory)) {
                return true;
            }
        }
        return false;
    }
    public boolean checkAvailableFortify()
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

    public Territory getAttackTo(Territory attackFrom) {
        ArrayList<Territory> neighbours = attackFrom.getAttackNeighbourTerritories(this);
        return findMinTroops(neighbours);
    }

    public int getNumDice(Territory attackFrom) {
        int dice = findTroops(attackFrom) - 1;
        if (dice > 3) {
            dice = 3;
        }
        return dice;
    }
}
