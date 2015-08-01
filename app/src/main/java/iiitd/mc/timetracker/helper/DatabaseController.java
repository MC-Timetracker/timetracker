package iiitd.mc.timetracker.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;

/**
 * Database Helper class to perform all Database CURD operations
 *
 * @author Shubham
 */
public class DatabaseController implements IDatabaseController {
    private DatabaseHelper dbHelper;
    private Context appContext;
    private SQLiteDatabase database;


    public DatabaseController(Context c) {
        appContext = c;
    }

    public DatabaseController open() throws SQLException {
        dbHelper = new DatabaseHelper(appContext);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    @Override
    public void insertTask(Task newTask) {
        ContentValues contentValue_task = new ContentValues();
        contentValue_task.put(DatabaseHelper.TASK_NAME, newTask.getName());
        contentValue_task.put(DatabaseHelper.TASK_DESCRIPTION, newTask.getDescription());

        // put parentId - if no parent is set, put -1
        //long parentId = -1;
        if (newTask.getParent() != null)
            contentValue_task.put(DatabaseHelper.TASK_PARENT, newTask.getParent().getId());
        else
            contentValue_task.putNull(DatabaseHelper.TASK_PARENT);

        long id = database.insert(DatabaseHelper.TABLE_TASK, null, contentValue_task);
        // update Task instance with the assigned auto-increment id
        newTask.setId(id);
    }

    @Override
    public Task getTask(long id) {
        String selectTaskQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + DatabaseHelper.KEY_ID + " = " + id;
        database = dbHelper.getReadableDatabase();
        Cursor c = database.rawQuery(selectTaskQuery, null);
        if (c == null || !c.moveToFirst())
            return null;
        Task task = new Task();
        task.setId(c.getInt(c.getColumnIndex(DatabaseHelper.KEY_ID)));
        task.setName(c.getString(c.getColumnIndex(DatabaseHelper.TASK_NAME)));
        task.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.TASK_DESCRIPTION)));

        // load parent task
        int parentId = c.getInt(c.getColumnIndex(DatabaseHelper.TASK_PARENT));
        if (parentId > -1) {
            Task parent = getTask(parentId);
            task.setParent(parent);
        }

        c.close();
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return getTasksWhere("1");
    }

    @Override
    public List<Task> getTasks(String name) {
        return getTasksWhere("UPPER(" + DatabaseHelper.TASK_NAME + ")='" + name.toUpperCase() + "'");
    }

    @Override
    public List<Task> getSubTasks(long id) {
        return getTasksWhere(DatabaseHelper.TASK_PARENT + "='" + id + "'");
    }

    public List<Task> getTasksWhere(String filter) {
        List<Task> tasks = new ArrayList<>();
        String selectTasksQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASK + " WHERE " + filter;
        database = dbHelper.getReadableDatabase();
        Cursor c = database.rawQuery(selectTasksQuery, null);
        if (c.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(c.getInt(c.getColumnIndex(DatabaseHelper.KEY_ID)));
                task.setName(c.getString(c.getColumnIndex(DatabaseHelper.TASK_NAME)));
                task.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.TASK_DESCRIPTION)));

                // load parent task
                int parentId = c.getInt(c.getColumnIndex(DatabaseHelper.TASK_PARENT));
                if (parentId > -1) {
                    Task parent = getTask(parentId);
                    task.setParent(parent);
                }

                tasks.add(task);
            } while (c.moveToNext());
        }

        c.close();
        return tasks;
    }

    @Override
    public void updateTask(Task updatedTask) {
        ContentValues contentValue_task = new ContentValues();
        contentValue_task.put(DatabaseHelper.TASK_NAME, updatedTask.getName());
        contentValue_task.put(DatabaseHelper.TASK_DESCRIPTION, updatedTask.getDescription());
        database.update(DatabaseHelper.TABLE_TASK, contentValue_task, DatabaseHelper.KEY_ID + " = " + updatedTask.getId(), null);
    }

    @Override
    public void deleteTask(long id) {
        database.delete(DatabaseHelper.TABLE_TASK, DatabaseHelper.KEY_ID + " = " + id, null);
    }

    @Override
    public void deleteTask(Task removedTask) {
        database.delete(DatabaseHelper.TABLE_TASK, DatabaseHelper.KEY_ID + " = " + removedTask.getId(), null);
    }

    @Override
    public void insertRecording(Recording newRecording) {
        ContentValues contentValue_recording = new ContentValues();
        contentValue_recording.put(DatabaseHelper.RECORDING_TASKID, newRecording.getTask().getId());
        contentValue_recording.put(DatabaseHelper.RECORDING_STARTTIME, newRecording.getStart().getTime());
        contentValue_recording.put(DatabaseHelper.RECORDING_STOPTIME, newRecording.getEnd().getTime());
        contentValue_recording.put(DatabaseHelper.RECORDING_BSSID, newRecording.getMacAddress());

        long id = database.insert(DatabaseHelper.TABLE_RECORDING, null, contentValue_recording);

        // update Recording instance with the assigned auto-increment id
        newRecording.setRecordingId(id);
    }

    @Override
    public Recording getRecording(long recordingId) {
        String selectRecordingQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECORDING + " WHERE " + DatabaseHelper.KEY_ID + " = " + recordingId;
        database = dbHelper.getReadableDatabase();
        Cursor c = database.rawQuery(selectRecordingQuery, null);
        if (c == null || !c.moveToFirst())
            return null;

        Recording r = createRecordingInstance(c);

        c.close();
        return r;
    }


    public List<Recording> getRecordings(long start, long end) {
        return getRecordingsWhere(DatabaseHelper.RECORDING_STARTTIME + " BETWEEN " + start + " AND " + end, -1);
    }

    @Override
    public List<Recording> getRecordings(long date) {
        return getRecordingsWhere(DatabaseHelper.RECORDING_STARTTIME + " >= " + (date - 86400000), -1);
    }

    @Override
    public List<Recording> getRecordings(long taskid, long start, long end) {
        return getRecordingsWhere(DatabaseHelper.RECORDING_TASKID + " = " + taskid + " AND " +
                DatabaseHelper.RECORDING_STARTTIME + " BETWEEN " + start + " AND " + end, -1);
    }

    @Override
    public List<Recording> getRecordings() {
        return getRecordingsWhere("1", -1);
    }

    @Override
    public List<Recording> getRecordings(int limit) {
        return getRecordingsWhere("1", limit);
    }

    public List<Recording> getRecordingsWhere(String filter, int limit) {
        List<Recording> recordings = new ArrayList<>();
        String selectRecordingQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECORDING + " WHERE " + filter
                + " ORDER BY " + DatabaseHelper.RECORDING_STARTTIME + " DESC";
        if (limit > 0)
            selectRecordingQuery += " LIMIT " + limit;

        database = dbHelper.getReadableDatabase();
        Cursor c = database.rawQuery(selectRecordingQuery, null);
        if (c.moveToFirst()) {
            do {
                Recording r = createRecordingInstance(c);
                recordings.add(r);
            } while (c.moveToNext());
        }

        c.close();
        return recordings;
    }

    /**
     * Helper function to create an instance of Recording from the currently selected query result.
     *
     * @param c The database query result cursor.
     * @return An instance of Recording containing the data from the current row of c.
     */
    private Recording createRecordingInstance(Cursor c) {
        Recording record = new Recording();
        record.setRecordingId(c.getLong(c.getColumnIndex(DatabaseHelper.KEY_ID)));
        record.setTask(getTask(c.getLong(c.getColumnIndex(DatabaseHelper.RECORDING_TASKID))));
        try {
            Date ds = new Date(c.getLong(c.getColumnIndex(DatabaseHelper.RECORDING_STARTTIME)));
            record.setStart(ds);
        } catch (Exception e) {
            // ignore date problems
        }
        try {
            Date de = new Date(c.getLong(c.getColumnIndex(DatabaseHelper.RECORDING_STOPTIME)));
            record.setEnd(de);
        } catch (Exception e) {
            // ignore date problems
        }
        record.setMacAddress(c.getString(c.getColumnIndex(DatabaseHelper.RECORDING_BSSID)));
        return record;
    }

    @Override
    public void updateRecording(Recording updatedRecording) {
        ContentValues contentValue_recording = new ContentValues();
        contentValue_recording.put(DatabaseHelper.RECORDING_TASKID, updatedRecording.getTask().getId());
        contentValue_recording.put(DatabaseHelper.RECORDING_STARTTIME, updatedRecording.getStart().getTime());
        contentValue_recording.put(DatabaseHelper.RECORDING_STOPTIME, updatedRecording.getEnd().getTime());
        contentValue_recording.put(DatabaseHelper.RECORDING_BSSID, updatedRecording.getMacAddress());
        database.update(DatabaseHelper.TABLE_RECORDING, contentValue_recording, DatabaseHelper.KEY_ID + " = " + updatedRecording.getRecordingId(), null);
    }

    @Override
    public void deleteRecording(long id) {
        database.delete(DatabaseHelper.TABLE_RECORDING, DatabaseHelper.KEY_ID + " = " + id, null);
    }

    @Override
    public void deleteRecording(Recording removedRecording) {
        database.delete(DatabaseHelper.TABLE_RECORDING, DatabaseHelper.KEY_ID + " = " + removedRecording.getRecordingId(), null);
    }
}
