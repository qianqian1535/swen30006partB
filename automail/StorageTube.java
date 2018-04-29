package automail;

// import exceptions.RobotNotInMailRoomException;
import exceptions.TubeFullException;
import strategies.IMailPool;

import java.util.Queue;
import java.util.Stack;

/**
 * The storage tube carried by the robot.
 */
public class StorageTube {

    private int MAXIMUM_CAPACITY;
    public Stack<MailItem> tube;
    IMailPool mailPool;
    

    /**
     * Constructor for the storage tube
     */
    public StorageTube(int size, IMailPool mailPool){
        this.tube = new Stack<MailItem>();
        this.MAXIMUM_CAPACITY = size;
        this.mailPool = mailPool;
    }

    /**
     * @return if the storage tube is full
     */
    public boolean isFull(){
        return tube.size() == MAXIMUM_CAPACITY;
    }

    /**
     * @return if the storage tube is empty
     */
    public boolean isEmpty(){
        return tube.isEmpty();
    }
    
    /**
     * @return the first item in the storage tube (without removing it)
     */
    public MailItem peek() {
    	return tube.peek();
    }

    /**
     * Add an item to the tube
     * @param item The item being added
     * @throws TubeFullException thrown if an item is added which exceeds the capacity
     */
    public void addItem(MailItem item) throws TubeFullException {
        if(tube.size() < MAXIMUM_CAPACITY){
        	tube.add(item);
        } else {
            throw new TubeFullException();
        }
    }

    /** @return the size of the tube **/
    public int getSize(){
    	return tube.size();
    }
    
    /** 
     * @return the first item in the storage tube (after removing it)
     */
    public MailItem pop(){
        return tube.pop();
    }
    
    public void emplyTube() {
	    	while(!tube.isEmpty()) {
	    		MailItem mailItem = tube.pop();
	    		mailPool.addToPool(mailItem);
	            System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), mailItem.toString());
	    	}
    }
    
    public void fillStorageTube() {
    		Queue<MailItem> q= mailPool.getMail();
	    	try{
				while(!isFull() && !q.isEmpty()) {
					addItem(q.remove());  // Could group/order by floor taking priority into account - but already better than simple
				}
			}
		catch(TubeFullException e){
				e.printStackTrace();
		}
    }

}
