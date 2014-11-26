package iiitd.mc.timetracker.charts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.widget.LinearLayout;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;

public class OverallPieChart extends ActionBarActivity
{
	//private List<Integer> colors;
	private int colors[] = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED,
            Color.YELLOW };
	private Map<String, Long> todrec;
	List<Recording> recs;
	Calendar cd;
	
	public OverallPieChart(int time_range)
	{
		cd = Calendar.getInstance();
		recs = new ArrayList<>();
		todrec = new HashMap<>();

		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		if(time_range == 1)
		{
			long start = (new Date()).getTime();
			cd.add(Calendar.DATE, 1);
			long end = cd.getTimeInMillis();
			recs = db.getRecordings(start, end);
			todrec.put("Others", (long)24*60);
		}
		db.close();
		
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
		
	}
	
	public GraphicalView makePieChart()
	{
		// Instantiating CategorySeries to plot Pie Chart
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
 
        defaultRenderer.setChartTitle("Pie Chart example");
        defaultRenderer.setChartTitleTextSize(20);
        defaultRenderer.setZoomButtonsVisible(true);
 
       
        GraphicalView mChart = ChartFactory.getPieChartView(this, distributionSeries , defaultRenderer);
        
        return mChart;
	}
}
