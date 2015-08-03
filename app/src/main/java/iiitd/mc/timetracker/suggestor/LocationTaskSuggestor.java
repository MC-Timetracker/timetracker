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
        db = ApplicationHelper.createDatabaseController();
    }

    /**
     * tracks the mac address of the current location
     *
     * @param context application context
     * @return mac address
     */
    public String trackbssid(Context context) {
        String bssid = "";

        WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mainWifi.getConnectionInfo();

        if (wifiInfo.getBSSID() != null) {

            bssid = wifiInfo.getBSSID();
        } else if (wifiInfo.getBSSID() == null) {

            bssid = "00:00:00:00:00";
        }

        return bssid;
    }

    @Override
    public List<SuggestedTask> getSuggestedTasks() {
        return setcurrentbssid(appContext, trackbssid(appContext));
    }

    /**
     * compares the current mac address to the mac address saved in all the recordings
     *
     * @param context
     * @param bssid   mac address
     * @return
     */
    public List<SuggestedTask> setcurrentbssid(Context context, String bssid) {
        db.open();
        List<Recording> recordings = db.getRecordings();
        db.close();

        double prob = 1.0;
        List<SuggestedTask> tasks = new ArrayList<>();
        for (Recording rec : recordings) {
            if (bssid != "00:00:00:00:00" && bssid.compareTo(rec.getMacAddress()) == 0) {
                SuggestedTask temp = new SuggestedTask(rec.getTask(), prob);
                tasks.add(temp);
            }
        }

        return tasks;
    }

}
