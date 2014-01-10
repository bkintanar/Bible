package org.b3studios.bible.tabswipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.b3studios.bible.R;

public class NTFragment extends Fragment {

    private String query;

    public NTFragment(String query) {

        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.xml.fragment_nt, container, false);

        Common common = new Common(getActivity(), query, 3, R.id.rowListView3);

        common.populateListView();

        return rootView;
    }
}