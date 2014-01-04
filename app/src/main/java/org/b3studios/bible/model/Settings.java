package org.b3studios.bible.model;

import android.graphics.Typeface;

import org.b3studios.bible.Bible;

import java.util.List;

public class Settings {

    private String currentTranslation = "kjv";
    private String currentBook = "Genesis";
    private int currentChapter = 1;
    private int currentMaxChapters = 50;

    private int mainViewTextSize = 18;

    private List<String> bookNames;

    private Typeface[] typefaces = {Typeface.SANS_SERIF, Typeface.SERIF, Typeface.MONOSPACE};

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

    public int getMainViewTextSize() { return mainViewTextSize; }

    public void setMainViewTextSize(int textSize) {

        this.mainViewTextSize = textSize;
        Bible.mainTextView.setTextSize(textSize);
    }

    public int getMainViewTypeface() {

        Typeface tf = Bible.mainTextView.getTypeface();

        int returnValue = 0;

        if (tf == typefaces[0]) {
            returnValue = 0;
        } else if (tf == typefaces[1]) {
            returnValue = 1;
        } else if (tf == typefaces[2]) {
            returnValue = 2;
        }

        return returnValue;
    }

    public void setMainViewTypeface(int index) {

        Bible.mainTextView.setTypeface(typefaces[index]);
    }


}
