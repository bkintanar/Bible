package org.b3studios.bible;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import org.b3studios.bible.adapter.BookButtonAdapter;
import org.b3studios.bible.slidingmenu.BibleFragment;

import java.util.List;

public class BookSelector extends Activity {

    private List<String> bookNames = BibleFragment.settings.getBookNames();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        this.setTitle("Select a Book");

        setContentView(R.layout.book_selector);
        GridView gw = (GridView) findViewById(R.id.grid_view);

        gw.setAdapter(new BookButtonAdapter(this));

        gw.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Toast.makeText(getBaseContext(), bookNames.get(position), Toast.LENGTH_SHORT).show();

                BibleFragment.settings.setCurrentBook(BibleFragment.settings.getBookNames().get(position));

                // create new activity
                Intent chapterChooser = new Intent(getApplicationContext(), ChapterSelector.class);
                startActivity(chapterChooser);

                finish();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        finish();

        return true;

    }
}

