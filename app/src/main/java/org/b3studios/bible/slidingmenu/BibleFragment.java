package org.b3studios.bible.slidingmenu;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.b3studios.bible.R;
import org.b3studios.bible.SplashScreen;
import org.b3studios.bible.adapter.TitleNavigationAdapter;
import org.b3studios.bible.model.Settings;
import org.b3studios.bible.model.SpinnerNavItem;
import org.b3studios.bible.slidingmenu.adapter.MainListViewAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class BibleFragment extends Fragment implements ActionBar.OnNavigationListener, OnRefreshListener {

    public static int GO_TO_CHAPTER;
    // action bar
    protected ActionBar actionBar;

    PullToRefreshLayout mPullToRefreshLayout;

    // Title navigation Spinner data
    protected ArrayList<SpinnerNavItem> navSpinner;

    // Navigation adapter
    protected TitleNavigationAdapter adapter;

    public static Settings settings = new Settings();

    public SearchView searchView;

    public static TextView bookTextView;
    public static ListView mainListView;

    public int PREVIOUS = -1;
    public int NEXT = 1;

    private ArrayList<Spannable> chapter;

    public static final String PREFS_NAME = "UserBibleInfo";

    public static int mScrollingDirection = 0;
    public static int DIRECTION_UP = 1;
    public static int DIRECTION_DOWN = -1;

    String[] sVersion = {"kjv", "adb", "ceb"};

    View rootView;

    public BibleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_bible, container, false);
        setHasOptionsMenu(true);

        actionBar = getActivity().getActionBar();



        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);

        // Enabling Spinner dropdown navigation
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("King James Version"));
        navSpinner.add(new SpinnerNavItem("Ang Dating Biblia (Tagalog)"));
        navSpinner.add(new SpinnerNavItem("Ang Biblia (Cebuano)"));

        // title drop down adapter
        adapter = new TitleNavigationAdapter(getActivity(), navSpinner);

        // assigning the spinner navigation
        actionBar.setListNavigationCallbacks(adapter, this);

        // load preferences stored in device

        settings.setBookNames(initBookNames());

        // Get components from XML
        setBookTextView((TextView) rootView.findViewById(R.id.current_book));

        mainListView = (ListView) rootView.findViewById(R.id.mainListView);

        mainListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    mScrollingDirection = DIRECTION_DOWN;
                    GO_TO_CHAPTER = NEXT;
                }
                if (0 == firstVisibleItem) {
                    mScrollingDirection = DIRECTION_UP;
                    GO_TO_CHAPTER = PREVIOUS;
                }
            }
        });

        // Set listeners
        getBookTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selector = new Intent("org.b3studios.bible.BOOKSELECTOR");
                startActivity(selector);
            }
        });

        loadSharedPreferences();

        // display default view
        goToChapter(0);

        // Create a PullToRefreshAttacher instance
//        mPullToRefreshAttacher = PullToRefreshAttacher.get(getActivity());

        // Retrieve the PullToRefreshLayout from the content view
        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.ptr_layout);

        // Give the PullToRefreshAttacher to the PullToRefreshLayout, along with a refresh listener.
