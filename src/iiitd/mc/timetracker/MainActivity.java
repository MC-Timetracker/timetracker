package iiitd.mc.timetracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iiitd.mc.timetracker.data.*;
import iiitd.mc.timetracker.helper.IDatabaseController;
import iiitd.mc.timetracker.view.TaskAutoCompleteTextView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


/**
 * The Main Activity of the timetracker application
 * @author gullal
 *
 */
public class MainActivity extends BaseActivity {
	
	public RelativeLayout relativelayoutstart, relativelayoutbuttons;
	
    Intent recorderIntent;
    TaskRecorderService taskRecorder;
    boolean taskRecorderBound = false;
    
    private TaskAutoCompleteTextView tvTask;
    private ListView recentAct;
    private ArrayAdapter recentActAdapter;
    //WifiManager mainWifiObj;

    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_main);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_main, null));
        
        tvTask = (TaskAutoCompleteTextView) findViewById(R.id.taskSelectionBox);
        
        initRecentActList();
		
		// init AutoRecorder triggers
		BootReceiver.setupAutoRecorderTriggers(this);
	}
	

	/**
	 * Handle everything to actually start recording the task in the business logic layer with TaskRecorder.
	 */
	public void startRecording(Task task)
	{	
		// Start recording in a Service
		recorderIntent = new Intent(this, TaskRecorderService.class);
		recorderIntent.putExtra(TaskRecorderService.EXTRA_TASK_ID, task.getId());
		startService(recorderIntent);
		
		Intent running_activity = new Intent(this, RunningActivity.class);
		startActivity(running_activity);
	}


	/**
	 * Event handler for click of start button.
	 * @param view
	 */
	public void Start(View view)
	{
		// Get the task instance that corresponds to the String entered by the user and start recording it
		tvTask.createTask(new TaskAutoCompleteTextView.OnTaskCreatedListener() 
			{
				@Override
				public void onTaskCreated(Task newTask)
				{
					startRecording(newTask);
				}
			}, 
			null);
	}

	
	public void initRecentActList()
	{
		recentAct = (ListView) findViewById(R.id.recentLv);
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		List<Recording> records = db.getRecordings((new Date()).getTime());
		db.close();
		
		List<String> recentTasks = new ArrayList<>();
		
		for(Recording rec: records){
			recentTasks.add(rec.getTask().getNameFull());
		}
		
		recentActAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,recentTasks);
		
		recentAct.setAdapter(recentActAdapter);
	}
}	