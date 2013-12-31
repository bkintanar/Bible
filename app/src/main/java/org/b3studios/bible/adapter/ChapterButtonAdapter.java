package org.b3studios.bible.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import org.b3studios.bible.Bible;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChapterButtonAdapter extends BaseAdapter {

    private Context mContext;
    private String bookName = Bible.settings.getCurrentBook();;
    private int chapterCount;

    public ChapterButtonAdapter(Context c) {

        mContext = c;

        chapterCount = getChapterList(bookName);

        Log.i("DEBUG", "" + chapterCount);

    }

    public int getChapterList(String bookName) {

        String [] list;

        List<String> items = new ArrayList<String>();

        try {

            list = mContext.getAssets().list("data/" + Bible.settings.getCurrentTranslation() + "/"+ bookName);

            if (list.length > 0) {

                // This is a folder
                Collections.addAll(items, list);
            }
        } catch (IOException e) {

            return 0;
        }

        return items.size();
    }

    @Override
    public int getCount() {

        return chapterCount;
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

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

        btn.setText("" + (position+1));

        btn.setId(position);

        return btn;
    }
}
