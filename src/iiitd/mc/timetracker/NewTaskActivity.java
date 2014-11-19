package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.ITaskSuggestor;
import iiitd.mc.timetracker.context.MainTaskSuggestor;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.data.TaskRecorderService;
import iiitd.mc.timetracker.helper.IDatabaseController;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity for creating a new task.
 * @author gullal
 *
 */
@SuppressLint("ShowToast")
public class NewTaskActivity extends BaseActivity {

	private Button insert, clear;
	private EditText taskname, description;
	private AutoCompleteTextView autoTv;
	private ITaskSuggestor suggester;
	private CustomArrayAdapter taskListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_new_task);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_new_task, null));
		
		autoTv = (AutoCompleteTextView) findViewById(R.id.taskSelectionBox);
		
		addTasksToAutoView();
		
		autoTv.setThreshold(0);
		
		autoTv.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View view, boolean arg1)
			{
				if(arg1)
				{
					((AutoCompleteTextView)view).showDropDown();
				}
				
			}
			
		});
		
		autoTv.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					((AutoCompleteTextView)view).showDropDown();
				}
		});
		
		insert = (Button)findViewById(R.id.btnDone);
		clear = (Button)findViewById(R.id.btnClear);
		taskname = (EditText)findViewById(R.id.tasknameedittext);
		description = (EditText)findViewById(R.id.descEditText);
		
		
		insert.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v)
			{
				insertTask();
			}
			
		});
		
		
		clear.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v)
			{
				autoTv.setText("");
				taskname.setText("");
				description.setText("");
			}
			
		});
	}
	
	
	void insertTask()
	{
		if(taskname == null){
			Toast.makeText(this,"Task name cannot be empty", Toast.LENGTH_LONG).show();
			return;
		}
		if(TaskRecorderService.getTaskFromString(taskname.getText().toString()) != null)
		{
			Toast.makeText(this,"Task already exists",Toast.LENGTH_LONG).show();
			return;
		}
		
		Task parent = TaskRecorderService.createTaskFromString(autoTv.getText().toString());
		Task task = new Task(taskname.getText().toString(), parent);
		task.setDescription(this.description.getText().toString());
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		db.insertTask(task);
		db.close();
		
		Toast.makeText(this,"Task added successfully", Toast.LENGTH_LONG).show();
		addTasksToAutoView();
		
		finish();
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
