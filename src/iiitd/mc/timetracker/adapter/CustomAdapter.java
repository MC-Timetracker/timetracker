package iiitd.mc.timetracker.adapter;

import java.util.List;

import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.data.Recording;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{
	
	private Context context;
	private List<Recording> recordedactivitydata;
	Recording tempRecording;
	
	public CustomAdapter(Context context, List<Recording> recordedactivitydata)
	{
		this.context=context;
		this.recordedactivitydata=recordedactivitydata;
	}
	
	@Override
	public int getCount() {
		return recordedactivitydata.size();
	}

	@Override
	public Object getItem(int position) {
		return recordedactivitydata.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String interval;
		
		if(convertView==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.recording_list, parent,false);
		}
		
		TextView txtviewrecording=(TextView)convertView.findViewById(R.id.textViewRecording);
		TextView txtviewinterval=(TextView)convertView.findViewById(R.id.textViewInterval);
		
		Recording record=recordedactivitydata.get(position);
		interval=record.getStart().toString() + "-" + record.getEnd().toString();
		
		txtviewrecording.setText(record.getTask().toString());
		txtviewinterval.setText(interval);
		
		return convertView;
	}

}