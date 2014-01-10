package org.b3studios.bible.slidingmenu;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.b3studios.bible.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        Context context = getActivity().getApplicationContext(); // or activity.getApplicationContext()
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        String myVersionName = "not available"; // initialize String

        try {

            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();

        } finally {

            TextView tvVersionName = (TextView) rootView.findViewById(R.id.tvVersionNumber);

            String gitCommit = "";

            try {

                InputStream is = getActivity().getAssets().open("version.txt");
                BufferedReader r = new BufferedReader(new InputStreamReader(is));

                gitCommit = r.readLine();

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                tvVersionName.setText("Version " + myVersionName + " (" + gitCommit.substring(32) + ")");
            }
        }

        return rootView;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        return true;

    }
}