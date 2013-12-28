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

public class ChapterChooser extends Activity {

    private List<String> bookNames = Bible.getBookNames();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String title = bookNames.get(Bible.getBooksList().indexOf(Bible.getKEY_BIBLE_BOOK()));

        this.setTitle(title);

        setContentView(R.layout.chapter_chooser);
        GridView gw = (GridView) findViewById(R.id.grid_view);

        gw.setAdapter(new ChapterButtonAdapter(this));

        gw.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

//                Toast.makeText(getBaseContext(), bookNames.get(position), Toast.LENGTH_SHORT).show();

                Bible.setKEY_BIBLE_CHAPTER(position + 1);

                InputStream is;
                BufferedReader r;
                String 		   passage;
                int 		   verse 	= 0;
                StringBuilder  chapter  = new StringBuilder();

                try {

                    is = getAssets().open("data/"+ Bible.getKEY_BIBLE_VERSION() +"/"+ Bible.getKEY_BIBLE_BOOK() +"/"+ Bible.getKEY_BIBLE_CHAPTER());
                    r  = new BufferedReader(new InputStreamReader(is));

                    while ((passage = r.readLine()) != null) {

                        chapter.append("<strong>").append(++verse).append("</strong> ").append(passage).append("<br />");
                    }

                } catch (IOException e) {

                    e.printStackTrace();

                } finally {

                    Bible.tv.setText(Html.fromHtml(chapter.toString()));

                    int index = Bible.getBooksList().indexOf(Bible.getKEY_BIBLE_BOOK());

                    Bible.bookTextView.setText(Bible.getBookNames().get(index) + " " + Bible.getKEY_BIBLE_CHAPTER());

                    finish();
                }

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent myIntent = new Intent("org.b3studios.bible.BOOKCHOOSER");
        startActivityForResult(myIntent, 0);

        finish();

        return true;
    }
}