//        ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        return rootView;
    }

    private void loadSharedPreferences() {

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        boolean hasSettingsStored = settings.getBoolean("hasSettingsStored", false);

        if (hasSettingsStored) {

            int index = Arrays.asList(sVersion).indexOf(settings.getString("currentTranslation", "kjv"));

            // for version = 1.0.1
            if (settings.getString("currentBook", "Genesis").length() == 3) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("currentBook", BibleFragment.settings.getCurrentBook());
                editor.commit();
            }

            // has user settings stored, load them
            BibleFragment.settings.setCurrentTranslation(settings.getString("currentTranslation", "kjv"));
            BibleFragment.settings.setCurrentBook(settings.getString("currentBook", "Genesis"));
            BibleFragment.settings.setCurrentChapter(settings.getInt("currentChapter", 1));
            BibleFragment.settings.setCurrentMaxChapters(settings.getInt("currentMaxChapters", 50));
            BibleFragment.settings.setMainViewTextSize(settings.getInt("mainViewTextSize", 18));
            BibleFragment.settings.setMainViewTypeface(settings.getInt("mainViewTypeface", 0));
            BibleFragment.settings.setNightMode(settings.getBoolean("night_mode", false));
            BibleFragment.settings.setPosition(settings.getInt("position", 0));

            BibleFragment.settings.setDefaults();

            actionBar.setSelectedNavigationItem(index);
        }
    }

    @Override
    public void onStop() {

        super.onStop();

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("currentTranslation", BibleFragment.settings.getCurrentTranslation());
        editor.putString("currentBook", BibleFragment.settings.getCurrentBook());
        editor.putInt("currentChapter", BibleFragment.settings.getCurrentChapter());
        editor.putInt("currentMaxChapters", BibleFragment.settings.getCurrentMaxChapters());
        editor.putInt("mainViewTextSize", BibleFragment.settings.getMainViewTextSize());
        editor.putInt("mainViewTypeface", BibleFragment.settings.getMainViewTypeface());
        editor.putBoolean("night_mode", BibleFragment.settings.getNightMode());
        editor.putInt("position", BibleFragment.settings.getPosition());

        editor.putBoolean("hasSettingsStored", true);

        // Commit the edits!
        editor.commit();
    }

    /**
     * Actionbar navigation item select listener
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        settings.setCurrentTranslation(sVersion[itemPosition]);

        goToChapter(0);

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.onActionViewCollapsed();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_search:

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }

    public List<String> initBookNames() {

        InputStream is;
        BufferedReader r;
        String bookTitle;

        List<String> items = new ArrayList<String>();

        try {

            is = getActivity().getAssets().open("data/book_names.txt");
            r = new BufferedReader(new InputStreamReader(is));

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
            settings.setCurrentBook(settings.getBookNames().get(currentIndex + 1));

            // Get the number of chapters
            setMaxChapters(i);

        } else if (settings.getCurrentChapter() - 1 == 0 && i == -1) {

            currentIndex = settings.getBookNames().indexOf(settings.getCurrentBook());

            // Reached first book, wrap to the last book
            if (settings.getCurrentBook().compareTo("Genesis") == 0) {
                currentIndex = settings.getBookNames().size();
            }

            // Get previous book
            settings.setCurrentBook(settings.getBookNames().get(currentIndex - 1));

            // Get the number of chapters
            setMaxChapters(i);
        }
    }

    private void setMaxChapters(int i) {

        int chapterSize = SplashScreen.db.getChapterSize(settings.getCurrentBook());

        settings.setCurrentMaxChapters(chapterSize);

        if (i == -1) {
            settings.setCurrentChapter(chapterSize);
        } else {
            settings.setCurrentChapter(1);
        }
    }

    private void goToChapter(int i) {

        // Check if current chapter is the first or the last chapter of the current book.
        if ((settings.getCurrentChapter() + 1 > settings.getCurrentMaxChapters() && i == 1) || (settings.getCurrentChapter() - 1 == 0 && i == -1)) {
            setCurrentMaxChapters(i);
        } else {
            settings.setCurrentChapter(settings.getCurrentChapter() + i);
        }

        setBookTextView((TextView) rootView.findViewById(R.id.current_book));

        getBookTextView().setText(settings.getCurrentBook() + " " + settings.getCurrentChapter() + " \u25BC");

        updateMainTextView(i);

        mScrollingDirection = 0;
    }

    private void updateMainTextView(final int i) {
        new Thread(new Runnable() {
            public void run() {

                chapter = SplashScreen.db.getChapterToDisplay();

                setMainTextViewText(i);
            }
        }).start();
    }

    @SuppressWarnings("ConstantConditions")
    public void setMainTextViewText(final int i) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final MainListViewAdapter adapter = new MainListViewAdapter(getActivity(), chapter);

                mainListView.setAdapter(adapter);
                mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

                mainListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode,
                                                          int position, long id, boolean checked) {
                        // Capture total checked items
                        final int checkedCount = mainListView.getCheckedItemCount();
                        // Set the CAB title according to total checked items
                        mode.setTitle(checkedCount + " Selected");
                        // Calls toggleSelection method from ListViewAdapter Class
                        adapter.toggleSelection(position);
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.highlight:

                                // Calls getSelectedIds method from ListViewAdapter Class
                                SparseBooleanArray selected = adapter.getSelectedIds();
                                // Captures all selected ids with a loop
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        adapter.highlight(selected.keyAt(i));
                                    }
                                }
                                // Close CAB
                                mode.finish();

                                selected.clear();

                                return true;

                            default:
                                return false;
                        }
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.listitem_menu, menu);
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        // TODO Auto-generated method stub
//                        adapter.removeSelection();
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        // TODO Auto-generated method stub
                        return false;
                    }
                });

                final SharedPreferences settings = getActivity().getSharedPreferences("UserBibleInfo", 0);

                if (settings.getInt("position", 0) > 0) {
                    mainListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mainListView.setSelection(settings.getInt("position", 0) - 1);

                            SharedPreferences.Editor editor = settings.edit();

                            editor.putInt("position", 0);

                            editor.commit();

                            BibleFragment.settings.position = 0;
                        }
                    });
                } else if (i == PREVIOUS) {
                    mainListView.setSelection(chapter.size());
                } else {
                    mainListView.setSelection(0);
                }
            }
        });

    }


    // Getters and Setters

    public TextView getBookTextView() {
        return bookTextView;
    }

    public void setBookTextView(TextView bookTextView) {
        BibleFragment.bookTextView = bookTextView;
    }

    @Override
    public void onRefreshStarted(View view) {
        /**
         * Simulate Refresh with 4 seconds sleep
         */
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                goToChapter(GO_TO_CHAPTER);

                // Notify PullToRefreshAttacher that the refresh has finished
                mPullToRefreshLayout.setRefreshComplete();

            }
        }.execute();
    }
}