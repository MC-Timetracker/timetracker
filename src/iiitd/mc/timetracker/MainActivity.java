package iiitd.mc.timetracker;


import iiitd.mc.timetracker.adapter.NavigationAdapter;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import java.util.List;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.*;
import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;


/**
 * The Main Activity of the timetracker application
 * @author gullal
 *
 */
public class MainActivity extends BaseActivity {
    
    Button btnStart,btnStop,btnPause,btnResume;
    private Chronometer chronometer;
    long stoptime=0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnStart =(Button) findViewById(R.id.btnStart);
		btnStop =(Button) findViewById(R.id.btnStop);
		btnStart.setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				setContentView(R.layout.pausestop);	
				chronometer=(Chronometer) findViewById(R.id.chronometer);
				chronometer.setBase(SystemClock.elapsedRealtime());
				chronometer.start();
					
			}
		});
		
		navigationDisplay();
		
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
	}
	public void Stop(View view)
	{
		chronometer.stop();
	}
	
	public void Pause(View view)
	{
		btnPause =(Button) findViewById(R.id.btnPause);
		btnResume =(Button) findViewById(R.id.btnResume);
		stoptime=chronometer.getBase()-SystemClock.elapsedRealtime();
		chronometer.stop();
		btnPause.setVisibility(View.INVISIBLE);
		btnResume.setVisibility(View.VISIBLE);
	}
    
	public void Resume(View view){
		btnResume =(Button) findViewById(R.id.btnResume);
		chronometer.setBase(SystemClock.elapsedRealtime()+stoptime);
		chronometer.start();
		btnPause.setVisibility(View.VISIBLE);
		btnResume.setVisibility(View.INVISIBLE);
	
	}

}	