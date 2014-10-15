package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.data.Task;

import java.util.ArrayList;
import java.util.List;

public class MainTaskSuggestor implements ITaskSuggestor
{

	private List<Task> tasks;
	
	@Override
	public List<Task> getTaskList()
	{
		tasks = new ArrayList<Task>();
		List<Task> topTasks = new TopHierarchySuggestor().getTopTasks();
		tasks.addAll(topTasks);
		List<Task> recentTasks = new RecentTaskSuggestor().getRecentTasks();
		tasks.addAll(recentTasks);
		
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
