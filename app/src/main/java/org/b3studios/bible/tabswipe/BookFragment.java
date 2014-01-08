package org.b3studios.bible.tabswipe;

/**
 * Created by bkintanar on 1/5/14.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.b3studios.bible.R;

public class BookFragment extends Fragment {

    private View rootView;
    private String query;

    public BookFragment(String query) {

        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.xml.fragment_book, container, false);

        Common common = new Common(getActivity(), query, 4, R.id.rowListView4);

        common.populateListView();

        return rootView;
    }

}