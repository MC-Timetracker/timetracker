package iiitd.mc.timetracker.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Merges different tasks into one Task list
 * @author gullal
 *
 */
public class MainTaskSuggestor implements ITaskSuggestor
{
	ITaskSuggestor[] suggestors = new ITaskSuggestor[] { 
			new TopHierarchySuggestor(),
			new RecentTaskSuggestor(),
			new TimeTaskSuggestor(),
	};
	double[] suggestorWeights = new double[] {
			0,		// top hierarchy
			0.3,	// recent tasks
			0.7,	// time
	};
	
	
	@Override
	public List<SuggestedTask> getSuggestedTasks()
	{
		List<SuggestedTask> tasks = new ArrayList<SuggestedTask>();
		
		for(int i = 0; i < suggestors.length; i++)
		{
			ITaskSuggestor suggestor = suggestors[i];
			
			for(SuggestedTask s : suggestor.getSuggestedTasks())
			{
				// scale probability according to weight
				double prob = s.getProbability() * suggestorWeights[i];
				s.setProbability(prob);
				
				addSuggestionToList(tasks, s);
			}
		}
		
		Collections.sort(tasks);
		return tasks;
	}
	
	/**
	 * Helper function to add a task to the list of suggestions.
	 * Avoids adding duplicate tasks and increases the probability of the task instead.
	 * @param list The list of suggestions to be extended.
	 * @param suggestedTask The task to be added to the list.
	 */
	public static void addSuggestionToList(List<SuggestedTask> list, SuggestedTask suggestedTask)
	{
		for(SuggestedTask l : list)
		{
			if(l.equals(suggestedTask))
			{
				// don't add duplicate, instead increase probability of suggested task
				l.increaseProbability(suggestedTask.getProbability());
				return;
			}
		}
		
		// task was not in the list yet
		list.add(suggestedTask);
	}

}
