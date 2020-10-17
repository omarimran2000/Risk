/**
 * Troop class for each member of a player's army
 * @author Wintana Yosief
 * @version  October 15, 2020
 */
public class Troop {
    private Territory location;
    private boolean deployed;


    /**
     * Creates a troop
     * Initially, a troop member has no deployment
     *
     */
    public Troop() {
       this.deployed = false;
       this.location = null;


    }

    /**
     * Determines whether a troop has been
     * deployed
     * @param deployed True, if deployed, false otherwise
     */
    public void setDeployed(Boolean deployed)
    {
        this.deployed = deployed;

    }

    /**
     * Gets the troop's current territory
     *
     * @return The troop's current territory
     */
    public Territory getLocation(){
        return location;
    }

    /**
     * Sets the troop's location
     *
     * @param newLocation The new location of troop
     */
    public void setLocation(Territory newLocation){
        location = newLocation;
    }

    /**
     * Determines whether a troop has been deployed
     * @return True, if deployed, false otherwise
     */
    public boolean isDeployed(){
        return deployed;
    }





}
