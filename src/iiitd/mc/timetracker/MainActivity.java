package iiitd.mc.timetracker;

import java.util.List;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.*;
import iiitd.mc.timetracker.data.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
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
    
    private ITaskSuggestor suggester;
    private CustomArrayAdapter taskListAdapter;
    private AutoCompleteTextView autoTv;

    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_main);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_main, null));
		
		initTaskAutocomplete();
	}
	
	
	/**
	 * Initial setup of the autocomplete dropdown task selection text box.
	 */
	public void initTaskAutocomplete()
	{
		autoTv = (AutoCompleteTextView) findViewById(R.id.taskSelectionBox);
		
		addTasksToAutoView();
		
		autoTv.setThreshold(0);
		autoTv.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					((AutoCompleteTextView)view).showDropDown();
				}
		});
		

	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		autoTv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// Hide keyboard when an item is selected
			    InputMethodManager inm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			    View currentFocus = getCurrentFocus();
		        if (currentFocus != null) {
		        	inm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
		        }
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
		
		Intent running_activity = new Intent(this, RunningActivity.class);
		startActivity(running_activity);
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
					   addTasksToAutoView();
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

	/*
	 * Adds tasks to the Auto Complete View for suggestions
	 */
	private void addTasksToAutoView()
	{
		suggester = new MainTaskSuggestor();
		List<String> suggestedTasks = suggester.getTaskStrings();
		taskListAdapter = new CustomArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestedTasks);
		taskListAdapter.notifyDataSetChanged();
		autoTv.setAdapter(taskListAdapter);	
	}
	
}	