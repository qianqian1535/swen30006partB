package strategies;


import automail.Building;
import automail.MailItem;
import automail.PriorityMailItem;

public class MailDistribution {
	private int divider;
	private IMailPool lowerPool;
	private IMailPool upperPool;
	private static final int MAX_WEIGHT = 2000;
	private boolean hasWeak;
	
	public MailDistribution(Building building, boolean hasWeak){
		this.lowerPool = new LowerMailPool();
		this.upperPool = new UpperMailPool();
		divider = building.getFloors() / 2;  // Top normal floor for strong robot
		this.hasWeak = hasWeak;
	}
	
	
	public void addToDistributionPool(MailItem mailItem) {
		if ((mailItem instanceof PriorityMailItem || mailItem.getWeight() > MAX_WEIGHT || mailItem.getDestFloor() <= divider) &&  hasWeak) { 
			//upper strong/big if weak mail item
			lowerPool.addToPool(mailItem);
		}
		else if(hasWeak){
			//add to pool of weak mail item
			upperPool.addToPool(mailItem);
		}
		else if(mailItem.getDestFloor() <= divider) {
			//lower if no weak item
			lowerPool.addToPool(mailItem);
		}
		else {
			//upper if no weak item
			upperPool.addToPool(mailItem);
		}
		
	}
	
	public IMailPool getUpper() {
		return upperPool;
	}
	
	public IMailPool getLower() {
		return lowerPool;
	}
}
