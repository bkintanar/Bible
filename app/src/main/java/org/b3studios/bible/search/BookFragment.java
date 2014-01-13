package org.b3studios.bible.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.b3studios.bible.R;

public class BookFragment extends Fragment {

    private String query;

    public BookFragment(String query) {

        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.xml.fragment_book, container, false);

        Common common = new Common(getActivity(), query, 4, R.id.rowListView4);

        common.populateListView();

        return rootView;
    }

}