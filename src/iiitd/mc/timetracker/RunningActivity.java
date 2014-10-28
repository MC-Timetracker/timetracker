package iiitd.mc.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;

public class RunningActivity extends MainActivity {
	
	RelativeLayout relativelayoutrunningactivity;
	Button btnStop,btnPause,btnResume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running);
		//TODO: Chronometer should not count independent of taskRecorder but use that timing instead.
				chronometer=(Chronometer) findViewById(R.id.chronometer);
				chronometer.setBase(SystemClock.elapsedRealtime());
				chronometer.start();
				navigationDisplay();
	}
	
	/**
	 * Handles all UI changes when recording is stopped.
	 */
	public void Stop(View view)
	{
		chronometer.stop();
		//Intent intent = new Intent(getApplicationContext(),RunningActivity.class);
		//startActivity(intent);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.running, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void closedrawer(){
		
		//brings relative layout of new task to the front on closing the drawer
		relativelayoutrunningactivity = (RelativeLayout) findViewById(R.id.pausestop); 
    	relativelayoutrunningactivity.bringToFront();
    	
    }
}
