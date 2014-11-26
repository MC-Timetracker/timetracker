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
public class TopHierarchySuggestor implements ITaskSuggestor
{

	private List<SuggestedTask> tasks;
	IDatabaseController db;
	
	public TopHierarchySuggestor()
	{
		db = ApplicationHelper.createDatabaseController();
	}

	@Override
	public List<SuggestedTask> getSuggestedTasks()
	{
		db.open();
		List<Task> topTasks = db.getTasks();
		db.close();
		
		tasks = new ArrayList<SuggestedTask>();
		for(Task t : topTasks)
		{
			// wrap the Tasks in SuggestTask objects with a constant probability
			//TODO: what probability for TopHierarchy tasks? does it matter?
			SuggestedTask item = new SuggestedTask(t, 0.75);
			tasks.add(item);
		}
		
		return tasks;
	}

}
