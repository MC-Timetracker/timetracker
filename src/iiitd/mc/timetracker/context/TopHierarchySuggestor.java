package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Suggests top hierarchy tasks
 * @author gullal
 *
 */
public class TopHierarchySuggestor
{

	private List<Task> tasks;
	IDatabaseController db;
	
	public TopHierarchySuggestor()
	{
		db = ApplicationHelper.createDatabaseController();
	}
	
	public List<Task> getTopTasks()
	{
		db.open();
		tasks = new ArrayList<Task>();
		
		List<Task> temp = db.getTasks();
		for(Task t: temp){
			if(t.getParent() == null){
				tasks.add(t);
			}
		}
		
		db.close();
		return tasks;
	}

}
