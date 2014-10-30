package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.ExpandableListAdapter;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.IDatabaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;

public class ListTasksActivity extends BaseActivity{
	
	private ExpandableListView expListView;
	private List<Task> listHeader;
	private HashMap<Task, List<Task>> listItems;
	private ExpandableListAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_list_main);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_list_main, null));
		
		// Get the ListView
		expListView = (ExpandableListView) findViewById(R.id.Explv);		
		
		loadTasksList();
		
		listAdapter = new ExpandableListAdapter(this, listHeader, listItems);
		expListView.setAdapter(listAdapter);
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
