/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.clientapp.vo;

import java.io.Serializable;
import java.util.Calendar;

import edu.iu.uis.eden.notes.Note;

/**
 * Transport object for a {@link Note}
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class NoteVO implements Serializable{
	
	static final long serialVersionUID = 3278600875270364172L;	
	
	private Long noteId;
    private Long routeHeaderId;
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
	public Long getNoteId() {
		return noteId;
	}
	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}
	public String getNoteText() {
		return noteText;
	}
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}
	public Long getRouteHeaderId() {
		return routeHeaderId;
	}
	public void setRouteHeaderId(Long routeHeaderId) {
		this.routeHeaderId = routeHeaderId;
	}

}
