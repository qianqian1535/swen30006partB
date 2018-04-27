package strategies;

import automail.Building;
import automail.IMailDelivery;
import automail.Robot;

public class Automail {
	      
    public Robot robot1, robot2;
    public IMailPool mailPool;
    
    public Automail(IMailDelivery delivery, String bot1, String bot2, Building building) {
    	// Swap between simple provided strategies and your strategies here
    	    	
    	/** Initialize the MailPool */
    	
    	//// Swap the next line for the one below
    	mailPool = new WeakStrongMailPool(building);
    	
        /** Initialize the RobotAction */
    	boolean weak = false;  // Can't handle more than 2000 grams
    	boolean strong = true; // Can handle any weight that arrives at the building
    	    	
    	/** Initialize robot */
    	robot1 = new Robot( delivery, mailPool, weak, building); /* shared behaviour because identical and stateless */
    	robot2 = new Robot( delivery, mailPool, strong, building);
    }
    
}
