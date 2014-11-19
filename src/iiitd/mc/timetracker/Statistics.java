package iiitd.mc.timetracker;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

public class Statistics extends BaseActivity {
	
	private View mChart;
	private Map<String, Long> todrec;
	private List<String> lblTasks;
	private XYMultipleSeriesDataset dataset;
	private XYSeries timeDurationseries;
	private XYSeriesRenderer timeDurationRenderer;
	private XYMultipleSeriesRenderer multiRenderer;
	private LinearLayout chartContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_statistics);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_statistics, null));
        
        Button btnChart = (Button) findViewById(R.id.btnChart);
        
        OnClickListener clickListener = new OnClickListener() {
        	public void onClick(View v) {
        		//showTodayStats();
        		showMonthStats();
        	}
        };
        btnChart.setOnClickListener(clickListener);
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
	
	public void showTodayStats()
	{
		int i=0;
		long dur=0;
		String tempstr;
		todrec=new HashMap<String, Long>();
		DateFormat formater = android.text.format.DateFormat.getDateFormat(this);
		String currdate=formater.format(new Date());
		lblTasks = new ArrayList<String>();
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		List<Recording> recs = db.getRecordings();	//new query should be hit now depending on time interval selected
		db.close();
		
		for(Recording r:recs)
		{
			String temp = formater.format(r.getStart());			
			if(temp.equals(currdate))
			{
				tempstr=r.getTask().getName();
				if(todrec.containsKey(tempstr))
				{
					dur = todrec.get(tempstr)+r.getDuration(TimeUnit.MINUTES);
					todrec.put(tempstr, dur);
				}
				else
					todrec.put(tempstr, r.getDuration(TimeUnit.MINUTES));
			}
		}
		
		timeDurationseries = new XYSeries("Duration");
		for(Map.Entry<String, Long> entry:todrec.entrySet())
		{
			timeDurationseries.add(i, entry.getValue());
			lblTasks.add(i, entry.getKey());
			i++;
		}
		
		dataset=new XYMultipleSeriesDataset();
		dataset.addSeries(timeDurationseries);
		
		timeDurationRenderer = new XYSeriesRenderer();
		timeDurationRenderer.setColor(Color.rgb(130, 130, 230));
		timeDurationRenderer.setFillPoints(true);
		timeDurationRenderer.setLineWidth(2);
		timeDurationRenderer.setDisplayChartValues(true);
		
		multiRenderer = new XYMultipleSeriesRenderer();
	    multiRenderer.setXLabels(0);
	    multiRenderer.setChartTitle("Task Stats today");
	    multiRenderer.setXTitle("Task");
	    multiRenderer.setYTitle("Time Spent");
	    multiRenderer.setBarSpacing(0.5);
	    multiRenderer.setZoomButtonsVisible(true);
	    for(int j=0; j < lblTasks.size(); j++)
	    	multiRenderer.addXTextLabel(j, lblTasks.get(j));
	     
	    multiRenderer.addSeriesRenderer(timeDurationRenderer);
	    chartContainer = (LinearLayout) findViewById(R.id.chart);
    	chartContainer.removeAllViews();
    	mChart = ChartFactory.getBarChartView(Statistics.this, dataset, multiRenderer,Type.DEFAULT);
    	chartContainer.addView(mChart);
	}
	
	public void showMonthStats(){
		int i=0;
		long dur=0;
		String tempstr;
		Calendar cd;
		todrec=new HashMap<String, Long>();
		DateFormat formater = android.text.format.DateFormat.getDateFormat(this);
		String currdate=formater.format(new Date());
		lblTasks = new ArrayList<String>();
		
		cd=Calendar.getInstance();
		cd.add(Calendar.MONTH,-1);
		
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		List<Recording> recs = db.getRecordings();		//new query here
		db.close();
		
		for(Recording r:recs)
		{
			Date temp = r.getStart();			
			if(temp.after(cd.getTime()))
			{
				tempstr=r.getTask().getName();
				if(todrec.containsKey(tempstr))
				{
					dur = todrec.get(tempstr)+r.getDuration(TimeUnit.MINUTES);
					todrec.put(tempstr, dur);
				}
				else
					todrec.put(tempstr, r.getDuration(TimeUnit.MINUTES));
			}
		}
		
		timeDurationseries = new XYSeries("Duration");
		for(Map.Entry<String, Long> entry:todrec.entrySet())
		{
			timeDurationseries.add(i, entry.getValue());
			lblTasks.add(i, entry.getKey());
			i++;
		}
		
		XYMultipleSeriesDataset dataset=new XYMultipleSeriesDataset();
		dataset.addSeries(timeDurationseries);
		
		timeDurationRenderer = new XYSeriesRenderer();
		timeDurationRenderer.setColor(Color.rgb(130, 130, 230));
		timeDurationRenderer.setFillPoints(true);
		timeDurationRenderer.setLineWidth(2);
		timeDurationRenderer.setDisplayChartValues(true);
		
		multiRenderer = new XYMultipleSeriesRenderer();
	    multiRenderer.setXLabels(0);
	    multiRenderer.setChartTitle("Task Stats for the month");
	    multiRenderer.setXTitle("Task");
	    multiRenderer.setYTitle("Time Spent");
	    multiRenderer.setBarSpacing(0.5);
	    multiRenderer.setZoomButtonsVisible(true);
	    for(int j=0; j < lblTasks.size(); j++)
	    	multiRenderer.addXTextLabel(j, lblTasks.get(j));
	     
	    multiRenderer.addSeriesRenderer(timeDurationRenderer);
	    chartContainer = (LinearLayout) findViewById(R.id.chart);
    	chartContainer.removeAllViews();
    	mChart = ChartFactory.getBarChartView(Statistics.this, dataset, multiRenderer,Type.DEFAULT);
    	chartContainer.addView(mChart);
	}
}
