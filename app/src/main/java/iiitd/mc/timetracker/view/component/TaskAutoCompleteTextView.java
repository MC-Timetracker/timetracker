package iiitd.mc.timetracker.view.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.TaskRecorderService;
import iiitd.mc.timetracker.model.Task;
import iiitd.mc.timetracker.suggestor.ITaskSuggestor;
import iiitd.mc.timetracker.suggestor.SuggestedTask;
import iiitd.mc.timetracker.view.adapter.CustomArrayAdapter;

public class TaskAutoCompleteTextView extends AutoCompleteTextView implements OnGestureListener {
    private ITaskSuggestor suggestor;
    private CustomArrayAdapter taskListAdapter;
    private List<SuggestedTask> suggestedTasks = new ArrayList<>();

    private Context context;

    private GestureDetectorCompat mDetector;


    public TaskAutoCompleteTextView(Context context) {
        super(context);

        this.context = context;
        initTaskAutocomplete();
    }

    public TaskAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        initTaskAutocomplete();
    }

    public TaskAutoCompleteTextView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;
        initTaskAutocomplete();
    }


    public void setTaskSuggestor(ITaskSuggestor taskSuggestor) {
        this.suggestor = taskSuggestor;

        updateSuggestionList();
    }


    /**
     * Initial setup of the autocomplete dropdown task selection text box.
     */
    private void initTaskAutocomplete() {
        setTaskSuggestor(ApplicationHelper.getMainTaskSuggestor());

        mDetector = new GestureDetectorCompat(context, this);
        //mDetector.setOnDoubleTapListener(this);


        taskListAdapter = new CustomArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, suggestedTasks);
        this.setAdapter(taskListAdapter);

        updateSuggestionList();

        this.setThreshold(0);
        this.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                updateSuggestionList();
                showDropDown();
            }
        });
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Show your popup here
                    updateSuggestionList();
                    showDropDown();
                } else {
                    //Hide your popup here?
                    dismissDropDown();
                }
            }
        });


        //TODO: necessary? getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Hide keyboard when an item is selected
                InputMethodManager inm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        });
    }


    /**
     * Reload the list of tasks displayed in the autocomplete dropdown.
     */
    private void updateSuggestionList() {
        if (suggestor != null) {
            //TODO: fix dropdown only showing after first typing
            suggestedTasks.clear();
            suggestedTasks.addAll(suggestor.getSuggestedTasks());
            //suggestedTasks = suggestor.getSuggestedTasks();
            //taskListAdapter = new CustomArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, suggestedTasks);
            //this.setAdapter(taskListAdapter);
            //taskListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Get the task object corresponding to the currently selected/typed task name.
     *
     * @return Returns an instance of the selected Task or null if no task with that name exists.
     */
    public Task getTask() {
        String text = this.getText().toString();
        return TaskRecorderService.getTaskFromString(text);
    }

    /**
     * Create a task with the given name after user confirmation. If a task with the exact full name already exists, no prompt is displayed and success is called directly.
     *
     * @param success  Callback to be executed after the task was created successfully. Can be null if no further action is desired.
     * @param canceled Callback to be executed if the user canceled the creation of the task, the newTask parameter when onTaskCreated is called will be null. Can be null.
     */
    public void createTask(final OnTaskCreatedListener success, final OnTaskCreatedListener canceled) {
        final String text = this.getText().toString();

        // check if task exists
        Task task = TaskRecorderService.getTaskFromString(text);
        if (task != null) {
            success.onTaskCreated(task);
            return;
        }


        if (TaskRecorderService.isValidTaskName(text)) {
            // if task does not exist, ask the user if it should be created
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog dialog = builder.setMessage(context.getString(R.string.promptCreateNewTask) + " (" + text + ")")
                    .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Create new task and start recording it
                            Task newTask = TaskRecorderService.createTaskFromString(text);
                            if (newTask != null) {
                                updateSuggestionList();
                                if (success != null)
                                    success.onTaskCreated(newTask);
                            }
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (canceled != null)
                                canceled.onTaskCreated(null);
                        }
                    })
                    .create();
            dialog.show();
            return; // further action is handled in dialog event handlers
        } else {
            Toast.makeText(context, R.string.prompt_taskname_invalid, Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnTaskCreatedListener {
        void onTaskCreated(Task newTask);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //left swipe
                this.setText("");
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //right swipe
                SuggestedTask s = this.taskListAdapter.getItem(0);
                this.setText(s.getTask().toString());
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }
}
