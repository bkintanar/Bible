package org.b3studios.bible;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by bkintanar on 12/24/13.
 */
public class Menu extends ListActivity {

    String items[] = {"About"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(Menu.this, android.R.layout.simple_list_item_1, items));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        String item = items[position];

        try {

            Class itemClass = Class.forName("org.b3studios.bible." + item);
            Intent itemIntent = new Intent(Menu.this, itemClass);

        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }

    }
}
