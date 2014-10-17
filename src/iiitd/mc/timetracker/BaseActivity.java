package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.NavigationAdapter;
import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BaseActivity extends ActionBarActivity implements OnItemClickListener{
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private NavigationAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
	}
	
	public void navigationDisplay()
	{
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#697848")));
		actionBar.setIcon(getWallpaper());
		mTitle = "Time Tracker";
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                actionBar.setTitle(mTitle);
            }
 
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(mTitle);
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
	    	Intent task = new Intent(this,New_Task.class);
		    startActivity(task);
	    	/*fragment = new NavigationOptions();
	    	transaction.replace(R.id.fragment1, fragment);
	    	transaction.addToBackStack(null);
	    	transaction.commit();*/
	    	break;
	    /*case 1: 
	    	//Intent list_task  = new Intent(this,List_Task.class);
	    	task = new Intent(this,List_Task.class); 
		    startActivity(task);
		    break;*/
		    
	    }
	    
	    //fragmentManager.beginTransaction().add(R.id.frame, fragment).commit();
	    mDrawerList.setItemChecked(position,true);
		/*setTitle(NavigationAdapter.mNavigationTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);*/
        
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id){
		selectItem(position);
		// TODO Auto-generated method stub
		
	}
	
}
