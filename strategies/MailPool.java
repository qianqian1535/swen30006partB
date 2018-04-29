package strategies;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.StorageTube;
import exceptions.TubeFullException;

public class MailPool implements IMailPool{
	
	private LinkedList<MailItem> mail;
	
	
	public MailPool() {
		mail = new LinkedList<MailItem>();
	}
	
	//finds the priorty of mail item if of instanceof PriorityMailItem
	private int priority(MailItem m) {
		return (m instanceof PriorityMailItem) ? ((PriorityMailItem) m).getPriorityLevel() : 0;
	}
	
	public void addToPool(MailItem mailItem) {
		if (mailItem instanceof PriorityMailItem) {  // sorts mail in priority order
			int priority = ((PriorityMailItem) mailItem).getPriorityLevel();
			ListIterator<MailItem> i = mail.listIterator();
			while (i.hasNext()) {
				if (priority(i.next()) < priority) {
					i.previous();
					i.add(mailItem);
					return; // Added it - done
				}
			}
		}
		mail.add(mailItem);
	}
	
	
	public void fillStorageTube(StorageTube tube) {
		Queue<MailItem> q = mail;
		try{
			while(!tube.isFull() && !q.isEmpty()) {
				tube.addItem(q.remove());  // Could group/order by floor taking priority into account - but already better than simple
			}
		}
		catch(TubeFullException e){
			e.printStackTrace();
		}
	}
}
