package iiitd.mc.timetracker.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.suggestor.SuggestedTask;

/**
 * Custom AutoComplete Adapter to suggest all kinds of tasks with parent tasks
 * as well as children tasks
 *
 * @author gullal
 */
public class CustomArrayAdapter extends ArrayAdapter<SuggestedTask> implements Filterable {

    private List<SuggestedTask> suggestedList;
    private List<SuggestedTask> originalList;
    private ArrayFilter cFilter;

    private Context context;

    public CustomArrayAdapter(Context context, int resource, List<SuggestedTask> suggestedTasks) {

        super(context, resource, suggestedTasks);
        suggestedList = suggestedTasks;
        originalList = suggestedTasks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return suggestedList.size();
    }

    @Override
    public SuggestedTask getItem(int position) {
        return suggestedList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            v = layoutInflater.inflate(R.layout.dropdown_item_2line, null);
        }

        SuggestedTask task = getItem(position);

        if (task != null) {
            TextView tvTitle = (TextView) v.findViewById(R.id.dropdownItemTitle);
            TextView tvText = (TextView) v.findViewById(R.id.dropdownItemText);

            tvTitle.setText(task.getTask().getName());
            tvText.setText(task.getTask().getNameFull());
        }

        return v;
    }

    @Override
    public Filter getFilter() {
        if (cFilter == null) {
            cFilter = new ArrayFilter();
        }

        return cFilter;
    }

    @SuppressLint("DefaultLocale")
    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults filteredTasks = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                List<SuggestedTask> list = new ArrayList<>(originalList);
                filteredTasks.values = list;
                filteredTasks.count = list.size();
            } else {

                final String prefixString = prefix.toString().toLowerCase();

                List<SuggestedTask> newValues = new ArrayList<>(originalList.size());
                for (SuggestedTask item : originalList) {
                    if (item.toString().toLowerCase().contains("." + prefixString) || item.toString().toLowerCase().startsWith(prefixString)) {
                        newValues.add(item);
                    }
                }

                filteredTasks.values = newValues;
                filteredTasks.count = newValues.size();
            }
            return filteredTasks;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence arg0, FilterResults filteredTasks) {
            if (filteredTasks.values != null) {
                suggestedList = (List<SuggestedTask>) filteredTasks.values;
            } else {
                suggestedList = new ArrayList<>();
            }

            if (filteredTasks.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
