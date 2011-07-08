/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Transport object for a Note
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NoteDTO implements Serializable{
	
	static final long serialVersionUID = 3278600875270364172L;	
	
	private String noteId;
    private String documentId;
    private String noteAuthorWorkflowId;
    private Calendar noteCreateDate;
    private String noteText;
    private Integer lockVerNbr;
    
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public String getNoteAuthorWorkflowId() {
		return noteAuthorWorkflowId;
	}
	public void setNoteAuthorWorkflowId(String noteAuthorWorkflowId) {
		this.noteAuthorWorkflowId = noteAuthorWorkflowId;
	}
	public Calendar getNoteCreateDate() {
		return noteCreateDate;
	}
	public void setNoteCreateDate(Calendar noteCreateDate) {
		this.noteCreateDate = noteCreateDate;
	}
	public String getNoteId() {
		return noteId;
	}
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}
	public String getNoteText() {
		return noteText;
	}
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

}
