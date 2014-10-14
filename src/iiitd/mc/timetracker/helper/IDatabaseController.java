package iiitd.mc.timetracker.helper;

import java.util.Date;
import java.util.List;

import iiitd.mc.timetracker.data.*;
import android.database.SQLException;

/**
 * Provides a simple CRUD interface for database access.
 * @author sebastian
 *
 */
public interface IDatabaseController
{
	/**
	 * Open the connection to the database.
	 * This has to be called before using the CRUD functions.
	 * @return The initialized DatabaseController
	 * @throws SQLException
	 */
	public IDatabaseController open() throws SQLException;
	
	/**
	 * Close the connection to the database.
	 */
	public void close();
	
	
	
	// Tasks
	
	/**
	 * Insert a new Task.
	 * @param name Name of the task.
	 * @param desc Additional description of the task (optional).
	 * @param parentid The ID of the parent task in the hierarchy or -1 for a "root" task without parent.
	 */
	public void insertTask(String name, String desc, int parentId);
	
	/**
	 * Insert a new Task.
	 * @param newTask A Task instance with the properties of the task to be created.
	 */
	public void insertTask(Task newTask);
	
	/**
	 * Get the Task with the given ID.
	 * @param id The task ID.
	 * @return Returns the Task instance or null if no task with the given ID exists.
	 */
	public Task getTask(int id);
	
	/**
	 * Returns a list of all tasks.
	 * @return A list of all tasks.
	 */
	public List<Task> getTasks();
	
	/**
	 * Edit an existing Task.
	 * @param updatedTask The Task with its updated properties, the Task ID has to stay unchanged.
	 */
	public void updateTask(Task updatedTask);
	
	/**
	 * Delete the given Task from the database.
	 * All Subtasks and Recordings for that Task will also be deleted (cascaded delete).
	 * @param id The ID of the Task to be deleted.
	 */
	public void deleteTask(int id);
	
	/**
	 * Delete the given Task from the database.
	 * @param removedTask The Task to be deleted.
	 */
	public void deleteTask(Task removedTask);
	
	
	
	// Recordings
	
	/**
	 * Create a new Recording indicating a time span the user worked on a specific Task.
	 * @param taskId The ID of the task to be recorded.
	 * @param start Time when the recording was started.
	 * @param stop Time when the recording was stopped.
	 */
	public void insertRecording(int id, int taskId, Date start, Date stop);
	
	/**
	 * Create a new Recording indicating a time span the user worked on a specific Task.
	 * @param newRecording The Recording containing the properties to be saved to the database.
	 */
	public void insertRecording(Recording newRecording);
	
	/**
	 * Get the Recordings with the given ID.
	 * @param recordingId The Recording ID.
	 * @return Returns the Recording instance or null if no Recording with the given ID exists.
	 */
	public Recording getRecording(int recordingId);
	
	/**
	 * Get a list of all Recordings.
	 * @return A list of all Recordings.
	 */
	public List<Recording> getRecordings();
	
	/**
	 * Edit an existing Recording.
	 * @param updatedRecording The Recording with its updated properties, the Recording ID has to stay unchanged.
	 */
	public void updateRecording(Recording updatedRecording);
	
	/**
	 * Delete the given Recording from the database.
	 * @param id The ID of the Recording to be removed.
	 */
	public void deleteRecording(int id);
	
	/**
	 * Delete the given Recording from the database.
	 * @param removedRecording The Recording to be removed.
	 */
	public void deleteRecording(Recording removedRecording);
	
}
