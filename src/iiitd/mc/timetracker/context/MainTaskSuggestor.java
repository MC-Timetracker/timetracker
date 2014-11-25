package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.data.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Merges different tasks into one Task list
 * @author gullal
 *
 */
public class MainTaskSuggestor implements ITaskSuggestor
{

	private List<Task> tasks;
	
	TopHierarchySuggestor topTasksSuggestor = new TopHierarchySuggestor();
	RecentTaskSuggestor recentTasksSuggestor = new RecentTaskSuggestor();
	
	
	@Override
	public List<Task> getSuggestedTasks()
	{
		tasks = new ArrayList<Task>();
		List<Task> topTasks = topTasksSuggestor.getSuggestedTasks();
		tasks.addAll(topTasks);
		List<Task> recentTasks = recentTasksSuggestor.getSuggestedTasks();
		for(Task t: recentTasks)
		{
			if(!tasks.contains(t))
				tasks.add(t);
		}
		
		return tasks;
	}

}
