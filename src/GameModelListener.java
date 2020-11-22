public interface GameModelListener {

    void addButtons(Territory t,int x,int y);
    void attack(String status);
    void setTroopsDeployed(int numTroops);
    void deploy();
    void attackWon(Territory newTerritory, int numAttackTroops);
    void start();
    void turn(Player p,int numTroops);
    void aiDeploy(Territory territory, int numTroops);
}
