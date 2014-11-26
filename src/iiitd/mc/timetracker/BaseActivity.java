package iiitd.mc.timetracker;

import java.util.Calendar;

import iiitd.mc.timetracker.adapter.NavigationAdapter;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class BaseActivity extends ActionBarActivity implements OnItemClickListener, ActionBar.TabListener, OnMenuItemClickListener {
	
	public static int timeRangeId = 1;
	DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    CharSequence mTitle;
    ActionBarDrawerToggle mDrawerToggle;
    ActionBar actionBar;
    NavigationAdapter myAdapter;
    RelativeLayout mstartrelativelayout;
    
    protected FrameLayout frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		frame = (FrameLayout) findViewById(R.id.frame);
		
		navigationDisplay();
	}
	
	private void navigationDisplay()
	{
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#697848")));
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
			Intent statistics = new Intent(this, Statistics.class);
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
    	
    	builder.setTitle("Custom Range");
    	
    	builder.setView(inflater.inflate(R.layout.custom_dialog, null))
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
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
		// TODO Auto-generated method stub
		
	}
}
