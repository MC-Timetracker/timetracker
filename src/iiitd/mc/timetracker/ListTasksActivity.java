package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.ExpandableListAdapter;
import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.IDatabaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;

@SuppressLint({"InflateParams", "ShowToast"})
public class ListTasksActivity extends BaseActivity {

    private ExpandableListView expListView;
    private List<Task> listHeader;
    private HashMap<Task, List<Task>> listItems;
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
        listHeader = new ArrayList<Task>();
        listItems = new HashMap<Task, List<Task>>();

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

    public void onCreateContextMenu(ContextMenu menu,
                                    View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "View");
        menu.add(1, v.getId(), 1, "Statistics");
        menu.add(2, v.getId(), 2, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        int grouppos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childpos = ExpandableListView.getPackedPositionChild(info.packedPosition);

        long taskId = 0;

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            taskId = listHeader.get(grouppos).getId();
        } else {
            taskId = listItems.get(listHeader.get(grouppos)).get(childpos).getId();
        }

        if (item.getTitle() == "View") {
            Intent intent = new Intent(ListTasksActivity.this, EditTaskActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("taskid", taskId);
            startActivity(intent);
        } else if (item.getTitle() == "Delete") {
            deleteTask(taskId);
        } else if (item.getTitle() == "Statistics") {
            Intent intent = new Intent(this, TaskWiseStats.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("taskid", taskId);
            startActivity(intent);
        } else
            return false;

        return true;
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

                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);
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
}
