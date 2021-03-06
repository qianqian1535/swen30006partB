package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;
import strategies.IRobotBehaviour;
import strategies.MyRobotBehaviour;

/**
 * The robot handle the movement of robot around the building only
 */
public class Robot {

	StorageTube tube;
    IRobotBehaviour behaviour;
    protected final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private Building building;
    private boolean big;
    
    private int deliveryCounter;
    

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param behaviour governs selection of mail items for delivery and behaviour on priority arrivals
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items, either upper or lower
     * @param big is whether the robot delivers is big or not
     */
    public Robot(IMailDelivery delivery, IMailPool mailPool, boolean big, boolean strong, Building building){
	    	id = "R" + hashCode();
	        // current_state = RobotState.WAITING;
	    	current_state = RobotState.RETURNING;
	    	this.building = building;
	   current_floor = building.getMailRoom();
	   	if(big) {
	   		tube = new StorageTube(6,mailPool,strong, delivery);
	   	}
	   	else {
	   		tube = new StorageTube(4,mailPool,strong, delivery);
	   	}
	    behaviour = new MyRobotBehaviour(strong); //Apply creator principle
	    this.deliveryCounter = 0;
    }

    /**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void step() throws ExcessiveDeliveryException, ItemTooHeavyException{    	
    	switch(current_state) {
    		/** This state is triggered when the robot is returning to the mailroom after a delivery */
    		case RETURNING:
    			/** If its current position is at the mailroom, then the robot should change state */
                if(current_floor == building.getMailRoom()){
                		tube.emptyTube();
                		changeState(RobotState.WAITING);
                } else {
                	/** If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(building.getMailRoom());
                	break;
                }
    		case WAITING:
    			/** Tell the sorter the robot is ready */
    			tube.fillStorageTube(); //configurations
            // System.out.println("Tube total size: "+tube.getTotalOfSizes());
            /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
            if(!tube.isEmpty()){
                	deliveryCounter = 0; // reset delivery counter
        			behaviour.startDelivery();
        			setRoute();
                	changeState(RobotState.DELIVERING);
            }
            break;
    		case DELIVERING:
    			/** Check whether or not the call to return is triggered manually **/
    			boolean wantToReturn = behaviour.returnToMailRoom(tube);
    			if(current_floor == destination_floor){ // If already here drop off either way
                    /** Delivery complete, report this to the simulator! */
                    tube.deliverMail();
                    deliveryCounter++;
                    if(deliveryCounter > 4 && big){
                    		throw new ExcessiveDeliveryException();
                    }
                    else if(deliveryCounter > 6 && !big) { //unsure why big variable changing but it is
                    		throw new ExcessiveDeliveryException();
                    }
                    /** Check if want to return or if there are more items in the tube*/
                    if(wantToReturn || tube.isEmpty()){
                    // if(tube.isEmpty()){
                    	changeState(RobotState.RETURNING);
                    }
                    else{
                        /** If there are more items, set the robot's route to the location to deliver the item */
                        setRoute();
                        changeState(RobotState.DELIVERING);
                    }
    			} else
    			{/*
	    			if(wantToReturn){
	    				// Put the item we are trying to deliver back
	    				try {
							tube.addItem(deliveryItem);
						} catch (TubeFullException e) {
							e.printStackTrace();
						}
	    				changeState(RobotState.RETURNING);
	    			}
	    			else{*/
	        			/** The robot is not at the destination yet, move towards it! */
	                        moveTowards(destination_floor);
	                /*
	    			}
	    			*/
    			}
                break;
    	}
    }

    /**
     * Sets the route for the robot
     */
    private void setRoute() throws ItemTooHeavyException{
        
        destination_floor = tube.getMailDestination();//get destination of current mail item
    }

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    private void moveTowards(int destination){
        if(current_floor < destination){
            current_floor++;
        }
        else{
            current_floor--;
        }
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %11s changed from %s to %s%n", Clock.Time(), id, current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %11s-> [%s]%n", Clock.Time(), id, tube.deliveryItem.toString());
    	}
    }
    

}
