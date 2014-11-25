package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.*;

import java.util.List;

/**
 * Suggests top hierarchy tasks
 * @author gullal
 *
 */
public class TopHierarchySuggestor implements ITaskSuggestor
{

	private List<Task> tasks;
	IDatabaseController db;
	
	public TopHierarchySuggestor()
	{
		db = ApplicationHelper.createDatabaseController();
	}

	@Override
	public List<Task> getSuggestedTasks()
	{		
		db.open();
		tasks = db.getTasks();
		db.close();
		
		return tasks;
	}

}
