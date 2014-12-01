package iiitd.mc.timetracker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import iiitd.mc.timetracker.adapter.ExpandableRecAdapter;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class ListRecordingsActivity extends BaseActivity {
	
	private ExpandableListView expRecView;
	private List<String> recHeader = new ArrayList<String>();
	private HashMap<String, List<Recording>> recItems = new HashMap<String, List<Recording>>();
	private ExpandableRecAdapter recAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_list_recordings);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_list_recordings, null)); //TODO: cleanup the other one
		
        expRecView = (ExpandableListView) findViewById(R.id.Explv);
        
        recAdapter = new ExpandableRecAdapter(this, recHeader, recItems);
		expRecView.setAdapter(recAdapter);
		loadRecordingsList();

		registerForContextMenu(expRecView);
	}
	
	/**
	 * Populate the list in the UI with the Recordings from the database.
	 */
	public void loadRecordingsList()
	{
		recHeader.clear();
		recItems.clear();
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
		
		recAdapter.notifyDataSetChanged();
		
		if(recAdapter.getGroupCount() > 0)
			expRecView.expandGroup(0);
	}
	
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		
		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
		
		//only show context menu for child items (recordings not dates)
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)		
		{
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.context_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
    	int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
    	int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
    	Recording r =(Recording) recAdapter.getChild(groupPos, childPos);
    	
		switch (item.getItemId()) {
		    case R.id.action_edit:
		        editRecording(r);
		        return true;
		    case R.id.action_delete:
		        deleteRecording(r);
		        return true;
		    default:
		        return super.onContextItemSelected(item);
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.list_recordings_actions, menu);
	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_new_recording:
	        	editRecording(null);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
	
	private void editRecording(Recording r)
	{
		Intent edit_recording = new Intent(this, EditRecordingActivity.class);
		if(r != null)
			edit_recording.putExtra(EditRecordingActivity.EXTRA_RECORDING_ID, r.getRecordingId());
		startActivity(edit_recording);
	}
	
	private void deleteRecording(Recording r)
	{
		final long rId = r.getRecordingId();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog dialog = builder.setMessage(R.string.prompt_delete_recording)
	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // delete Recording
	        	   IDatabaseController db = ApplicationHelper.createDatabaseController();
	        	   db.open();
	        	   db.deleteRecording(rId);
	        	   db.close();
	        	   loadRecordingsList();
	           }
	       })
	       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // Do nothing
	           }
	       })
	       .create();
		dialog.show();
	}
}
