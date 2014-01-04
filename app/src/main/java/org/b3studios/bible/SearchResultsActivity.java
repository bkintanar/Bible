package org.b3studios.bible;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import org.b3studios.bible.adapter.SearchResultAdapter;
import org.b3studios.bible.helper.DatabaseHelper;

import java.util.ArrayList;

public class SearchResultsActivity extends Activity {

    private ArrayList<String> searchResult =  new ArrayList<String>();
    private SearchResultAdapter arrayListViewAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // get the action bar
        ActionBar actionBar = getActionBar();

        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);

            this.setTitle("Search Result");

            new Thread(new Runnable() {

                public void run() {

                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    Cursor cursor = db.query(query);

                    if (cursor.moveToFirst()) {
                        do {
                            searchResult.add("<strong>" + cursor.getString(0) + " " + cursor.getString(1) + ":" + cursor.getString(2) + "</strong> " + cursor.getString(3));

                        } while (cursor.moveToNext());
                    } else {
                        searchResult.add("No search result found for `" + query.trim() + "'");
                    }

                    cursor.close();

                    refreshList();

                }

            }).start();
        }
    }

    private void refreshList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ListView myListView = (ListView) findViewById(R.id.rowListView);

                arrayListViewAdapter = new SearchResultAdapter(getApplicationContext(), searchResult);


                myListView.setAdapter(arrayListViewAdapter);
                arrayListViewAdapter.notifyDataSetChanged();
            }
        });
    }
}