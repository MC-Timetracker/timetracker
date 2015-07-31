package iiitd.mc.timetracker.adapter;

import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.data.Task;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Adapter for Listing Tasks in an Expandable List
 *
 * @author gullal
 */
@SuppressLint("InflateParams")
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Task> listHeader;
    private HashMap<Task, List<Task>> listItems;

    public ExpandableListAdapter(Context context, List<Task> listHeader, HashMap<Task, List<Task>> listItems) {
        this.context = context;
        this.listHeader = listHeader;
        this.listItems = listItems;
    }


    @Override
    public Object getChild(int groupPos, int childPos) {
        return this.listItems.get(this.listHeader.get(groupPos)).get(childPos);
    }

    @Override
    public long getChildId(int groupPos, int childPos) {
        return childPos;
    }

    @Override
    public View getChildView(int groupPos, final int childPos, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        final Task childText = (Task) getChild(groupPos, childPos);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_simple, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lvItem);
        String task = childText.getNameFull();
        txtListChild.setText(task.substring(task.indexOf(".") + 1, task.length()));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPos) {
        return this.listItems.get(this.listHeader.get(groupPos)).size();
    }

    @Override
    public Object getGroup(int groupPos) {
        return this.listHeader.get(groupPos);
    }

    @Override
    public int getGroupCount() {
        return this.listHeader.size();
    }

    @Override
    public long getGroupId(int groupPos) {
        return groupPos;
    }

    @Override
    public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {
        Task headerTitle = (Task) getGroup(groupPos);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView lvHeader = (TextView) convertView.findViewById(R.id.lvHeader);

        lvHeader.setTypeface(null, Typeface.BOLD);
        lvHeader.setText(headerTitle.getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}
