package org.b3studios.bible;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bkintanar on 12/28/13.
 */
public class ButtonAdapterChapter extends BaseAdapter {

    private Context mContext;

    private String bookName;

    private int chapterCount;

    public ButtonAdapterChapter(Context c) {

        mContext = c;

        bookName = Bible.getKEY_BIBLE_BOOK();

        chapterCount =  getChapterList(bookName);

        Bible.CURRENT_MAX_CHAPTERS = chapterCount;

        Log.i("DEBUG", "" + chapterCount);

    }

    public int getChapterList(String bookName) {

        String [] list;

        List<String> items = new ArrayList<String>();

        try {

            list = mContext.getAssets().list("data/" + Bible.getKEY_BIBLE_VERSION() + "/"+ bookName);

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
