package iiitd.mc.timetracker;

import java.util.List;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.*;
import iiitd.mc.timetracker.data.*;
import iiitd.mc.timetracker.data.TaskRecorderService.TaskRecorderBinder;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;


/**
 * The Main Activity of the timetracker application
 * @author gullal
 *
 */
public class MainActivity extends BaseActivity {
	
	public RelativeLayout relativelayoutstart, relativelayoutbuttons;
    Button btnStart,btnStop,btnPause,btnResume;
    private Chronometer chronometer;
    long stoptime=0;
    
    Intent recorderIntent;
    TaskRecorderService taskRecorder;
    boolean taskRecorderBound = false;
    
    ITaskSuggestor suggester;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		navigationDisplay();
		
		initTaskAutocomplete();
	}
	
	
	/**
	 * Initial setup of the autocomplete dropdown task selection text box.
	 */
	public void initTaskAutocomplete()
	{
		AutoCompleteTextView autoTv = (AutoCompleteTextView) findViewById(R.id.taskSelectionBox);
		
		suggester = new MainTaskSuggestor();
		List<String> suggestedTasks = suggester.getTaskStrings();
		CustomArrayAdapter taskListAdapter = new CustomArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestedTasks);
		autoTv.setAdapter(taskListAdapter);
		
		autoTv.setThreshold(0);
		autoTv.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					((AutoCompleteTextView)view).showDropDown();
				}
		});
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
		
		startUI();
	}


	/**
	 * Event handler for click of start button.
	 * @param view
	 */
	public void Start(View view)
	{
		// Get the task instance that corresponds to the String entered by the user
		AutoCompleteTextView taskSelector = (AutoCompleteTextView) findViewById(R.id.taskSelectionBox);
		final String sTask = taskSelector.getText().toString();
		
		Task task = TaskRecorderService.getTaskFromString(sTask);
		
		if (task != null)
		{
			startRecording(task);
		}
		else
		{
			// if task does not exist, ask the user if it should be created
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog dialog = builder.setMessage(R.string.promptCreateNewTask)
			       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Create new task and start recording it
			        	   Task newTask = TaskRecorderService.createTaskFromString(sTask);
			        	   startRecording(newTask);
			           }
			       })
			       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Do nothing
			           }
			       })
			       .create();
			dialog.show();
			return; // further action is handled in dialog event handlers
		}
	}
	/**
	 * Event handler for click of stop button.
	 * @param view
	 */
	public void Stop(View view)
	{
		stopService(recorderIntent);
		
		stopUI();
	}
	
	/**
	 * Handles all UI changes when recording is started.
	 */
	public void startUI()
	{
		setContentView(R.layout.pausestop);
		navigationDisplay();
		
		//TODO: Chronometer should not count independent of taskRecorder but use that timing instead.
		chronometer=(Chronometer) findViewById(R.id.chronometer);
		chronometer.setBase(SystemClock.elapsedRealtime());
		chronometer.start();
		
		//closedrawerpausestop();
	}
	
	/**
	 * Handles all UI changes when recording is stopped.
	 */
	public void stopUI()
	{
		chronometer.stop();
	}
	
	public void Pause(View view)
	{
		btnPause =(Button) findViewById(R.id.btnPause);
		btnResume =(Button) findViewById(R.id.btnResume);
		stoptime=chronometer.getBase()-SystemClock.elapsedRealtime();
		chronometer.stop();
		btnPause.setVisibility(View.INVISIBLE);
		btnResume.setVisibility(View.VISIBLE);
	}
    
	public void Resume(View view){
		btnResume =(Button) findViewById(R.id.btnResume);
		chronometer.setBase(SystemClock.elapsedRealtime()+stoptime);
		chronometer.start();
		btnPause.setVisibility(View.VISIBLE);
		btnResume.setVisibility(View.INVISIBLE);
	}
	
	public void closedrawer() {
		//brings relative layout where pause stop buttons come to the front on closing the drawer
		relativelayoutstart = (RelativeLayout) findViewById(R.id.root_layout);
		relativelayoutstart.bringToFront();
    }
	public void closedrawerpausestop(){
		relativelayoutbuttons = (RelativeLayout) findViewById(R.id.pausestop);
		relativelayoutbuttons.bringToFront();
	}
	
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to TaskRecorderService, cast the IBinder and get TaskRecorderService instance
            TaskRecorderBinder binder = (TaskRecorderBinder) service;
            taskRecorder = binder.getService();
            taskRecorderBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	taskRecorderBound = false;
        }
    };
}	