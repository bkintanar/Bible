package org.b3studios.bible.model;

import android.graphics.Color;
import android.graphics.Typeface;

import org.b3studios.bible.slidingmenu.BibleFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Settings {

    private String currentTranslation = "kjv";
    private String currentBook = "Genesis";
    private int currentChapter = 1;
    private int currentMaxChapters = 50;

    private int mainViewTextSize = 18;

    private List<String> bookNames;

    public int currentTypeface = 0;

    public int position = 0;
    public Boolean nightMode = false;

    private Typeface typeface;
    private String fontFilename = "HelveticaNeue";
    private int style = 4;
    private String[] fontsArrayString = {"AmericanTypewriter", "Baskerville", "Cochin", "Futura", "HelveticaNeue",
            "Monospace", "Optima", "Palatino", "Papyrus", "Roboto", "SansSerif", "Serif", "SnellRoundhand", "TrebuchetMS"};

    public String getCurrentTranslation() {
        return currentTranslation;
    }

    public void setCurrentTranslation(String currentTranslation) {
        this.currentTranslation = currentTranslation;
    }

    public String getCurrentBook() {
        return currentBook;
    }

    public void setCurrentBook(String currentBook) {
        this.currentBook = currentBook;
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
    }

    public int getCurrentMaxChapters() {
        return currentMaxChapters;
    }

    public void setCurrentMaxChapters(int currentMaxChapters) {
        this.currentMaxChapters = currentMaxChapters;
    }

    public List<String> getBookNames() {
        return bookNames;
    }

    public void setBookNames(List<String> bookNames) {
        this.bookNames = bookNames;
    }

    public int getMainViewTextSize() {
        return mainViewTextSize;
    }

    public void setMainViewTextSize(int textSize) {

        this.mainViewTextSize = textSize;
    }

    public int getMainViewTypeface() {

        return currentTypeface;
    }

    public void setMainViewTypeface(int index) {

        currentTypeface = index;
    }


    public void setDefaults() {

        if (this.nightMode) {
            BibleFragment.mainListView.setBackgroundColor(Color.BLACK);
            BibleFragment.bookTextView.setBackgroundColor(Color.BLACK);
            BibleFragment.bookTextView.setTextColor(Color.WHITE);
        } else {
            BibleFragment.mainListView.setBackgroundColor(Color.WHITE);
            BibleFragment.bookTextView.setBackgroundColor(Color.WHITE);
            BibleFragment.bookTextView.setTextColor(Color.BLACK);
        }
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public Boolean getNightMode() {
        return nightMode;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public Typeface getTypeface() {

        String fontName = getFontFilename();

        Typeface typeface = null;
        ArrayList<String> builtInFonts = new ArrayList<String>();
        builtInFonts.add("Monospace");
        builtInFonts.add("Serif");
        builtInFonts.add("SansSerif");

        if (builtInFonts.contains(fontName)) {

            switch (Arrays.asList(BibleFragment.settings.fontsArrayString).indexOf(fontName)) {
                case 5:
                    typeface = Typeface.MONOSPACE;
                    break;
                case 10:
                    typeface = Typeface.SANS_SERIF;
                    break;
                case 11:
                    typeface = Typeface.SERIF;
                    break;
            }
        } else {
            typeface = Typeface.createFromAsset(BibleFragment.mActivity.getAssets(), "fonts/" + fontName + ".ttf");
        }

        return typeface;
    }

    public void setFontFilename(String fontFilename) {
        this.fontFilename = fontFilename;
    }

    public String getFontFilename() {
        return fontFilename;
    }


    public void setFontStyle(int style) {
        this.style = style;
    }

    public int getFontStyle() {
        return style;
    }

    public String[] getFontArrayString() {
        return fontsArrayString;
    }
}
