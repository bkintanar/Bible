package org.b3studios.bible.model;

/**
 * Created by bkintanar on 1/14/14.
 */
public class Highlight {

    //private variables
    private int _id;
    private String _book;
    private int _chapter;
    private int _verse;
    private int highlight;

    // Empty constructor
    public Highlight() {

    }

    // constructor
    public Highlight(int id, String book, int chapter, int verse, int highlight) {
        this.setID(id);
        this.setBook(book);
        this.setChapter(chapter);
        this.setVerse(verse);
        this.setHighlight(highlight);
    }

    // constructor
    public Highlight(String book, int chapter, int verse, int highlight) {
        this.setBook(book);
        this.setChapter(chapter);
        this.setVerse(verse);
        this.setHighlight(highlight);
    }

    public int getID() {
        return _id;
    }

    public void setID(int _id) {
        this._id = _id;
    }

    public String getBook() {
        return _book;
    }

    public void setBook(String _book) {
        this._book = _book;
    }

    public int getChapter() {
        return _chapter;
    }

    public void setChapter(int _chapter) {
        this._chapter = _chapter;
    }

    public int getVerse() {
        return _verse;
    }

    public void setVerse(int _verse) {
        this._verse = _verse;
    }

    public int getHighlight() {
        return highlight;
    }

    public void setHighlight(int highlight) {
        this.highlight = highlight;
    }
}
