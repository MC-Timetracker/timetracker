package iiitd.mc.timetracker.adapter;

import iiitd.mc.timetracker.data.Task;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * Custom AutoComplete Adapter to suggest all kinds of tasks with parent tasks
 * as well as children tasks
 * 
 * @author gullal
 *
 */
public class CustomArrayAdapter extends ArrayAdapter<Task> implements Filterable {
	
	private List<Task> suggestedList;
	private List<Task> originalList;
	private ArrayFilter cFilter;

	public CustomArrayAdapter(Context context, int resource, List<Task> suggestedTasks) {
		
		super(context,resource,suggestedTasks);
		suggestedList = suggestedTasks;
		originalList = new ArrayList<Task>(suggestedList);
	}
	
	@Override
	public int getCount(){
		return suggestedList.size();
	}
	
	@Override
	public Task getItem(int position) {
		return suggestedList.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}
	
	@Override
	public Filter getFilter(){
		if(cFilter == null) {
			cFilter = new ArrayFilter();
		}
		
		return cFilter;
	}
	
	@SuppressLint("DefaultLocale")
	private class ArrayFilter extends Filter {

		private Object lock;
		
		@Override
		protected FilterResults performFiltering(CharSequence prefix)
		{
			FilterResults filteredTasks = new FilterResults();
			
			if(originalList == null){
				//synchronized(lock){
					originalList = new ArrayList<Task>(suggestedList);
				}
			//}
			
			if(prefix == null || prefix.length() == 0){
				//synchronized (lock){
					List<Task> list = new ArrayList<Task>(originalList);
					filteredTasks.values = list;
					filteredTasks.count = list.size();
				}
			//}
			else{
				
				final String prefixString = prefix.toString().toLowerCase();
				
				List<Task> values = originalList;
				
				int count = values.size();
				
				List<Task> newValues = new ArrayList<Task>(count);
				
				for(int i=0;i < count; i++){
					Task item = values.get(i);
					if(item.toString().toLowerCase().contains("."+prefixString) || item.toString().toLowerCase().startsWith(prefixString)){
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
		protected void publishResults(CharSequence arg0, FilterResults filteredTasks)
		{
			if(filteredTasks.values != null){
				suggestedList = (List<Task>) filteredTasks.values;
			}
			else{
				suggestedList = new ArrayList<Task>();
			}
			
			if(filteredTasks.count > 0){
				notifyDataSetChanged();
			}
			else{
				notifyDataSetInvalidated();
			}		
		}
	}
}
