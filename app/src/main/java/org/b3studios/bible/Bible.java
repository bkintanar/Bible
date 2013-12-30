package org.b3studios.bible;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.b3studios.bible.adapter.TitleNavigationAdapter;
import org.b3studios.bible.model.Setting;
import org.b3studios.bible.model.SpinnerNavItem;

public class Bible extends Activity implements ActionBar.OnNavigationListener {

    // action bar
    protected ActionBar actionBar;

    // Title navigation Spinner data
    protected ArrayList<SpinnerNavItem> navSpinner;

    // Navigation adapter
    protected TitleNavigationAdapter adapter;

    public static Setting setting = new Setting();
    
    public static TextView tv;
    public static TextView bookTextView;

    public int PREVIOUS = -1;
    public int NEXT     = 1;

    public static final String PREFS_NAME = "UserBibleInfo";

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
        loadSharedPreferences();

        setTv((TextView) findViewById(R.id.main_text));

        setting.setBooksList(listAssetFiles("data/" + setting.getCurrentTranslation()));
        setting.setBookNames(initBookNames());
        
        Button previousBtn = (Button) findViewById(R.id.previous_button);
        Button nextBtn     = (Button) findViewById(R.id.next_button);
        
        setBookTextView((TextView) findViewById(R.id.current_book));

        // Set listeners
        getBookTextView().setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooser = new Intent("org.b3studios.bible.BOOKCHOOSER");
                startActivity(chooser);
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
        
        // go to the default view
        goToChapter(0);
    }

    private void loadSharedPreferences() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        boolean hasSettingsStored = settings.getBoolean("hasSettingsStored", false);

        Log.i("DEBUG", "hasSettingsStored: " + hasSettingsStored);

        if (hasSettingsStored) {

            String[] sVersion = {"kjv", "adb", "ceb"};

            int index = Arrays.asList(sVersion).indexOf(settings.getString("currentTranslation", ""));

            // has user settings stored, load them
            setting.setCurrentTranslation(settings.getString("currentTranslation", ""));
            setting.setCurrentBook(settings.getString("currentBook", ""));
            setting.setCurrentChapter(settings.getInt("currentChapter", 0));
            setting.setCurrentMaxChapters(settings.getInt("currentMaxChapters", 0));

            actionBar.setSelectedNavigationItem(index);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("currentTranslation", Bible.setting.getCurrentTranslation());
        editor.putString("currentBook", Bible.setting.getCurrentBook());
        editor.putInt("currentChapter", Bible.setting.getCurrentChapter());
        editor.putInt("currentMaxChapters", Bible.setting.getCurrentMaxChapters());

        editor.putBoolean("hasSettingsStored", true);

        // Commit the edits!
        editor.commit();
    }

    /**
     * Actionbar navigation item select listener
     * */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        String[] sVersion = {"kjv", "adb", "ceb"};

        setting.setCurrentTranslation(sVersion[itemPosition]);

        goToChapter(0);

        return false;
    }



    @Override
    public void onResume() {

        setDefaultDataTextView(tv);

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

            is = getAssets().open("data/"+ setting.getCurrentTranslation() +"/"+ setting.getCurrentBook() +"/"+ setting.getCurrentChapter());
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
				
		if (setting.getCurrentChapter() + 1 > setting.getCurrentMaxChapters() && i == +1) {

            setting.setCurrentChapter(1);
			
			currentIndex = setting.getBooksList().indexOf(setting.getCurrentBook());

			// Reached last book, wrap to the first book
			if (currentIndex + 1 == setting.getBooksList().size()) {
				currentIndex = -1;
			}
			
			// Get next book
            setting.setCurrentBook(setting.getBooksList().get(currentIndex + 1));
			
			// Get the number of chapters
			setMaxChapters(i);
			
			Log.i("DEBUG", "Book changed to: " + setting.getCurrentBook());
		}
		else if (setting.getCurrentChapter() -1 == 0 && i == -1) {

			currentIndex = setting.getBooksList().indexOf(setting.getCurrentBook());

			// Reached first book, wrap to the last book
			if (currentIndex - 1 < 0)
			{
				currentIndex = setting.getBooksList().size();
			}
			
			// Get previous book
            setting.setCurrentBook(setting.getBooksList().get(currentIndex - 1));
			
			// Get the number of chapters
			setMaxChapters(i);
														
			Log.i("DEBUG", "Book changed to: " + setting.getCurrentBook());
		}
	}
	
	private void setMaxChapters(int i) {

	    List<String> chaptersList =  listAssetFiles("data/" + setting.getCurrentTranslation() + "/"+ setting.getCurrentBook());

        setting.setCurrentMaxChapters(chaptersList.size());
		
		if (i == -1) {
            setting.setCurrentChapter(chaptersList.size());
		}
		else {
            setting.setCurrentChapter(1);
		}
	}

	private void goToChapter(int i) {
		
		int index;
		
		// Check if current chapter is the first or the last chapter of the current book.		
		if ((setting.getCurrentChapter() + 1 > setting.getCurrentMaxChapters() && i == 1) || (setting.getCurrentChapter() - 1 == 0 && i == -1))
        {
	        setCurrentMaxChapters(i);
        }
		else
		{
            setting.setCurrentChapter(setting.getCurrentChapter() + i);
		}
		
		index = setting.getBooksList().indexOf(setting.getCurrentBook());
		
		setBookTextView((TextView) findViewById(R.id.current_book));
		
		getBookTextView().setText(setting.getBookNames().get(index) + " " + setting.getCurrentChapter());
		
		Log.i("DEBUG", "Displaying: " + setting.getBookNames().get(index) + " " + setting.getCurrentChapter());
		
		setDefaultDataTextView(getTv());
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

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public TextView getBookTextView() {
        return bookTextView;
    }

    public void setBookTextView(TextView bookTextView) {
        this.bookTextView = bookTextView;
    }
}
