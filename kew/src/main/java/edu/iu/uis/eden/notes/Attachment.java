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

import java.io.InputStream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * An attachment which is attached to a {@link Note}.
 * 
 * @see Note
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_ATTACHMENT_T")
public class Attachment {

	@Id
	@Column(name="ATTACHMENT_ID")
	private Long attachmentId;
	@Column(name="NTE_ID")
	private Long noteId;
	@Column(name="FILE_NM")
	private String fileName;
	@Column(name="FILE_LOC")
	private String fileLoc;
	@Column(name="MIME_TYP")
	private String mimeType;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;
    @Transient
	private InputStream attachedObject;
	@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="NTE_ID", insertable=false, updatable=false)
	private Note note;
	
	public Long getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}
	public String getFileLoc() {
		return fileLoc;
	}
	public void setFileLoc(String fileLoc) {
		this.fileLoc = fileLoc;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public Long getNoteId() {
		return noteId;
	}
	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}
	public Note getNote() {
		return note;
	}
	public void setNote(Note note) {
		this.note = note;
	}
	public InputStream getAttachedObject() {
		return attachedObject;
	}
	public void setAttachedObject(InputStream attachedObject) {
		this.attachedObject = attachedObject;
	}
	
	
	
}

