package org.b3studios.bible.search;

import android.app.Activity;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.b3studios.bible.SplashScreen;
import org.b3studios.bible.helper.DatabaseHelper;
import org.b3studios.bible.helper.ThemeHelper;
import org.b3studios.bible.search.adapter.SearchResultAdapter;
import org.b3studios.bible.slidingmenu.BibleFragment;
import org.b3studios.bible.slidingmenu.adapter.MainListViewAdapter;

import java.util.ArrayList;

public class Common {

    private final int RESULT_TYPE;
    private Activity activity;
    private String query;
    private int id;

    public Common(Activity activity, String query, int RESULT_TYPE, int id) {

        this.activity = activity;
        this.query = query;
        this.RESULT_TYPE = RESULT_TYPE;
        this.id = id;
    }

    public void populateListView() {

        new Thread(new Runnable() {

            public void run() {

                ArrayList<Spannable> searchResult = new ArrayList<Spannable>();
                ArrayList<String> verseCallback = new ArrayList<String>();

                DatabaseHelper db = new DatabaseHelper(activity);
                Cursor cursor;

                if (RESULT_TYPE == 4) {
                    cursor = db.customQuery(RESULT_TYPE, query, BibleFragment.settings.getCurrentBook());
                } else {
                    cursor = db.customQuery(RESULT_TYPE, query, "");
                }

                if (cursor != null)
                    if (cursor.moveToFirst()) {
                        do {

                            String passage = cursor.getString(3);

                            String verse = cursor.getString(0) + " " + cursor.getString(1) + ":" + cursor.getString(2) + " ";

                            Spannable spanRange = new SpannableString(verse + passage);

                            String text = (verse + passage).toLowerCase();

                            int startSpan;
                            int endSpan = verse.length();

                            query = query.toLowerCase();

                            while (true) {

                                startSpan = text.indexOf(query, endSpan);
                                BackgroundColorSpan backColour = new BackgroundColorSpan(Color.YELLOW);

                                ForegroundColorSpan foreColour = new ForegroundColorSpan(Color.BLACK);


                                // Need a NEW span object every loop, else it just moves the span
                                if (startSpan < 0)
                                    break;
                                endSpan = startSpan + query.length();
                                spanRange.setSpan(backColour, startSpan, endSpan,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                if (BibleFragment.settings.nightMode) {
                                    spanRange.setSpan(foreColour, startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }

                            // Add Bold text to the verse
                            spanRange.setSpan(new StyleSpan(Typeface.BOLD), 0, verse.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            searchResult.add(spanRange);
                            verseCallback.add(cursor.getString(0) + "-" + cursor.getString(1) + "-" + cursor.getString(2));

                        } while (cursor.moveToNext());
                    } else {
                        searchResult.add(new SpannableString("No search result found for `" + query + "'"));
                        verseCallback.add("No search result found for `" + query + "'");
                    }

                cursor.close();

                refreshList(searchResult, verseCallback);
            }

        }).start();
    }

    private void refreshList(final ArrayList<Spannable> searchResult, final ArrayList<String> verseCallback) {

        Runnable runnable = new Runnable() {
            public void run() {

                final ListView myListView = (ListView) activity.findViewById(id);

                SearchResultAdapter arrayListViewAdapter = new SearchResultAdapter(activity, searchResult);

                if (myListView != null) {

                    ThemeHelper themeHelper = new ThemeHelper(activity);
                    TypedArray ta = activity.obtainStyledAttributes(themeHelper.getmTheme(), themeHelper.attrs);

                    int backgroundColor = ta.getColor(themeHelper.BACKGROUND_COLOR, Color.BLACK);

                    myListView.setAdapter(arrayListViewAdapter);
                    myListView.setBackgroundColor(backgroundColor);

                    myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            final String[] verse = verseCallback.get(position).split("-");

                            if (verse.length == 3) {

                                BibleFragment.settings.setCurrentBook(verse[0]);
                                BibleFragment.settings.setCurrentChapter(Integer.parseInt(verse[1]));

                                BibleFragment.bookTextView.setText(BibleFragment.settings.getCurrentBook() + " " + BibleFragment.settings.getCurrentChapter() + " \u25BC");

                                ArrayList<Spannable> chapter = SplashScreen.db.getChapterToDisplay();

                                final MainListViewAdapter adapter = new MainListViewAdapter(activity, chapter);

                                BibleFragment.mainListView.setAdapter(adapter);

                                BibleFragment.mainListView.setSelection(Integer.parseInt(verse[2]) - 1);

                                activity.finish();

                                Toast.makeText(activity, verse[0] + " " + verse[1] + ":" + verse[2], Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
                synchronized (this) {
                    this.notify();
                }
            }
        };
//
        startOnUiAndWait(runnable);
    }

    /**
     * Start runnable on UI thread and wait until finished
     */
    public void startOnUiAndWait(Runnable runnable) {
        synchronized (runnable) {
            // Execute code on UI thread
            activity.runOnUiThread(runnable);

            // Wait until runnable finished
            try {
                runnable.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
