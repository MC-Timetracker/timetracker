package iiitd.mc.timetracker.adapter;

import iiitd.mc.timetracker.context.SuggestedTask;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

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

    public CustomArrayAdapter(Context context, int resource, List<SuggestedTask> suggestedTasks) {

        super(context, resource, suggestedTasks);
        suggestedList = suggestedTasks;
        originalList = suggestedTasks;
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
                List<SuggestedTask> list = new ArrayList<SuggestedTask>(originalList);
                filteredTasks.values = list;
                filteredTasks.count = list.size();
            } else {

                final String prefixString = prefix.toString().toLowerCase();

                List<SuggestedTask> newValues = new ArrayList<SuggestedTask>(originalList.size());
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
                suggestedList = new ArrayList<SuggestedTask>();
            }

            if (filteredTasks.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
