/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.notes;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.note.NoteContract;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A note attached to a document.  May also contain a List of attachments.
 * 
 * @see Attachment
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity(name="org.kuali.rice.kew.notes.Note")
@Table(name="KREW_DOC_NTE_T")
@NamedQueries({
	@NamedQuery(name="KewNote.FindNoteByDocumentId", query="select n from org.kuali.rice.kew.notes.Note as n "
            + "where n.documentId = :documentId order by n.noteId")
})
public class Note implements Serializable, NoteContract {

	private static final long serialVersionUID = -6136544551121011531L;

	@Id
    @GeneratedValue(generator = "KREW_DOC_NTE_S")
	@PortableSequenceGenerator(name="KREW_DOC_NTE_S")
	@Column(name="DOC_NTE_ID")
	private String noteId;

    @Column(name="DOC_HDR_ID")
	private String documentId;

    @Column(name="AUTH_PRNCPL_ID")
	private String noteAuthorWorkflowId;

	@Column(name="CRT_DT")
	private Timestamp noteCreateDate;

    @Column(name="TXT")
	private String noteText;

    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
    
    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="note")
    private List<Attachment> attachments = new ArrayList<Attachment>();

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
        DateFormat dateFormat = new SimpleDateFormat(KewApiConstants.TIMESTAMP_DATE_FORMAT_PATTERN2);
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

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	// new methods from NoteContract in 2.0

	@Override
	public String getId() {
		if (getNoteId() == null) {
			return null;
		}
		return getNoteId().toString();
	}

	@Override
	public Long getVersionNumber() {
		if (getLockVerNbr() == null) {
			return null;
		}
		return new Long(getLockVerNbr().longValue());
	}

	@Override
	public String getAuthorPrincipalId() {
		return getNoteAuthorWorkflowId();
	}

	@Override
	public DateTime getCreateDate() {
		if (getNoteCreateDate() == null) {
			return null;
		}
		return new DateTime(getNoteCreateDate().getTime());
	}

	@Override
	public String getText() {
		return getNoteText();
	}

    public Note deepCopy(Map<Object, Object> visited) {
        if (visited.containsKey(this)) {
            return (Note)visited.get(this);
        }
        Note copy = new Note();
        visited.put(this, copy);
        copy.noteId = noteId;
        copy.documentId = documentId;
        copy.noteAuthorWorkflowId = noteAuthorWorkflowId;
        if (noteCreateDate != null) {
            copy.noteCreateDate = new Timestamp(noteCreateDate.getTime());
        }
        copy.noteText = noteText;
        copy.lockVerNbr = lockVerNbr;
        copy.noteAuthorEmailAddress = noteAuthorEmailAddress;
        copy.noteAuthorNetworkId = noteAuthorNetworkId;
        copy.noteAuthorFullName = noteAuthorFullName;
        copy.noteCreateLongDate = noteCreateLongDate;
        copy.authorizedToEdit = authorizedToEdit;
        copy.editingNote = editingNote;
        if (attachments != null) {
            List<Attachment> copies = new ArrayList<Attachment>();
            for (Attachment attachment : attachments) {
                copies.add(attachment.deepCopy(visited));
            }
            copy.attachments = copies;
        }
        return copy;
    }
	
	public static org.kuali.rice.kew.api.note.Note to(Note note) {
		if (note == null) {
			return null;
		}
		return org.kuali.rice.kew.api.note.Note.Builder.create(note).build();
	}
	
	public static Note from(org.kuali.rice.kew.api.note.Note note) {
		if (note == null) {
			return null;
		}
		Note noteBo = new Note();
		if (note.getId() != null) {
			noteBo.setNoteId(note.getId());
		}
		noteBo.setDocumentId(note.getDocumentId());
		noteBo.setNoteAuthorWorkflowId(note.getAuthorPrincipalId());
		if (note.getCreateDate() != null) {
			noteBo.setNoteCreateDate(new Timestamp(note.getCreateDate().getMillis()));
		}
		noteBo.setNoteText(note.getText());
		if (note.getVersionNumber() != null) {
			noteBo.setLockVerNbr(Integer.valueOf(note.getVersionNumber().intValue()));
		}
		return noteBo;
	}
	
}

