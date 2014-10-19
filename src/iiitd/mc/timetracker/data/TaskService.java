package iiitd.mc.timetracker.data;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.helper.DatabaseController;
import iiitd.mc.timetracker.helper.IDatabaseController;

/**
 * Service class holding the state of currently recorded tasks.
 * Also provides helper methods to interact with Tasks.
 * @author sebastian
 *
 */
public class TaskService
{
	private Recording currentRecording = null;
	
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
	}
	
	public void startRecording(String taskString)
	{
		final String sTask = taskString;
		Task task = TaskService.getTaskFromString(sTask);
		
		if (task != null)
		{
			this.startRecording(task);
		}
		else
		{
			// if task does not exist, ask the user if it should be created
			AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationHelper.getAppContext());
			builder.setMessage(R.string.createNewTask)
			       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Create new task and start recording it
			        	   Task newTask = TaskService.createTaskFromString(sTask);
			        	   startRecording(newTask);
			           }
			       })
			       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Do nothing
			           }
			       })
			       .create();
			return; // further action is handled in dialog event handlers
		}
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
		
		IDatabaseController db = ApplicationHelper.createDatabaseController(); //TODO: why does DatabaseController need a Context?
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

	public static Task createTaskFromString(String taskString)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
