package iiitd.mc.timetracker.adapter;

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
public class CustomArrayAdapter extends ArrayAdapter<String> implements Filterable {
	
	private ArrayList<String> suggestedList;
	private ArrayList<String> originalList;
	private ArrayFilter cFilter;

	public CustomArrayAdapter(Context context, int resource,List<String> suggestedTasks){
		
		super(context,resource,suggestedTasks);
		suggestedList = (ArrayList<String>)suggestedTasks;
		originalList = new ArrayList<String>(suggestedList);
	}
	
	@Override
	public int getCount(){
		return suggestedList.size();
	}
	
	@Override
	public String getItem(int position) {
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
				synchronized(lock){
					originalList = new ArrayList<String>(suggestedList);
				}
			}
			
			if(prefix == null || prefix.length() == 0){
				synchronized (lock){
					ArrayList<String> list = new ArrayList<String>(originalList);
					filteredTasks.values = list;
					filteredTasks.count = list.size();
				}
			}
			else{
				
				final String prefixString = prefix.toString().toLowerCase();
				
				ArrayList<String> values = originalList;
				
				int count = values.size();
				
				ArrayList<String> newValues = new ArrayList<String>(count);
				
				for(int i=0;i < count; i++){
					String item = values.get(i);
					if(item.toLowerCase().contains(prefixString)){
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
				suggestedList = (ArrayList<String>) filteredTasks.values;
			}
			else{
				suggestedList = new ArrayList<String>();
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
