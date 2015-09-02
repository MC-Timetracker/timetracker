package iiitd.mc.timetracker.database;

import android.content.Context;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import iiitd.mc.timetracker.model.Recording;
import iiitd.mc.timetracker.model.Task;

/**
 * Wrapper for DatabaseController that caches the Records and Tasks list in memory for faster access.
 */
public class CachedDatabaseController implements IDatabaseController {
    private IDatabaseController db;
    /* in-memory caches of data. to invalidate clear the map. */
    private boolean cachedTasksComplete = false;
    private HashMap<Long, Task> cachedTasks = new HashMap<>();
    private boolean cachedRecordingsComplete = false;
    private HashMap<Long, Recording> cachedRecordings = new HashMap<>();

    public CachedDatabaseController(Context context) {
        db = new DatabaseController(context);
    }

    @Override
    public IDatabaseController open() throws SQLException {
        db.open();
        return this;
    }

    @Override
    public void close() {
        db.close();
    }

    @Override
    public void insertTask(Task newTask) {
        db.insertTask(newTask);

        cachedTasks.put(newTask.getId(), newTask);
    }

    @Override
    public Task getTask(long id) {
        Task task = cachedTasks.get(id);

        if (task == null && !cachedTasksComplete) {
            task = db.getTask(id);
            cachedTasks.put(id, task);
        }

        return task;
    }

    @Override
    public List<Task> getTasks() {
        if (!cachedTasksComplete) {
            List<Task> tasks = db.getTasks();
            for (Task t : tasks) {
                cachedTasks.put(t.getId(), t);
            }
            cachedTasksComplete = true;
        }

        return new ArrayList<>(cachedTasks.values());
    }

    @Override
    public List<Task> getTasks(String name) {
        //TODO: Cache name searches?
        return db.getTasks(name);
    }

    @Override
    public void updateTask(Task updatedTask) {
        db.updateTask(updatedTask);

        cachedTasks.put(updatedTask.getId(), updatedTask);
    }

    @Override
    public void deleteTask(long id) {
        db.deleteTask(id);

        cachedTasks.remove(id);
    }

    @Override
    public void deleteTask(Task removedTask) {
        deleteTask(removedTask.getId());
    }


    @Override
    public void insertRecording(Recording newRecording) {
        db.insertRecording(newRecording);

        cachedRecordings.put(newRecording.getRecordingId(), newRecording);
    }

    @Override
    public Recording getRecording(long recordingId) {
        Recording recording = cachedRecordings.get(recordingId);

        if (recording == null && !cachedRecordingsComplete) {
            recording = db.getRecording(recordingId);
            cachedRecordings.put(recordingId, recording);
        }

        return recording;
    }

    @Override
    public List<Recording> getRecordings() {
        if (cachedRecordingsComplete) {
            return convertToSortedList(cachedRecordings.values());
        } else {
            List<Recording> recordings = db.getRecordings();
            for (Recording r : recordings) {
                cachedRecordings.put(r.getRecordingId(), r);
            }
            cachedRecordingsComplete = true;
            return recordings;
        }
    }

    private List<Recording> convertToSortedList(Collection<Recording> recordingValues) {
        List<Recording> recordings = new ArrayList<>(recordingValues);
        Collections.sort(recordings);
        return recordings;
    }

    @Override
    public List<Recording> getRecordings(int limit) {
        //TODO: Cache limit recordings search?
        return db.getRecordings(limit);
    }

    @Override
    public List<Recording> getRecordings(long date) {
        //TODO: Cache date recordings search?
        return db.getRecordings(date);
    }

    @Override
    public List<Recording> getRecordings(long start, long end) {
        //TODO: Cache limit recordings search?
        return db.getRecordings(start, end);
    }

    @Override
    public List<Recording> getRecordings(long taskid, long start, long end) {
        //TODO: Cache task and time recordings search?
        return db.getRecordings(taskid, start, end);
    }

    @Override
    public void updateRecording(Recording updatedRecording) {
        db.updateRecording(updatedRecording);

        cachedRecordings.put(updatedRecording.getRecordingId(), updatedRecording);
    }

    @Override
    public void deleteRecording(long id) {
        db.deleteRecording(id);

        cachedRecordings.remove(id);
    }

    @Override
    public void deleteRecording(Recording removedRecording) {
        deleteRecording(removedRecording.getRecordingId());
    }

    @Override
    public List<Task> getSubTasks(long id) {
        //TODO: Cache subtask requests?
        return db.getSubTasks(id);
    }

    @Override
    public void resetDatabase() {
        db.resetDatabase();

        cachedTasks.clear();
        cachedTasksComplete = false;
        cachedRecordings.clear();
        cachedRecordingsComplete = false;
    }
}
