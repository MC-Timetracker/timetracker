package iiitd.mc.timetracker.view;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Recording;
import iiitd.mc.timetracker.model.Task;

public class TaskWiseStatsFragment extends StatisticsOverviewFragment {
    private long taskid;
    private TextView rangeTv;
    TextView textView, textView2;
    TextView no_data;
    LinearLayout chartContainer, chartContainer2;
    private long startTime, endTime;
    private HashMap<String, Long> pierec;
    private HashMap<String, Float> barrec;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_task_wise_stats, container, false);

        drawOverallPie = false; // workaround because this class extends OverallStats

        taskid = getActivity().getIntent().getLongExtra("taskid", 0); //TODO: rework this for fragment

        rangeTv = (TextView) v.findViewById(R.id.timeRangeTv);
        textView = (TextView) v.findViewById(R.id.textView);
        textView2 = (TextView) v.findViewById(R.id.textView2);
        chartContainer = (LinearLayout) v.findViewById(R.id.chart1);
        chartContainer2 = (LinearLayout) v.findViewById(R.id.chart2);
        no_data = (TextView) v.findViewById(R.id.no_data2);

        initTimeRanges();
        drawBarChart();
        drawPieChart();

        return v;
    }

    public void drawBarChart() {
        textView.setText("Average Hours Spent");

        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();
        Task task = db.getTask(taskid);
        db.close();

        List<Recording> recs = getRecordings();

        initializeMapforBarGraph();

        SimpleDateFormat sdf = new SimpleDateFormat("EE");

        for (Recording r : recs) {
            String tday = sdf.format(r.getStart());
            float dur_hrs = 0f;
            if (barrec.containsKey(tday)) {
                dur_hrs = (float) (barrec.get(tday) + (r.getDuration(TimeUnit.MINUTES) / 60.0));
                barrec.put(tday, dur_hrs);
            }
        }

        XYSeries timeDurationseries;
        XYSeriesRenderer timeDurationRenderer;
        XYMultipleSeriesRenderer multiRenderer;
        XYMultipleSeriesDataset dataset;

        NumberFormat numformat = NumberFormat.getInstance();
        numformat.setMaximumFractionDigits(2);

        timeDurationseries = new XYSeries("Duration(in hrs)");

        List<String> lblTasks = new ArrayList<String>();
        int i = 0;

        for (Map.Entry<String, Float> entry : barrec.entrySet()) {
            timeDurationseries.add(i, entry.getValue());
            lblTasks.add(i, entry.getKey());
            i++;
        }

        dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(timeDurationseries);

        timeDurationRenderer = new XYSeriesRenderer();
        timeDurationRenderer.setColor(Color.rgb(220, 80, 80));
        timeDurationRenderer.setFillPoints(true);
        timeDurationRenderer.setLineWidth(2);
        timeDurationRenderer.setDisplayChartValues(true);

        multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        String name = task.getName();
        multiRenderer.setChartTitle(name.substring(0, 1).toUpperCase() + name.substring(1, name.length()) + " and Sub Activities");
        multiRenderer.setXTitle("Day");
        multiRenderer.setYTitle("Time Spent");
        multiRenderer.setXAxisMin(-0.5);
        multiRenderer.setXAxisMax(6.5);
        multiRenderer.setBarSpacing(0.5);
        multiRenderer.setYAxisMin(-0.5);
        multiRenderer.setLabelFormat(numformat);
        timeDurationRenderer.setChartValuesFormat(numformat);
        timeDurationRenderer.setChartValuesTextAlign(Align.CENTER);

        if (timeRangeId == 5 || timeRangeId == 6)
            multiRenderer.setYAxisMax(210);
        else
            multiRenderer.setYAxisMax(24);
        multiRenderer.setYLabelsAlign(Align.RIGHT, 0);
        multiRenderer.setShowGrid(true);
        multiRenderer.setGridColor(Color.GRAY);
        multiRenderer.setZoomButtonsVisible(false);
        for (int j = 0; j < lblTasks.size(); j++)
            multiRenderer.addXTextLabel(j, lblTasks.get(j));
        multiRenderer.addSeriesRenderer(timeDurationRenderer);

        GraphicalView mChart = ChartFactory.getBarChartView(getActivity(), dataset, multiRenderer, org.achartengine.chart.BarChart.Type.DEFAULT);

        chartContainer.addView(mChart);
    }

    public void drawPieChart() {
        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();
        Task task = db.getTask(taskid);
        db.close();

        List<Recording> recs = getRecordings();

        pierec = new HashMap<>();

        long utilTime = 0;
        for (Recording r : recs) {
            String name = r.getTask().getName();
            long dur = 0;
            if (pierec.containsKey(name)) {
                dur = pierec.get(name) + r.getDuration(TimeUnit.MINUTES);
                pierec.put(name, dur);
            } else {
                dur = r.getDuration(TimeUnit.MINUTES);
                pierec.put(name, dur);
            }
            utilTime += dur;
        }


        if (!pierec.isEmpty()) {
            String name = task.getName();
            textView2.setText(name.substring(0, 1).toUpperCase() + name.substring(1, name.length()) + " and Sub Activities Proportion");
            CategorySeries distributionSeries = new CategorySeries("Subjects Studied Over Time");

            for (Map.Entry<String, Long> entry : pierec.entrySet()) {
                // Adding a slice with its values and name to the Pie Chart
                distributionSeries.add(entry.getKey(), entry.getValue());
            }

            // Instantiating a renderer for the Pie Chart
            DefaultRenderer defaultRenderer = new DefaultRenderer();
            for (int i = 0; i < pierec.size(); i++) {
                SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
                seriesRenderer.setColor(colors[i % 8]);
                seriesRenderer.setDisplayChartValues(true);
                // Adding a renderer for a slice
                defaultRenderer.addSeriesRenderer(seriesRenderer);
            }

            defaultRenderer.setChartTitleTextSize(20);
            defaultRenderer.setZoomButtonsVisible(false);
            defaultRenderer.setLabelsColor(Color.BLACK);

            no_data.setVisibility(View.GONE);
            GraphicalView mChart = ChartFactory.getPieChartView(getActivity(), distributionSeries, defaultRenderer);
            chartContainer2.addView(mChart);
        } else {
            no_data.setText("No data available for this range");
        }

    }

    public List<Recording> getRecordings() {
        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();
        List<Task> subtasks = db.getSubTasks(taskid);
        List<Recording> recs = new ArrayList<>();

        recs.addAll(db.getRecordings(taskid, startTime, endTime));

        if (!subtasks.isEmpty()) {
            for (int i = 0; i < subtasks.size(); i++) {
                recs.addAll(db.getRecordings(subtasks.get(i).getId(), startTime, endTime));
            }
        }
        db.close();
        return recs;
    }

    public void initTimeRanges() {
        if (timeRangeId == 1) {
            rangeTv.setText("Today");
            startTime = getStartOfDay();
            endTime = getEndOfDay();
        } else if (timeRangeId == 2) {
            rangeTv.setText("Yesterday");
            startTime = getYesterdayStart();
            endTime = getYesterdayEnd();
        } else if (timeRangeId == 3) {
            rangeTv.setText("Current week");
            startTime = getWeekStart();
            endTime = getWeekEnd();
        } else if (timeRangeId == 4) {
            rangeTv.setText("Last Week");
            startTime = getLastWeekStart();
            endTime = getLastWeekEnd();
        } else if (timeRangeId == 5) {
            rangeTv.setText("Current Month");
            startTime = getMonthStart();
            endTime = getMonthEnd();
        } else if (timeRangeId == 6) {
            rangeTv.setText("Last Month");
            startTime = getLastMonthStart();
            endTime = getLastMonthEnd();
        } else {
            if (startDate == null || endDate == null)
                rangeTv.setText("No date specified");
            else {
                rangeTv.setText(startDate.get(Calendar.DATE) + "/" + startDate.get(Calendar.MONTH) + " - " + endDate.get(Calendar.DATE) + "/" + endDate.get(Calendar.MONTH));
                startTime = startDate.getTimeInMillis();
                endTime = endDate.getTimeInMillis();
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        super.onMenuItemClick(item);

        //TODO this.frame.removeAllViews();
        //this.frame.addView(inflater.inflate(R.layout.activity_task_wise_stats, null));
        initTimeRanges();
        drawBarChart();
        drawPieChart();

        return true;
    }

    public void initializeMapforBarGraph() {
        barrec = new LinkedHashMap<>();
        barrec.put("Mon", (float) 0);
        barrec.put("Tue", (float) 0);
        barrec.put("Wed", (float) 0);
        barrec.put("Thu", (float) 0);
        barrec.put("Fri", (float) 0);
        barrec.put("Sat", (float) 0);
        barrec.put("Sun", (float) 0);
    }
}