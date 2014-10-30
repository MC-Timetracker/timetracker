package iiitd.mc.timetracker;

import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.adapter.CustomAdapter;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_recordings, menu);
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
	
	/**
	 * Populate the list in the UI with the Recordings from the database.
	 */
	public void loadRecordingsList()
	{
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		List<Recording> recordings = db.getRecordings();
		db.close();
		
		for(Recording r : recordings)
		{
			customlvrec.add(r);
		}
	}
}
