package iiitd.mc.timetracker;

import java.util.concurrent.TimeUnit;

import iiitd.mc.timetracker.data.TaskRecorderService;
import iiitd.mc.timetracker.data.TaskRecorderService.TaskRecorderBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;

public class RunningActivity extends BaseActivity {
	
	Button btnStop,btnPause,btnResume;
    Chronometer chronometer;
    long stoptime=0;
    
    TaskRecorderService taskRecorder;
    boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_running);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_running, null));
		
		btnPause =(Button) findViewById(R.id.btnPause);
		btnResume =(Button) findViewById(R.id.btnResume);
		
		//TODO: Chronometer should not count independent of taskRecorder but use that timing instead.
		chronometer=(Chronometer) findViewById(R.id.chronometer);
		chronometer.setBase(SystemClock.elapsedRealtime());
		chronometer.start();
		
		navigationDisplay();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// bind to TaskRecorderService
        Intent intent = new Intent(this, TaskRecorderService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
	}
	
	/**
	 * Handles all UI changes when recording is stopped.
	 */
	public void Stop(View view)
	{
		chronometer.stop();
		
		if (mBound) {
			taskRecorder.stopRecording();
		}
		
		finish();
	}
	
	public void Pause(View view)
	{
		if (mBound) {
			taskRecorder.pauseRecording();
		}
		
		stoptime=chronometer.getBase()-SystemClock.elapsedRealtime();
		chronometer.stop();
		btnPause.setVisibility(View.INVISIBLE);
		btnResume.setVisibility(View.VISIBLE);
	}
    
	public void Resume(View view){
		if (mBound) {
			taskRecorder.resumeRecording();
		}
		
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
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	TaskRecorderBinder binder = (TaskRecorderBinder) service;
            taskRecorder = binder.getService();
            mBound = true;
            
            //close this Activity if no task is currently recorded
            if(!taskRecorder.isRecording())
            {
            	finish();
            } 
            else
            {
	            //update view & timer based on actual recording time
	            chronometer.setBase(SystemClock.elapsedRealtime()-taskRecorder.getCurrentRecording().getDuration(TimeUnit.MILLISECONDS));
	    		chronometer.start();
	    		//TODO set task name: taskRecorder.getCurrentRecording().toString();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
