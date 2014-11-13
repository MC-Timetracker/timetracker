package iiitd.mc.timetracker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.ITaskSuggestor;
import iiitd.mc.timetracker.context.MainTaskSuggestor;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.data.TaskRecorderService;
import iiitd.mc.timetracker.helper.IDatabaseController;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditRecordingActivity extends BaseActivity
{
	static final String EXTRA_RECORDING_ID = "recordingId";
	Recording recording;
	boolean isNewRecording;
	
	private AutoCompleteTextView etTaskName;
	private ITaskSuggestor suggester;
	private List<String> suggestedTasks;
	private CustomArrayAdapter adapter;
	
	EditText etStartTime, etStartDate, etStopTime, etStopDate;
	DateFormat formatDate, formatTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_edit_recording);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_edit_recording, null));
		
        etTaskName = (AutoCompleteTextView) findViewById(R.id.taskSelectionBoxRecording);
		addTasksToAutoView();
		etTaskName.setThreshold(0);
		etTaskName.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					((AutoCompleteTextView)view).showDropDown();
				}
		});
        
		etStartTime = (EditText) findViewById(R.id.editTextStartRecordingTime);
		etStartDate = (EditText) findViewById(R.id.editTextStartRecordingDate);
		etStopTime = (EditText) findViewById(R.id.editTextStopRecordingTime);
		etStopDate = (EditText) findViewById(R.id.editTextStopRecordingDate);
		View.OnFocusChangeListener fcDate = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                	//Show your popup here
                	showDatePicker(v);
                	//prevent keyboard to start loading, which shifts the dialog up and then down again
                	v.clearFocus();
                }
                else
                {
                	//Hide your popup here?
                }
            }
        };
        View.OnFocusChangeListener fcTime = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                	//Show your popup here
                	showTimePicker(v);
                	//prevent keyboard to start loading, which shifts the dialog up and then down again
                	v.clearFocus();
                }
                else
                {
                	//Hide your popup here?
                }
            }
        };
        etStartDate.setOnFocusChangeListener(fcDate);
        etStopDate.setOnFocusChangeListener(fcDate);
        etStartTime.setOnFocusChangeListener(fcTime);
        etStopTime.setOnFocusChangeListener(fcTime);
        
		
		formatTime = android.text.format.DateFormat.getTimeFormat(this);
		formatDate = android.text.format.DateFormat.getDateFormat(this);
		
		Intent intent = getIntent();
		recording = null;
		isNewRecording = true;
		if(intent.hasExtra(EXTRA_RECORDING_ID))
		{
			IDatabaseController db = ApplicationHelper.createDatabaseController();
			db.open();
			recording = db.getRecording(intent.getLongExtra(EXTRA_RECORDING_ID, 0));
			db.close();
			
			if(recording != null)
			{
				isNewRecording = false;
				etTaskName.setText(recording.getTask().getNameFull());
				etStartTime.setText(formatTime.format(recording.getStart()));
				etStartDate.setText(formatDate.format(recording.getStart()));
				etStopTime.setText(formatTime.format(recording.getEnd()));
				etStopDate.setText(formatDate.format(recording.getEnd()));
			}
		}
		
		if(isNewRecording)
		{
			recording = new Recording();
		}
	}
	
	public void onSave(View view)
	{
		// get data from UI
		
		// Get the task instance that corresponds to the String entered by the user
		final String sTask = etTaskName.getText().toString();
		Task task = TaskRecorderService.getTaskFromString(sTask);
		if (task == null)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			if(!TaskRecorderService.isValidTaskName(sTask))
			{
				AlertDialog dialog = builder.setMessage(R.string.prompt_taskname_invalid)
			       .create();
				dialog.show();
			}
			else
			{
				// task does not exist, ask the user if it should be created
				AlertDialog dialog = builder.setMessage(R.string.promptCreateNewTask)
			       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Create new task and start recording it
			        	   Task newTask = TaskRecorderService.createTaskFromString(sTask);
			        	   if(newTask != null)
			        	   {
				        	   addTasksToAutoView();
				        	   onSave(null);
			        	   }
			           }
			       })
			       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Do nothing
			           }
			       })
			       .create();
				dialog.show();
			}
			return; // abort onSave(), further action is handled in dialog event handlers
		}
		
		//get times
		Date start, stop;
		String pattern = ((SimpleDateFormat)formatDate).toPattern() + " " + ((SimpleDateFormat)formatTime).toPattern();
		DateFormat df = new SimpleDateFormat(pattern);
		try
		{
			start = df.parse(etStartDate.getText().toString()+" "+etStartTime.getText().toString());
			stop = df.parse(etStopDate.getText().toString()+" "+etStopTime.getText().toString());
		} catch (ParseException e)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog dialog = builder.setMessage(R.string.prompt_time_invalid)
					.setNeutralButton(R.string.cancel, null)
					.create();
			dialog.show();
			return; //abort saving
		}
		
		
		recording.setTask(task);
		recording.setStart(start);
		recording.setEnd(stop);
		
		// save to database
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		if(isNewRecording)
		{
			db.insertRecording(recording);
		}
		else
		{
			db.updateRecording(recording);
		}
		db.close();
		
		// close Activity
		Toast.makeText(this, R.string.toast_recording_saved, Toast.LENGTH_LONG).show();
		finish();
	}
	
	/*
	 * Adds tasks to the Auto Complete View for suggestions
	 */
	private void addTasksToAutoView()
	{
		suggester = new MainTaskSuggestor();
		suggestedTasks = suggester.getTaskStrings();
		adapter = new CustomArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestedTasks);
		etTaskName.setAdapter(adapter);		
	}
	
	public void showDatePicker(View view)
	{
		final EditText editTextRef =(EditText) view;
		
		int year = 0;
		int month = 0;
		int day = 0;
		try
		{
			Date date = formatDate.parse(editTextRef.getText().toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
		} catch (ParseException e)
		{
			// ignore
		}
		
		DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
			{			
				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth)
				{
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, year);
					cal.set(Calendar.MONTH, monthOfYear);
					cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					
					//TODO: check consistency (before/after recording's start/end
					
					String date = formatDate.format(cal.getTime());
					editTextRef.setText(date);
				}
			}, year, month, day);
		dialog.show();
	}
	public void showTimePicker(View view)
	{
		final EditText editTextRef =(EditText) view;
		
		int hour = 0;
		int minute = 0;
		try
		{
			Date date = formatTime.parse(editTextRef.getText().toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			hour = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
		} catch (ParseException e)
		{
			// ignore
		}
		
		TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
			{			
				@Override
				public void onTimeSet(TimePicker picker, int hourOfDay, int minute)
				{
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
					cal.set(Calendar.MINUTE, minute);
					
					//TODO: check consistency (before/after recording's start/end
					
					String time = formatTime.format(cal.getTime());
					editTextRef.setText(time);
				}
			}, hour, minute, true);
		dialog.show();
	}
	
	public void onCancel(View view)
	{
		finish();
	}
}
