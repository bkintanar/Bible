package org.b3studios.bible;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.util.List;

public class BookChooser extends Activity {

    private List<String> bookNames = Bible.getBookNames();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.setTitle("Select a Book");

        setContentView(R.layout.book_chooser);
        GridView gw = (GridView) findViewById(R.id.grid_view);

        gw.setAdapter(new ButtonAdapterBook(this));

        gw.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Toast.makeText(getBaseContext(), bookNames.get(position), Toast.LENGTH_SHORT).show();

                Bible.setKEY_BIBLE_BOOK(Bible.getBooksList().get(position));

                Log.i("DEBUG", "Book changed to: " + Bible.getKEY_BIBLE_BOOK());

                // create new activity
                Intent chapterChooser = new Intent("org.b3studios.bible.CHAPTERCHOOSER");
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

