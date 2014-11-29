package iiitd.mc.timetracker.context;

import java.util.List;
import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.MainActivity;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.data.TaskRecorderService;
import iiitd.mc.timetracker.data.TaskRecorderService.TaskRecorderBinder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Constantly checks for Tasks that are likely to be started right now
 * and potentially act automatically on this.
 */
public class AutoRecorder extends BroadcastReceiver
{
	public final int NOTIFICATION_ID_TASK_SUGGESTION = 2;
	private static final String TAG = "AutoRecorder";
	
	TaskRecorderService taskRecorder;
    boolean mBound = false;
	
    ITaskSuggestor taskSuggestor;
    
	
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		if(taskSuggestor == null)
			taskSuggestor = new MainTaskSuggestor();
		
		if(mBound)
		{
			action();
		}
		else
		{
			// need to connect TaskRecorderService first
			bindTaskRecorderService(context);
		}
	}
	
	
	private void action()
	{
		if(!mBound || taskRecorder == null)
		{
			Log.w(TAG, "Not bound to TaskRecorderService when attempted action().");
			return;
		}
		
		Context context = ApplicationHelper.getAppContext();
		List<SuggestedTask> suggestedTasks = taskSuggestor.getSuggestedTasks();
		if(suggestedTasks.isEmpty())
			return;
		
		Task suggestedTask = suggestedTasks.get(0).getTask();
		
		//if this suggestedTask is already currently being recorded, abort further autoRecorder actions
		Recording currentRec = taskRecorder.getCurrentRecording();
		if(currentRec != null && currentRec.getTask().equals(suggestedTask))
			return;
		
		SuggestionAction action = getSuggestionAction(suggestedTasks);
		switch(action)
		{
		case AutoRecord:
			startRecording(context, suggestedTask);
			break;
			
		case Notify:
			setNotification(context, suggestedTask);
			//TODO: pre-select that suggested task in the autocomplete dropdown
			break;
			
		case Ignore:
			break;
		}
	}
	
	
	/**
	 * Decides what action should be taken with regard to the probabilities of the suggested tasks.
	 * @param suggestedTasks List of suggested tasks
	 * @return Returns the SuggestionAction how the AutoRecorder will act upon the given task suggestions.
	 */
	private SuggestionAction getSuggestionAction(List<SuggestedTask> suggestedTasks)
	{
		//TODO: proper implementation of SuggestionAction
		// based on absolute probability of top suggested task
		// and relative distance to the next likely suggested task?
		return SuggestionAction.Notify;
	}
	
	enum SuggestionAction
	{
		AutoRecord,
		Notify,
		Ignore
	}
	
	
	/**
	 * Start automatically recording the given suggested Task.
	 * @param context The application context.
	 * @param suggestedTask The most likely Task to be recorded currently.
	 */
	private void startRecording(Context context, Task suggestedTask)
	{
		Intent recorderIntent = new Intent(context, TaskRecorderService.class);
		recorderIntent.putExtra(TaskRecorderService.EXTRA_TASK_ID, suggestedTask.getId());
		context.startService(recorderIntent);
	}
	
	
	/**
	 * Remind the user to check the time tracker and maybe update the recording status.
	 * @param context The application context.
	 * @param suggestedTask Most likely Task to be recorded currently.
	 */
	private void setNotification(Context context, Task suggestedTask)
	{
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(context)
			    .setSmallIcon(R.drawable.ic_stat_recording)
			    .setContentTitle(context.getText(R.string.notification_suggestion_title))
			    .setContentText(context.getText(R.string.notification_suggestion_text) + " " + suggestedTask.getNameFull());
		
		Intent resultIntent = new Intent(context, MainActivity.class);
		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    context,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
		mBuilder.setContentIntent(resultPendingIntent);
		
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
		        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(NOTIFICATION_ID_TASK_SUGGESTION, mBuilder.build());
		
		
		// vibrate
		long pattern[]={0,150,600,150,600,150}; //The first value indicates the number of milliseconds to wait before turning the vibrator ON. The next value indicates the number of milliseconds for which to keep the vibrator on before turning it off. Subsequent values, alternates between ON and OFF.
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, -1);
	}
	
	
	
	
	
	private void bindTaskRecorderService(Context context)
	{
        Intent intent = new Intent(context, TaskRecorderService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	TaskRecorderBinder binder = (TaskRecorderBinder) service;
            taskRecorder = binder.getService();
            mBound = true;
            
            //use the TaskRecorderService
            action();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
