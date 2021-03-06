
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




}

