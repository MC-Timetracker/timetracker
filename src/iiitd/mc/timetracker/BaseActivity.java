package iiitd.mc.timetracker;


import java.text.DateFormat;
import java.util.Calendar;

import iiitd.mc.timetracker.adapter.NavigationAdapter;
import android.app.ActionBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class BaseActivity extends Activity implements OnItemClickListener, OnMenuItemClickListener {

	
	static int timeRangeId = 1;
	int colors[] = {Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.MAGENTA, Color.RED, Color.YELLOW};
	static Calendar startDate;
	static Calendar endDate;
	EditText from_date;
	EditText to_date;
	DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
	DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    CharSequence mTitle;
    ActionBarDrawerToggle mDrawerToggle;
    ActionBar actionBar;
    NavigationAdapter myAdapter;
    RelativeLayout mstartrelativelayout;
    DateFormat format;
    
    protected FrameLayout frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		frame = (FrameLayout) findViewById(R.id.frame);
		format = android.text.format.DateFormat.getDateFormat(this);
		navigationDisplay();
	}
	
	private void navigationDisplay()
	{
		actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_launcher);
		mTitle = getString(R.string.app_name);
		
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mstartrelativelayout = (RelativeLayout) findViewById(R.id.root_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        myAdapter = new NavigationAdapter(this);
        mDrawerList.setAdapter(myAdapter);
        mDrawerList.setOnItemClickListener(this);
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
 
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            	super.onDrawerClosed(view);
            	
                //actionBar.setTitle(mTitle);
            	invalidateOptionsMenu();
            }
 
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            	super.onDrawerOpened(drawerView);
            	
                //actionBar.setTitle(mTitle);
                invalidateOptionsMenu();
                mDrawerLayout.bringToFront();
            }
        };
 
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
	}
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
 
    /**
     * Swaps fragments in the main content view
     */
   private void selectItem(int position) {
	    switch(position)
		{
		case 0:
			Intent home_activity = new Intent(this, MainActivity.class);
			home_activity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			home_activity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mDrawerLayout.closeDrawer(mDrawerList);
			startActivity(home_activity);
			break;
		case 1:
			Intent new_task = new Intent(this, NewTaskActivity.class);
			new_task.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			new_task.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mDrawerLayout.closeDrawer(mDrawerList);
			startActivity(new_task);
			break;
		case 2:
			Intent list_task = new Intent(this, ListTasksActivity.class);
			list_task.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			list_task.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mDrawerLayout.closeDrawer(mDrawerList);
			startActivity(list_task);
			break;
		case 3:
			Intent list_recordings = new Intent(this,
					ListRecordingsActivity.class);
			list_recordings.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			list_recordings.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mDrawerLayout.closeDrawer(mDrawerList);
			startActivity(list_recordings);
			break;
		case 4:
			Intent statistics = new Intent(this, OverallStats.class);
			statistics.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			statistics.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mDrawerLayout.closeDrawer(mDrawerList);
			startActivity(statistics);
			break;
		default:
	    }
	    
	    mDrawerList.setItemChecked(position,true);
    }
 
    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id){
		selectItem(position);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        switch(item.getItemId())
        {
        	case R.id.time_range:
        		View v = findViewById(R.id.time_range);
        		showPopUp(v);
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
        
    }
    
    public void showPopUp(View v)
    {
    	PopupMenu popup = new PopupMenu(this,v);
    	MenuInflater inflater = popup.getMenuInflater();
    	inflater.inflate(R.menu.time_ranges, popup.getMenu());
    	popup.setOnMenuItemClickListener(this);
        popup.show();
    }
    
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    		case R.id.today:
    			timeRangeId = 1;
    			return true;
    		case R.id.yesterday:
    			timeRangeId = 2;
    			return true;
    		case R.id.thisweek:
    			timeRangeId = 3;
    			return true;
    		case R.id.lastweek:
    			timeRangeId = 4;
    			return true;
    		case R.id.thismonth:
    			timeRangeId = 5;
    			return true;
    		case R.id.lastmonth:
    			timeRangeId = 6;
    			return true;
    		case R.id.custom:
    			timeRangeId = 7;
    			rangeCustomDialog();
    			return true;
    		default:
    			return false;
    	}
    }
    
    
    public void rangeCustomDialog()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	LayoutInflater inflater = this.getLayoutInflater();
    	View view = null;
    	view = inflater.inflate(R.layout.custom_dialog, null);
    	from_date = (EditText) view.findViewById(R.id.from_date);
    	to_date = (EditText) view.findViewById(R.id.to_date);
    	
    	builder.setTitle("Custom Range");
    	
    	builder.setView(view)
    	
    	.setPositiveButton("Done", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{		
				dialog.dismiss();			
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});
    	
    	AlertDialog alertdialog = builder.create();    	
    	alertdialog.show();
    	
    	from_date.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
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
                if(hasFocus)
                {
                	//Show your popup here
                	toDatePickerDialog.show();
                	//prevent keyboard to start loading, which shifts the dialog up and then down again
                	//v.clearFocus();
                }
            }
        });
    	
    	final Calendar cal1 = Calendar.getInstance();
    	
        fromDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
 
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            	cal1.set(Calendar.YEAR, year);
				cal1.set(Calendar.MONTH, monthOfYear);
				cal1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				cal1.set(Calendar.HOUR_OF_DAY, 0);
		        cal1.set(Calendar.MINUTE, 0);
		        cal1.set(Calendar.SECOND, 0);
		        cal1.set(Calendar.MILLISECOND,0);
				startDate = cal1;
                from_date.setText(format.format(startDate.getTime()));
            }
 
        },cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH));
        
        final Calendar cal2 = Calendar.getInstance();
        
        toDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
 
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            	cal2.set(Calendar.YEAR, year);
				cal2.set(Calendar.MONTH, monthOfYear);
				cal2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				cal2.set(Calendar.HOUR_OF_DAY, 23);
		        cal2.set(Calendar.MINUTE, 59);
		        cal2.set(Calendar.SECOND, 59);
		        cal2.set(Calendar.MILLISECOND,999);
				endDate = cal2;
                to_date.setText(format.format(endDate.getTime()));
            }
 
        },cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH));
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
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
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
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
	
	public long getStartOfDay()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
	}
	
	public long getEndOfDay()
	{
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
	}
}
