package org.b3studios.bible.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;

import org.b3studios.bible.model.Highlight;
import org.b3studios.bible.slidingmenu.BibleFragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/org.b3studios.bible/databases/";

    private static String DB_NAME = "bible";

    private SQLiteDatabase myDatabase;

    private Context myContext;

    // Highlights Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_BOOK = "book";
    private static final String KEY_CHAPTER = "chapter";
    private static final String KEY_VERSE = "verse";
    private static final String KEY_HIGHLIGHT = "highlight";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;

    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDatabase() throws IOException {

        boolean dbExist = checkDatabase();

        if (!dbExist) {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

//                Log.i("DEBUG", "Copying Database");

                copyDatabase("bible");
//                copyDatabase("bible-journal");

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDatabase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDatabase(String filename) throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open("data/database/" + filename);

        // Path to the just created empty db
        String outFileName = DB_PATH + filename;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDatabase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDatabase != null)
            myDatabase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Spannable> getChapterToDisplay() {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + BibleFragment.settings.getCurrentTranslation() + " WHERE book=? AND chapter=?";

        Cursor cursor = db != null ? db.rawQuery(query, new String[]{BibleFragment.settings.getCurrentBook(), String.valueOf(BibleFragment.settings.getCurrentChapter())}) : null;

        ArrayList<Spannable> searchResult = new ArrayList<Spannable>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String passage = cursor.getString(3);

                    String verse = cursor.getString(2) + " ";

                    Spannable spanRange = new SpannableString(verse + passage);

                    // Add Bold text to the verse
                    spanRange.setSpan(new StyleSpan(Typeface.BOLD), 0, verse.length() - 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    searchResult.add(spanRange);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return searchResult;
    }

    public int getChapterSize(String currentBook) {

        int size = 1;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT chapter FROM " + BibleFragment.settings.getCurrentTranslation() + " WHERE book=?";

        Cursor cursor = db != null ? db.rawQuery(query, new String[]{currentBook}) : null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                size = cursor.getCount();
            }
        }

        cursor.close();

        return size;
    }

    private String appendWildcard(String query) {
        if (TextUtils.isEmpty(query)) return query;

        final StringBuilder builder = new StringBuilder();
        final String[] splits = TextUtils.split(query, " ");

        for (String split : splits)
            builder.append("*").append(split).append("*").append(" ");

        return builder.toString().trim();
    }

    String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }


    public Cursor customQuery(int result_type, String params, String book) {

        SQLiteDatabase db = this.getReadableDatabase();

        if (db != null) {
            switch (result_type) {
                case 1:

                    return db.query(BibleFragment.settings.getCurrentTranslation(),
                            new String[]{"book", "chapter", "verse", "passage"},
                            "passage" + " LIKE ?",
                            new String[]{"%" + params + "%"},
                            null, null, null);

                case 2:

                    return db.query(BibleFragment.settings.getCurrentTranslation(),
                            new String[]{"book", "chapter", "verse", "passage"},
                            "passage" + " LIKE ? AND book IN ('Genesis', 'Exodus', 'Leviticus', 'Numbers', " +
                                    "'Deuteronomy', 'Joshua', 'Judges', 'Ruth', '1 Samuel', '2 Samuel', " +
                                    "'1 Kings', '2 Kings', '1 Chronicles', '2 Chronicles', 'Ezra', " +
                                    "'Nehemiah', 'Esther', 'Job', 'Psalm', 'Proverbs', 'Ecclesiastes', " +
                                    "'Song of Songs', 'Isaiah', 'Jeremiah', 'Lamentations', 'Ezekiel', " +
                                    "'Daniel', 'Hosea', 'Joel', 'Amos', 'Obadiah', 'Jonah', 'Micah', " +
                                    "'Nahum', 'Habakkuk', 'Zephaniah', 'Haggai', 'Zechariah', 'Malachi')",
                            new String[]{"%" + params + "%"},
                            null, null, null);

                case 3:

                    return db.query(BibleFragment.settings.getCurrentTranslation(),
                            new String[]{"book", "chapter", "verse", "passage"},
                            "passage" + " LIKE ? AND book NOT IN ('Genesis', 'Exodus', 'Leviticus', 'Numbers', " +
                                    "'Deuteronomy', 'Joshua', 'Judges', 'Ruth', '1 Samuel', '2 Samuel', " +
                                    "'1 Kings', '2 Kings', '1 Chronicles', '2 Chronicles', 'Ezra', " +
                                    "'Nehemiah', 'Esther', 'Job', 'Psalm', 'Proverbs', 'Ecclesiastes', " +
                                    "'Song of Songs', 'Isaiah', 'Jeremiah', 'Lamentations', 'Ezekiel', " +
                                    "'Daniel', 'Hosea', 'Joel', 'Amos', 'Obadiah', 'Jonah', 'Micah', " +
                                    "'Nahum', 'Habakkuk', 'Zephaniah', 'Haggai', 'Zechariah', 'Malachi')",
                            new String[]{"%" + params + "%"},
                            null, null, null);

                case 4:

                    return db.query(BibleFragment.settings.getCurrentTranslation(),
                            new String[]{"book", "chapter", "verse", "passage"},
                            "passage" + " LIKE ? AND book IN ('" + book + "')",
                            new String[]{"%" + params + "%"},
                            null, null, null);
            }
        }

        return null;
    }

    // Adding new highlight
    public void addHighlight(Highlight highlight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BOOK, highlight.getBook());
        values.put(KEY_CHAPTER, highlight.getChapter());
        values.put(KEY_VERSE, highlight.getVerse());
        values.put(KEY_HIGHLIGHT, highlight.getHighlight());

        // Inserting Row
        db.insert("highlights", null, values);
        // Closing database connection
    }

    public Highlight getHighlight(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("highlights", new String[]{KEY_ID,
                KEY_BOOK, KEY_CHAPTER, KEY_VERSE, KEY_HIGHLIGHT}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Highlight highlight = new Highlight(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));


        // return highlight
        return highlight;
    }

    public Highlight getHighlight(Highlight hl) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + "highlights" + " WHERE" +
                " " + KEY_BOOK + " = '" + hl.getBook() + "' AND " + KEY_CHAPTER + " = " + hl.getChapter() +
                " AND " + KEY_VERSE + " = " + hl.getVerse();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            Highlight highlight = new Highlight(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
            cursor.close();
            return highlight;
        }
        cursor.close();
        return null;
    }

    public List<Highlight> getAllHighlights() {
        List<Highlight> contactList = new ArrayList<Highlight>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + "highlights";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Highlight highlight = new Highlight();
                highlight.setID(Integer.parseInt(cursor.getString(0)));
                highlight.setBook(cursor.getString(1));
                highlight.setChapter(Integer.parseInt(cursor.getString(2)));
                highlight.setVerse(Integer.parseInt(cursor.getString(3)));
                highlight.setHighlight(Integer.parseInt(cursor.getString(4)));
                // Adding highlight to list
                contactList.add(highlight);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return contactList;
    }

    public int updateHighlight(Highlight highlight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BOOK, highlight.getBook());
        values.put(KEY_CHAPTER, highlight.getChapter());
        values.put(KEY_VERSE, highlight.getVerse());
        values.put(KEY_HIGHLIGHT, highlight.getHighlight());

        // updating row
        int returnValue = db.update("highlights", values, KEY_ID + " = ?",
                new String[]{String.valueOf(highlight.getID())});

        return returnValue;
    }

    public void deleteHighlight(Highlight contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("highlights", KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getID())});
    }

    public void checkHighlightTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.query("highlights", null,
                    null, null, null, null, null);
        } catch (Exception e) {
            Log.i("DEBUG", "table doesnt exist, creating...");
            String CREATE_CONTACTS_TABLE = "CREATE TABLE " + "highlights" + "("
                    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BOOK + " TEXT,"
                    + KEY_CHAPTER + " INTEGER," + KEY_VERSE + " INTEGER," + KEY_HIGHLIGHT + " INTEGER" + ")";
            db.execSQL(CREATE_CONTACTS_TABLE);

        }
    }

    public boolean isHighlight(Highlight hl) {

        String selectQuery = "SELECT  * FROM " + "highlights" + " WHERE" +
                " " + KEY_BOOK + " = '" + hl.getBook() + "' AND " + KEY_CHAPTER + " = " + hl.getChapter() +
                " AND " + KEY_VERSE + " = " + hl.getVerse() + " AND " + KEY_HIGHLIGHT + " = " + "1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }
}