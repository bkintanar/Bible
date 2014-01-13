package org.b3studios.bible.search.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.b3studios.bible.search.BookFragment;
import org.b3studios.bible.search.NTFragment;
import org.b3studios.bible.search.OTFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    String query;

    public TabsPagerAdapter(FragmentManager fm, String query) {
        super(fm);
        this.query = query;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new BookFragment(query);
            case 1:
                // Old Testament fragment activity
//                return new BookFragment();
                return new OTFragment(query);
            case 2:
                // New Testament fragment activity
                return new NTFragment(query);
            // Book fragment activity
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}