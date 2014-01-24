package org.b3studios.bible.slidingmenu;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.b3studios.bible.R;
import org.b3studios.bible.slidingmenu.adapter.NotesDbAdapter;

public class NotesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View rootView;
    private ListView notesList;

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;

    public NotesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        setHasOptionsMenu(true);

        /* \b[a-zA-Z]+(?:\s+\d+)?(?::\d+(?:–\d+)?(?:,\s*\d+(?:–\d+)?)*)? */

        rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        notesList = (ListView) rootView.findViewById(R.id.notesList);

        notesList.setOnItemClickListener(this);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        mDbHelper = new NotesDbAdapter(getActivity());
        mDbHelper.open();
        fillData();

        return rootView;
    }

    private void fillData() {
        // Get all of the rows from the database and create the item list
        mNotesCursor = mDbHelper.fetchAllNotes();
        getActivity().startManagingCursor(mNotesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(getActivity(), R.layout.notes_row, mNotesCursor, from, to);
        notesList.setAdapter(notes);
    }

    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);

        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(getActivity(), NoteEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        Cursor c = mNotesCursor;
//        c.moveToPosition(position);
//        Intent i = new Intent(getActivity(), NoteEditActivity.class);
//        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
//        i.putExtra(NotesDbAdapter.KEY_TITLE, c.getString(
//                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
//        i.putExtra(NotesDbAdapter.KEY_BODY, c.getString(
//                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
//        startActivityForResult(i, ACTIVITY_EDIT);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent == null)
            return;

        Bundle extras = intent.getExtras();
        switch(requestCode) {
            case ACTIVITY_CREATE:
                String title = extras.getString(NotesDbAdapter.KEY_TITLE);
                String body = extras.getString(NotesDbAdapter.KEY_BODY);
                mDbHelper.createNote(title, body);
                fillData();
                break;
            case ACTIVITY_EDIT:
                Long rowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
                if (rowId != null) {
                    String editTitle = extras.getString(NotesDbAdapter.KEY_TITLE);
                    String editBody = extras.getString(NotesDbAdapter.KEY_BODY);
                    mDbHelper.updateNote(rowId, editTitle, editBody);
                }
                fillData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = mNotesCursor;
        c.moveToPosition(position);
        Intent i = new Intent(getActivity(), NoteEditActivity.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        i.putExtra(NotesDbAdapter.KEY_TITLE, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
        i.putExtra(NotesDbAdapter.KEY_BODY, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        startActivityForResult(i, ACTIVITY_EDIT);
    }
}