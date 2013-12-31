package org.b3studios.bible;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import org.b3studios.bible.adapter.ChapterButtonAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ChapterSelector extends Activity {

    private List<String> bookNames = Bible.settings.getBookNames();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String title = bookNames.get(Bible.settings.getBooksList().indexOf(Bible.settings.getCurrentBook()));

        this.setTitle(title);

        setContentView(R.layout.chapter_selector);
        GridView gw = (GridView) findViewById(R.id.grid_view);

        gw.setAdapter(new ChapterButtonAdapter(this));

        gw.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

//                Toast.makeText(getBaseContext(), bookNames.get(position), Toast.LENGTH_SHORT).show();

                Bible.settings.setCurrentChapter(position + 1);

                InputStream is;
                BufferedReader r;
                String 		   passage;
                int 		   verse 	= 0;
                StringBuilder  chapter  = new StringBuilder();

                try {

                    is = getAssets().open("data/"+ Bible.settings.getCurrentTranslation() +"/"+ Bible.settings.getCurrentBook() +"/"+ Bible.settings.getCurrentChapter());
                    r  = new BufferedReader(new InputStreamReader(is));

                    while ((passage = r.readLine()) != null) {

                        chapter.append("<strong>").append(++verse).append("</strong> ").append(passage).append("<br />");
                    }

                } catch (IOException e) {

                    e.printStackTrace();

                } finally {

                    Bible.mainTextView.setText(Html.fromHtml(chapter.toString()));

                    int index = Bible.settings.getBooksList().indexOf(Bible.settings.getCurrentBook());

                    Bible.bookTextView.setText(Bible.settings.getBookNames().get(index) + " " + Bible.settings.getCurrentChapter());

                    finish();
                }

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

