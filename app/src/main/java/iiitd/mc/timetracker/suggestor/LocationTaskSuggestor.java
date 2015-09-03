package iiitd.mc.timetracker.suggestor;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Recording;

public class LocationTaskSuggestor implements ITaskSuggestor {
    private IDatabaseController db;
    private Context appContext;


    public LocationTaskSuggestor() {
        appContext = ApplicationHelper.getAppContext();
        db = ApplicationHelper.getDatabaseController();
    }

    /**
     * tracks the mac address of the current location
     *
     * @param context application context
     * @return mac address
     */
    private String trackbssid(Context context) {
        WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mainWifi.getConnectionInfo();

        String bssid = "00:00:00:00:00";
        if (wifiInfo.getBSSID() != null) {
            bssid = wifiInfo.getBSSID();
        }

        return bssid;
    }

    @Override
    public List<SuggestedTask> getSuggestedTasks() {
        String bssid = trackbssid(appContext);
        db.open();
        List<Recording> recordings = db.getRecordings();
        db.close();

        double prob = 1.0;
        List<SuggestedTask> tasks = new ArrayList<>();
        for (Recording rec : recordings) {
            if (bssid.equals(rec.getMacAddress()) && !bssid.equals("00:00:00:00:00")) {
                SuggestedTask temp = new SuggestedTask(rec.getTask(), prob);
                tasks.add(temp);
            }
        }

        return tasks;
    }

}
