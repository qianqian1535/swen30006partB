package strategies;

import automail.Building;
import automail.IMailDelivery;
import automail.Robot;

public class Automail {

	public Robot robot1, robot2;
	public IMailPool mailPool;

	public static enum BotType {
		BIG, STRONG, WEAK, INVALID;
	}

	public Automail(IMailDelivery delivery, String bot1, String bot2, Building building) {
		// Swap between simple provided strategies and your strategies here

		/** Initialize the MailPool */

		//// Swap the next line for the one below
		mailPool = new WeakStrongMailPool(building);
		BotType robotType1, robotType2;
		robotType1 = botType(bot1);
		robotType2 = botType(bot2);
		/*
		 * 1. In all configurations, one robot (upper) will deliver only to the top half
		 * of the building, while the other robot (lower) will deliver to the bottom
		 * half as well all priority items.
		 */

		/** Initialize the RobotAction */
		if (robotType1 == BotType.WEAK) {
			// the weak robot will deliver to upper half, the lower robot will deliver all
			// parcels too heavy for the weak robot
			if (robotType2 != BotType.WEAK) {
				// Constructor: Robot(delivery,mailPool, boolean upper, boolean allHeavy,boolean strong
				// building)
				robot2 = new Robot(delivery, mailPool, false, true,true, building);
				robot1 = new Robot(delivery, mailPool, true, false,false, building);
			} // Two weak robots is considered an invalid configuration, no initialization for
				// 2 weak bots
		} else if (robotType2 == BotType.WEAK) {
			robot1 = new Robot(delivery, mailPool, false, true,true, building);
			robot2 = new Robot(delivery, mailPool, true, false, false, building);
		} else {
			// If one robot is big and the other is strong, the big robot will deliver to
			// the upper half of the building.
			if (robotType2 == BotType.BIG) {
				robot1 = new Robot(delivery, mailPool, false, false,true, building);
				robot2 = new Robot(delivery, mailPool, true, false,true, building); // big
			}else {
				//if robot1 is big or both are strong
				robot2 = new Robot(delivery, mailPool, false, false,true, building);
				robot1 = new Robot(delivery, mailPool, true, false, true, building); 
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
