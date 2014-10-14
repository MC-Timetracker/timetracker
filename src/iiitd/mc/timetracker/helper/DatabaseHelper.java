/**
 * 
 */
package iiitd.mc.timetracker.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Helper class helps to perform all CURD operations on the database.
 * @author Shubham
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
		
		//LOGCAT tag for logs
		public static final String LOG = "DatabaseHelper";
		
		//DB version
		public static final int DATABASE_VERSION = 1;
		
		//DB name
		public static final String DATABASE_NAME = "timeTracker";
		
		//Table names
		public static final String TABLE_RECORDING = "RECORDING";
		public static final String TABLE_TASK = "TASK";
		
		//Common column name for id
		public static final String KEY_ID = "id";
		
		//Task Table - Column names
		public static final String TASK_NAME = "name";
		public static final String TASK_DESCRIPTION = "description";
		public static final String TASK_PARENT = "parent";
		//private static final String TASK_LOCATION = "location";	//TODO: 
		
		//Recording Table - Column names
		public static final String RECORDING_TASKID = "taskid";
		public static final String RECORDING_STARTTIME = "starttime";
		public static final String RECORDING_STOPTIME = "stoptime";
		
		//Table Create Statements
		//Task Table Create Statement
		public static final String CREATE_TABLE_TASK = "create table " + TABLE_TASK + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TASK_NAME + " TEXT NOT NULL, " + TASK_DESCRIPTION + " TEXT, " + TASK_PARENT + " INTEGER, FOREIGN KEY("
				+ TASK_PARENT + ") REFERENCES " + TABLE_TASK + "(" + KEY_ID + ") ON DELETE CASCADE;";
		
		//Recording Table Create Statement
		public static final String CREATE_TABLE_RECORDING = "create table " + TABLE_RECORDING + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ RECORDING_TASKID + " INTEGER, "+ RECORDING_STARTTIME + " DATETIME, " + RECORDING_STOPTIME + " DATETIME, FOREIGN KEY("
				+ RECORDING_TASKID + ") REFERENCES " + TABLE_TASK + "(" + KEY_ID + ") ON DELETE CASCADE;";
		
		public DatabaseHelper(Context context){
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Create the required tables 
			db.execSQL(CREATE_TABLE_TASK);
			db.execSQL(CREATE_TABLE_RECORDING);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO On Upgrade drop the required tables
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDING);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
			
			// TODO  Now create new tables
			onCreate(db);
		}
}
