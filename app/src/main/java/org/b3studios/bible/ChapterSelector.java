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

import org.b3studios.bible.adapter.ChapterButtonAdapter;
import org.b3studios.bible.slidingmenu.BibleFragment;

import java.util.List;

public class ChapterSelector extends Activity {

    private List<String> bookNames = BibleFragment.settings.getBookNames();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String currentBook = BibleFragment.settings.getCurrentBook();
        final int index = BibleFragment.settings.getBookNames().indexOf(currentBook);
        final String title = bookNames.get(BibleFragment.settings.getBookNames().indexOf(currentBook));

        super.onCreate(savedInstanceState);
        this.setTitle(title);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        setContentView(R.layout.chapter_selector);
        GridView gw = (GridView) findViewById(R.id.grid_view);

        gw.setAdapter(new ChapterButtonAdapter(this));

        gw.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Toast.makeText(getBaseContext(), title + " " + (position + 1), Toast.LENGTH_SHORT).show();

                BibleFragment.settings.setCurrentChapter(position + 1);
                BibleFragment.settings.setCurrentMaxChapters(BibleFragment.db.getChapterSize(currentBook));

                BibleFragment.bookTextView.setText(bookNames.get(index) + " " + BibleFragment.settings.getCurrentChapter() + " \u25BC");

                finish();

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent myIntent = new Intent("org.b3studios.bible.BOOKSELECTOR");
        startActivityForResult(myIntent, 0);

        finish();

        return true;
    }
}

