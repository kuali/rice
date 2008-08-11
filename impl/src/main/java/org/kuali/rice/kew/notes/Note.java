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
package org.kuali.rice.kew.notes;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.bo.WorkflowPersistable;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * A note attached to a document.  May also contain a List of attachments.
 * 
 * @see Attachment
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_DOC_NTE_T")
public class Note implements WorkflowPersistable {

	private static final long serialVersionUID = -6136544551121011531L;
	@Id
	@Column(name="DOC_NTE_ID")
	private Long noteId;
    @Column(name="DOC_HDR_ID")
	private Long routeHeaderId;
    @Column(name="DOC_NTE_AUTH_PRSN_EN_ID")
	private String noteAuthorWorkflowId;
    //@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DOC_NTE_CRT_DT")
	private Timestamp noteCreateDate;
    @Column(name="DOC_NTE_TXT")
	private String noteText;
    @Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;
    
    @Transient
    private List attachments = new ArrayList();

    //additional data not in database
    @Transient
    private String noteAuthorEmailAddress;
    @Transient
    private String noteAuthorNetworkId;
    @Transient
    private String noteAuthorFullName;
    @Transient
    private Long noteCreateLongDate;
    @Transient
    private Boolean authorizedToEdit; 
    @Transient
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
        DateFormat dateFormat = new SimpleDateFormat(KEWConstants.TIMESTAMP_DATE_FORMAT_PATTERN2);
        return dateFormat.format(date);
    }

    public String getFormattedCreateDate() {
        long time = getNoteCreateDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = RiceConstants.getDefaultDateFormat();
        return dateFormat.format(date);
    }
    
    public String getFormattedCreateTime() {
        long time = getNoteCreateDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = RiceConstants.getDefaultTimeFormat();
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

