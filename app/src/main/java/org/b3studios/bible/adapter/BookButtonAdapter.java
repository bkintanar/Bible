package org.b3studios.bible.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import org.b3studios.bible.Bible;

import java.util.List;

/**
 * Created by bkintanar on 12/28/13.
 */
public class BookButtonAdapter extends BaseAdapter {

    private Context mContext;

    private List<String> bookNames;

    public BookButtonAdapter(Context c) {

        bookNames = Bible.getBookNames();

        mContext = c;
    }

    @Override
    public int getCount() {

        return bookNames.size();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Button btn;

        if (convertView == null) {

            btn = new Button(mContext);
            btn.setLayoutParams(new GridView.LayoutParams(100, 100));
            btn.setPadding(0, 0, 0, 0);

            btn.setFocusable(false);
            btn.setClickable(false);
            btn.setBackgroundResource(0);

        } else {

            btn = (Button) convertView;
        }

        String bookName        = bookNames.get(position);
        String bookNameTrimmed = bookName.replaceAll("\\s+","");

        btn.setText(bookNameTrimmed.substring(0, 3));

        if (position > 38) {

            btn.setTextColor(Color.RED);

        }
        else {

            btn.setTextColor(Color.BLUE);
        }

        btn.setId(position);

        return btn;
    }
}
