package iiitd.mc.timetracker;

<<<<<<< HEAD
import iiitd.mc.timetracker.adapter.NavigationAdapter;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
=======
import java.util.List;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.*;
>>>>>>> f28f19228cd86ecdfda543e2c77d1f18479c7665
import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
<<<<<<< HEAD
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements OnItemClickListener{
=======
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;

/**
 * The Main Activity of the timetracker application
 * @author gullal
 *
 */
public class MainActivity extends ActionBarActivity {
>>>>>>> f28f19228cd86ecdfda543e2c77d1f18479c7665

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private NavigationAdapter myAdapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
<<<<<<< HEAD
		navigationDisplay();
=======
		
		AutoCompleteTextView autoTv = (AutoCompleteTextView) findViewById(R.id.taskSelectionBox);
		
		ITaskSuggestor suggester = new MainTaskSuggestor();
		List<String> suggestedTasks = suggester.getTaskStrings();
		CustomArrayAdapter adapter = new CustomArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestedTasks);
		autoTv.setAdapter(adapter);
		
		autoTv.setThreshold(0);
		autoTv.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					((AutoCompleteTextView)view).showDropDown();
				}
		});
>>>>>>> f28f19228cd86ecdfda543e2c77d1f18479c7665
	}

	
	public void navigationDisplay()
	{
		actionBar = getActionBar();
		actionBar.setIcon(getWallpaper());
		mTitle = "Time Tracker";
		 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
 
        // Set the adapter for the list view
        /*mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mNavigationTitles));*/
        //Set the list's click listener
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
        //Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
 
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(mNavigationTitles[position]);
        //mDrawerLayout.closeDrawer(mDrawerList);
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
 
    /*private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }*/

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//Toast.makeText(this, mNavigationTitles[position]+ "was selected", Toast.LENGTH_LONG).show();
		selectItem(position);
		// TODO Auto-generated method stub
		
	}
}
