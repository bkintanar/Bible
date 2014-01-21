package org.b3studios.bible.slidingmenu;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import org.b3studios.bible.R;

public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
    }

    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);
//        bindPreferenceSummaryToValue(findPreference("font_list"));
        bindPreferenceSummaryToValue(findPreference("font_size_list"));
        bindPreferenceSummaryToValue(findPreference("font_style"));

        final CheckBoxPreference nightModeCheckBox = (CheckBoxPreference) getPreferenceManager().findPreference("night_mode");

        if (nightModeCheckBox != null) {
            nightModeCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {

                        BibleFragment.settings.setNightMode((Boolean) newValue);


                        SharedPreferences.Editor editor = settings.edit();

                        editor.putBoolean("night_mode", BibleFragment.settings.getNightMode());

                        editor.commit();

                        BibleFragment.settings.setDefaults();
                    }
                    return true;
                }
            });
        }


    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof FontStyleListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                FontStyleListPreference fontStyleListPreference = (FontStyleListPreference) preference;
                int index = settings.getInt("font_style", 2);//fontStyleListPreference.findIndexOfValue(stringValue);

//                settings = getActivity().getSharedPreferences("UserBibleInfo", 0);


                setDefaultFontStyle(index);

                Log.i("DEBUG", "index = " + index);

                // Set the summary to reflect the new value.
                if (index >= 0) preference.setSummary(
                        fontStyleListPreference.getEntries()[index]);
                else preference.setSummary(
                        null);

            } else if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);


                if ("font_size_list".compareTo(listPreference.getKey()) == 0) {
                    setDefaultFontValue(index);
                }

                // Set the summary to reflect the new value.
                if (index >= 0) preference.setSummary(
                        listPreference.getEntries()[index]);
                else preference.setSummary(
                        null);

            } else {
                Log.i("DEBUG", "preference.getClass().toString() = " + preference.getClass().toString());
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    public void setDefaultFontValue(int index) {

        int[] values = {12, 14, 16, 18, 20, 22, 24};

        BibleFragment.settings.setMainViewTextSize(values[index]);

        settings = getActivity().getSharedPreferences("UserBibleInfo", 0);

        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("mainViewTextSize", BibleFragment.settings.getMainViewTextSize());
        editor.putInt("mainViewTypeface", BibleFragment.settings.getMainViewTypeface());

        editor.commit();
    }

    public void setDefaultFontStyle(int index) {

        String[] values = {"AmericanTypewriter", "Baskerville", "Cochin", "Futura", "HelveticaNeue", "Optima", "Palatino", "Papyrus", "Roboto", "SnellRoundhand", "TrebuchetMS"};

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + values[index] + ".ttf");

        BibleFragment.settings.setTypeface(typeface);

        settings = getActivity().getSharedPreferences("UserBibleInfo", 0);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString("fontFilename", values[index]);
        editor.putInt("font_style", index);

        editor.commit();
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        return true;

    }
}