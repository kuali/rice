/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants.NoteTypeEnum;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.NoteDao;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the Note structure.
 *
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
@Transactional
public class NoteServiceImpl implements NoteService {
    // set up logging
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NoteServiceImpl.class);

    private NoteDao noteDao;

    /**
     * Default constructor
     */
    public NoteServiceImpl() {
        super();
    }

    /**
     *
     * @see org.kuali.core.service.NoteService#saveNoteValueList(java.util.List)
     */
    public void saveNoteList(List notes) {
        if (notes != null) {
            for (Iterator iter = notes.iterator(); iter.hasNext();) {
                noteDao.save((Note)iter.next());
            }
        }
    }

    /**
     * Saves a Note to the DB.
     *
     * @param Note The accounting Note object to save - can be any object that extends Note (i.e. Source and
     *        Target lines).
     */
    public Note save(Note note) throws Exception {
        noteDao.save(note);
        return note;
    }

    /**
     * Retrieves a Note by its associated object id.
     * @see org.kuali.core.service.NoteService#getByRemoteObjectId(java.lang.String)
     */
    public ArrayList getByRemoteObjectId(String remoteObjectId) {

        return noteDao.findByremoteObjectId(remoteObjectId);
    }

    /**
     * Deletes a Note from the DB.
     *
     * @param Note The Note object to delete.
     */
    public void deleteNote(Note note) throws Exception {
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

    public Note createNote(Note note, PersistableBusinessObject bo) throws Exception {
            Note tmpNote = (Note)ObjectUtils.deepCopy(note);
            UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();
            tmpNote.setRemoteObjectIdentifier(bo.getObjectId());
            tmpNote.setAuthorUniversalIdentifier(kualiUser.getPersonUniversalIdentifier());
            return tmpNote;
    }
    
    /**
     * This method gets the property name for the note
     * @param note
     * @return note property text
     */
    public String extractNoteProperty(Note note) {
        String propertyName = null;
        for (NoteTypeEnum nte : NoteTypeEnum.values()) {
            if(StringUtils.equals(nte.getCode(), note.getNoteTypeCode())) {
                propertyName = nte.getPath();
            }
        }
        return propertyName;
    }

}