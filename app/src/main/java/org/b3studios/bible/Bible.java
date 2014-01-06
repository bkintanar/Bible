package org.b3studios.bible;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import org.b3studios.bible.adapter.TitleNavigationAdapter;
import org.b3studios.bible.helper.DatabaseHelper;
import org.b3studios.bible.model.Settings;
import org.b3studios.bible.model.SpinnerNavItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bible extends Activity implements ActionBar.OnNavigationListener {
    // action bar
    protected ActionBar actionBar;

    // Title navigation Spinner data
    protected ArrayList<SpinnerNavItem> navSpinner;

    // Navigation adapter
    protected TitleNavigationAdapter adapter;

    public static Settings settings = new Settings();

    public static DatabaseHelper db;
    public SearchView searchView;


    public static TextView mainTextView;
    public static TextView bookTextView;

    public int PREVIOUS = -1;
    public int NEXT     = 1;

    public static final String PREFS_NAME = "UserBibleInfo";

    String[] sVersion = {"kjv", "adb", "ceb"};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        actionBar = getActionBar();

        db = new DatabaseHelper(getApplicationContext());

        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        // Enabling Spinner dropdown navigation
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("King James Version"));
        navSpinner.add(new SpinnerNavItem("Ang Dating Biblia (Tagalog)"));
        navSpinner.add(new SpinnerNavItem("Ang Biblia (Cebuano)"));

        // title drop down adapter
        adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);

        // assigning the spinner navigation
        actionBar.setListNavigationCallbacks(adapter, this);

        // load preferences stored in device

        try {
            db.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.openDatabase();
        }

        settings.setBookNames(initBookNames());

        // Get components from XML
        setMainTextView((TextView) findViewById(R.id.main_text));
        setBookTextView((TextView) findViewById(R.id.current_book));

        loadSharedPreferences();

        Button previousBtn = (Button) findViewById(R.id.previous_button);
        Button nextBtn     = (Button) findViewById(R.id.next_button);

        // Set listeners
        getBookTextView().setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selector = new Intent("org.b3studios.bible.BOOKSELECTOR");
                startActivity(selector);
            }
        });

        previousBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChapter(PREVIOUS);
            }
        });
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChapter(NEXT);
            }
        });

        // display default view
        goToChapter(0);
    }

    private void loadSharedPreferences() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        boolean hasSettingsStored = settings.getBoolean("hasSettingsStored", false);

        if (hasSettingsStored) {

            int index = Arrays.asList(sVersion).indexOf(settings.getString("currentTranslation", "kjv"));

            // for version = 1.0.1
            if (settings.getString("currentBook", "Genesis").length() == 3)
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("currentBook", Bible.settings.getCurrentBook());
                editor.commit();
            }


            // has user settings stored, load them
            Bible.settings.setCurrentTranslation(settings.getString("currentTranslation", "kjv"));
            Bible.settings.setCurrentBook(settings.getString("currentBook", "Genesis"));
            Bible.settings.setCurrentChapter(settings.getInt("currentChapter", 1));
            Bible.settings.setCurrentMaxChapters(settings.getInt("currentMaxChapters", 50));
            Bible.settings.setMainViewTextSize(settings.getInt("mainViewTextSize", 18));
            Bible.settings.setMainViewTypeface(settings.getInt("mainViewTypeface", 0));

            actionBar.setSelectedNavigationItem(index);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("currentTranslation", Bible.settings.getCurrentTranslation());
        editor.putString("currentBook", Bible.settings.getCurrentBook());
        editor.putInt("currentChapter", Bible.settings.getCurrentChapter());
        editor.putInt("currentMaxChapters", Bible.settings.getCurrentMaxChapters());
        editor.putInt("mainViewTextSize", Bible.settings.getMainViewTextSize());
        editor.putInt("mainViewTypeface", Bible.settings.getMainViewTypeface());

        editor.putBoolean("hasSettingsStored", true);

        // Commit the edits!
        editor.commit();
    }

    /**
     * Actionbar navigation item select listener
     * */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        settings.setCurrentTranslation(sVersion[itemPosition]);

        goToChapter(0);

        return false;
    }

    @Override
    public void onResume() {

        updateMainTextView();

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                MenuItem menuSearch = menu.findItem(R.id.action_search);
                SearchView searchView = (SearchView) menuSearch.getActionView();

                searchView.onActionViewCollapsed();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_about:

                Intent about = new Intent("org.b3studios.bible.ABOUT");
                startActivity(about);

                return true;

            case R.id.action_search:

                return true;

            case R.id.action_settings:

                Intent settings = new Intent("org.b3studios.bible.SETTINGS");
                startActivity(settings);

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }

    public List<String> initBookNames() {

        InputStream    is;
        BufferedReader r;
        String 		   bookTitle;

        List<String> items = new ArrayList<String>();

        try {

            is = getAssets().open("data/book_names.txt");
            r  = new BufferedReader(new InputStreamReader(is));

            while ((bookTitle = r.readLine()) != null) {

                items.add(bookTitle);
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

        return items;
    }

    public void setCurrentMaxChapters(int i) {

		int currentIndex;

		if (settings.getCurrentChapter() + 1 > settings.getCurrentMaxChapters() && i == +1) {

            settings.setCurrentChapter(1);

			currentIndex = settings.getBookNames().indexOf(settings.getCurrentBook());

			// Reached last book, wrap to the first book
			if (settings.getCurrentBook().compareTo("Revelation") == 0) {
				currentIndex = -1;
			}

			// Get next book
            settings.setCurrentBook(settings.getBookNames().get(currentIndex+1));

			// Get the number of chapters
			setMaxChapters(i);

		} else if (settings.getCurrentChapter() -1 == 0 && i == -1) {

            currentIndex = settings.getBookNames().indexOf(settings.getCurrentBook());

			// Reached first book, wrap to the last book
			if (settings.getCurrentBook().compareTo("Genesis") == 0)
			{
				currentIndex = settings.getBookNames().size();
			}

			// Get previous book
            settings.setCurrentBook(settings.getBookNames().get(currentIndex - 1));

			// Get the number of chapters
			setMaxChapters(i);
		}
	}

	private void setMaxChapters(int i) {

	    int chapterSize = db.getChapterSize(settings.getCurrentBook());

        settings.setCurrentMaxChapters(chapterSize);

		if (i == -1) {
            settings.setCurrentChapter(chapterSize);
		}
		else {
            settings.setCurrentChapter(1);
		}
	}

	private void goToChapter(int i) {

		// Check if current chapter is the first or the last chapter of the current book.
		if ((settings.getCurrentChapter() + 1 > settings.getCurrentMaxChapters() && i == 1) || (settings.getCurrentChapter() - 1 == 0 && i == -1))
        {
	        setCurrentMaxChapters(i);
        }
		else
		{
            settings.setCurrentChapter(settings.getCurrentChapter() + i);
		}

		setBookTextView((TextView) findViewById(R.id.current_book));

		getBookTextView().setText(settings.getCurrentBook() + " " + settings.getCurrentChapter());

//		Log.i("DEBUG", "Displaying: " + settings.getBookNames().get(index) + " " + settings.getCurrentChapter() + " " + settings.getCurrentTranslation().toUpperCase());

        updateMainTextView();
	}

    // Getters and Setters

    public void setMainTextView(TextView mainTextView) {
        this.mainTextView = mainTextView;
    }

    public TextView getBookTextView() {
        return bookTextView;
    }

    public void setBookTextView(TextView bookTextView) {
        this.bookTextView = bookTextView;
    }

    public void updateMainTextView() {

        new Thread(new Runnable() {
            public void run() {

                String chapter = Bible.db.getChapterToDisplay();

                setMainTextViewText(chapter.toString());
            }
        }).start();
    }

    public void setMainTextViewText(final String s) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mainTextView.setText(Html.fromHtml(s));
            }
        });

    }
}
