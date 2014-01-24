package org.b3studios.bible.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.b3studios.bible.R;
import org.b3studios.bible.model.SpinnerNavItem;

import java.util.ArrayList;

public class TitleNavigationAdapter extends BaseAdapter {

    private TextView txtTitle;
    private ArrayList<SpinnerNavItem> spinnerNavItem;
    private Context context;

    public TitleNavigationAdapter(Context context, ArrayList<SpinnerNavItem> spinnerNavItem) {
        this.spinnerNavItem = spinnerNavItem;
        this.context = context;
    }

    @Override
    public int getCount() {
        return spinnerNavItem.size();
    }

    @Override
    public Object getItem(int index) {
        return spinnerNavItem.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_title_navigation, null);
        }

        if (convertView != null) {
            txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
//            txtTitle.setTextColor(Color.WHITE);
        }

        txtTitle.setText(spinnerNavItem.get(position).getTitle());
        return convertView;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_title_navigation, null);
        }

        if (convertView != null) {
            txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
        }

        txtTitle.setText(spinnerNavItem.get(position).getTitle());
//        txtTitle.setTextColor(Color.WHITE);
        return convertView;
    }

}