package org.b3studios.bible;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by bkintanar on 12/24/13.
 */
public class About extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

        Context context = getApplicationContext(); // or activity.getApplicationContext()
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        String myVersionName = "not available"; // initialize String

        try {

            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();

        } finally {

            TextView tvVersionName = (TextView) findViewById(R.id.tvVersionNumber);

            String gitCommit = "";

            try {

                InputStream is = getAssets().open("version.txt");
                BufferedReader r  = new BufferedReader(new InputStreamReader(is));

                gitCommit = r.readLine();

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                tvVersionName.setText("Version " + myVersionName + " (" + gitCommit.substring(32) + ")");
            }
        }
    }
}
