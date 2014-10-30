package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.NavigationAdapter;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class BaseActivity extends ActionBarActivity implements OnItemClickListener{
	
	public DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    public CharSequence mTitle;
    public ActionBarDrawerToggle mDrawerToggle;
    public ActionBar actionBar;
    public NavigationAdapter myAdapter;
    public RelativeLayout mstartrelativelayout;
    
    protected FrameLayout frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		frame = (FrameLayout) findViewById(R.id.frame);
		
		navigationDisplay();
	}
	
	public void navigationDisplay()
	{
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#697848")));
		actionBar.setIcon(getWallpaper());
		mTitle = "Time Tracker";
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
            }
 
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            	super.onDrawerOpened(drawerView);
            	
                //actionBar.setTitle(mTitle);
                //invalidateOptionsMenu();
                //mDrawerLayout.bringToFront();
            }
        };
 
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
 
    /**
     * Swaps fragments in the main content view
     */
   private void selectItem(int position) {
	    
	  
        //Fragment fragment;
	    //Bundle argument = new Bundle();
	    //argument.putInt(NavigationOptions.option, position);
	    //fragment.setArguments(argument);
	    //FragmentTransaction transaction = getFragmentManager().beginTransaction();
	    switch(position){
	    
	    case 0:
	    	
	    	Intent new_task = new Intent(this,NewTaskActivity.class);
	    	mDrawerLayout.closeDrawer(mDrawerList);
		    startActivity(new_task);
		    break;
		    
	    case 1: 
	    	
	    	Intent list_task = new Intent(this,ListTasksActivity.class);
	    	mDrawerLayout.closeDrawer(mDrawerList);
		    startActivity(list_task);
		    break;
		    
	    case 2: 
		    
	    	Intent list_recordings = new Intent(this,ListRecordingsActivity.class);
	    	mDrawerLayout.closeDrawer(mDrawerList);
	    	startActivity(list_recordings);
	    	break;
	    	
	    case 3: 
	    	
	    	Intent statistics = new Intent(this, Statistics.class);
	    	mDrawerLayout.closeDrawer(mDrawerList);
	    	startActivity(statistics);
	    	break;
	    	
		default:
		    
	    }
	    
	    //fragmentManager.beginTransaction().add(R.id.frame, fragment).commit();
	    mDrawerList.setItemChecked(position,true);
		/*setTitle(NavigationAdapter.mNavigationTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);*/
        
    }
 
    @Override
    /*public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }*/
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id){
		selectItem(position);
		// TODO Auto-generated method stub
		
	}
    public void closedrawer(){
    
    }
	
}
