package iiitd.mc.timetracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

public class BootReceiver extends BroadcastReceiver {

    /**
     * Receives android.intent.action.BOOT_COMPLETED intent to setup background services.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        setupAutoRecorderTriggers();
    }

    private static boolean isSetup = false;

    public static void setupAutoRecorderTriggers() {
        if (isSetup)
            return;

        Context context = ApplicationHelper.getAppContext();

        // Set regular alarm for AutoRecorder
        //		see https://developer.android.com/training/scheduling/alarms.html
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent handlerIntent = new Intent(context, AutoRecorder.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, handlerIntent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

        // Call AutoRecorder whenever new Wifi is in range
        AutoRecorder autoRecorder = new AutoRecorder();
        context.registerReceiver(autoRecorder, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));

        isSetup = true;
    }

}
