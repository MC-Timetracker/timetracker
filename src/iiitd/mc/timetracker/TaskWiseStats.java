package iiitd.mc.timetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.IDatabaseController;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskWiseStats extends BaseActivity
{
	private long taskid;
	private TextView rangeTv;
	private long startTime, endTime;
	private LayoutInflater inflater;
	private HashMap<String,Long> todrec;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_task_wise_stats, null));
		
		taskid = getIntent().getLongExtra("taskid",0);
				
		rangeTv = (TextView) findViewById(R.id.timeRangeTv);
		
		drawPieChart();
		
	}

	public void drawPieChart()
	{
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		List<Task> subtasks = db.getSubTasks(taskid);
		List<Recording> recs = new ArrayList<>();
		
		if(timeRangeId == 1)
		{
			rangeTv.setText("Today");
			startTime = getStartOfDay();
			endTime = getEndOfDay();
		}
		else if(timeRangeId == 2)
		{
			rangeTv.setText("Yesterday");
			startTime = getYesterdayStart();
			endTime = getYesterdayEnd();
		}
		else if(timeRangeId == 3)
		{
			rangeTv.setText("Current week");
			startTime = getWeekStart();
			endTime = getWeekEnd();
		}
		else if(timeRangeId == 4)
		{
			rangeTv.setText("Last Week");
			startTime = getLastWeekStart();
			endTime = getLastWeekEnd();
		}
		else if(timeRangeId == 5)
		{
			rangeTv.setText("Current Month");
			startTime = getMonthStart();
			endTime = getMonthEnd();
		}
		else if(timeRangeId == 6)
		{
			rangeTv.setText("Last Month");
			startTime = getLastMonthStart();
			endTime = getLastMonthEnd();
		}
		else
		{
			if(startDate == null || endDate == null)
				rangeTv.setText("No date specified");
			else
			{
				rangeTv.setText(startDate.get(Calendar.DATE)+"/"+startDate.get(Calendar.MONTH)+" - "+endDate.get(Calendar.DATE)+"/"+endDate.get(Calendar.MONTH));
				startTime = startDate.getTimeInMillis();
				endTime = endDate.getTimeInMillis();
			}
		}
		
		if(!subtasks.isEmpty())
		{
			for(int i=0;i<subtasks.size();i++)
			{
				recs.addAll(db.getRecordings(subtasks.get(i).getId(),startTime, endTime));
			}
		}
		
		todrec = new HashMap<>();
		
		long utilTime = 0;
		for(Recording r: recs)
		{
			String name = r.getTask().getName();
			long dur = 0;
			if(todrec.containsKey(name))
			{
				dur = todrec.get(name)+r.getDuration(TimeUnit.MINUTES);
				todrec.put(name, dur);
			}
			else
			{
				dur = r.getDuration(TimeUnit.MINUTES);
				todrec.put(name,dur);	
			}
			utilTime += dur;
		}
		
		CategorySeries distributionSeries = new CategorySeries("Subjects Studied Over Time");
        
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
 
        defaultRenderer.setChartTitle("Studies Pie Chart");
        defaultRenderer.setChartTitleTextSize(20);
        defaultRenderer.setZoomButtonsVisible(false);
 
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart1);
        GraphicalView mChart = ChartFactory.getPieChartView(this, distributionSeries , defaultRenderer);
        chartContainer.addView(mChart);
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		super.onMenuItemClick(item);
		
		finish();
		startActivity(getIntent().setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
		return true;
	}
}
