package iiitd.mc.timetracker.data;

import java.util.List;

/**
 * A Task describes the details of a specific activity.
 * The user can record time spent for a Task through (multiple) Recordings referencing the Task.
 * @author gullal
 *
 */
public class Task 
{
	private String name;
	private Task parent;
	//private String geo;			//TODO: proper data type
	private String description;
	private List<Task> subtasks;
	
	/**
	 * Get the name describing the Task.
	 * @return
	 */
	public String getName() 
	{
		return name;
	}
	/**
	 * Set the name describing the Task.
	 * @param name
	 */
	public void setName(String name) 
	{
		this.name = name;
	}
	/**
	 * Get the parent Task of this Task.
	 * Through parent relationships a task hierarchy can be built.
	 * @return 
	 */
	public Task getParent() 
	{
		return parent;
	}
	/**
	 * Set the parent Task of this Task.
	 * Through parent relationships a task hierarchy can be built.
	 * @return 
	 */
	public void setParent(Task parent)
	{
		this.parent = parent;
	}
	/**
	 * Get the description text giving further details about the Task.
	 * @return
	 */
	public String getDescription() 
	{
		return description;
	}
	/**
	 * Set the description text giving further details about the Task.
	 * @param description
	 */
	public void setDescription(String description) 
	{
		this.description = description;
	}
	/**
	 * Get the list of subtasks, Tasks for which this Task serves as parent.
	 * This is a convenience function, modifications of the list will not propagate to the database.
	 * @return
	 */
	public List<Task> getSubtasks() 
	{
		return subtasks;
	}
		
}
