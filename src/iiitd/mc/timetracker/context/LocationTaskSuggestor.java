package iiitd.mc.timetracker.context;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;
import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.BootReceiver;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.data.TaskRecorderService;
import iiitd.mc.timetracker.helper.DatabaseController;
import iiitd.mc.timetracker.helper.DatabaseHelper;
import iiitd.mc.timetracker.helper.IDatabaseController;

public class LocationTaskSuggestor implements ITaskSuggestor{

	
	IDatabaseController db;
	private Context appContext;
	DatabaseController mac = new DatabaseController(null);
	WifiManager mainWifiObj;
	private List<SuggestedTask> tasks = null;
	public static final String WIFI_SCAN_AVAILABLE = "wifi_scan_available";

	public LocationTaskSuggestor()
	{
		appContext = ApplicationHelper.getAppContext(); 
		db = ApplicationHelper.createDatabaseController();
		
	}
	
	/**
	 * tracks the mac address of the current location
	 * @param context application context
	 * @return mac address
	 */
	public String trackbssid(Context context){
		
		String bssid = "";
		
		WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mainWifi.getConnectionInfo();
			
		if (wifiInfo.getBSSID()!=null){
			
			bssid = wifiInfo.getBSSID();
		}
		else if(wifiInfo.getBSSID()== null){
			
			bssid = "00:00:00:00:00";
		}
		
		return bssid;
	}

	@Override
	public List<SuggestedTask> getSuggestedTasks() {
		
		String bssid = trackbssid(appContext);
		tasks = setcurrentbssid(appContext, bssid);
		
		return tasks;	
	}
	
	/**
	 * compares the current mac address to the mac address saved in all the recordings
	 * @param context
	 * @param bssid mac address
	 * @return
	 */
	public List<SuggestedTask> setcurrentbssid(Context context,String bssid){
		
		Toast.makeText(context, bssid, Toast.LENGTH_SHORT).show();
		db.open();
		List<Recording> recordings= db.getRecordings();
		db.close();
		double prob = 1.0;
		tasks = new ArrayList<SuggestedTask>();
		int i=0;
		for(Recording rec : recordings)
		{
			
			if(bssid == "00:00:00:00:00"){
				
				Toast.makeText(appContext, "No Wifi Connection", Toast.LENGTH_SHORT).show();
			}
			
			else if(bssid.compareTo(rec.getMacAddress())==0){
				
				SuggestedTask temp = new SuggestedTask(rec.getTask(), prob);
				tasks.add(temp);
				i++;
			}
			

		}
		for(int j=0;j<i ;j++){
			
			Toast.makeText(appContext, tasks.get(j).getTask().getNameFull(), Toast.LENGTH_SHORT).show();
		}
		
		return tasks;
	}
	
}
	
