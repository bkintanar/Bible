package org.b3studios.bible;

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

import java.util.List;

public class BookSelector extends Activity {

    private List<String> bookNames = Bible.settings.getBookNames();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        this.setTitle("Select a Book");

        setContentView(R.layout.book_selector);
        GridView gw = (GridView) findViewById(R.id.grid_view);

        gw.setAdapter(new BookButtonAdapter(this));

        gw.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Toast.makeText(getBaseContext(), bookNames.get(position), Toast.LENGTH_SHORT).show();

                Bible.settings.setCurrentBook(Bible.settings.getBookNames().get(position));

                // create new activity
                Intent chapterChooser = new Intent(getApplicationContext(), ChapterSelector.class);
                startActivity(chapterChooser);

                finish();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), Bible.class);
        startActivityForResult(myIntent, 0);

        finish();

        return true;

    }
}

