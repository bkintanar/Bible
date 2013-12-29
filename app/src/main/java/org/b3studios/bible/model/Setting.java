package org.b3studios.bible.model;

import java.util.List;

/**
 * Created by bkintanar on 12/29/13.
 */
public class Setting {

    private String currentTranslation = "kjv";
    private String currentBook = "01O";
    private int currentChapter = 1;
    private int currentMaxChapters = 50;

    private List<String> bookNames;
    private List<String> booksList;

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

    public List<String> getBooksList() {
        return booksList;
    }

    public void setBooksList(List<String> booksList) {
        this.booksList = booksList;
    }
}
