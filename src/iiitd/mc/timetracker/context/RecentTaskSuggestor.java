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
	 * Returns all previously used tasks, most recently used task first.
	 */
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
		
		for(Recording rec: recordings){
			//TODO: calculate some meaningful probability or recent tasks?!
			SuggestedTask temp = new SuggestedTask(rec.getTask(), 0.5);
			if(!tasks.contains(temp)){
				tasks.add(temp);
			}
		}
		return tasks;
	}
}
