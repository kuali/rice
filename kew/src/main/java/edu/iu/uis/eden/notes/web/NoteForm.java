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
package edu.iu.uis.eden.notes.web;

import java.util.List;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.notes.Note;
import edu.iu.uis.eden.util.Utilities;

/**
 * Struts ActionForm for {@link NoteAction}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NoteForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	private Note note;
    private Note existingNote;
    private String methodToCall = "";    
    private String showEdit;
    private Boolean showAdd;
    private Long docId;
    private Long noteIdNumber;
    private Integer numberOfNotes;
    private String sortOrder = "DESCENDING";
    private Boolean sortNotes;
    private String currentUserName;
    private String currentDate;
    private Boolean authorizedToAdd;
    private List noteList;
    private String addText;
    private Long idInEdit;
    private Boolean showAttachments;
    private String attachmentTarget;
    
    
    private Object file;
    
    public NoteForm() {
        note = new Note();
    }

    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
 
    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Note getExistingNote() {
        return existingNote;
    }

    public void setExistingNote(Note existingNote) {
        this.existingNote = existingNote;
    }

    public String getShowEdit() {
        return showEdit;
    }

    public void setShowEdit(String showEdit) {
        this.showEdit = showEdit;
    }
  
    public String getInstructionForCreateNew() {
        return Utilities.getApplicationConstant(EdenConstants.NOTE_CREATE_NEW_INSTRUCTION_KEY);
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Integer getNumberOfNotes() {
        return numberOfNotes;
    }

    public void setNumberOfNotes(Integer numberOfNotes) {
        this.numberOfNotes = numberOfNotes;
    }

    public Boolean getShowAdd() {
        return showAdd;
    }

    public void setShowAdd(Boolean showAdd) {
        this.showAdd = showAdd;
    }

    public Long getNoteIdNumber() {
        return noteIdNumber;
    }

    public void setNoteIdNumber(Long noteIdNumber) {
        this.noteIdNumber = noteIdNumber;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Boolean getSortNotes() {
        return sortNotes;
    }

    public void setSortNotes(Boolean sortNotes) {
        this.sortNotes = sortNotes;
    }
    
    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public Boolean getAuthorizedToAdd() {
        return authorizedToAdd;
    }

    public void setAuthorizedToAdd(Boolean authorizedToAdd) {
        this.authorizedToAdd = authorizedToAdd;
    }
    
    public List getNoteList() {
        return noteList;
    }

    public void setNoteList(List noteList) {
        this.noteList = noteList;
    }
    
    public String getAddText() {
        return addText;
    }

    public void setAddText(String addText) {
        this.addText = addText;
    }

    public Long getIdInEdit() {
        return idInEdit;
    }

    public void setIdInEdit(Long idInEdit) {
        this.idInEdit = idInEdit;
    }

	public Object getFile() {
		return file;
	}

	public void setFile(Object file) {
		this.file = file;
	}

	public Boolean getShowAttachments() {
		return showAttachments;
	}

	public void setShowAttachments(Boolean showAttachments) {
		this.showAttachments = showAttachments;
	}

	public String getAttachmentTarget() {
		return attachmentTarget;
	}

	public void setAttachmentTarget(String attachmentTarget) {
		this.attachmentTarget = attachmentTarget;
	}
}