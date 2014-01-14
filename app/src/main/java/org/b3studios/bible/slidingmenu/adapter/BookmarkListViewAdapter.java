package org.b3studios.bible.slidingmenu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.b3studios.bible.R;
import org.b3studios.bible.slidingmenu.BibleFragment;

import java.util.ArrayList;

public class BookmarkListViewAdapter extends ArrayAdapter<Spannable> {
    private final Context context;
    LayoutInflater inflater;
    private final ArrayList<Spannable> values;

    public BookmarkListViewAdapter(Context context, ArrayList<Spannable> values) {
        super(context, R.xml.main_listview_item, values);
        this.context = context;
        this.values = values;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.xml.main_listview_item, parent, false);
        final TextView textView;
        if (rowView != null) {
            textView = (TextView) rowView.findViewById(R.id.textview_item);
            textView.setText(values.get(position));
            textView.setTypeface(BibleFragment.settings.typefaces[BibleFragment.settings.currentTypeface]);
            textView.setTextSize(BibleFragment.settings.getMainViewTextSize());

            if (BibleFragment.settings.nightMode) {
                textView.setTextColor(Color.WHITE);
            }
        }

        return rowView;
    }
}