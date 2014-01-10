package org.b3studios.bible.adapter;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.b3studios.bible.Bible;
import org.b3studios.bible.R;

import java.util.ArrayList;

public class SearchResultAdapter extends ArrayAdapter<Spannable> {
    private final Context context;
    private final ArrayList<Spannable> values;

    public SearchResultAdapter(Context context, ArrayList<Spannable> values) {
        super(context, R.xml.row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.xml.row, parent, false);
        TextView textView;
        if (rowView != null) {
            textView = (TextView) rowView.findViewById(R.id.rowSearchResultText);
            textView.setText(values.get(position));
            textView.setTypeface(Bible.settings.typefaces[Bible.settings.currentTypeface]);
            textView.setTextSize(Bible.settings.getMainViewTextSize());
        }

        return rowView;
    }
}