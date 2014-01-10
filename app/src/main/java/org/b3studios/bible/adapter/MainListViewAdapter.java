package org.b3studios.bible.adapter;

/**
 * Created by bkintanar on 1/5/14.
 */

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.b3studios.bible.Bible;
import org.b3studios.bible.R;

import java.util.ArrayList;

public class MainListViewAdapter extends ArrayAdapter<Spannable> {
    private final Context context;
    private final ArrayList<Spannable> values;

    public MainListViewAdapter(Context context, ArrayList<Spannable> values) {
        super(context, R.xml.row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.xml.row, parent, false);
        final TextView textView = (TextView) rowView.findViewById(R.id.rowSearchResultText);
        textView.setText(values.get(position));
        textView.setTypeface(Bible.settings.typefaces[Bible.settings.currentTypeface]);
        textView.setTextSize(Bible.settings.getMainViewTextSize());

        if(Bible.settings.nightMode) {
            textView.setTextColor(Color.WHITE);
        }

//        textView.setTextIsSelectable(true);

//        rowView.setOnTouchListener( new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                CharSequence t = textView.getText();
//                Spannable spanRange = new SpannableString(t);
//
//
//                spanRange.setSpan(backColour, 0, t.length(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                textView.setText(spanRange);
//
//                return false;
//            }
//        });

        return rowView;
    }
}