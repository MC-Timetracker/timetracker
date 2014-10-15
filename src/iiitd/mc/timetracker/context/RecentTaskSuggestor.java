package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.data.*;
import iiitd.mc.timetracker.helper.MockupDatabaseController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Suggests tasks that were entered by the user previously.
 * @author sebastian
 *
 */
public class RecentTaskSuggestor
{
	/**
	 * Returns all previously used tasks, most recently used task first.
	 */
	private List<Task> tasks;
	private List<Recording> recordings;
	
	MockupDatabaseController mDBC = new MockupDatabaseController();
	
	
	public List<Task> getRecentTasks()
	{
		//TODO: get list of recordings from database
		/* e.g. as SQL:
		 * SELECT DISTINCT Tasks.ID 
		 * FROM Tasks INNER JOIN Recordings ON Tasks.ID = Recordings.task_id
		 * ORDER BY Recordings.start_time DESC
		 * LIMIT 10
		 */
		recordings = mDBC.getRecordings();
		tasks = new ArrayList<Task>();
		
		Collections.sort(recordings,new Comparator<Recording>(){

			@Override
			public int compare(Recording r1, Recording r2)
			{
				return r2.getStart().compareTo(r1.getStart());
			}
			
		});
		
		for(Recording rec: recordings){
			
			Task temp = rec.getTask();
			if(!tasks.contains(temp)){
				tasks.add(temp);
			}
		}
		return tasks;
	}
}
