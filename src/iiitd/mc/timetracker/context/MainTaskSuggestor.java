package iiitd.mc.timetracker.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Merges different tasks into one Task list
 * @author gullal
 *
 */
public class MainTaskSuggestor implements ITaskSuggestor
{
	private List<SuggestedTask> tasks;
	
	TopHierarchySuggestor topTasksSuggestor = new TopHierarchySuggestor();
	RecentTaskSuggestor recentTasksSuggestor = new RecentTaskSuggestor();
	
	
	@Override
	public List<SuggestedTask> getSuggestedTasks()
	{
		tasks = new ArrayList<SuggestedTask>();
		List<SuggestedTask> topTasks = topTasksSuggestor.getSuggestedTasks();
		tasks.addAll(topTasks);
		List<SuggestedTask> recentTasks = recentTasksSuggestor.getSuggestedTasks();
		for(SuggestedTask r : recentTasks)
		{
			boolean duplicate = false;
			for(SuggestedTask t : tasks)
			{
				if(r.equals(t))
				{
					// don't add duplicate, instead set probability to higher value of the two SuggestedTasks
					if(r.getProbability() < t.getProbability())
						r.setProbability(t.getProbability());
					
					duplicate = true;
				}
			}
			
			if(!duplicate)
				tasks.add(r);
		}
		
		return tasks;
	}

}
