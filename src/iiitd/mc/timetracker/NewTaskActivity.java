package iiitd.mc.timetracker;

import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.data.TaskRecorderService;
import iiitd.mc.timetracker.helper.IDatabaseController;
import iiitd.mc.timetracker.view.TaskAutoCompleteTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	private TaskAutoCompleteTextView autoTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_new_task);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_new_task, null));
		
		autoTv = (TaskAutoCompleteTextView) findViewById(R.id.taskSelectionBox);
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
		
		Task parent = null;
		if(!autoTv.getText().toString().isEmpty())
		{
			// parent name not empty - get/create parent task
			parent = autoTv.getTask();
			if(parent == null)
			{
				autoTv.createTask(new TaskAutoCompleteTextView.OnTaskCreatedListener() 
				{
					@Override
					public void onTaskCreated(Task newTask)
					{
						// restart insertTask, parent will exist now
						insertTask();
					}
				}, 
				null);
				
				// abort for now, will be continued when callback returns
				return;
			}
		}
		
		Task task = new Task(taskname.getText().toString(), parent);
		task.setDescription(this.description.getText().toString());
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		db.insertTask(task);
		db.close();
		
		Toast.makeText(this,"Task added successfully", Toast.LENGTH_LONG).show();
		
		finish();
	}
}
