package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.NavigationAdapter;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class BaseActivity extends Activity implements OnItemClickListener, ActionBar.TabListener {
	
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
 
        return super.onOptionsItemSelected(item);
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
