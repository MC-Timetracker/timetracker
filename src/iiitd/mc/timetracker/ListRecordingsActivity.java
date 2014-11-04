package iiitd.mc.timetracker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import iiitd.mc.timetracker.adapter.ExpandableRecAdapter;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

public class ListRecordingsActivity extends BaseActivity {
	
	private ExpandableListView expRecView;
	private List<String> recHeader;
	private HashMap<String, List<Recording>> recItems;
	private ExpandableRecAdapter recAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_list_recordings);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_list_recordings, null)); //TODO: cleanup the other one
		
        expRecView = (ExpandableListView) findViewById(R.id.Explv);
		
		loadRecordingsList();
		
		recAdapter=new ExpandableRecAdapter(this, recHeader, recItems);
		expRecView.setAdapter(recAdapter);

		registerForContextMenu(expRecView);
	}
	
	/**
	 * Populate the list in the UI with the Recordings from the database.
	 */
	public void loadRecordingsList()
	{
		recHeader = new ArrayList<String>();
		recItems = new HashMap<String, List<Recording>>();
		DateFormat formater = android.text.format.DateFormat.getDateFormat(this);
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		List<Recording> recs = db.getRecordings();
		db.close();
		
		for(Recording r:recs)
		{
			String temp = formater.format(r.getStart());
			if(!recHeader.contains(temp))
			{
				recHeader.add(temp);
				recItems.put(temp, new ArrayList<Recording>());
			}
			
			recItems.get(temp).add(r);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Select The Action");
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(1, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getTitle() == "Edit")
		{
			Intent edit_recording = new Intent(this,
					EditRecordingActivity.class);
			startActivity(edit_recording);
		} else if (item.getTitle() == "Delete")
		{
		} else
			return false;
		return true;
	}
}
