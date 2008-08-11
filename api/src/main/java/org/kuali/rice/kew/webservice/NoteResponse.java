package org.kuali.rice.kew.webservice;

/**
 * Response object for note-related methods
 */
public class NoteResponse extends ErrorResponse {
    protected String author;
    protected String noteId;
    protected String timestamp;
    protected String noteText;
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getNoteId() {
        return noteId;
    }
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getNoteText() {
        return noteText;
    }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}