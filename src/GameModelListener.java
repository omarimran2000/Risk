
public interface GameModelListener {

    void addButtons(Territory t, int x, int y);

    void attack(String status);

    void setTroopsDeployed(int numTroops);

    void pass();

    void deploy(String status);

    void attackWon(Territory newTerritory, int numAttackTroops);

    void start();

    void turn(Player p, int numTroops);

    void aiDeploy(Territory territory, int numTroops);

    void aiAttack(String status);

    void fortify(String status);

    void gameOver(Player winner);

    void restoreView(GameModel.Phase phase, String status);


    void deployTerritoryAction(Territory temp); //m4

    void attackFromTerritoryAction(Territory temp); //m4

    void attackToTerritoryAction(Territory territory, Territory attackFromTerritory); //m4

    void fortifyFromTerritoryAction(Territory fortifyFromTerritory); //m4

    void fortifyToTerritoryAction(Territory fortifyFromTerritory, Territory fortifyToTerritory); //m4

    void attackButtonAction(Territory attackFromTerritory, Territory attackToTerritory); //m4

    void passButtonAction(); //m4

    void moveButtonAction(Territory attackToTerritory, int numTroops); //m4

    void fortifyButtonAction(); //m4
}

