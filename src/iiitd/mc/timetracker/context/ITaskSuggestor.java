package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.data.Task;

import java.util.List;

/**
 * Interface for any class implementing filtering or ranking of tasks.
 * The list of tasks processed in this way can then be suggested to the user
 * or used in further TaskSuggesters to combine results.
 * 
 * @author sebastian
 *
 */
public interface ITaskSuggestor
{
	/**
	 * Get an array of the suggested tasks.
	 * @return
	 */
	public List<Task> getTaskList();
	
	/**
	 * Get an array of the suggested tasks represented as Strings.
	 * @return an array of task strings
	 */
	public List<String> getTaskStrings();
	
}
