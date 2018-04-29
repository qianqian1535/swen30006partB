package automail;

import exceptions.ItemTooHeavyException;
// import exceptions.RobotNotInMailRoomException;
import exceptions.TubeFullException;
import strategies.IMailPool;

import java.util.Queue;
import java.util.Stack;

/**
 * The storage tube handles all actions of mailItem, the filling of storage tube, the delivery of mail, the finding of destination
 */
public class StorageTube {

    private int MAXIMUM_CAPACITY;
    public Stack<MailItem> tube;
    IMailPool mailPool;
    public MailItem deliveryItem;
    private boolean strong;
    private IMailDelivery delivery;
    

    /**
     * Constructor for the storage tube
     */
    public StorageTube(int size, IMailPool mailPool, boolean strong, IMailDelivery delivery){
        this.tube = new Stack<MailItem>();
        this.MAXIMUM_CAPACITY = size;
        this.mailPool = mailPool;
        this.strong = strong;
        this.delivery = delivery;
    }

    public boolean isEmpty(){
        return tube.isEmpty();
    }

    public int mailPriority() {
    		MailItem item = tube.peek();
		return (item instanceof PriorityMailItem) ? ((PriorityMailItem) item).getPriorityLevel() : 0;
    }

    /**
     * Add an item to the tube
     * @param item The item being added
     * @throws TubeFullException thrown if an item is added which exceeds the capacity
     */
    private void addItem(MailItem item) throws TubeFullException {
        if(tube.size() < MAXIMUM_CAPACITY){
        	tube.add(item);
        } else {
            throw new TubeFullException();
        }
    }
    
    public void emptyTube() {
	    	while(!tube.isEmpty()) {
	    		MailItem mailItem = tube.pop();
	    		mailPool.addToPool(mailItem);
	            System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), mailItem.toString());
	    	}
    }
    
    public void fillStorageTube() {
    		Queue<MailItem> q= mailPool.getMail();
	    	try{
				while(!(tube.size() == MAXIMUM_CAPACITY) && !q.isEmpty()) {
					addItem(q.remove());  // Could group/order by floor taking priority into account - but already better than simple
				}
			}
		catch(TubeFullException e){
				e.printStackTrace();
		}
    }
    
    public int getMailDestination() throws ItemTooHeavyException {
    		deliveryItem = tube.pop();
    		if (!strong && deliveryItem.weight > 2000) throw new ItemTooHeavyException(); 
    		return deliveryItem.getDestFloor();
    }
    
    
    public void deliverMail() {
    		delivery.deliver(deliveryItem);
    }
    

}
