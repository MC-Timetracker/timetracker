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
public class MainActivity extends ActionBarActivity {
	
	ITaskSuggestor suggester;
	TaskService taskService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		taskService = new TaskService();
		
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
		final String taskString = taskSelector.getText().toString();
		
		taskService.startRecording(taskString);
		
		toggleStartStopButton(true);
		//TODO: implement toggleStartStop through event of TaskService instead.
		// currently the button state will be wrong if the user cancels the creation of a new task
	}
	
	public void onStopRecording(View v)
	{
		taskService.stopRecording();
		toggleStartStopButton(false);
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
}
