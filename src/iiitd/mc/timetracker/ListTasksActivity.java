package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.ExpandableListAdapter;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.IDatabaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ListTasksActivity extends BaseActivity{
	
	public RelativeLayout RelativeLayoutlist_task; 
	private ExpandableListView expListView;
	private List<Task> listHeader;
	private HashMap<Task, List<Task>> listItems;
	private ExpandableListAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_main);
		navigationDisplay();
		
		// Get the ListView
		expListView = (ExpandableListView) findViewById(R.id.Explv);		
		
		loadTasksList();
		
		listAdapter = new ExpandableListAdapter(this, listHeader, listItems);
		expListView.setAdapter(listAdapter);
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
		RelativeLayoutlist_task = (RelativeLayout) findViewById(R.id.relativelayoutlist_task); 
    	RelativeLayoutlist_task.bringToFront();
    }
	
	
	/**
	 * Populate the list in the UI with the Tasks from the database.
	 */
	/**
	 * Populate the list in the UI with the Tasks from the database.
	 */
	public void loadTasksList()
	{
		listHeader = new ArrayList<Task>();
		listItems = new HashMap<Task,List<Task>>();
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		List<Task> tasks = db.getTasks();
		db.close();
		
		for(Task t: tasks)
		{
			if(t.getParent() == null)
			{
				listHeader.add(t);
			}
		}
		
		for(Task t: listHeader)
		{
			listItems.put(t,t.getSubtasks());
		}
		
	}
}
