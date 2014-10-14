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
public class RecentTaskSuggestor implements ITaskSuggestor
{
	/**
	 * Returns all previously used tasks, most recently used task first.
	 */
	private List<Task> tasks;
	private List<Recording> recordings;
	
	MockupDatabaseController mDBC = new MockupDatabaseController();
	
	
	public List<Task> getTaskList()
	{ 
		//TODO: get list of recordings from database
		/* e.g. as SQL:
		 * SELECT DISTINCT Tasks.ID 
		 * FROM Tasks INNER JOIN Recordings ON Tasks.ID = Recordings.task_id
		 * ORDER BY Recordings.start_time DESC
		 * LIMIT 10
		 */
		
		tasks = mDBC.getTasks();
		recordings = mDBC.getRecordings();
		
		/*Collections.sort(tasks,new Comparator<Task>(){

			@Override
			public int compare(Task task1, Task task2)
			{
				//return recordings.get(task1.getTaskId()).getStart().compareTo(recordings.get(task2.getTaskId()).getStart());
			}
			
		});*/
		
		return tasks;
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
