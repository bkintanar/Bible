package org.b3studios.bible;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Bible extends Activity implements OnClickListener {

    // TODO Get these values from config
    private static String KEY_BIBLE_VERSION = "ceb";
    private static String KEY_BIBLE_BOOK = "01O";
    private static int KEY_BIBLE_CHAPTER = 1;
    public static int CURRENT_MAX_CHAPTERS = 50;
    
    public static TextView tv;
    public static TextView bookTextView;
    private static List<String> booksList;
    private static List<String> bookNames;

    public int PREVIOUS = -1;
    public int NEXT     = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setTv((TextView) findViewById(R.id.main_text));
                        
        setBooksList(listAssetFiles("data/" + getKEY_BIBLE_VERSION()));
        setBookNames(initBookNames());
        
        Button previousBtn = (Button) findViewById(R.id.previous_button);
        Button nextBtn     = (Button) findViewById(R.id.next_button);
        
        setBookTextView((TextView) findViewById(R.id.current_book));
        
        getBookTextView().setOnClickListener(this);
        
        // Set button listeners
        previousBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        
        // go to the default view
        goToChapter(0);
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
    
    public void setDefaultDataTextView(TextView tv) {
    	
        InputStream    is;
        BufferedReader r;
        String 		   passage;
        int 		   verse 	= 0;
        StringBuilder  chapter  = new StringBuilder();
                
    	try {

            is = getAssets().open("data/"+ getKEY_BIBLE_VERSION() +"/"+ getKEY_BIBLE_BOOK() +"/"+ getKEY_BIBLE_CHAPTER());
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

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.previous_button) {

			goToChapter(PREVIOUS);
		}
		if (v.getId() == R.id.next_button) {

            goToChapter(NEXT);
		}
		if (v.getId() == R.id.current_book) {

            Intent chooser = new Intent("org.b3studios.bible.BOOKCHOOSER");
            startActivity(chooser);
		}
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
	
	public void setCurrentMaxChapters(int i) {
		
		int currentIndex;
				
		if (getKEY_BIBLE_CHAPTER() + 1 > getCURRENT_MAX_CHAPTERS() && i == +1) {

			setKEY_BIBLE_CHAPTER(1);
			
			currentIndex = getBooksList().indexOf(getKEY_BIBLE_BOOK());

			// Reached last book, wrap to the first book
			if (currentIndex + 1 == getBooksList().size()) {
				currentIndex = -1;
			}
			
			// Get next book
			setKEY_BIBLE_BOOK(getBooksList().get(currentIndex + 1));
			
			// Get the number of chapters
			setMaxChapters(i);
			
			Log.i("DEBUG", "Book changed to: " + getKEY_BIBLE_BOOK());
		}
		else if (getKEY_BIBLE_CHAPTER() -1 == 0 && i == -1) {

			currentIndex = getBooksList().indexOf(getKEY_BIBLE_BOOK());

			// Reached first book, wrap to the last book
			if (currentIndex - 1 < 0)
			{
				currentIndex = getBooksList().size();
			}
			
			// Get previous book
			setKEY_BIBLE_BOOK(getBooksList().get(currentIndex - 1));
			
			// Get the number of chapters
			setMaxChapters(i);
														
			Log.i("DEBUG", "Book changed to: " + getKEY_BIBLE_BOOK());
		}
	}
	
	private void setMaxChapters(int i) {

	    List<String> chaptersList =  listAssetFiles("data/" + getKEY_BIBLE_VERSION() + "/"+ getKEY_BIBLE_BOOK());
		
		setCURRENT_MAX_CHAPTERS(chaptersList.size());
		
		if (i == -1) {
			setKEY_BIBLE_CHAPTER(chaptersList.size());
		}
		else {
			setKEY_BIBLE_CHAPTER(1);
		}
	}

	private void goToChapter(int i) {
		
		int index;
		
		// Check if current chapter is the first or the last chapter of the current book.		
		if ((getKEY_BIBLE_CHAPTER() + 1 > getCURRENT_MAX_CHAPTERS() && i == 1) || (getKEY_BIBLE_CHAPTER() - 1 == 0 && i == -1))
        {
	        setCurrentMaxChapters(i);
        }
		else
		{
			setKEY_BIBLE_CHAPTER(getKEY_BIBLE_CHAPTER() + i);
		}
		
		index = getBooksList().indexOf(getKEY_BIBLE_BOOK());
		
		setBookTextView((TextView) findViewById(R.id.current_book));
		
		getBookTextView().setText(getBookNames().get(index) + " " + getKEY_BIBLE_CHAPTER());
		
		Log.i("DEBUG", "Displaying: " + getBookNames().get(index) + " " + getKEY_BIBLE_CHAPTER());
		
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

    public int getCURRENT_MAX_CHAPTERS() {
        return CURRENT_MAX_CHAPTERS;
    }

    public void setCURRENT_MAX_CHAPTERS(int CURRENT_MAX_CHAPTERS) {
        this.CURRENT_MAX_CHAPTERS = CURRENT_MAX_CHAPTERS;
    }

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

    public static List<String> getBooksList() {
        return booksList;
    }

    public void setBooksList(List<String> booksList) {
        this.booksList = booksList;
    }

    public static List<String> getBookNames() {
        return bookNames;
    }

    public void setBookNames(List<String> bookNames) {
        this.bookNames = bookNames;
    }

    public static String getKEY_BIBLE_VERSION() {
        return KEY_BIBLE_VERSION;
    }

    public static void setKEY_BIBLE_VERSION(String KEY_BIBLE_VERSION) {
        Bible.KEY_BIBLE_VERSION = KEY_BIBLE_VERSION;
    }

    public static String getKEY_BIBLE_BOOK() {
        return KEY_BIBLE_BOOK;
    }

    public static void setKEY_BIBLE_BOOK(String KEY_BIBLE_BOOK) {
        Bible.KEY_BIBLE_BOOK = KEY_BIBLE_BOOK;
    }

    public static int getKEY_BIBLE_CHAPTER() {
        return KEY_BIBLE_CHAPTER;
    }

    public static void setKEY_BIBLE_CHAPTER(int KEY_BIBLE_CHAPTER) {
        Bible.KEY_BIBLE_CHAPTER = KEY_BIBLE_CHAPTER;
    }

}
