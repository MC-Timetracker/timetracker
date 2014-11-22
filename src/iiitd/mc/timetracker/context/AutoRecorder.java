package iiitd.mc.timetracker.context;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Constantly checks for Tasks that are likely to be started right now
 * and potentially act automatically on this.
 */
public class AutoRecorder extends  BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//TODO: implement automatic actions for likely Task
		//if(Task t is very likely in current context)
		//	create notification and vibrate
		//	or start recording that automatically
		
		Toast.makeText(context, "Check your timetracker to update current recording.", Toast.LENGTH_LONG).show();
	}
}
