public class Game {

    public static void main(String[] args){

    }


    private Player checkWinner(Player attacker, int numOfAttackingDice, Player defender, int numOfDefendingDice) {
        int offence = 0;
        int defence = 0;
        for (int i = 0; i < numOfDefendingDice; i++)
        {
            offence += attacker.getDice()[i]; //access the die number saved at position i
            defence += defence.getDice().[i];
        }
        if (offence > defence) return attacker;
        return defender;
    }
}
