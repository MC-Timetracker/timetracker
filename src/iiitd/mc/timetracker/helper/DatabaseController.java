package iiitd.mc.timetracker.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database Helper class to perform all Database CURD operations
 * @author Shubham
 *
 */
public class DatabaseController {
	private DatabaseHelper dbHelper;
	private Context appContext;
	private SQLiteDatabase database;
	
	public DatabaseController(Context c){
		appContext = c;
	}
	
	public DatabaseController open() throws SQLException{
		dbHelper = new DatabaseHelper(appContext);
		database=dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void insert_in_task(String name, String desc, int parentid){
		ContentValues contentValue_task = new ContentValues();
		contentValue_task.put(DatabaseHelper.TASK_NAME,name);
		contentValue_task.put(DatabaseHelper.TASK_DESCRIPTION, desc);
		contentValue_task.put(DatabaseHelper.TASK_PARENT,parentid);
		database.insert(DatabaseHelper.TABLE_TASK, null, contentValue_task);
	}
	
	public void insert_in_recording(String start,String stop){
		ContentValues contentValue_recording = new ContentValues();
		contentValue_recording.put(DatabaseHelper.RECORDING_STARTTIME, start);
		contentValue_recording.put(DatabaseHelper.RECORDING_STOPTIME,stop);
		database.insert(DatabaseHelper.TABLE_RECORDING, null, contentValue_recording);
	}
}
