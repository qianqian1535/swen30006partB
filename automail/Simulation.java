package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import exceptions.MailAlreadyDeliveredException;
import strategies.Automail;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class simulates the behaviour of AutoMail
 */
public class Simulation {
   
    private static ArrayList<MailItem> MAIL_DELIVERED;
    private static double total_score = 0;
    private static double penalty;
    public static void main(String[] args) throws IOException {
   	// Should probably be using properties here
    	Properties automailProperties = new Properties();
		// Defaults
		//automailProperties.setProperty("Seed",  Integer.parseInt("36"));  
		// Property value may need to be converted from a string to the appropriate type

		FileReader inStream = null;
		
		try {
			inStream = new FileReader("automail.properties");
			automailProperties.load(inStream);
		} finally {
			 if (inStream != null) {
	                inStream.close();
	            }
		}
		/*define values and initiate objects from the info in Properties file*/
        HashMap<Boolean, Integer> seedMap = new HashMap<>();
		int seed = Integer.parseInt(automailProperties.getProperty("Seed"));
		seedMap.put(true, seed);
		 /** Read the first argument and save it as a seed if it exists */
	      /*  if(args.length != 0){
	        	int seed = Integer.parseInt(args[0]);
	        	seedMap.put(true, seed);
	        } else{
	        	seedMap.put(false, 0);
	        }*/
		String bot1 = automailProperties.getProperty("Robot_Type_1");
		String bot2 = automailProperties.getProperty("Robot_Type_2");
		//create building object with default mailroom location and lowest level being 1
        Building building =new Building(Integer.parseInt(automailProperties.getProperty("Number_of_Floors")), 1,1);
		
        Automail automail = new Automail(new ReportDelivery(), bot1, bot2, building);
        
        int mailToCreate = Integer.parseInt(automailProperties.getProperty("Mail_to_Create"));
        MailGenerator generator = new MailGenerator(mailToCreate, automail.mailPool, seedMap, building);
        Clock.LAST_DELIVERY_TIME = Integer.parseInt(automailProperties.getProperty("Last_Delivery_Time"));
        penalty = Double.parseDouble(automailProperties.getProperty("Delivery_Penalty"));
        /** Initiate all the mail */
        MAIL_DELIVERED = new ArrayList<MailItem>();
        generator.generateAllMail();
        PriorityMailItem priority;
        
        while(MAIL_DELIVERED.size() != generator.MAIL_TO_CREATE) {
        	//System.out.println("-- Step: "+Clock.Time());
            priority = generator.step();
            if (priority != null) {
            	automail.robot1.behaviour.priorityArrival(priority.getPriorityLevel(), priority.weight);
            	automail.robot2.behaviour.priorityArrival(priority.getPriorityLevel(), priority.weight);
            }
            try {
				automail.robot1.step();
				automail.robot2.step();
			} catch (ExcessiveDeliveryException|ItemTooHeavyException e) {
				e.printStackTrace();
				System.out.println("Simulation unable to complete.");
				System.exit(0);
			}
            Clock.Tick();
        }
        printResults();
    }
    
    static class ReportDelivery implements IMailDelivery {
    	
    	/** Confirm the delivery and calculate the total score */
    	public void deliver(MailItem deliveryItem){
    		if(!MAIL_DELIVERED.contains(deliveryItem)){
                System.out.printf("T: %3d > Delivered     [%s]%n", Clock.Time(), deliveryItem.toString());
    			MAIL_DELIVERED.add(deliveryItem);
    			// Calculate delivery score
    			total_score += calculateDeliveryScore(deliveryItem);
    		}
    		else{
    			try {
    				throw new MailAlreadyDeliveredException();
    			} catch (MailAlreadyDeliveredException e) {
    				e.printStackTrace();
    			}
    		}
    	}

    }
    
    private static double calculateDeliveryScore(MailItem deliveryItem) {
   
    	double priority_weight = 0;
        // Take (delivery time - arrivalTime)**penalty * (1+sqrt(priority_weight))
    	if(deliveryItem instanceof PriorityMailItem){
    		priority_weight = ((PriorityMailItem) deliveryItem).getPriorityLevel();
    	}
        return Math.pow(Clock.Time() - deliveryItem.getArrivalTime(),penalty)*(1+Math.sqrt(priority_weight));
    }

    public static void printResults(){
   // 	System.out.println("Mail to create: "+ mailToCreate);
        System.out.println("T: "+Clock.Time()+" | Simulation complete!");
        System.out.println("Final Delivery time: "+Clock.Time());
        System.out.printf("Final Score: %.2f%n", total_score);
    }
}
