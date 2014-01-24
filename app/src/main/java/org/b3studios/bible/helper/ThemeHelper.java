package org.b3studios.bible.helper;

import android.content.Context;

import org.b3studios.bible.R;
import org.b3studios.bible.slidingmenu.BibleFragment;

/**
 * Created by bkintanar on 1/24/14.
 */
public class ThemeHelper {

    private Context mContext;
    private int mTheme;
    public int[] attrs = {android.R.attr.textColorPrimary, android.R.attr.windowBackground};

    public int TEXT_COLOR = 0;
    public int BACKGROUND_COLOR = 1;

    public ThemeHelper(Context context) {
        mContext = context;
        mTheme = BibleFragment.settings.nightMode ? R.style.uBibleNightMode : R.style.uBibleDefault;
    }

    public int getmTheme() {
        return mTheme;
    }

    public void setmTheme(int mTheme) {
        this.mTheme = mTheme;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
