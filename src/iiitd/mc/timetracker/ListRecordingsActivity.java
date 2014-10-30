package iiitd.mc.timetracker;

import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.adapter.CustomAdapter;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;

public class ListRecordingsActivity extends BaseActivity {
	
	ListView lvRecordings;
	CustomAdapter adapter;
	public List<Recording> customlvrec = new ArrayList<Recording>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_list_recordings);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_list_recordings, null));
		
		lvRecordings = (ListView) findViewById(R.id.lvRecordings);
		
		loadRecordingsList();
		
		adapter = new CustomAdapter(this, customlvrec);
		lvRecordings.setAdapter(adapter);
	}
	
	/**
	 * Populate the list in the UI with the Recordings from the database.
	 */
	public void loadRecordingsList()
	{
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		customlvrec = db.getRecordings();
		db.close();
	}
}
