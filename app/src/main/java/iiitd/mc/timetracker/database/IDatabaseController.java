package iiitd.mc.timetracker.database;

import android.database.SQLException;

import java.util.List;

import iiitd.mc.timetracker.model.Recording;
import iiitd.mc.timetracker.model.Task;

/**
 * Provides a simple CRUD interface for database access.
 */
public interface IDatabaseController {
    /**
     * Open the connection to the database.
     * This has to be called before using the CRUD functions.
     *
     * @return The initialized DatabaseController
     * @throws SQLException
     */
    IDatabaseController open() throws SQLException;

    /**
     * Close the connection to the database.
     */
    void close();

    //public void printTasks(String abc);

    // Tasks

    /**
     * Insert a new Task.
     *
     * @param newTask A Task instance with the properties of the task to be created.
     */
    void insertTask(Task newTask);

    /**
     * Get the Task with the given ID.
     *
     * @param id The task ID.
     * @return Returns the Task instance or null if no task with the given ID exists.
     */
    Task getTask(long id);

    /**
     * Returns a list of all tasks.
     *
     * @return A list of all tasks.
     */
    List<Task> getTasks();

    /**
     * Get the task(s) that have the given name.
     * As the name is not necessarily unique to a task this may list containing several Task instances.
     *
     * @param name The name of the tasks to retrieve.
     * @return A list of all the tasks with the given name (empty, one or several elements).
     */
    List<Task> getTasks(String name);

    /**
     * Edit an existing Task.
     *
     * @param updatedTask The Task with its updated properties, the Task ID has to stay unchanged.
     */
    void updateTask(Task updatedTask);

    /**
     * Delete the given Task from the database.
     * All Subtasks and Recordings for that Task will also be deleted (cascaded delete).
     *
     * @param id The ID of the Task to be deleted.
     */
    void deleteTask(long id);

    /**
     * Delete the given Task from the database.
     *
     * @param removedTask The Task to be deleted.
     */
    void deleteTask(Task removedTask);


    // Recordings

    /**
     * Create a new Recording indicating a time span the user worked on a specific Task.
     *
     * @param newRecording The Recording containing the properties to be saved to the database.
     */
    void insertRecording(Recording newRecording);

    /**
     * Get the Recordings with the given ID.
     *
     * @param recordingId The Recording ID.
     * @return Returns the Recording instance or null if no Recording with the given ID exists.
     */
    Recording getRecording(long recordingId);

    /**
     * Get a list of all Recordings.
     *
     * @return A list of all Recordings.
     */
    List<Recording> getRecordings();

    /**
     * Get a list of all Recordings.
     *
     * @param limit The maximum number of recordings to be returned.
     * @return A list of all Recordings.
     */
    List<Recording> getRecordings(int limit);

    /**
     * Get a list of Recordings for a particular date
     */
    List<Recording> getRecordings(long date);

    List<Recording> getRecordings(long start, long end);

    List<Recording> getRecordings(long taskid, long start, long end);

    /**
     * Edit an existing Recording.
     *
     * @param updatedRecording The Recording with its updated properties, the Recording ID has to stay unchanged.
     */
    void updateRecording(Recording updatedRecording);

    /**
     * Delete the given Recording from the database.
     *
     * @param id The ID of the Recording to be removed.
     */
    void deleteRecording(long id);

    /**
     * Delete the given Recording from the database.
     *
     * @param removedRecording The Recording to be removed.
     */
    void deleteRecording(Recording removedRecording);

    /**
     * Gets the subtasks under a particular task
     */
    List<Task> getSubTasks(long id);

    /**
     * Clears the whole database.
     * Use with care!
     */
    void resetDatabase();

}
