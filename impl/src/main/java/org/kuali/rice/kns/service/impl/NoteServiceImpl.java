/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.NoteDao;
import org.kuali.rice.kns.service.AttachmentService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the Note structure.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional
public class NoteServiceImpl implements NoteService {

    private NoteDao noteDao;
    
    private AttachmentService attachmentService;

    /**
     * Default constructor
     */
    public NoteServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.service.NoteService#saveNoteValueList(java.util.List)
     */
    public void saveNoteList(List<Note> notes) {
        if (notes != null) {
            for (Note note : notes) {
                save(note);
            }
        }
    }

    /**
     * Saves a Note to the data store.  Will also ensure that any attachments associated with the Note are persisted 
     * 
     * @param note the note to save, must be non-null and must have a valid remoteObjectIdentifier
     * @return the note that was saved
     */
    public Note save(Note note) {
    	if (note == null) {
    		throw new IllegalArgumentException("Note must be non-null.");
    	}
    	if (StringUtils.isBlank(note.getRemoteObjectIdentifier())) {
    		throw new IllegalStateException("The remote object identifier must be established on a Note before it can be saved.  Given note had a null remote object identifier.");
    	}
        noteDao.save(note);
        // move attachment from pending directory
        if (note.getAttachment() != null) {
        	attachmentService.moveAttachmentWherePending(note);
        }
        return note;
    }

    /**
     * Retrieves a Note by its associated object id.
     * 
     * @see org.kuali.rice.kns.service.NoteService#getByRemoteObjectId(java.lang.String)
     */
    public List<Note> getByRemoteObjectId(String remoteObjectId) {

        return noteDao.findByremoteObjectId(remoteObjectId);
    }
    
    /**
     * Retrieves a Note by note identifier.
     * 
     * @see org.kuali.rice.kns.service.NoteService#getNoteByNoteId(java.lang.Long)
     */
    public Note getNoteByNoteId(Long noteId) {
		return noteDao.getNoteByNoteId(noteId);
	}

    /**
     * Deletes a Note from the DB.
     * 
     * @param Note The Note object to delete.
     */
    public void deleteNote(Note note) {
        noteDao.deleteNote(note);
    }

    // needed for Spring injection
    /**
     * Sets the data access object
     * 
     * @param d
     */
    public void setNoteDao(NoteDao d) {
        this.noteDao = d;
    }

    /**
     * Retrieves a data access object
     */
    public NoteDao getNoteDao() {
        return noteDao;
    }

    public Note createNote(Note note, PersistableBusinessObject bo) {
        // TODO: Why is a deep copy being done?  Nowhere that this is called uses the given note argument
        // again after calling this method.
        Note tmpNote = (Note) ObjectUtils.deepCopy(note);
        Person kualiUser = GlobalVariables.getUserSession().getPerson();
        tmpNote.setRemoteObjectIdentifier(bo.getObjectId());
        tmpNote.setAuthorUniversalIdentifier(kualiUser.getPrincipalId());
        return tmpNote;
    }
    
    public void setAttachmentService(AttachmentService attachmentService) {
    	this.attachmentService = attachmentService;
    }
    
    protected AttachmentService getAttachmentService() {
    	return this.attachmentService;
    }
    
}
