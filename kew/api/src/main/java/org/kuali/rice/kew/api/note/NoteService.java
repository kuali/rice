package org.kuali.rice.kew.api.note;

import java.util.List;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

public interface NoteService {

	List<Note> getNotes(String documentId) throws RiceIllegalArgumentException;
	
	Note getNote(String noteId) throws RiceIllegalArgumentException;
	
	Note createNote(Note note) throws RiceIllegalArgumentException;
	
	Note updateNote(Note note) throws RiceIllegalArgumentException;
	
	Note deleteNote(String noteId) throws RiceIllegalArgumentException;
	
}
