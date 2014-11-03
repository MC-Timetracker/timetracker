/**
 * 
 */
package iiitd.mc.timetracker.adapter;

import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.data.Recording;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * @author Shubham
 * Adapter for Listing Recordings in an Expandable List
 */
@SuppressLint("InflateParams")
public class ExpandableRecAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> recHeader;
	private HashMap<String, List<Recording>> recItems;
	
	public ExpandableRecAdapter(Context context,List<String> recHeader, HashMap<String,List<Recording>> recItems)
	{
		this.context = context;
		this.recHeader = recHeader;
		this.recItems = recItems;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.recItems.get(this.recHeader.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		String headerTitle = (String)getGroup(groupPosition);
		
		if(convertView ==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_rec_group, parent,false);
		}
		
		TextView lvHeader = (TextView) convertView.findViewById(R.id.lvHeader);
		
		lvHeader.setTypeface(null, Typeface.BOLD);
		lvHeader.setText(headerTitle);
		
		return convertView;
	}

	@SuppressLint("SimpleDateFormat") @Override
	public View getChildView(int groupPosition, int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
		
		final Recording i = (Recording) getChild(groupPosition,childPosition);
		View view = convertView;
		
		if(view ==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.recording_list, parent,false);
		}
		
		TextView taskName =(TextView)view.findViewById(R.id.taskName);
		TextView recDur =(TextView)view.findViewById(R.id.duration);
		TextView timeRange = (TextView)view.findViewById(R.id.time);
		
		if(taskName != null){
			taskName.setText(i.getTask().getName());
		}
		if(recDur != null){
			long dur = i.getDuration(TimeUnit.MINUTES);
			recDur.setText(dur/60+":"+dur%60);
		}
		if(timeRange!= null){
			SimpleDateFormat dformat = new SimpleDateFormat("HH:mm");
			
			timeRange.setText("("+dformat.format(i.getStart())+" - "+dformat.format(i.getEnd())+")");
		}
		
		return view;
		
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public int getGroupCount() {
		return this.recHeader.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.recItems.get(this.recHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.recHeader.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

}
