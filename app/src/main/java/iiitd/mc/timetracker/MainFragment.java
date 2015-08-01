package iiitd.mc.timetracker;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import iiitd.mc.timetracker.data.RecorderListener;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.data.TaskRecorderService;
import iiitd.mc.timetracker.data.TaskRecorderService.TaskRecorderBinder;
import iiitd.mc.timetracker.helper.IDatabaseController;
import iiitd.mc.timetracker.view.TaskAutoCompleteTextView;


/**
 * The main UI of the application to start tracking of tasks.
 */
public class MainFragment extends Fragment implements RecorderListener {

    Intent recorderIntent;
    TaskRecorderService taskRecorder;
    boolean taskRecorderBound = false;
    boolean mBound = false;

    private View vRunning;
    private TaskAutoCompleteTextView tvTask;
    private TextView tvRecordingTask;
    private Button btnPause, btnStart, btnStop;
    private ListView recentAct;
    private ArrayAdapter<String> recentActAdapter;

    Chronometer chronometer;
    long stoptime = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_main, container, false);

        vRunning = (View) v.findViewById(R.id.running_layout);
        recentAct = (ListView) v.findViewById(R.id.recentLv);
        tvTask = (TaskAutoCompleteTextView) v.findViewById(R.id.taskSelectionBox);
        btnPause = (Button) v.findViewById(R.id.btnPause);
        btnStop = (Button) v.findViewById(R.id.btnStop);
        btnStart = (Button) v.findViewById(R.id.btnStart);
        chronometer = (Chronometer) v.findViewById(R.id.chronometer);
        tvRecordingTask = (TextView) v.findViewById(R.id.tv_recording_task);

        btnPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Pause(v);
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Start(v);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Stop(v);
            }
        });


        initRecentActList();

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        // bind to TaskRecorderService
        Intent intent = new Intent(getActivity(), TaskRecorderService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unbind from the service
        if (mBound) {
            taskRecorder.removeRecorderListener(this);
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initRecentActList();
    }


    /**
     * Event handler for click of stop button.
     *
     * @param view
     */
    public void Stop(View view) {
        chronometer.stop();

        if (mBound) {
            taskRecorder.stopRecording();
        }

        showRunningLayout(null);
    }

    /**
     * Event handler for click of pause button.
     *
     * @param view
     */
    public void Pause(View view) {
        if (mBound) {
            taskRecorder.pauseRecording();
        }
    }

    /**
     * Event handler for click of start button.
     *
     * @param view
     */
    public void Start(View view) {
        // Get the task instance that corresponds to the String entered by the user and start recording it
        tvTask.createTask(new TaskAutoCompleteTextView.OnTaskCreatedListener() {
                              @Override
                              public void onTaskCreated(Task newTask) {
                                  startRecording(newTask);
                              }
                          },
                null);
    }


    @Override
    public void onRecordingStopped(Recording oldRec) {
        showRunningLayout(null);
    }

    @Override
    public void onRecordingStarted(Recording newRec) {
        showRunningLayout(newRec);
    }


    /**
     * Handle everything to actually start recording the task in the business logic layer with TaskRecorder.
     */
    public void startRecording(Task task) {
        // Start recording in a Service
        recorderIntent = new Intent(getActivity(), TaskRecorderService.class);
        recorderIntent.putExtra(TaskRecorderService.EXTRA_TASK_ID, task.getId());
        getActivity().startService(recorderIntent);

        //Layout visibilities are changed indirectly through onRecordingStarted
    }


    /**
     * Triggers visibility of the layout part showing information about the running Recording.
     *
     * @param currentRecording The recording to be displayed or null to hide all view elements.
     */
    private void showRunningLayout(Recording currentRecording) {
        if (currentRecording == null) {
            chronometer.stop();

            vRunning.setVisibility(View.GONE);
            btnStart.setText(R.string.button_start);
        } else {
            //update view & timer based on actual recording time
            chronometer.setBase(SystemClock.elapsedRealtime() - currentRecording.getDuration(TimeUnit.MILLISECONDS));
            chronometer.start();
            tvRecordingTask.setText(currentRecording.getTask().toString());

            if (currentRecording.getTask().equals(taskRecorder.getBreakTask())) {
                tvTask.setText(taskRecorder.getLastRecording().getTask().getNameFull());
                btnPause.setEnabled(false);
            } else {
                tvTask.setText("");
                btnPause.setEnabled(false);
            }

            vRunning.setVisibility(View.VISIBLE);
            btnStart.setText(R.string.button_start_switch);
        }
    }


    /**
     * Load a list of recent recordings
     */
    public void initRecentActList() {
        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();
        List<Recording> records = db.getRecordings((new Date()).getTime());
        db.close();

        List<String> recentTasks = new ArrayList<>();

        SimpleDateFormat dformat = new SimpleDateFormat("HH:mm");
        for (Recording rec : records) {
            String fullrec = rec.getTask().getNameFull() + "\n(" + dformat.format(rec.getStart()) + " - " + dformat.format(rec.getEnd()) + ")";
            recentTasks.add(fullrec);
        }

        recentActAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, recentTasks);

        recentAct.setAdapter(recentActAdapter);
    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TaskRecorderBinder binder = (TaskRecorderBinder) service;
            taskRecorder = binder.getService();
            mBound = true;

            //register for RecorderEvents
            taskRecorder.addRecorderListener(MainFragment.this);

            //close this Activity if no task is currently recorded
            if (!taskRecorder.isRecording()) {
                showRunningLayout(null);
            } else {
                showRunningLayout(taskRecorder.getCurrentRecording());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}