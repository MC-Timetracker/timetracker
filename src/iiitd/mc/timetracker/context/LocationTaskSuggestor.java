package iiitd.mc.timetracker.context;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.DatabaseController;
import iiitd.mc.timetracker.helper.DatabaseHelper;
import iiitd.mc.timetracker.helper.IDatabaseController;

public class LocationTaskSuggestor {

	
	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub

	}*/
	
	private List<Task> tasks;
	IDatabaseController db;
	private DatabaseHelper dbHelper;
	private Context appContext;
	private SQLiteDatabase database;
	
	public LocationTaskSuggestor(Context context)
	{
		appContext = context;
		db = ApplicationHelper.createDatabaseController();
	}
	
	/*public List<Task> getRecentTasks()
	{
		db.open();
		//recordings = db.p; //TODO: Why save them in the RecentTaskSuggester instance if not reused?
		db.close();
		
		tasks = new ArrayList<Task>();

	}*/
	
	public void printTasks(String bssid){
		//dbHelper = new DatabaseHelper(appContext);
		//Toast.makeText(appContext, "hello", Toast.LENGTH_SHORT).show();
		db.open();
		db.close();
	}
	
}
	
