package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.MainActivity;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.data.Task;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Constantly checks for Tasks that are likely to be started right now
 * and potentially act automatically on this.
 */
public class AutoRecorder extends  BroadcastReceiver
{
	public final int NOTIFICATION_ID_TASK_SUGGESTION = 2;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		//TODO: implement automatic actions for likely Task
		//if(Task t is very likely in current context)
		//	create notification and vibrate
		//	or start recording that automatically?
			//TODO replace this stub with real selection of likely Task
			Task suggestedTask = new Task("SuggestedTask");
			// set Notification
			setNotification(context, suggestedTask);
	}
	
	void setNotification(Context context, Task suggestedTask)
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
		long pattern[]={0,200,500,100,100,100}; //The first value indicates the number of milliseconds to wait before turning the vibrator ON. The next value indicates the number of milliseconds for which to keep the vibrator on before turning it off. Subsequent values, alternates between ON and OFF.
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, -1);
	}
}
