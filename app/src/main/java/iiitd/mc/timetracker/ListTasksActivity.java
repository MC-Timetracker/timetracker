package iiitd.mc.timetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import iiitd.mc.timetracker.adapter.ExpandableListAdapter;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.IDatabaseController;

public class ListTasksActivity extends BaseActivity {

    private ExpandableListView expListView;
    private List<Task> listHeader = new ArrayList<>();
    private HashMap<Task, List<Task>> listItems = new HashMap<>();
    private ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_list_main);
        // use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_list_tasks, null));

        // Get the ListView
        expListView = (ExpandableListView) findViewById(R.id.Explv);

        loadTasksList();

        listAdapter = new ExpandableListAdapter(this, listHeader, listItems);
        expListView.setAdapter(listAdapter);

        registerForContextMenu(expListView);
        getActionBar().setIcon(R.drawable.ic_launchertimeturner);

    }

    /**
     * Populate the list in the UI with the Tasks from the database.
     */
    public void loadTasksList() {
        listHeader.clear();
        listItems.clear();

        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();
        List<Task> tasks = db.getTasks();
        db.close();

        // We have to change the following implementation.
        // Probably make some changes in the database to efficiently fetch all the tasks.
        for (Task t : tasks) {
            if (t.getParent() == null) {
                listHeader.add(t);
            }
        }

        tasks.removeAll(listHeader);

        for (Task t : listHeader) {
            List<Task> templist = new ArrayList<>();
            for (Task t2 : tasks) {
                Task temp = t2.getParent();

                while (temp.getParent() != null) {
                    temp = temp.getParent();
                }

                if (temp.getId() == t.getId()) {
                    templist.add(t2);
                }
            }
            listItems.put(t, templist);
            tasks.removeAll(templist);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_tasks_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
        int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        long taskId = -1;
        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            taskId = listHeader.get(groupPos).getId();
        } else {
            taskId = listItems.get(listHeader.get(groupPos)).get(childPos).getId();
        }


        switch (item.getItemId()) {
            case R.id.action_edit:
                editTask(taskId);
                return true;
            case R.id.action_delete:
                deleteTask(taskId);
                return true;
            case R.id.action_statistics:
                statisticsTask(taskId);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_tasks_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new_task:
                editTask(-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void editTask(long taskId) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        if (taskId >= 0)
            intent.putExtra("taskid", taskId);
        startActivity(intent);
    }


    private void deleteTask(final long taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage(R.string.prompt_delete_task)
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // delete Task
                        IDatabaseController db = ApplicationHelper.createDatabaseController();
                        db.open();
                        db.deleteTask(taskId);
                        db.close();

                        //refresh task list after delete
                        loadTasksList();
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                })
                .create();
        dialog.show();
    }


    private void statisticsTask(long taskId) {
        //TODO: load the fragment instead of launching activity?!
//		Intent intent = new Intent(getActivity(), TaskWiseStatsFragment.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//		intent.putExtra("taskid", taskId);
//		startActivity(intent);
    }

}
