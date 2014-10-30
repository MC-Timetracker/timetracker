package iiitd.mc.timetracker.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.data.Recording;

import android.annotation.SuppressLint;
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
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		if(view ==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.recording_list, parent,false);
		}
		
		Recording i = recordedactivitydata.get(position);
		
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

}