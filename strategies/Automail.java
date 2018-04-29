package strategies;

import automail.Building;
import automail.IMailDelivery;
import automail.Robot;

public class Automail {

	public Robot robot1, robot2;
	private IMailPool lowerMailPool;
	private IMailPool upperMailPool;
	private boolean robot1Big = false;
	private boolean robot2Big = false;
	private boolean hasBig = false;

	public MailDistribution mailDistribution;

	public static enum BotType {
		BIG, STRONG, WEAK, INVALID;
	}

	public Automail(IMailDelivery delivery, String bot1, String bot2, Building building) {
		// Swap between simple provided strategies and your strategies here

		/** Initialize the MailPool */

		//// Swap the next line for the one below
		BotType robotType1, robotType2;
		robotType1 = botType(bot1);
		robotType2 = botType(bot2);
		/*
		 * 1. In all configurations, one robot (upper) will deliver only to the top half
		 * of the building, while the other robot (lower) will deliver to the bottom
		 * half as well all priority items.
		 */
		if(robotType1 == BotType.BIG) {
			robot1Big = true;
			hasBig = true;
		}
		if(robotType2 == BotType.BIG) {
			robot2Big = true;
			hasBig = true;
		}
		
		/** Initialize the RobotAction */
		//if both robots aren't weak
		if (!(robotType1 == BotType.WEAK && robotType2 == BotType.WEAK)) {
			if(robotType1 == BotType.WEAK || robotType2 == BotType.WEAK) {
				mailDistribution = new MailDistribution(building,true);
				lowerMailPool = mailDistribution.getLower();
				upperMailPool = mailDistribution.getUpper();
				// the weak robot will deliver to upper half, the lower robot will deliver all
				// parcels too heavy for the weak robot
					// Constructor: Robot(delivery,mailPool, boolean big ,boolean strong
					// building)
				robot2 = new Robot(delivery, lowerMailPool, hasBig,true, building);
				robot1 = new Robot(delivery, upperMailPool, false,false, building);
			} // Two weak robots is considered an invalid configuration, no initialization for
			else{
				mailDistribution = new MailDistribution(building,false);
				lowerMailPool = mailDistribution.getLower();
				upperMailPool = mailDistribution.getUpper();
				// If one robot is big and the other is strong, the big robot will deliver to
				// the upper half of the building.
				if(robot1Big) { //at least one big robot --> upper pool
					robot1 = new Robot(delivery, upperMailPool, robot1Big, true, building);
					robot2 = new Robot(delivery, lowerMailPool, robot2Big, true, building); 
				}
				else if (robot2Big) { //robot 2 big --> upperPool
					robot1 = new Robot(delivery, lowerMailPool, false, true, building);
					robot2 = new Robot(delivery, upperMailPool, robot2Big, true, building);
				}
				else { // ALL strong
					robot1 = new Robot(delivery, lowerMailPool, false, true, building);
					robot2 = new Robot(delivery, upperMailPool, false, true, building);
				}
			}
		}
	}

	private BotType botType(String name) {
		switch (name) {
		case "weak":
			return BotType.WEAK;
		case "strong":
			return BotType.STRONG;
		case "big":
			return BotType.BIG;
		default:
			return BotType.INVALID;
		}
	}
	

}
