package iiitd.mc.timetracker.adapter;

import iiitd.mc.timetracker.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationAdapter extends BaseAdapter{

	private Context context;
	String[] mNavigationTitles;
	int[] images = {R.drawable.ic_newproject, R.drawable.ic_newtask, R.drawable.ic_edittask, R.drawable.ic_statistics};
	
	public NavigationAdapter(Context context){
		this.context= context;
		mNavigationTitles = context.getResources().getStringArray(R.array.nav_drawer_items);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNavigationTitles.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mNavigationTitles[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row= null;
		// TODO Auto-generated method stub
		if(convertView==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row= inflater.inflate(R.layout.list_item, parent,false);
		}
		else{
			row = convertView;
		}
		TextView title = (TextView) row.findViewById(R.id.textView);
		title.setGravity(Gravity.LEFT);
		ImageView navigation_image = (ImageView) row.findViewById(R.id.imageView);
		title.setText(mNavigationTitles[position]);
		navigation_image.setImageResource(images[position]);
		return row;
	}

}
