package automail;

public class Building {
	/** The number of floors in the building**/
	private int numFloors;
	/** Represents the ground floor location */
	private int lowest;

	/** Represents the mailroom location */
	private int mailRoomLoc;
	public Building(int numFloors, int lowest, int mailRoomLoc) {
		this.lowest = lowest;
		this.numFloors = numFloors;
		this.mailRoomLoc = mailRoomLoc;
	}
	public int getFloors() {
    	return numFloors;
    }
	public int getLowest() {
    	return lowest;
    }
	public int getMailRoom() {
    	return mailRoomLoc;
    }
}
