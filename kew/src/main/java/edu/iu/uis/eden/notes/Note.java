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
package edu.iu.uis.eden.notes;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.WorkflowPersistable;

/**
 * A note attached to a document.  May also contain a List of attachments.
 * 
 * @see Attachment
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Note implements WorkflowPersistable {

	private static final long serialVersionUID = -6136544551121011531L;
	private Long noteId;
    private Long routeHeaderId;
    private String noteAuthorWorkflowId;
    private Timestamp noteCreateDate;
    private String noteText;
    private Integer lockVerNbr;
    
    private List attachments = new ArrayList();

    //additional data not in database
    private String noteAuthorEmailAddress;
    private String noteAuthorNetworkId;
    private String noteAuthorFullName;
    private Long noteCreateLongDate;
    private Boolean authorizedToEdit; 
    private Boolean editingNote;
    
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

    public Timestamp getNoteCreateDate() {
        return noteCreateDate;
    }

    public void setNoteCreateDate(Timestamp noteCreateDate) {
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

    public String getNoteAuthorEmailAddress() {
        return noteAuthorEmailAddress;
    }
 
    public void setNoteAuthorEmailAddress(String noteAuthorEmailAddress) {
        this.noteAuthorEmailAddress = noteAuthorEmailAddress;
    }

    public String getNoteAuthorFullName() {
        return noteAuthorFullName;
    }

    public void setNoteAuthorFullName(String noteAuthorFullName) {
        this.noteAuthorFullName = noteAuthorFullName;
    }

    public String getNoteAuthorNetworkId() {
        return noteAuthorNetworkId;
    }

    public void setNoteAuthorNetworkId(String noteAuthorNetworkId) {
        this.noteAuthorNetworkId = noteAuthorNetworkId;
    }

    public Long getNoteCreateLongDate() {
        return noteCreateLongDate;
    }

    public void setNoteCreateLongDate(Long noteCreateLongDate) {
        this.noteCreateLongDate = noteCreateLongDate;
    }

    public Boolean getAuthorizedToEdit() {
        return authorizedToEdit;
    }

    public void setAuthorizedToEdit(Boolean authorizedToEdit) {
        this.authorizedToEdit = authorizedToEdit;
    }

    public Boolean getEditingNote() {
        return editingNote;
    }

    public void setEditingNote(Boolean editingNote) {
        this.editingNote = editingNote;
    }

    public String getFormattedCreateDateTime() {
        long time = getNoteCreateDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(EdenConstants.TIMESTAMP_DATE_FORMAT_PATTERN2);
        return dateFormat.format(date);
    }

    public String getFormattedCreateDate() {
        long time = getNoteCreateDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = EdenConstants.getDefaultDateFormat();
        return dateFormat.format(date);
    }
    
    public String getFormattedCreateTime() {
        long time = getNoteCreateDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = EdenConstants.getDefaultTimeFormat();
        return dateFormat.format(date);
    }
    
    public Object copy(boolean preserveKeys) {
        return null;
    }

	public List getAttachments() {
		return attachments;
	}

	public void setAttachments(List attachments) {
		this.attachments = attachments;
	}

}
