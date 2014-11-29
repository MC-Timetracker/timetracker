package iiitd.mc.timetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;
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
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class OverallStats extends BaseActivity {

	public static final String PREFS_NAME = "Tab_Pref";
	int mDisplayMode;
	private LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_statistics);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_overall_statistics, null));
     
        drawOverallPieChart();
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		super.onMenuItemClick(item);
		
		finish();
		startActivity(getIntent().setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
		return true;
	}
	
	public void drawOverallPieChart()
	{
		Map<String, Long> todrec = new HashMap<>();
		List<Recording> recs = new ArrayList<>();
		TextView tv = (TextView) findViewById(R.id.timeRangeTv);

		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		
		if(timeRangeId == 1)
		{
			tv.setText("Today");
			
			Date today = new Date();
			recs = db.getRecordings(getStartOfDay(), getEndOfDay());
			todrec.put("Others", (long)24*60);
		}
		
		else if(timeRangeId == 2)
		{
			tv.setText("Yesterday");
			
			recs = db.getRecordings(getYesterdayStart(), getYesterdayEnd());
			todrec.put("Others", (long)24*60);
		}
		
		else if(timeRangeId == 3)
		{
			tv.setText("This Week");
			
			recs = db.getRecordings(getWeekStart(), getWeekEnd());
			todrec.put("Others", (long)7*24*60);
		}
		
		else if(timeRangeId == 4)
		{
			tv.setText("Last Week");
			
			recs = db.getRecordings(getLastWeekStart(), getLastWeekEnd());
			todrec.put("Others", (long)7*24*60);
		}
		
		else if(timeRangeId == 5)
		{
			tv.setText("Current Month");
			
			recs = db.getRecordings(getMonthStart(), getMonthEnd());
			
			todrec.put("Others", (long)Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)*24*60);
		}
		
		else if(timeRangeId == 6)
		{
			tv.setText("Last Month");
			
			recs = db.getRecordings(getLastMonthStart(), getLastMonthEnd());
			Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.MONTH,-1);
			todrec.put("Others", (long)calendar.getActualMaximum(Calendar.DAY_OF_MONTH)*24*60);
		}
		
		else if(timeRangeId == 7)
		{
			int inter = 1;
			if(startDate == null || endDate == null)
				tv.setText("No date specified");
			else
			{
				tv.setText(startDate.get(Calendar.DATE)+"/"+startDate.get(Calendar.MONTH)+" - "+endDate.get(Calendar.DATE)+"/"+endDate.get(Calendar.MONTH));
			
				recs = db.getRecordings(startDate.getTimeInMillis(),endDate.getTimeInMillis());
				inter = endDate.get(Calendar.DATE) - startDate.get(Calendar.DATE) + 1;
			}
			
			todrec.put("Others", (long)inter*24*60);			
		}
		db.close();
		
		int utilTime = 0;
		for(Recording r: recs)
		{
			String fullname = r.getTask().getNameFull();
			String tname;
			if(fullname.contains("."))
				tname = fullname.substring(0, fullname.indexOf("."));
			else
				tname = fullname;
			
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
}
