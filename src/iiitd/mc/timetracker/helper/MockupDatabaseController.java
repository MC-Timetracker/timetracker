package iiitd.mc.timetracker.helper;

import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.SQLException;

/**
 * 
 * @author gullal
 *
 */
public class MockupDatabaseController implements IDatabaseController
{

	private List<Task> taskList;
	private List<Recording> recordsList;
	
	@Override
	public IDatabaseController open() throws SQLException
	{
		taskList = new ArrayList<Task>();
		recordsList = new ArrayList<Recording>();
		
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
		
		taskList.add(t1);
		taskList.add(t2);
		taskList.add(t3);
		taskList.add(t4);
		taskList.add(t5);
		taskList.add(t6);
		taskList.add(t7);
		taskList.add(t8);
		
		recordsList.add(r1);
		recordsList.add(r2);
		recordsList.add(r3);
		recordsList.add(r4);
		recordsList.add(r5);
		
		return this;
	}

	@Override
	public void close()
	{
		
	}

	@Override
	public void insertTask(Task newTask)
	{
		
	}

	@Override
	public Task getTask(int id)
	{
				return null;
	}

	@Override
	public List<Task> getTasks()
	{
		return taskList;
	}

	@Override
	public void updateTask(Task updatedTask)
	{
		
	}

	@Override
	public void deleteTask(int id)
	{
		
	}

	@Override
	public void deleteTask(Task removedTask)
	{
		
	}

	@Override
	public void insertRecording(Recording newRecording)
	{
		
	}

	@Override
	public Recording getRecording(int recordingId)
	{
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
		
	}

	@Override
	public void deleteRecording(int id)
	{
		
	}

	@Override
	public void deleteRecording(Recording removedRecording)
	{
		
	}

}
