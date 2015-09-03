package iiitd.mc.timetracker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.TaskRecorderService;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Task;
import iiitd.mc.timetracker.view.component.TaskAutoCompleteTextView;

/**
 * This activity shows the parent, task name, description and probably the recordings w.r.t
 * to the task to the user.
 */
public class EditTaskActivity extends Activity {
    private EditText etTaskname, etDescription;
    private TaskAutoCompleteTextView acParent;

    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        acParent = (TaskAutoCompleteTextView) findViewById(R.id.ac_taskparent);
        etTaskname = (EditText) findViewById(R.id.et_taskname);
        etDescription = (EditText) findViewById(R.id.et_taskdescription);

        btnUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        long taskId = intent.getLongExtra("taskid", -1);
        IDatabaseController db = ApplicationHelper.getDatabaseController();
        db.open();
        task = db.getTask(taskId);
        db.close();

        if (task != null) {
            if (task.getParent() != null)
                acParent.setText(task.getParent().getNameFull());
            etTaskname.setText(task.getName());
            etDescription.setText(task.getDescription());
        }


        getActionBar().setIcon(R.mipmap.ic_launcher);
    }

    /**
     * Save the Task by updating/inserting it into the database.
     */
    private void save() {
        String taskname = etTaskname.getText().toString();
        String parentTaskname = acParent.getText().toString();
        String fullTaskname = !parentTaskname.isEmpty() ? parentTaskname + "." + taskname : taskname;

        if (!TaskRecorderService.isValidTaskName(taskname)) {
            // empty string or otherwise not allowed name
            Toast.makeText(this, R.string.prompt_taskname_invalid, Toast.LENGTH_LONG).show();
            return;
        }

        Task e = TaskRecorderService.getTaskFromString(fullTaskname);
        if (e != null && e != task) {
            // Task exists and is not the one we are currently editing
            Toast.makeText(this, R.string.prompt_taskname_exists, Toast.LENGTH_LONG).show();
            return;
        }


        boolean create = false;
        if (task == null) {
            create = true;
            task = new Task();
        }


        task.setName(taskname);
        task.setDescription(etDescription.getText().toString());

        Task parent = null;
        if (!parentTaskname.isEmpty()) {
            // parent name not empty - get/create parent task
            parent = acParent.getTask();
            if (parent == null) {
                acParent.createTask(new TaskAutoCompleteTextView.OnTaskCreatedListener() {
                                        @Override
                                        public void onTaskCreated(Task newTask) {
                                            // restart save, parent will exist now
                                            save();
                                        }
                                    },
                        null);

                // abort for now, will be continued when callback returns
                return;
            }
        }
        task.setParent(parent);


        IDatabaseController db = ApplicationHelper.getDatabaseController();
        db.open();
        if (create)
            db.insertTask(task);
        else
            db.updateTask(task);
        db.close();


        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

}