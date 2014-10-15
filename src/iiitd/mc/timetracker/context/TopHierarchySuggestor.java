package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.MockupDatabaseController;

import java.util.ArrayList;
import java.util.List;

public class TopHierarchySuggestor
{

	private List<Task> tasks;
	
	MockupDatabaseController mDBC = new MockupDatabaseController();
	
	public List<Task> getTopTasks()
	{
		tasks = new ArrayList<Task>();
		List<Task> temp = mDBC.getTasks();
		for(Task t: temp){
			if(t.getParent() == null){
				tasks.add(t);
			}
		}
		
		return tasks;
	}

}
