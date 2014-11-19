package iiitd.mc.timetracker;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

public class Statistics extends BaseActivity {

	public static final String PREFS_NAME = "Tab_Pref";
	public int mDisplayMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_statistics);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_statistics, null));
        
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mDisplayMode = settings.getInt("displayMode", 0);
        
        ActionBar bar = getActionBar();
        
        bar.addTab(bar.newTab().setText("Overall").setTabListener(this));
        bar.addTab(bar.newTab().setText("Taskwise").setTabListener(this));
        
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        bar.selectTab(bar.getTabAt(mDisplayMode));
        
        
        
        
        
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		mDisplayMode = tab.getPosition();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistics, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
