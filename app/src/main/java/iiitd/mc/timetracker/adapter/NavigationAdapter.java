package iiitd.mc.timetracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import iiitd.mc.timetracker.NavigationItem;
import iiitd.mc.timetracker.R;


public class NavigationAdapter extends BaseAdapter {

    private Context context;
    private NavigationItem[] navigationItems;

    public NavigationAdapter(Context context, NavigationItem[] items) {
        this.context = context;
        navigationItems = items;
    }


    @Override
    public int getCount() {
        return navigationItems.length;
    }

    @Override
    public Object getItem(int position) {
        return navigationItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater
                    .inflate(R.layout.list_item_navigation, parent, false);
        } else {
            row = convertView;
        }

        TextView tvTitle = (TextView) row.findViewById(R.id.textView);
        ImageView ivIcon = (ImageView) row.findViewById(R.id.imageView);

        tvTitle.setText(navigationItems[position].stringTitle);
        ivIcon.setImageResource(navigationItems[position].drawableIcon);
        return row;
    }

}