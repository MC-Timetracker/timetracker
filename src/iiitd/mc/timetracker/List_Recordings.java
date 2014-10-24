package iiitd.mc.timetracker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class List_Recordings extends BaseActivity {
	
	public RelativeLayout relativelayoutlist_recordings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list__recordings);
		navigationDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list__recordings, menu);
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
}
