package iiitd.mc.timetracker.data;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.helper.IDatabaseController;

/**
 * Service class holding the state of currently recorded tasks.
 * Also provides helper methods to interact with Tasks.
 * @author sebastian
 *
 */
public class TaskRecorder
{
	private transient Vector<RecorderListener> listeners;
	private Recording currentRecording = null;
	
	/**
	 * Add a listener for events of the TaskRecorder.
	 * @param l
	 */
	synchronized public void addListener(RecorderListener l) {
		if (listeners == null)
			listeners = new Vector<>();
		listeners.addElement(l);
	}  

	/**
	 * Remove a listener for events of the TaskRecorder.
	 * @param l
	 */
	synchronized public void removeListener(RecorderListener l) {
		if (listeners == null)
			listeners = new Vector<>();
		listeners.removeElement(l);
	}

	/**
	 * Send an event to all listeners announcing a state change.
	 * @param state
	 */
	protected void fireRecorderEvent(RecorderEventState state) {
		// if we have no listeners, do nothing...
		if (listeners != null && !listeners.isEmpty()) {
			// create the event object to send
			RecorderEvent event = new RecorderEvent(this, state);

			// make a copy of the listener list in case
			//   anyone adds/removes listeners
			Vector<RecorderListener> targets;
			synchronized (this) {
				targets = (Vector<RecorderListener>) listeners.clone();
			}

			// walk through the listener list and
			//   call the sunMoved method in each
			Enumeration<RecorderListener> e = targets.elements();
			while (e.hasMoreElements()) {
				RecorderListener l = (RecorderListener) e.nextElement();
				l.onRecorderStateChanged(event);
			}
		}
	}
	
	
	/**
	 * Start recording the given Task.
	 * If another task is currently recorded that Recording is stopped.
	 * The state of current recordings is held in this instance of the class.
	 * @param task The task for which recording will be started.
	 */
	public void startRecording(Task task)
	{
		// if some Task is currently being recorded, stop that first as do not record parallel tasks
		if (this.isRecording())
		{
			stopRecording();
		}
		
		currentRecording = new Recording();
		currentRecording.setTask(task);
		currentRecording.setStart(new Date());
		
		this.fireRecorderEvent(RecorderEventState.Started);
	}
	
	
	/**
	 * Stop the currently recorded Task and save the finished Recording to the database.
	 */
	public void stopRecording()
	{
		if(currentRecording == null)
			return;
		
		currentRecording.setEnd(new Date());
		
		// save Recording to database
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		db.insertRecording(currentRecording); //TODO: check result of DB operation?
		db.close();
		
		currentRecording = null;
		
		this.fireRecorderEvent(RecorderEventState.Stopped);
	}
	
	/**
	 * 
	 * @return Returns true if a Task is currently being recorded.
	 */
	public boolean isRecording()
	{
		return (this.currentRecording != null);
	}
	
	
	
	/**
	 * Return the Task instance that corresponds to the given string describing a task path.
	 * @param taskString A string describing the full path to the required Task (e.g. "Studies.Maths.Assignment")
	 * @return The Task instance for the given path or null if no such Task exists.
	 */
	public static Task getTaskFromString(String taskString)
	{
		Task task = null;
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		
		String[] taskStringParts = taskString.split(Pattern.quote(Task.THS));
		//get all tasks with name like the lowest hierarchy part of the string	
		List<Task> taskCandidates = db.getTasks(taskStringParts[taskStringParts.length-1]);
		for(Iterator<Task> i = taskCandidates.iterator(); i.hasNext(); ) 
		{
			Task t = i.next();
			if(taskString.equalsIgnoreCase(t.toString()))
			{
				task = t;
				break;
			}
		}
		
		db.close();
		return task;
	}

	/**
	 * Adds the new task to the database.
	 * Cascadingly creates parent tasks if they do not exist yet.
	 * If a task with the exact given taskString already exists the existing task is returned and no new task is created.
	 * @param taskString The full task path describing the task to be created with its parents. 
	 * @return The newly created or already existing task for the given taskString.
	 */
	public static Task createTaskFromString(String taskString)
	{
		// check for existing task
		Task task = getTaskFromString(taskString);
		if(task != null)
			return task;
		
		// create new task for lowest part in hierarchy
		int separator = taskString.lastIndexOf(Task.THS);
		String taskName = taskString.substring(separator+1);
		Task taskParent = null;
		if(separator != -1)
		{
			String parentName = taskString.substring(0, separator);
			taskParent = createTaskFromString(parentName);
		}
		Task newTask = new Task(taskName, taskParent);
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();	
		db.insertTask(newTask);	
		db.close();
		
		return newTask;
	}
}
