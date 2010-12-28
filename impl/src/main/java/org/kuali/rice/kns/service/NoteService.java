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
package org.kuali.rice.kns.service;

import java.util.List;

import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;



/**
 * This interface defines methods that a Note service must provide
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface NoteService {
    /**
     * Retrieves a list of notes for a object id
     *
     * @param remoteObjectId
     * @return A list of Notes
     * @throws Exception
     */
    public List<Note> getByRemoteObjectId(String remoteObjectId);

    /**
     * Retrieves the notes for a note identifier
     *
     * @param noteId
     * @return A Note
     */
    public Note getNoteByNoteId(Long noteId);
    
    /**
     *
     * This method saves a list of notes
     * @param noteValues
     */
    public void saveNoteList(List<Note> notes);

    /**
     * Saves a note
     *
     * @param note
     * @return The note
     * @throws Exception
     */
    public Note save(Note note);

    /**
     * Deletes a note
     *
     * @param Note
     * @throws Exception
     */
    public void deleteNote(Note note);

    public Note createNote(Note note, PersistableBusinessObject bo);

}
