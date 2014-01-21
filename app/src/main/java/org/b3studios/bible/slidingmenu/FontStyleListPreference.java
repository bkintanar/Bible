package org.b3studios.bible.slidingmenu;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import org.b3studios.bible.R;

import java.util.ArrayList;

public class FontStyleListPreference extends ListPreference
{
    FontStyleListPreferenceAdapter fontStyleListPreferenceAdapter = null;
    Context mContext;
    private LayoutInflater mInflater;
    CharSequence[] entries;
    CharSequence[] entryValues;
    ArrayList<RadioButton> rButtonList;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int selected;

    public FontStyleListPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        rButtonList = new ArrayList<RadioButton>();
        prefs = mContext.getSharedPreferences("UserBibleInfo", 0);
        editor = prefs.edit();
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder)
    {
        super.onPrepareDialogBuilder(builder);

        entries = getEntries();
        entryValues = getEntryValues();

        selected = prefs.getInt("font_style", 4);

        if (entries == null || entryValues == null || entries.length != entryValues.length )
        {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        fontStyleListPreferenceAdapter = new FontStyleListPreferenceAdapter(mContext);

        builder.setAdapter(fontStyleListPreferenceAdapter, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });



        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);
    }

    @Override
    public void setValueIndex(int index) {
        if (entryValues != null) {
            setValue(entryValues[index].toString());
        }
    }

        private class FontStyleListPreferenceAdapter extends BaseAdapter
    {
        View[] Views;

        public FontStyleListPreferenceAdapter(Context context)
        {
            Views = new View[entries.length];
        }

        public int getCount()
        {
            return entries.length;
        }

        public Object getItem(int position)
        {
            return null;
        }

        public long getItemId(int position)
        {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View row = Views[position];
            CustomHolder holder;

            if(row == null)
            {
                row = mInflater.inflate(R.layout.font_style_list_preference_row, parent, false);
                holder = new CustomHolder(row, position);
                row.setTag(holder);
                Views[position] = row;
            }

            return row;
        }

        class CustomHolder
        {
            private TextView text = null;
            private RadioButton rButton = null;

            CustomHolder(View row, final int position)
            {
                text = (TextView)row.findViewById(R.id.custom_list_view_row_text_view);
                text.setText(entries[position]);

                String fontName = (String) entries[position];
                String fontFilename = fontName.replaceAll("\\s", "");

                Typeface mFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/"+fontFilename+".ttf");

                text.setTypeface(mFont);

                rButton = (RadioButton)row.findViewById(R.id.custom_list_view_row_radio_button);
                rButton.setId(position);

                if((""+selected).compareTo((String)entryValues[position])==0)
                    rButton.setChecked(true);

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int value = Integer.valueOf((String) entryValues[position]);
                        editor.putInt("font_style", value);
                        editor.commit();

                        callChangeListener(entryValues[position]);

                        Dialog mDialog = getDialog();
                        mDialog.dismiss();
                    }
                });

                rButtonList.add(rButton);
                rButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        if(isChecked)
                        {
                            for(RadioButton rb : rButtonList)
                            {
                                if(rb != buttonView)
                                    rb.setChecked(false);
                            }

                            int index = buttonView.getId();
                            int value = Integer.valueOf((String) entryValues[index]);
                            editor.putInt("font_style", value);
                            editor.commit();

                            callChangeListener(entryValues[index]);

                            Dialog mDialog = getDialog();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        }
    }
}