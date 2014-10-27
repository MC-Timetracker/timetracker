package iiitd.mc.timetracker.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A Task describes the details of a specific activity.
 * The user can record time spent for a Task through (multiple) Recordings referencing the Task.
 * @author gullal
 *
 */
public class Task 
{
	/**
	 * String to separate task names in the hierarchy.
	 * e.g. "." for "Studies.Maths.Assignment"
	 */
	public static final String THS = ".";
	/**
	 * Member for Id variable added
	 * @author Shubham
	 */
	private long id;
	private String name;
	private Task parent;
	//private String geo;			//TODO: proper data type for location?
	private String description;
	private List<Task> subtasks;
	
	
	/**
	 * Constructs a new Task.
	 * @param name The name describing the task
	 * @param parent If parent is null or not being set the task is considered on the top most hierarchy level.
	 */
	public Task(String name, Task parent)
	{
		setName(name);
		setParent(parent);
	}
	public Task(String name)
	{
		this(name, null);
	}
	public Task() {
		
	}
	
	
	/**
	 * Get the id of the task
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * Set the id of the task 
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
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
		if(subtasks == null)
			subtasks = new ArrayList<Task>();
		
		return subtasks;
	}
	
	@Override
	public String toString()
	{
		String p = "";
		if (parent != null)
			p = parent.toString() + THS;
		
		return p + this.name;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Task)
		{
			if(id == ((Task)obj).id)
				return true;
		}
		return false;
	}
		
}
