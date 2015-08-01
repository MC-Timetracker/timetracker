package iiitd.mc.timetracker.data;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.MainActivity;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.helper.IDatabaseController;


/**
 * Service class holding the state of currently recorded tasks. Also provides
 * helper methods to interact with Tasks.
 */
public class TaskRecorderService extends Service {
    public final static String EXTRA_TASK_ID = "TASK_ID";
    private final IBinder mBinder = new TaskRecorderBinder();
    public final int ONGOING_NOTIFICATION_ID = 1;
    private Recording currentRecording = null;
    private Recording lastRecording = null;
    IDatabaseController db = ApplicationHelper.createDatabaseController();
    Task breakTask;
    private static final String SETTINGS_BREAK_TASK_ID = "breakTaskId";
    private static final String BREAK_TASK_NAME = "Break";
    WifiManager mainWifiObj;

    @Override
    public void onCreate() {
        super.onCreate();
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TaskRecorderBinder extends Binder {
        public TaskRecorderService getService() {
            // Return this instance of TaskRecorderService so clients can call
            // public methods
            return TaskRecorderService.this;
        }
    }


    private List<RecorderListener> listeners = new ArrayList<RecorderListener>();

    /**
     * Add an object to get notified about changes of the Recorder state like start of a new recording.
     *
     * @param listener The object to be notified.
     */
    public void addRecorderListener(RecorderListener listener) {
        listeners.add(listener);
    }

    /**
     * Add an object to get notified about changes of the Recorder state like start of a new recording.
     *
     * @param listener The object to be notified.
     */
    public void removeRecorderListener(RecorderListener listener) {
        listeners.remove(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get Task to be recorded
        long taskId = intent.getLongExtra(EXTRA_TASK_ID, -1);
        if (taskId != -1) {
            db.open();
            Task task = db.getTask(taskId);
            db.close();
            startRecording(task);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopRecording();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Start recording the given Task. If another task is currently recorded
     * that Recording is stopped. The state of current recordings is held in
     * this instance of the class.
     *
     * @param task
     *            The task for which recording will be started.
     */
    public void startRecording(Task task) {
        if (task == null)
            return;
        // if some Task is currently being recorded, stop that first as do not
        // record parallel tasks
        if (this.isRecording()) {
            stopRecording();
        }
        WifiInfo wifiInfo = mainWifiObj.getConnectionInfo();
        String bssid = wifiInfo.getBSSID();
        if (bssid == null)
            bssid = "00:00:00:00:00";
        currentRecording = new Recording();
        currentRecording.setTask(task);
        currentRecording.setStart(new Date());
        currentRecording.setMacAddress(bssid);
        String notificationTitle = getText(R.string.notification_recording)
                + " " + task.getName();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_stat_recording)
                .setContentTitle(notificationTitle)
                .setContentText(task.toString())
                .setWhen(System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        mBuilder.setContentIntent(pendingIntent);
        startForeground(ONGOING_NOTIFICATION_ID, mBuilder.build());

        //fire event at listeners
        for (RecorderListener l : listeners) {
            l.onRecordingStarted(currentRecording);
        }
    }

    /**
     * Stop the currently recorded Task and save the finished Recording to the
     * database.
     */
    public void stopRecording() {
        if (currentRecording == null)
            return;
        currentRecording.setEnd(new Date());
        // save Recording to database
        db.open();
        db.insertRecording(currentRecording);
        db.close();
        stopForeground(true);
        if (!currentRecording.getTask().equals(getBreakTask()))
            lastRecording = currentRecording;

        //fire event at listeners
        for (RecorderListener l : listeners) {
            l.onRecordingStopped(currentRecording);
        }

        currentRecording = null;
    }

    /**
     *
     * @return Returns true if a Task is currently being recorded.
     */
    public boolean isRecording() {
        return (this.currentRecording != null);
    }

    /**
     * Get the Recording that is currently recorded.
     *
     * @return The current Recording or null if not recording.
     */
    public Recording getCurrentRecording() {
        return this.currentRecording;
    }

    /**
     * Get the last completed Recording.
     *
     * @return The Recording that was last completed or null if this
     *         TaskRecorderService instance did not finish recording anything
     *         yet.
     */
    public Recording getLastRecording() {
        return this.lastRecording;
    }

    /**
     * Get the Task used to represent a break taken during other tasks.
     *
     * @return The Task instance representing a break.
     */
    public Task getBreakTask() {
        if (breakTask == null) {
            SharedPreferences settings = getSharedPreferences(
                    getString(R.string.app_name), 0);
            long breakTaskId = settings.getLong(SETTINGS_BREAK_TASK_ID, -1);
            db.open();
            breakTask = db.getTask(breakTaskId);
            if (breakTask == null) {
                breakTask = new Task(BREAK_TASK_NAME, null);
                db.insertTask(breakTask);
                breakTaskId = breakTask.getId();
                // save ID to settings
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong(SETTINGS_BREAK_TASK_ID, breakTaskId);
                editor.commit();
            }
            db.close();
        }
        return breakTask;
    }

    /**
     * Return the Task instance that corresponds to the given string describing
     * a task path.
     *
     * @param taskString
     *            A string describing the full path to the required Task (e.g.
     *            "Studies.Maths.Assignment")
     * @return The Task instance for the given path or null if no such Task
     *         exists.
     */
    public static Task getTaskFromString(String taskString) {
        Task task = null;
        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();
        String[] taskStringParts = taskString.split(Pattern.quote(Task.THS));
        // get all tasks with name like the lowest hierarchy part of the string
        List<Task> taskCandidates = db
                .getTasks(taskStringParts[taskStringParts.length - 1]);
        for (Iterator<Task> i = taskCandidates.iterator(); i.hasNext(); ) {
            Task t = i.next();
            if (taskString.equalsIgnoreCase(t.toString())) {
                task = t;
                break;
            }
        }
        db.close();
        return task;
    }

    /**
     * Adds the new task to the database. Cascadingly creates parent tasks if
     * they do not exist yet. If a task with the exact given taskString already
     * exists the existing task is returned and no new task is created.
     *
     * @param taskString
     *            The full task path describing the task to be created with its
     *            parents.
     * @return The newly created or already existing task for the given
     *         taskString.
     */
    public static Task createTaskFromString(String taskString) {
        // check for existing task
        Task task = getTaskFromString(taskString);
        if (task != null)
            return task;
        if (!isValidTaskName(taskString))
            return null;
        // create new task for lowest part in hierarchy
        int separator = taskString.lastIndexOf(Task.THS);
        String taskName = taskString.substring(separator + 1);
        Task taskParent = null;
        if (separator != -1) {
            String parentName = taskString.substring(0, separator);
            taskParent = createTaskFromString(parentName);
        }
        Task newTask = new Task(taskName, taskParent);
        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();
        db.insertTask(newTask);
        db.close();
        return newTask;
    }

    /**
     * Pause Recording the current task. This will stop recording and start
     * recording a "Break" Task. resumeRecording() allows to restart recording
     * the paused Task later.
     */
    public void pauseRecording() {
        stopRecording();
        startRecording(getBreakTask());
    }

    /**
     * Restart recording the Task that was most recently recorded. If no
     * LastRecording is available in this TaskRecorderService, this call is
     * ignored.
     */
    public void resumeRecording() {
        if (lastRecording != null) {
            Task lastTask = lastRecording.getTask();
            startRecording(lastTask);
        }
    }

    /**
     * Checks the given String against all constraints for new task names.
     *
     * @param sTask
     *            The string to be checked if it is a valid new task name.
     * @return True if the string is valid as a task name.
     */
    public static boolean isValidTaskName(String sTask) {
        if (sTask == null || sTask.trim().equals(""))
            return false;
        return true;
    }
}