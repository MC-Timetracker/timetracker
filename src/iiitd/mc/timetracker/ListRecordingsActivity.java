package iiitd.mc.timetracker;

import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.adapter.CustomAdapter;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ListRecordingsActivity extends BaseActivity {
	
	public RelativeLayout relativelayoutlist_recordings;
	ListView lvRecordings;
	CustomAdapter adapter;
	public List<Recording> customlvrec = new ArrayList<Recording>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_recordings);
		navigationDisplay();
		
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
	public void closedrawer(){
        
		//brings relative layout of list recordings to the front on closing the drawer
		relativelayoutlist_recordings = (RelativeLayout) findViewById(R.id.relativelayoutlist_recordings); 
    	relativelayoutlist_recordings.bringToFront();
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
