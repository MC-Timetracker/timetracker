package iiitd.mc.timetracker;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class Statistics extends BaseActivity {

	public static final String PREFS_NAME = "Tab_Pref";
	public int mDisplayMode;
	private LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_statistics);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		if(mDisplayMode == 0)
		{
			this.frame.removeAllViews();
			this.frame.addView(inflater.inflate(R.layout.overall_stats, null));
			
			// Pie Chart Section Names
	        String[] code = new String[] {
	            "Eclair & Older", "Froyo", "Gingerbread", "Honeycomb",
	            "IceCream Sandwich", "Jelly Bean"
	        };
	 
	        // Pie Chart Section Value
	        double[] distribution = { 3.9, 12.9, 55.8, 1.9, 23.7, 1.8 } ;
	 
	        // Color of each Pie Chart Sections
	        int[] colors = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED,
	                        Color.YELLOW };
	 
	        // Instantiating CategorySeries to plot Pie Chart
	        CategorySeries distributionSeries = new CategorySeries(" Android version distribution as on October 1, 2012");
	        for(int i=0 ;i < distribution.length;i++){
	            // Adding a slice with its values and name to the Pie Chart
	            distributionSeries.add(code[i], distribution[i]);
	        }
	 
	        // Instantiating a renderer for the Pie Chart
	        DefaultRenderer defaultRenderer  = new DefaultRenderer();
	        for(int i = 0 ;i<distribution.length;i++){
	            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
	            seriesRenderer.setColor(colors[i]);
	            seriesRenderer.setDisplayChartValues(true);
	            // Adding a renderer for a slice
	            defaultRenderer.addSeriesRenderer(seriesRenderer);
	        }
	 
	        defaultRenderer.setChartTitle("Pie Chart example");
	        defaultRenderer.setChartTitleTextSize(20);
	        defaultRenderer.setZoomButtonsVisible(true);
	 
	        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);
	        GraphicalView mChart = ChartFactory.getPieChartView(this, distributionSeries , defaultRenderer);
	        chartContainer.addView(mChart);
	        
		}
		else
		{
			this.frame.removeAllViews();
			this.frame.addView(inflater.inflate(R.layout.taskwise_stats, null));
		}
		
		
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
