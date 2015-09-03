package iiitd.mc.timetracker.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Recording;
import iiitd.mc.timetracker.model.Task;
import iiitd.mc.timetracker.view.component.TaskAutoCompleteTextView;

public class EditRecordingActivity extends Activity {
    static final String EXTRA_RECORDING_ID = "recordingId";
    private Recording recording;
    private boolean isNewRecording;

    private TaskAutoCompleteTextView etTaskName;

    private EditText etStartTime;
    private EditText etStartDate;
    private EditText etStopTime;
    private EditText etStopDate;
    private DateFormat formatDate;
    private DateFormat formatTime;
    private Calendar calStart;
    private Calendar calEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recording);

        etTaskName = (TaskAutoCompleteTextView) findViewById(R.id.taskSelectionBoxRecording);
        etStartTime = (EditText) findViewById(R.id.editTextStartRecordingTime);
        etStartDate = (EditText) findViewById(R.id.editTextStartRecordingDate);
        etStopTime = (EditText) findViewById(R.id.editTextStopRecordingTime);
        etStopDate = (EditText) findViewById(R.id.editTextStopRecordingDate);

        etStartDate.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Show your popup here
                    showDatePickerStart(v);
                    //prevent keyboard to start loading, which shifts the dialog up and then down again
                    v.clearFocus();
                }
            }
        });
        etStopDate.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Show your popup here
                    showDatePickerStop(v);
                    //prevent keyboard to start loading, which shifts the dialog up and then down again
                    v.clearFocus();
                }
            }
        });
        etStartTime.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Show your popup here
                    showTimePickerStart(v);
                    //prevent keyboard to start loading, which shifts the dialog up and then down again
                    v.clearFocus();
                }
            }
        });
        etStopTime.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Show your popup here
                    showTimePickerStop(v);
                    //prevent keyboard to start loading, which shifts the dialog up and then down again
                    v.clearFocus();
                }
            }
        });


        formatTime = android.text.format.DateFormat.getTimeFormat(this);
        formatDate = android.text.format.DateFormat.getDateFormat(this);

        recording = null;
        calStart = Calendar.getInstance();
        calEnd = Calendar.getInstance();
        calEnd.add(Calendar.MINUTE, 1);

        isNewRecording = true;
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_RECORDING_ID)) {
            IDatabaseController db = ApplicationHelper.getDatabaseController();
            db.open();
            recording = db.getRecording(intent.getLongExtra(EXTRA_RECORDING_ID, 0));
            db.close();

            if (recording != null) {
                isNewRecording = false;
                etTaskName.setText(recording.getTask().getNameFull());

                calStart.setTime(recording.getStart());
                calEnd.setTime(recording.getEnd());
                updateTimeViews();
            }
        }

        if (isNewRecording) {
            //set start time to end time of last recording
            IDatabaseController db = ApplicationHelper.getDatabaseController();
            db.open();
            List<Recording> recordings = db.getRecordings(1);
            db.close();
            if (!recordings.isEmpty()) {
                Recording last = recordings.get(0);
                calStart.setTime(last.getEnd());
                Calendar now = new GregorianCalendar();
                calEnd.setTime(now.getTime());
                updateTimeViews();
            }

            recording = new Recording();
        }
        getActionBar().setIcon(R.mipmap.ic_launcher);
    }

    private void updateTimeViews() {
        etStartTime.setText(formatTime.format(calStart.getTime()));
        etStartDate.setText(formatDate.format(calStart.getTime()));
        etStopTime.setText(formatTime.format(calEnd.getTime()));
        etStopDate.setText(formatDate.format(calEnd.getTime()));
    }

    public void onSave(View view) {
        // Get the task instance that corresponds to the String entered by the user and save it
        etTaskName.createTask(new TaskAutoCompleteTextView.OnTaskCreatedListener() {
                                  @Override
                                  public void onTaskCreated(Task newTask) {
                                      save(newTask);
                                  }
                              },
                null);
    }

    private void save(Task task) {
        recording.setTask(task);
        recording.setStart(calStart.getTime());
        recording.setEnd(calEnd.getTime());

        // save to database
        IDatabaseController db = ApplicationHelper.getDatabaseController();
        db.open();
        if (isNewRecording) {
            db.insertRecording(recording);
        } else {
            db.updateRecording(recording);
        }
        db.close();

        // close Activity
        Toast.makeText(this, R.string.toast_recording_saved, Toast.LENGTH_LONG).show();
        finish();
    }


    public void showDatePickerStart(View view) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(calStart.getTime());

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //check consistency: recording's start before end?
                if (!cal.before(calEnd)) {
                    // keep duration between start/end and change the end time accordingly
                    int duration = (int) (calEnd.getTimeInMillis() - calStart.getTimeInMillis());

                    calEnd.setTime(cal.getTime());
                    calEnd.add(Calendar.MILLISECOND, duration);

                    //TODO: somehow highlight the changed end time?
                }
                calStart = cal;

                updateTimeViews();
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void showDatePickerStop(View view) {
        final Calendar cal = calEnd;
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //check consistency: recording's end after start?
                if (!calEnd.after(calStart)) {
                    // reset start to a date just before end
                    calStart.setTime(calEnd.getTime());
                    calStart.add(Calendar.DAY_OF_MONTH, -1); //date was changed, so difference will be 1 day

                    //TODO: somehow highlight the changed start time?
                }

                updateTimeViews();
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void showTimePickerStart(View view) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(calStart.getTime());

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker picker, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);

                //check consistency: recording's start before end?
                if (!cal.before(calEnd)) {
                    // keep duration between start/end and change the end time accordingly
                    long duration = (calEnd.getTimeInMillis() - calStart.getTimeInMillis());

                    calEnd.setTime(cal.getTime());
                    calEnd.add(Calendar.MILLISECOND, (int) duration);

                    //TODO: somehow highlight the changed end time?
                }
                calStart = cal;

                updateTimeViews();
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void showTimePickerStop(View view) {
        final Calendar cal = calEnd;
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker picker, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);

                //check consistency: recording's end after start?
                if (!calEnd.after(calStart)) {
                    // reset start to a time just before end
                    calStart.setTime(calEnd.getTime());
                    calStart.add(Calendar.MINUTE, -1); //time was changed, so difference will be 1 minute

                    //TODO: somehow highlight the changed start time?
                }

                updateTimeViews();
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        dialog.show();
    }


    public void onCancel(View view) {
        finish();
    }
}