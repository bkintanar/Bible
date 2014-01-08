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

import java.util.List;

public class ChapterSelector extends Activity {

    private List<String> bookNames = Bible.settings.getBookNames();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String currentBook = Bible.settings.getCurrentBook();
        final int index = Bible.settings.getBookNames().indexOf(currentBook);
        final String title = bookNames.get(Bible.settings.getBookNames().indexOf(currentBook));

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

                Bible.settings.setCurrentChapter(position + 1);
                Bible.settings.setCurrentMaxChapters(Bible.db.getChapterSize(currentBook));

                Bible.bookTextView.setText(bookNames.get(index) + " " + Bible.settings.getCurrentChapter() + " \u25BC");

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

