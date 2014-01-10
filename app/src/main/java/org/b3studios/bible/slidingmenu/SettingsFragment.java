package org.b3studios.bible.slidingmenu;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
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

        // Add 'notifications' preferences, and a corresponding header.
//        PreferenceCategory fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_index);
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_full_text_search);

        // Add 'data and sync' preferences, and a corresponding header.
//        fakeHeader = new PreferenceCategory(this);
//        fakeHeader.setTitle(R.string.pref_header_data_sync);
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_data_sync);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
//        bindPreferenceSummaryToValue(findPreference("example_text"));
        bindPreferenceSummaryToValue(findPreference("font_list"));
        bindPreferenceSummaryToValue(findPreference("font_size_list"));
//        bindPreferenceSummaryToValue(findPreference("night_mode"));
//        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        bindPreferenceSummaryToValue(findPreference("sync_frequency"));

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

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);


                if ("font_size_list".compareTo(listPreference.getKey()) == 0) {
                    setDefaultFontValue(index);
                } else if ("font_list".compareTo(listPreference.getKey()) == 0) {
                    setDefaultFontTypeface(index);
                }

                // Set the summary to reflect the new value.
                if (index >= 0) preference.setSummary(
                        listPreference.getEntries()[index]);
                else preference.setSummary(
                        null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                Log.i("DEBUG", "prefkey" + preference.getKey());
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

    public void setDefaultFontTypeface(int index) {

        BibleFragment.settings.setMainViewTypeface(index);

        settings = getActivity().getSharedPreferences("UserBibleInfo", 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("mainViewTypeface", BibleFragment.settings.getMainViewTypeface());

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