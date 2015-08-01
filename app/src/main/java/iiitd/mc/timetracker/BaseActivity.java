package iiitd.mc.timetracker;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.DateFormat;

import iiitd.mc.timetracker.adapter.NavigationAdapter;

public class BaseActivity extends Activity implements OnItemClickListener {

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
        getActionBar().setIcon(R.drawable.ic_launchertimeturner);
        navigationDisplay();
    }

    private void navigationDisplay() {
        actionBar = getActionBar();
        actionBar.setIcon(R.drawable.ic_launchertimeturner);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        return mDrawerToggle.onOptionsItemSelected(item);
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
        switch (position) {
            case 0:
                Intent home_activity = new Intent(this, MainActivity.class);
                home_activity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                home_activity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivity(home_activity);
                break;
            case 1:
                Intent new_task = new Intent(this, EditTaskActivity.class);
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
            case 5:
                Intent settings = new Intent(this, SettingsActivity.class);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivity(settings);
                break;
            default:
        }

        mDrawerList.setItemChecked(position, true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        selectItem(position);
    }


}