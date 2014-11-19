package iiitd.mc.timetracker.helper;

import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.database.SQLException;

/**
 * A naive in-memory implementation of the IDatabaseController interface for debugging purposes.
 * @author gullal
 *
 */
public class MockupDatabaseController implements IDatabaseController
{

	private static List<Task> taskList = new ArrayList<Task>();
	private static int tid = 1;
	private static List<Recording> recordsList = new ArrayList<Recording>();
	private static int rid = 1;
	private static boolean initialized = false;
	
	
	@Override
	public IDatabaseController open() throws SQLException
	{
		if(initialized)
			return this;
		
		//if this is the first call, set some test data
		
		Task t1 = new Task("Studies", null);
			Task t2 = new Task("MC", t1);
			t1.getSubtasks().add(t2);
				Task t5 = new Task("Assignment", t2);
				t2.getSubtasks().add(t5);
			Task t3 = new Task("DMG", t1);
			t1.getSubtasks().add(t3);
			Task t4 = new Task("GradAlgo", t1);
			t1.getSubtasks().add(t4);
		Task t6 = new Task("Sports",null);
			Task t7 = new Task("Football",t6);
			Task t8 = new Task("Cricket",t6);
		
		long date = 200000;
		Recording r1 = new Recording(1,t2,new Date(date+200),new Date(date+1000));
		Recording r2 = new Recording(2,t3,new Date(date-600),new Date(date));
		Recording r3 = new Recording(3,t4,new Date(date+1100),new Date(date+2000));
		Recording r4 = new Recording(4,t7,new Date(date-2000),new Date(date-1000));
		Recording r5 = new Recording(5,t8,new Date(date+2100),new Date(date+3000));
		
		insertTask(t1);
		insertTask(t2);
		insertTask(t3);
		insertTask(t4);
		insertTask(t5);
		insertTask(t6);
		insertTask(t7);
		insertTask(t8);
		
		insertRecording(r1);
		insertRecording(r2);
		insertRecording(r3);
		insertRecording(r4);
		insertRecording(r5);
		
		initialized = true;
		return this;
	}

	@Override
	public void close()
	{
		
	}

	@Override
	public void insertTask(Task newTask)
	{
		// "auto-increment" id
		newTask.setId(tid);
		tid++;
		
		taskList.add(newTask);
	}

	@Override
	public Task getTask(long id)
	{
		for(Iterator<Task> i = taskList.iterator(); i.hasNext(); ) 
		{
		    Task t = i.next();
		    if(t.getId() == id)
		    	return t;
		}
		
		return null;
	}

	@Override
	public List<Task> getTasks()
	{
		return taskList;
	}

	@Override
	public List<Task> getTasks(String name)
	{
		List<Task> namedTasks = new ArrayList<>();
		
		for(Iterator<Task> i = taskList.iterator(); i.hasNext(); ) 
		{
		    Task t = i.next();
		    if(t.getName().equalsIgnoreCase(name))
		    	namedTasks.add(t);
		}
		
		return namedTasks;
	}

	@Override
	public void updateTask(Task updatedTask)
	{
		deleteTask(updatedTask.getId());
		insertTask(updatedTask);
	}

	@Override
	public void deleteTask(long id)
	{
		taskList.remove(getTask(id));
	}

	@Override
	public void deleteTask(Task removedTask)
	{
		taskList.remove(removedTask);
	}

	
	@Override
	public void insertRecording(Recording newRecording)
	{
		// "auto-increment" id
		newRecording.setRecordingId(rid);
		rid++;
		
		recordsList.add(newRecording);
	}

	@Override
	public Recording getRecording(long recordingId)
	{
		for(Iterator<Recording> i = recordsList.iterator(); i.hasNext(); ) 
		{
		    Recording r = i.next();
		    if(r.getRecordingId() == recordingId)
		    	return r;
		}
		
		return null;
	}

	@Override
	public List<Recording> getRecordings()
	{
		open();
		return recordsList;
	}

	@Override
	public void updateRecording(Recording updatedRecording)
	{
		deleteRecording(updatedRecording.getRecordingId());
		insertRecording(updatedRecording);
	}

	@Override
	public void deleteRecording(long id)
	{
		recordsList.remove(getRecording(id));
	}

	@Override
	public void deleteRecording(Recording removedRecording)
	{
		recordsList.remove(removedRecording);
	}

	@Override
	public List<Task> getSubTasks(long id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Recording> getRecordings(long date)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
