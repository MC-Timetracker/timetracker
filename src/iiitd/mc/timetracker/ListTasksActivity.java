package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.ExpandableListAdapter;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.IDatabaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

@SuppressLint("InflateParams")
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
        this.frame.addView(inflater.inflate(R.layout.activity_list_tasks, null));
		
		// Get the ListView
		expListView = (ExpandableListView) findViewById(R.id.Explv);		
		
		loadTasksList();
		
		listAdapter = new ExpandableListAdapter(this, listHeader, listItems);
		expListView.setAdapter(listAdapter);
		
		expListView.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView explview, View view,
					int grouppos, int childpos, long id)
			{
				long taskId = listItems.get(listHeader.get(grouppos)).get(childpos).getId();
				Intent intent = new Intent(ListTasksActivity.this,EditTaskActivity.class);
				intent.putExtra("taskid", taskId);
				startActivity(intent);
				return false;
			}
			
		});
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
		
		// We have to change the following implementation.
		// Probably make some changes in the database to efficiently fetch all the tasks.
		for(Task t: tasks)
		{
			if(t.getParent() == null)
			{
				listHeader.add(t);
			}
		}
		
		tasks.removeAll(listHeader);
		
		for(Task t: listHeader)
		{
			List<Task> templist = new ArrayList<>();
			for(Task t2: tasks)
			{	
				Task temp  = t2.getParent();
				
				while(temp.getParent() != null){
					temp = temp.getParent();
				}
				
				if(temp.getId() == t.getId())
				{
					templist.add(t2);
				}
			}
			listItems.put(t,templist);
			tasks.removeAll(templist);
		}
		
	}
}
