public class Command {
    private static final String[] commands = { "print", "attack", "deploy", "help", "fortify"};
    private String command;

    /**
     * Constructor
     * @param command The inputted command
     */
    public Command(String command){
        this.command = command;
    }

    public String getCommand(){
        return command;
    }

    /**
     * Check if the inputted command is valid
     * @param string The command
     * @return True if string is a valid command
     */
    public boolean isCommand(String string){
        for(int i = 0; i < commands.length; i++) {
            if (commands[i].equals(string))
                return true;
        }
        return false;
    }


}
