package iiitd.mc.timetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import iiitd.mc.timetracker.charts.OverallPieChart;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;

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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

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
		int colors[] = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED,
	            Color.YELLOW };
		Map<String, Long> todrec = new HashMap<>();
		List<Recording> recs = new ArrayList<>();
		
		if(mDisplayMode == 0)
		{
			this.frame.removeAllViews();
			this.frame.addView(inflater.inflate(R.layout.overall_stats, null));
			TextView tv = (TextView) findViewById(R.id.timeRangeTv);
			if(timeRangeId == 1)
			{
				tv.setText("Today");
	
				IDatabaseController db = ApplicationHelper.createDatabaseController();
				db.open();
				
				Date today = new Date();
				recs = db.getRecordings(getStartOfDay(today), getEndOfDay(today));
				todrec.put("Others", (long)24*60);
				
				db.close();
			}
			
			else if(timeRangeId == 2)
			{
				tv.setText("Yesterday");
				
				IDatabaseController db = ApplicationHelper.createDatabaseController();
				db.open();
				
				recs = db.getRecordings(getYesterdayStart(), getYesterdayEnd());
				todrec.put("Others", (long)24*60);
				
				db.close();
			}
			
			else if(timeRangeId == 3)
			{
				tv.setText("This Week");
				IDatabaseController db = ApplicationHelper.createDatabaseController();
				db.open();
				
				recs = db.getRecordings(getWeekStart(), getWeekEnd());
				todrec.put("Others", (long)7*24*60);
				
				db.close();
			}
			
			else if(timeRangeId == 4)
			{
				tv.setText("Last Week");
				IDatabaseController db = ApplicationHelper.createDatabaseController();
				db.open();
				
				recs = db.getRecordings(getLastWeekStart(), getLastWeekEnd());
				todrec.put("Others", (long)7*24*60);
				
				db.close();
			}
			
			else if(timeRangeId == 5)
			{
				tv.setText("Current Month");
				IDatabaseController db = ApplicationHelper.createDatabaseController();
				db.open();
				
				recs = db.getRecordings(getMonthStart(), getMonthEnd());
				
				todrec.put("Others", (long)Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)*24*60);
			
				db.close();
			}
			
			else if(timeRangeId == 6)
			{
				tv.setText("Last Month");
				IDatabaseController db = ApplicationHelper.createDatabaseController();
				db.open();
				
				recs = db.getRecordings(getLastMonthStart(), getLastMonthEnd());
				Calendar calendar = Calendar.getInstance();
		        calendar.add(Calendar.MONTH,-1);
				todrec.put("Others", (long)calendar.getActualMaximum(Calendar.DAY_OF_MONTH)*24*60);
				
				db.close();
			}
			
			int utilTime = 0;
			for(Recording r: recs)
			{
				String fullname = r.getTask().getNameFull();
				String tname = fullname.substring(0, fullname.indexOf("."));
				long dur = 0;
				if(todrec.containsKey(tname))
				{
					dur = todrec.get(tname)+r.getDuration(TimeUnit.MINUTES);
					todrec.put(tname, dur);
				}
				else
				{
					dur = r.getDuration(TimeUnit.MINUTES);
					todrec.put(tname,dur);	
				}
				utilTime += dur;
			}
			todrec.put("Others", todrec.get("Others")-utilTime);
			
			CategorySeries distributionSeries = new CategorySeries("Overall Comparison of Parent Tasks");
	        
	        for(Map.Entry<String, Long> entry:todrec.entrySet()){
	            // Adding a slice with its values and name to the Pie Chart
	            distributionSeries.add(entry.getKey(), entry.getValue());
	        }
	 
	        // Instantiating a renderer for the Pie Chart
	        DefaultRenderer defaultRenderer  = new DefaultRenderer();
	        for(int i = 0 ;i<todrec.size();i++){
	            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
	            seriesRenderer.setColor(colors[i]);
	            seriesRenderer.setDisplayChartValues(true);
	            // Adding a renderer for a slice
	            defaultRenderer.addSeriesRenderer(seriesRenderer);
	        }
	 
	        defaultRenderer.setChartTitle("Overall Pie Chart");
	        defaultRenderer.setChartTitleTextSize(20);
	        defaultRenderer.setZoomButtonsVisible(false);
	 
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
	
	public long getLastMonthEnd()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTimeInMillis();
	}
	
	public long getLastMonthStart()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
	}
	
	public long getMonthEnd()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTimeInMillis();
	}
	
	public long getMonthStart()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
	}
	
	public long getLastWeekStart()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTimeInMillis();
	}
	
	public long getLastWeekEnd()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTimeInMillis();
	}
	
	public long getWeekStart()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
	}
	
	public long getWeekEnd()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, 1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTimeInMillis();
	}
	
	public long getYesterdayStart()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE,-1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
	}
	
	public long getYesterdayEnd()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE,-1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
	}
	
	public long getStartOfDay(Date date)
	{
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
	}
	
	public long getEndOfDay(Date date)
	{
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
	}
}
