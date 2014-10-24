package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.NavigationAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import java.util.List;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.*;
import iiitd.mc.timetracker.data.*;
import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.RelativeLayout;


/**
 * The Main Activity of the timetracker application
 * @author gullal
 *
 */

public class MainActivity extends BaseActivity implements RecorderListener {
    
	public RelativeLayout relativelayoutstart, relativelayoutbuttons;
    Button btnStart,btnStop,btnPause,btnResume;
    private Chronometer chronometer;
    long stoptime=0;
    
    TaskRecorder taskRecorder;
    ITaskSuggestor suggester;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		navigationDisplay();
		
		taskRecorder = new TaskRecorder();
		taskRecorder.addListener(this);
		
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
	public void startRecording()
	{	
		// Get the task instance that corresponds to the String entered by the user
		AutoCompleteTextView taskSelector = (AutoCompleteTextView) findViewById(R.id.taskSelectionBox);
		final String sTask = taskSelector.getText().toString();
		
		Task task = TaskRecorder.getTaskFromString(sTask);
		
		if (task != null)
		{
			taskRecorder.startRecording(task);
		}
		else
		{
			// if task does not exist, ask the user if it should be created
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog dialog = builder.setMessage(R.string.promptCreateNewTask)
			       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Create new task and start recording it
			        	   Task newTask = TaskRecorder.createTaskFromString(sTask);
			        	   taskRecorder.startRecording(newTask);
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
	 * Receives events from the TaskRecorder informing the Activity when recording is started/stopped.
	 */
	@Override
	public void onRecorderStateChanged(RecorderEvent e)
	{
		switch(e.getState())
		{
		case Started:
			startUI();
			break;
		case Stopped:
			stopUI();
			break;
		}
	}


	/**
	 * Event handler for click of start button.
	 * @param view
	 */
	public void Start(View view)
	{
		startRecording();
	}
	/**
	 * Event handler for click of stop button.
	 * @param view
	 */
	public void Stop(View view)
	{
		taskRecorder.stopRecording();
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

}	