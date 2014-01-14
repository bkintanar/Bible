package org.b3studios.bible.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import org.b3studios.bible.SplashScreen;
import org.b3studios.bible.slidingmenu.BibleFragment;

public class ChapterButtonAdapter extends BaseAdapter {

    private Context mContext;
    private int chapterCount;

    public ChapterButtonAdapter(Context c) {

        mContext = c;

        chapterCount = SplashScreen.db.getChapterSize(BibleFragment.settings.getCurrentBook());

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

        btn.setText("" + (position + 1));

        btn.setId(position);

        return btn;
    }
}
