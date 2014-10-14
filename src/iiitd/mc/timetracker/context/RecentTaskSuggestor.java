package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.data.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Suggests tasks that were entered by the user previously.
 * @author sebastian
 *
 */
public class RecentTaskSuggestor implements ITaskSuggestor
{
	/**
	 * Returns all previously used tasks, most recently used task first.
	 */
	private List<Task> temp;
	public List<Task> getTaskList()
	{ 
		//TODO: get list of recordings from database
		/* e.g. as SQL:
		 * SELECT DISTINCT Tasks.ID 
		 * FROM Tasks INNER JOIN Recordings ON Tasks.ID = Recordings.task_id
		 * ORDER BY Recordings.start_time DESC
		 * LIMIT 10
		 */
		
		
		temp = new ArrayList<Task>();
		
		Task t1 = new Task("Studies", null);
			Task t2 = new Task("MC", t1);
			t1.getSubtasks().add(t2);
				Task t5 = new Task("Assignment", t2);
				t2.getSubtasks().add(t5);
			Task t3 = new Task("DMG", t1);
			t1.getSubtasks().add(t3);
			Task t4 = new Task("GradAlgo", t1);
			t1.getSubtasks().add(t4);
		
		temp.add(t1);
		temp.add(t2);
		temp.add(t3);
		temp.add(t4);
		temp.add(t5);
		
		return temp;
	}
	
	public List<String> getTaskStrings()
	{
		List<Task> lst = this.getTaskList();
		
		List<String> array = new ArrayList<String>(lst.size());
		
		for (Task value : lst) {
			array.add(value.toString());
		}
		
		return array;
	}
}
