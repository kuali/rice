/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.notes.dao;

import java.util.List;

import edu.iu.uis.eden.notes.Attachment;
import edu.iu.uis.eden.notes.Note;

public interface NoteDAO {

    public Note getNoteByNoteId(Long noteId);

    public List getNotesByRouteHeaderId(Long routeHeaderId);
    
    public void saveNote(Note note);

    public void deleteNote(Note note);    
    
    public void deleteAttachment(Attachment attachment);

    public Attachment findAttachment(Long attachmentId);
}