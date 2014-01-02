package org.b3studios.bible;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.b3studios.bible.adapter.TitleNavigationAdapter;
import org.b3studios.bible.model.Setting;
import org.b3studios.bible.model.SpinnerNavItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bible extends Activity implements ActionBar.OnNavigationListener {

    // action bar
    protected ActionBar actionBar;

    // Title navigation Spinner data
    protected ArrayList<SpinnerNavItem> navSpinner;

    // Navigation adapter
    protected TitleNavigationAdapter adapter;

    public static Setting settings = new Setting();
    
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

        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(false);

        // Enabling Spinner dropdown navigation
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("King James Version", R.drawable.ic_launcher));
        navSpinner.add(new SpinnerNavItem("Ang Dating Biblia (Tagalog)", R.drawable.ic_launcher));
        navSpinner.add(new SpinnerNavItem("Ang Biblia (Cebuano)", R.drawable.ic_launcher));

        // title drop down adapter
        adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);

        // assigning the spinner navigation
        actionBar.setListNavigationCallbacks(adapter, this);

        // load preferences stored in device

        settings.setBooksList(listAssetFiles("data/" + settings.getCurrentTranslation()));
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

            // has user settings stored, load them
            Bible.settings.setCurrentTranslation(settings.getString("currentTranslation", "kjv"));
            Bible.settings.setCurrentBook(settings.getString("currentBook", "01O"));
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

        setDefaultDataTextView(mainTextView);

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_about:

                Intent about = new Intent("org.b3studios.bible.ABOUT");
                startActivity(about);

                return true;

            case R.id.action_settings:

                Intent settings = new Intent("org.b3studios.bible.SETTINGS");
                startActivity(settings);

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }

    public void setDefaultDataTextView(TextView tv) {

        InputStream    is;
        BufferedReader r;
        String 		   passage;
        int 		   verse 	= 0;
        StringBuilder  chapter  = new StringBuilder();

        try {

            is = getAssets().open("data/"+ settings.getCurrentTranslation() +"/"+ settings.getCurrentBook() +"/"+ settings.getCurrentChapter());
            r  = new BufferedReader(new InputStreamReader(is));

            while ((passage = r.readLine()) != null) {

                chapter.append("<strong>").append(++verse).append("</strong> ").append(passage).append("<br />");
            }

            tv.setText(Html.fromHtml(chapter.toString()));

        } catch (IOException e) {

            e.printStackTrace();
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
			
			currentIndex = settings.getBooksList().indexOf(settings.getCurrentBook());

			// Reached last book, wrap to the first book
			if (currentIndex + 1 == settings.getBooksList().size()) {
				currentIndex = -1;
			}
			
			// Get next book
            settings.setCurrentBook(settings.getBooksList().get(currentIndex + 1));
			
			// Get the number of chapters
			setMaxChapters(i);

		} else if (settings.getCurrentChapter() -1 == 0 && i == -1) {

			currentIndex = settings.getBooksList().indexOf(settings.getCurrentBook());

			// Reached first book, wrap to the last book
			if (currentIndex - 1 < 0)
			{
				currentIndex = settings.getBooksList().size();
			}
			
			// Get previous book
            settings.setCurrentBook(settings.getBooksList().get(currentIndex - 1));
			
			// Get the number of chapters
			setMaxChapters(i);
		}
	}
	
	private void setMaxChapters(int i) {

	    List<String> chaptersList =  listAssetFiles("data/" + settings.getCurrentTranslation() + "/"+ settings.getCurrentBook());

        settings.setCurrentMaxChapters(chaptersList.size());
		
		if (i == -1) {
            settings.setCurrentChapter(chaptersList.size());
		}
		else {
            settings.setCurrentChapter(1);
		}
	}

	private void goToChapter(int i) {
		
		int index;
		
		// Check if current chapter is the first or the last chapter of the current book.		
		if ((settings.getCurrentChapter() + 1 > settings.getCurrentMaxChapters() && i == 1) || (settings.getCurrentChapter() - 1 == 0 && i == -1))
        {
	        setCurrentMaxChapters(i);
        }
		else
		{
            settings.setCurrentChapter(settings.getCurrentChapter() + i);
		}
		
		index = settings.getBooksList().indexOf(settings.getCurrentBook());
		
		setBookTextView((TextView) findViewById(R.id.current_book));
		
		getBookTextView().setText(settings.getBookNames().get(index) + " " + settings.getCurrentChapter());
		
//		Log.i("DEBUG", "Displaying: " + settings.getBookNames().get(index) + " " + settings.getCurrentChapter() + " " + settings.getCurrentTranslation().toUpperCase());
		
		setDefaultDataTextView(getMainTextView());
	}
	
	public List<String> listAssetFiles(String path) {

	    String [] list;
	    
	    List<String> items = new ArrayList<String>();
	    
	    try {

	        list = getAssets().list(path);

            if (list.length > 0) {

	            // This is a folder
                Collections.addAll(items, list);
	        }
        } catch (IOException e) {

            return null;
	    }

	    return items;
	}

    // Getters and Setters

    public TextView getMainTextView() {
        return mainTextView;
    }

    public void setMainTextView(TextView mainTextView) {
        this.mainTextView = mainTextView;
    }

    public TextView getBookTextView() {
        return bookTextView;
    }

    public void setBookTextView(TextView bookTextView) {
        this.bookTextView = bookTextView;
    }
}
