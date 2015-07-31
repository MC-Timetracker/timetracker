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

public class NavigationAdapter extends BaseAdapter {
    //TODO: Make this navigation menu more flexible & maintainable
    private Context context;
    public static String[] mNavigationTitles;
    int[] images = {R.drawable.ic_menu_home, R.drawable.ic_newtask, R.drawable.ic_listtask, R.drawable.ic_listrecordings, R.drawable.ic_statistics, R.drawable.ic_settings};

    public NavigationAdapter(Context context) {
        this.context = context;
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
        View row = null;
        // TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_navigation, parent, false);
        } else {
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
