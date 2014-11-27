package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.data.*;
import iiitd.mc.timetracker.helper.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Suggests tasks that were entered by the user previously.
 * @author sebastian & gullal
 *
 */
public class RecentTaskSuggestor implements ITaskSuggestor
{
	/**
	 * How many recordings are considered.
	 */
	private final int recordingLimit = 5;
	
	private List<SuggestedTask> tasks;
	private List<Recording> recordings;
	IDatabaseController db;
	
	public RecentTaskSuggestor()
	{
		db = ApplicationHelper.createDatabaseController();
	}
	
	@Override
	public List<SuggestedTask> getSuggestedTasks()
	{
		db.open();
		//TODO: use some limit to only get the last 10 (?) recordings?
		recordings = db.getRecordings(); //TODO: Why save them in the RecentTaskSuggester instance if not reused?
		db.close();
		
		tasks = new ArrayList<SuggestedTask>();
		
		Collections.sort(recordings,new Comparator<Recording>(){

			@Override
			public int compare(Recording r1, Recording r2)
			{
				return r2.getStart().compareTo(r1.getStart());
			}
			
		});
		
		// add unique tasks to list and update their probability (ratio of occurrences)
		int i = recordingLimit;
		double prob = 1.0 / recordingLimit;
		for(Recording rec : recordings)
		{
			if(i <= 0)
				break;
			
			SuggestedTask temp = new SuggestedTask(rec.getTask(), prob);
			int index = tasks.indexOf(temp);
			if(index == -1)
			{
				// add new task
				tasks.add(temp);
			}
			else
			{
				// add probability to existing task
				tasks.get(index).increaseProbability(prob);
			}
			
			i--;
		}
		
		Collections.sort(tasks);
		return tasks;
	}
}
