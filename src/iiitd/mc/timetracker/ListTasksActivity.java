package iiitd.mc.timetracker;

import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.IDatabaseController;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ListTasksActivity extends BaseActivity {
	
	public RelativeLayout relativelayoutlist_task; 
	ListView lvTasks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_tasks);
		navigationDisplay();
		
		lvTasks = (ListView) findViewById(R.id.lvTasks);
		
		loadTasksList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_tasks, menu);
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
	public void closedrawer(){
     
		//brings relative layout of list task to the front on closing the drawer
		relativelayoutlist_task = (RelativeLayout) findViewById(R.id.relativelayoutlist_task); 
    	relativelayoutlist_task.bringToFront();
    }
	
	
	/**
	 * Populate the list in the UI with the Tasks from the database.
	 */
	public void loadTasksList()
	{
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		List<Task> tasks = db.getTasks();
		db.close();
		
		ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(this, 
				android.R.layout.simple_list_item_1, android.R.id.text1, tasks);
		lvTasks.setAdapter(adapter);
	}
}
