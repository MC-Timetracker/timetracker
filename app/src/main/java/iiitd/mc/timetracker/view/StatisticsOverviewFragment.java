package iiitd.mc.timetracker.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Recording;

public class StatisticsOverviewFragment extends Fragment implements OnMenuItemClickListener {

    int colors[] = {Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.MAGENTA, Color.RED, Color.YELLOW};
    static Calendar startDate;
    static Calendar endDate;
    static int timeRangeId = 1;
    EditText from_date;
    EditText to_date;
    DatePickerDialog fromDatePickerDialog, toDatePickerDialog;

    public static final String PREFS_NAME = "Tab_Pref";
    int mDisplayMode;
    private LinearLayout chartContainer;
    private TextView no_data;
    private TextView tvTimeRange;

    protected boolean drawOverallPie = true;
    DateFormat format;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_overall_statistics, container, false);

        tvTimeRange = (TextView) v.findViewById(R.id.timeRangeTv);
        no_data = (TextView) v.findViewById(R.id.no_data1);
        chartContainer = (LinearLayout) v.findViewById(R.id.chart);

        format = android.text.format.DateFormat.getDateFormat(getActivity());

        setHasOptionsMenu(true);

        if (drawOverallPie) drawOverallPieChart();

        return v;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.today:
                timeRangeId = 1;
                break;
            case R.id.yesterday:
                timeRangeId = 2;
                break;
            case R.id.thisweek:
                timeRangeId = 3;
                break;
            case R.id.lastweek:
                timeRangeId = 4;
                break;
            case R.id.thismonth:
                timeRangeId = 5;
                break;
            case R.id.lastmonth:
                timeRangeId = 6;
                break;
            case R.id.custom:
                timeRangeId = 7;
                rangeCustomDialog();
                break;
            default:

        }

        //TODO: refresh
        //this.frame.removeAllViews();
        //this.frame.addView(inflater.inflate(R.layout.activity_overall_statistics, null));
        drawOverallPieChart();

        return true;
    }

    public void drawOverallPieChart() {
        Map<String, Long> todrec = new HashMap<>();
        List<Recording> recs = new ArrayList<>();

        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();

        if (timeRangeId == 1) {
            tvTimeRange.setText("Today");

            recs = db.getRecordings(getStartOfDay(), getEndOfDay());
            todrec.put("Others", (long) 24 * 60);
        } else if (timeRangeId == 2) {
            tvTimeRange.setText("Yesterday");

            recs = db.getRecordings(getYesterdayStart(), getYesterdayEnd());
            todrec.put("Others", (long) 24 * 60);
        } else if (timeRangeId == 3) {
            tvTimeRange.setText("This Week");

            recs = db.getRecordings(getWeekStart(), getWeekEnd());
            todrec.put("Others", (long) 7 * 24 * 60);
        } else if (timeRangeId == 4) {
            tvTimeRange.setText("Last Week");

            recs = db.getRecordings(getLastWeekStart(), getLastWeekEnd());
            todrec.put("Others", (long) 7 * 24 * 60);
        } else if (timeRangeId == 5) {
            tvTimeRange.setText("Current Month");

            recs = db.getRecordings(getMonthStart(), getMonthEnd());

            todrec.put("Others", (long) Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) * 24 * 60);
        } else if (timeRangeId == 6) {
            tvTimeRange.setText("Last Month");

            recs = db.getRecordings(getLastMonthStart(), getLastMonthEnd());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            todrec.put("Others", (long) calendar.getActualMaximum(Calendar.DAY_OF_MONTH) * 24 * 60);
        } else if (timeRangeId == 7) {
            int inter = 1;
            if (startDate == null || endDate == null)
                tvTimeRange.setText("No date specified");
            else {
                tvTimeRange.setText(startDate.get(Calendar.DATE) + "/" + startDate.get(Calendar.MONTH) + " - " + endDate.get(Calendar.DATE) + "/" + endDate.get(Calendar.MONTH));

                recs = db.getRecordings(startDate.getTimeInMillis(), endDate.getTimeInMillis());
                inter = endDate.get(Calendar.DATE) - startDate.get(Calendar.DATE) + 1;
            }

            todrec.put("Others", (long) inter * 24 * 60);
        }
        db.close();

        int utilTime = 0;
        for (Recording r : recs) {
            String fullname = r.getTask().getNameFull();
            String tname;
            if (fullname.contains("."))
                tname = fullname.substring(0, fullname.indexOf("."));
            else
                tname = fullname;

            long dur = 0;
            if (todrec.containsKey(tname)) {
                dur = todrec.get(tname) + r.getDuration(TimeUnit.MINUTES);
                todrec.put(tname, dur);
            } else {
                dur = r.getDuration(TimeUnit.MINUTES);
                todrec.put(tname, dur);
            }
            utilTime += dur;
        }
        todrec.put("Others", todrec.get("Others") - utilTime);


        if (todrec.size() > 1) {
            CategorySeries distributionSeries = new CategorySeries("Overall Comparison of Parent Tasks");

            for (Map.Entry<String, Long> entry : todrec.entrySet()) {
                // Adding a slice with its values and name to the Pie Chart
                distributionSeries.add(entry.getKey(), entry.getValue());
            }

            // Instantiating a renderer for the Pie Chart
            DefaultRenderer defaultRenderer = new DefaultRenderer();
            for (int i = 0; i < todrec.size(); i++) {
                SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
                seriesRenderer.setColor(colors[i % 8]);
                seriesRenderer.setDisplayChartValues(true);
                // Adding a renderer for a slice
                defaultRenderer.addSeriesRenderer(seriesRenderer);
            }

            defaultRenderer.setChartTitle("Overall Pie Chart");
            defaultRenderer.setChartTitleTextSize(20);
            defaultRenderer.setZoomButtonsVisible(false);
            defaultRenderer.setLabelsColor(Color.BLACK);

            no_data.setVisibility(View.GONE);
            GraphicalView mChart = ChartFactory.getPieChartView(getActivity(), distributionSeries, defaultRenderer);
            chartContainer.addView(mChart);
        } else {
            no_data.setText("No data is available for this range");
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.time_range:
                showPopUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void showPopUp() {
        PopupMenu popup = new PopupMenu(getActivity(), tvTimeRange);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.time_ranges, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }


    public void rangeCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = null;
        view = inflater.inflate(R.layout.custom_dialog, null);
        from_date = (EditText) view.findViewById(R.id.from_date);
        to_date = (EditText) view.findViewById(R.id.to_date);

        builder.setTitle("Custom Range");

        builder.setView(view)

                .setPositiveButton("Done", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertdialog = builder.create();
        alertdialog.show();

        from_date.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Show your popup here
                    fromDatePickerDialog.show();
                    //prevent keyboard to start loading, which shifts the dialog up and then down again
                    //v.clearFocus();
                }
            }
        });

        to_date.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Show your popup here
                    toDatePickerDialog.show();
                    //prevent keyboard to start loading, which shifts the dialog up and then down again
                    //v.clearFocus();
                }
            }
        });

        final Calendar cal1 = Calendar.getInstance();

        fromDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal1.set(Calendar.YEAR, year);
                cal1.set(Calendar.MONTH, monthOfYear);
                cal1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                cal1.set(Calendar.HOUR_OF_DAY, 0);
                cal1.set(Calendar.MINUTE, 0);
                cal1.set(Calendar.SECOND, 0);
                cal1.set(Calendar.MILLISECOND, 0);
                startDate = cal1;
                from_date.setText(format.format(startDate.getTime()));
            }

        }, cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH));

        final Calendar cal2 = Calendar.getInstance();

        toDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal2.set(Calendar.YEAR, year);
                cal2.set(Calendar.MONTH, monthOfYear);
                cal2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                cal2.set(Calendar.HOUR_OF_DAY, 23);
                cal2.set(Calendar.MINUTE, 59);
                cal2.set(Calendar.SECOND, 59);
                cal2.set(Calendar.MILLISECOND, 999);
                endDate = cal2;
                to_date.setText(format.format(endDate.getTime()));
            }

        }, cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.statistics, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public long getLastMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public long getLastMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public long getMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getLastWeekStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getLastWeekEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public long getWeekStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getWeekEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, 1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public long getYesterdayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getYesterdayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public long getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}