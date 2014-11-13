package iiitd.mc.timetracker;

import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.IDatabaseController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This activity shows the parent, task name, description and probably the recordings w.r.t
 * to the task to the user.
 * @author gullal
 *
 */
@SuppressLint("InflateParams")
public class EditTaskActivity extends BaseActivity
{
	private Button update,cancel;
	private EditText parent, taskname, description;
	private Task task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_edit_task, null));
        
        update = (Button) findViewById(R.id.btnUpdate);
        cancel = (Button) findViewById(R.id.btnCancel);
        parent = (EditText)findViewById(R.id.parentEdtitext);
        taskname = (EditText) findViewById(R.id.tasknameedittext);
        description = (EditText) findViewById(R.id.descEdittext);
        
        Intent intent = getIntent();
        IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		task = db.getTask(intent.getLongExtra("taskid",0));
		db.close();
		
		if(task.getParent() != null)
			parent.setText(task.getParent().getNameFull());
		taskname.setText(task.getName());
		description.setText(task.getDescription());
		
		update.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v)
			{
				updateTask();
			}
			
		});
		
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v)
			{
				finish();		
			}
			
		});
	}
	
	public void updateTask()
	{

		task.setName(taskname.getText().toString());
		task.setDescription(description.getText().toString());
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		db.updateTask(task);
		db.close();
		Toast.makeText(this,"Task updated successfully", Toast.LENGTH_LONG).show();
		cancel.setText("Back");
		
		finish();
	}
}
