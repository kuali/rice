/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.util.cache.CopiedObject;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.AttachmentService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.NoteService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for the Note structure
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional
public class NoteServiceImpl implements NoteService {

    protected LegacyDataAdapter lda;
    protected AttachmentService attachmentService;
//    protected DataObjectService dataObjectService;

    @Required
    public void setLegacyDataAdapter(LegacyDataAdapter lda) {
        this.lda = lda;
    }

    @Override
	public void saveNoteList(List<Note> notes) {
        if (notes != null) {
            for (Note note : notes) {
            	if (StringUtils.isBlank(note.getRemoteObjectIdentifier())) {
            		throw new IllegalStateException("The remote object identifier must be established on a Note before it can be saved.  The following note in the given list had a null or empty remote object identifier: " + note);
            	}
                save(note);
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.service.NoteService#save(org.kuali.rice.krad.bo.Note)
     */
    @Override
	public Note save(Note note) {
    	validateNoteNotNull(note);
    	if (StringUtils.isBlank(note.getRemoteObjectIdentifier())) {
    		throw new IllegalStateException("The remote object identifier must be established on a Note before it can be saved.  Given note had a null or empty remote object identifier.");
    	}
        if (note.getAttachment() != null && note.getAttachment().getAttachmentFileName() == null) {
            note.setAttachment(null);
        }
        if (note != null && note.getNoteIdentifier() == null && note.getAttachment() != null) {
            Attachment attachment = note.getAttachment();
            note.setAttachment(null);
            // store without attachment
            note = lda.save(note);
            attachment.setNoteIdentifier(note.getNoteIdentifier());
            // put attachment back
            note.setAttachment(attachment);
        }
        note = lda.save(note);
        // move attachment from pending directory
        if (note.getAttachment() != null) {
        	attachmentService.moveAttachmentWherePending(note);
        }
        return note;
    }

    /**
     * @see org.kuali.rice.krad.service.NoteService#getByRemoteObjectId(java.lang.String)
     */
    @SuppressWarnings("deprecation")
	@Override
	public List<Note> getByRemoteObjectId(String remoteObjectId) {
    	if (StringUtils.isBlank(remoteObjectId)) {
    		throw new IllegalArgumentException("The remoteObjectId must not be null or blank.");
    	}
    	return new ArrayList<Note>( lda.findMatchingOrderBy(Note.class, Collections.singletonMap("remoteObjectIdentifier", remoteObjectId), "notePostedTimestamp", true) );
    }

    /**
     * @see org.kuali.rice.krad.service.NoteService#getNoteByNoteId(java.lang.Long)
     */
    @Override
	public Note getNoteByNoteId(Long noteId) {
    	if (noteId == null) {
    		throw new IllegalArgumentException("The noteId must not be null.");
    	}
    	return lda.findBySinglePrimaryKey(Note.class, noteId);
	}

    /**
     * @see org.kuali.rice.krad.service.NoteService#deleteNote(org.kuali.rice.krad.bo.Note)
     */
    @Override
	public void deleteNote(Note note) {
    	validateNoteNotNull(note);
    	if ( note.getAttachment() != null ) { // Not sure about this - might blow up when no attachment
    		lda.delete(note.getAttachment());
    	}
        lda.delete(note);
    }

    /**
     * TODO this method seems awfully out of place in this service
     *
     */
    @Override
	public Note createNote(Note noteToCopy, GloballyUnique bo, String authorPrincipalId) {
    	validateNoteNotNull(noteToCopy);
    	if (bo == null) {
    		throw new IllegalArgumentException("The bo must not be null.");
    	}
    	if (StringUtils.isBlank(authorPrincipalId)) {
    		throw new IllegalArgumentException("The authorPrincipalId must not be null.");
    	}

        Note tmpNote = new CopiedObject<Note>(noteToCopy).getContent();
        tmpNote.setRemoteObjectIdentifier(bo.getObjectId());
        tmpNote.setAuthorUniversalIdentifier(authorPrincipalId);

        return tmpNote;
    }

    public void setAttachmentService(AttachmentService attachmentService) {
    	this.attachmentService = attachmentService;
    }

    protected AttachmentService getAttachmentService() {
    	return this.attachmentService;
    }

    private void validateNoteNotNull(Note note) {
    	if (note == null) {
    		throw new IllegalArgumentException("Note must not be null.");
    	}
    }

}
