package iiitd.mc.timetracker.view;

import android.app.AlertDialog;
import android.app.Fragment;
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
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Recording;
import iiitd.mc.timetracker.view.adapter.ExpandableRecAdapter;

public class ListRecordingsFragment extends Fragment {

    private ExpandableListView expRecView;
    private List<String> recHeader = new ArrayList<>();
    private HashMap<String, List<Recording>> recItems = new HashMap<>();
    private ExpandableRecAdapter recAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_list_recordings, container, false);

        expRecView = (ExpandableListView) v.findViewById(R.id.Explv);

        recAdapter = new ExpandableRecAdapter(getActivity(), recHeader, recItems);
        expRecView.setAdapter(recAdapter);

        setHasOptionsMenu(true);
        registerForContextMenu(expRecView);

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        //refresh list
        loadRecordingsList();
    }

    /**
     * Populate the list in the UI with the Recordings from the database.
     */
    private void loadRecordingsList() {
        recHeader.clear();
        recItems.clear();
        DateFormat formater = android.text.format.DateFormat.getDateFormat(getActivity());

        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();
        List<Recording> recs = db.getRecordings();
        db.close();

        for (Recording r : recs) {
            String temp = formater.format(r.getStart());
            if (!recHeader.contains(temp)) {
                recHeader.add(temp);
                recItems.put(temp, new ArrayList<Recording>());
            }

            recItems.get(temp).add(r);
        }

        recAdapter.notifyDataSetChanged();

        if (recAdapter.getGroupCount() > 0)
            expRecView.expandGroup(0);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

        //only show context menu for child items (recordings not dates)
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.list_recordings_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
        int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
        Recording r = (Recording) recAdapter.getChild(groupPos, childPos);

        switch (item.getItemId()) {
            case R.id.action_edit:
                editRecording(r);
                return true;
            case R.id.action_delete:
                deleteRecording(r);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.list_recordings_actions, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new_recording:
                editRecording(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void editRecording(Recording r) {
        Intent edit_recording = new Intent(getActivity(), EditRecordingActivity.class);
        if (r != null)
            edit_recording.putExtra(EditRecordingActivity.EXTRA_RECORDING_ID, r.getRecordingId());
        startActivity(edit_recording);
    }

    private void deleteRecording(Recording r) {
        final long rId = r.getRecordingId();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setMessage(R.string.prompt_delete_recording)
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // delete Recording
                        IDatabaseController db = ApplicationHelper.createDatabaseController();
                        db.open();
                        db.deleteRecording(rId);
                        db.close();

                        //refresh recordings list
                        loadRecordingsList();
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