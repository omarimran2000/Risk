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
            if(findTroops(territory) > findTroops(maxTerritory)){
                maxTerritory = territory;
            }
        }
        return maxTerritory;
    }

    public void deploy(int numberOfTroops){
        Territory deployment = findMinTroops(territories);
        List<Troop> troops = army.getTroops();
        int count = 0;
        while(count < numberOfTroops) {
            for (Troop troop : troops) {
                if (!troop.isDeployed()) {
                    troop.setDeployed(true);
                    troop.setLocation(deployment);
                    count += 1;
                }
            }
        }
    }

    public boolean checkAvailableAttack() {
        for(Territory territory : territories) {
            if(findTroops(territory) > 1) {
                return true;
            }
        }
        return false;
    }

    public Territory getAttackTo(Territory attackFrom) {
        ArrayList<Territory> neighbours = attackFrom.getNeighbourTerritories(this);
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
