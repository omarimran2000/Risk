import java.util.List;

public class AIPlayer extends Player {

    /**
     * @param name The player's name
     */
    public AIPlayer(String name) {
        super(name);

    }

    public Territory findMinTroops(){
        Territory minTerritory = territories.get(0);
        for(Territory territory: territories){
            if(findTroops(territory) < findTroops(minTerritory)){
                minTerritory = territory;
            }
        }
        return minTerritory;

    }

    public void deploy(int numberOfTroops){
        Territory deployment = findMinTroops();
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

}
