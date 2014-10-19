package iiitd.mc.timetracker;

import java.util.Date;
import java.util.List;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.*;
import iiitd.mc.timetracker.data.*;
import iiitd.mc.timetracker.helper.IDatabaseController;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

/**
 * The Main Activity of the timetracker application
 * @author gullal
 *
 */
public class MainActivity extends ActionBarActivity implements RecorderListener {
	
	ITaskSuggestor suggester;
	TaskRecorder taskRecorder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		taskRecorder = new TaskRecorder();
		taskRecorder.addListener(this);
		
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void onStartRecording(View v)
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
			AlertDialog dialog = builder.setMessage(R.string.createNewTask)
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
	
	public void onStopRecording(View v)
	{
		taskRecorder.stopRecording();
	}
	

	/**
	 * Change visibility of the Start/Stop buttons.
	 * @param recording Whether a recording is in progress, resulting in visibility of the "Stop" button.
	 */
	public void toggleStartStopButton(boolean recording)
	{
		int startVisible = View.VISIBLE;
		int stopVisible = View.GONE;
		if(recording)
		{
			startVisible = View.GONE;
			stopVisible = View.VISIBLE;
		}
		
		Button bttStart = (Button) findViewById(R.id.bttStart);
		Button bttStop = (Button) findViewById(R.id.bttStop);
		bttStart.setVisibility(startVisible);
		bttStop.setVisibility(stopVisible);
	}

	@Override
	public void onRecorderStateChanged(RecorderEvent e)
	{
		switch(e.getState())
		{
		case Started:
			toggleStartStopButton(true);
			break;
		case Stopped:
			toggleStartStopButton(false);
			break;
		}
	}
}
