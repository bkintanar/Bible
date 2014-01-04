package org.b3studios.bible.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import org.b3studios.bible.Bible;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by bkintanar on 1/3/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_IDX = "idx";
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/org.b3studios.bible/databases/";

    private static String DB_NAME = "bible";

    private SQLiteDatabase myDatabase;

    private final Context myContext;

    protected List<String> bookList;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;

    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDatabase() throws IOException {

        boolean dbExist = checkDatabase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

//                Log.i("DEBUG", "Copying Database");

                copyDatabase("bible");
                copyDatabase("bible-journal");

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDatabase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDatabase(String filename) throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open("data/database/" + filename);

        // Path to the just created empty db
        String outFileName = DB_PATH + filename;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
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

        if(myDatabase != null)
            myDatabase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void setBookList() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM book_list", null);

        if(cursor.moveToFirst()) {
            do {
                bookList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    public String getChapterToDisplay() {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + Bible.settings.getCurrentTranslation() +  " WHERE book=? AND chapter=?";

        Cursor cursor = db.rawQuery(query, new String[] { Bible.settings.getCurrentBook(),  String.valueOf(Bible.settings.getCurrentChapter()) });

        StringBuilder  chapter  = new StringBuilder();

        if(cursor.moveToFirst()) {
            do {
                chapter.append("<strong>").append(cursor.getString(2)).append("</strong> ").append(cursor.getString(3)).append("<br />");
            } while (cursor.moveToNext());
        }
        cursor.close();

        return chapter.toString();
    }

    public int getChapterSize(String currentBook) {

        int size = 1;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT chapter FROM " + Bible.settings.getCurrentTranslation() +  " WHERE book=?";

        Cursor cursor = db.rawQuery(query, new String[] { currentBook });

        if(cursor.moveToFirst())
        {
            size = cursor.getCount();
        }

        return size;
    }

    public Cursor query(String queryString) {

        SQLiteDatabase db = this.getReadableDatabase();
        queryString = queryString.trim();

        return db.query("kjv",
                new String[]{"book", "chapter", "verse", "passage"},
                "kjv" + " MATCH ?",
                new String[]{appendWildcard(queryString)},
                null, null, null);
    }

    private String appendWildcard(String query) {
        if (TextUtils.isEmpty(query)) return query;

        final StringBuilder builder = new StringBuilder();
        final String[] splits = TextUtils.split(query, " ");

        for (String split : splits)
            builder.append(split).append("*").append(" ");

        return builder.toString().trim();
    }

}