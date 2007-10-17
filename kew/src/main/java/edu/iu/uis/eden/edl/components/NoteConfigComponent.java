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
package edu.iu.uis.eden.edl.components;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.edl.EDLContext;
import edu.iu.uis.eden.edl.EDLModelComponent;
import edu.iu.uis.eden.edl.EDLXmlUtils;
import edu.iu.uis.eden.edl.RequestParser;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.notes.Attachment;
import edu.iu.uis.eden.notes.CustomNoteAttribute;
import edu.iu.uis.eden.notes.Note;
import edu.iu.uis.eden.notes.NoteService;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.Utilities;

/**
 * Adds notes support to EDL
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class NoteConfigComponent implements EDLModelComponent {

	private static final Logger LOG = Logger.getLogger(NoteConfigComponent.class);
	
	public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
		NoteForm noteForm = new NoteForm(edlContext.getRequestParser());
		WorkflowDocument document = (WorkflowDocument)edlContext.getRequestParser().getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);
		try {
			//establish notes depends on a document id being set on noteform or nothing happens
			if (document != null) {
				noteForm.setDocId(document.getRouteHeaderId());
			}
			establishNotes(noteForm, edlContext, dom);
			addNotes(dom, noteForm);
		} catch (Exception e) {
			throw new WorkflowRuntimeException("Caught exception processing notes", e);
		}
		
	}
	
	public void establishNotes(NoteForm form, EDLContext edlContext, Document dom) throws Exception {

		form.setCurrentUserName(edlContext.getUserSession().getWorkflowUser().getDisplayName());
		form.setCurrentDate(getCurrentDate());
		String methodToCall = form.getMethodToCall();
		if (!Utilities.isEmpty(methodToCall)) {
			if ("save".equalsIgnoreCase(methodToCall)) {
				this.saveNote(form, edlContext, dom);
			} else if ("edit".equalsIgnoreCase(methodToCall)) {
				this.editNote(form);
			} else if ("add".equalsIgnoreCase(methodToCall)) {
				this.addNote(form);
			} else if ("cancel".equalsIgnoreCase(methodToCall)) {
				this.cancelEdit(form);
			} else if ("delete".equalsIgnoreCase(methodToCall)) {
				this.deleteNote(form);
			} else if ("sort".equalsIgnoreCase(methodToCall)) {
				this.sortNotes(form);
			} else if ("deleteAttachment".equalsIgnoreCase(methodToCall)) {
				this.deleteAttachment(form);
			}
		}
		retrieveNoteList(form, edlContext);

	}
	
    /**
     * Method added for notes editing function. Retrieve Note Listing from Route Header and put that in EdocLiteForm.
     * @param request
     * @param noteForm
     * @throws Exception
     */
    
    private void retrieveNoteList(NoteForm form, EDLContext edlContext) throws Exception {
        if (form.getDocId() != null) {
            List allNotes = getNoteService().getNotesByRouteHeaderId(form.getDocId());
            CustomNoteAttribute customNoteAttribute = null;
            DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(form.getDocId());
            boolean canAddNotes = false;
            if (routeHeader != null) {
                customNoteAttribute = routeHeader.getCustomNoteAttribute();
                if (customNoteAttribute != null) {
                	customNoteAttribute.setUserSession(edlContext.getUserSession());
                    canAddNotes = customNoteAttribute.isAuthorizedToAddNotes();       
                }
            }
            Iterator notesIter = allNotes.iterator();
            while (notesIter.hasNext()) {
                Note singleNote = (Note) notesIter.next();
                singleNote.setNoteCreateLongDate(new Long(singleNote.getNoteCreateDate().getTime()));
                getAuthorData(singleNote);
                boolean canEditNote = false;
                if (customNoteAttribute != null) {
                	canEditNote = customNoteAttribute.isAuthorizedToEditNote(singleNote);
                }
            	singleNote.setAuthorizedToEdit(Boolean.valueOf(canEditNote));
                if (form.getNoteIdNumber() != null && (form.getNoteIdNumber().intValue() == singleNote.getNoteId().intValue())) {
                    singleNote.setEditingNote(Boolean.TRUE);
                }
            }
            if (form.getSortNotes() != null && form.getSortNotes().booleanValue()) { 
                if (EdenConstants.Sorting.SORT_SEQUENCE_DSC.equalsIgnoreCase(form.getSortOrder())) {
                    form.setSortOrder(EdenConstants.Sorting.SORT_SEQUENCE_ASC);
                    form.setSortNotes(new Boolean(false));
                } else {
                    form.setSortOrder(EdenConstants.Sorting.SORT_SEQUENCE_DSC);
                    form.setSortNotes(new Boolean(false));
                }
            } else {
                form.setSortOrder(form.getSortOrder());
            }
            form.setNoteList(sortNotes(allNotes, form.getSortOrder())); 
            form.setNumberOfNotes(new Integer(allNotes.size()));
            form.setAuthorizedToAdd(new Boolean(canAddNotes));
            form.setShowAdd(Boolean.TRUE);
            if (! canAddNotes) {
                form.setShowAdd(Boolean.FALSE);
            } else if (form.getNoteList().size() == 0) {
                //form.setShowAdd(Boolean.TRUE);
            }
        }
    }
	
    public void editNote(NoteForm form) throws Exception {
        form.setShowEdit("yes");
        // Note noteToEdit =
		// getNoteService().getNoteByNoteId(form.getNoteIdNumber());
        //form.setNote(noteToEdit);
        form.getNote().setNoteCreateLongDate(new Long(form.getNote().getNoteCreateDate().getTime()));
        form.getNote().setNoteText(form.getNoteText());
        //retrieveNoteList(request, form);
    }
    
    public void addNote(NoteForm form) throws Exception {
    	form.setShowEdit("no");
    	form.setNoteIdNumber(null);
    	form.setShowAdd(Boolean.TRUE);
    	//retrieveNoteList(request,form);
    	
    }
    
    public void cancelEdit(NoteForm form) throws Exception{
    	form.setShowEdit("no");
        form.setNote(new Note());
        form.setNoteIdNumber(null);
        //retrieveNoteList(request, form);
    }
    
    public void deleteNote(NoteForm form) throws Exception{
    	Note noteToDelete = getNoteService().getNoteByNoteId(form.getNoteIdNumber());
        getNoteService().deleteNote(noteToDelete);
    	form.setShowEdit("no");
        //retrieveNoteList(request, form);
        form.setNote(new Note());
        form.setNoteIdNumber(null);
    }
    
    public void sortNotes(NoteForm form) throws Exception{
    	form.setShowEdit("no");
    }
    
    public void deleteAttachment(NoteForm form) throws Exception{
    	Note note = getNoteService().getNoteByNoteId(form.getNoteIdNumber());
    	getNoteService().deleteAttachment((Attachment)note.getAttachments().remove(0)); 	
	}	
	
	public void saveNote(NoteForm form, EDLContext edlContext, Document dom) throws Exception {
        Note noteToSave = null;
        if (form.getShowEdit()!= null && form.getShowEdit().equals("yes")) {
        	//LOG.debug(form.getNoteIdNumber());
            noteToSave = getNoteService().getNoteByNoteId(form.getNoteIdNumber());
            String noteText = form.getNoteText();
            if (noteText != null){
            	noteToSave.setNoteText(noteText);
            }
            //LOG.debug(noteToSave);
            //LOG.debug(noteToSave.getNoteCreateDate());
            //noteToSave.setNoteCreateDate(new Timestamp(noteToSave.getNoteCreateLongDate().longValue()));
        } else {
            noteToSave = new Note();
            noteToSave.setNoteId(null);
            noteToSave.setRouteHeaderId(form.getDocId());
            noteToSave.setNoteCreateDate(new Timestamp((new Date()).getTime()));            
            noteToSave.setNoteAuthorWorkflowId(edlContext.getUserSession().getWorkflowUser().getWorkflowUserId().getWorkflowId());
            noteToSave.setNoteText(form.getAddText());
        }
        CustomNoteAttribute customNoteAttribute = null;
        DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(noteToSave.getRouteHeaderId());
        boolean canEditNote = false;
        boolean canAddNotes = false;
        if (routeHeader != null) {
            customNoteAttribute = routeHeader.getCustomNoteAttribute();
            if (customNoteAttribute != null) {
                customNoteAttribute.setUserSession(edlContext.getUserSession());
                canAddNotes = customNoteAttribute.isAuthorizedToAddNotes();
                canEditNote = customNoteAttribute.isAuthorizedToEditNote(noteToSave);       
            }
        }
        if ((form.getShowEdit()!=null && form.getShowEdit().equals("yes") && canEditNote) ||
                ((form.getShowEdit() == null || ! form.getShowEdit().equals("yes")) && canAddNotes)) {
        			FileItem uploadedFile = (FileItem)form.getFile();        			
        			if (uploadedFile != null && org.apache.commons.lang.StringUtils.isNotBlank(uploadedFile.getName())) {
        				Attachment attachment = new Attachment();
        				attachment.setAttachedObject(uploadedFile.getInputStream());
        				String internalFileIndicator=uploadedFile.getName();
        				int indexOfSlash = internalFileIndicator.lastIndexOf("/");
        				int indexOfBackSlash=internalFileIndicator.lastIndexOf("\\");
        				if(indexOfSlash>=0){
        					internalFileIndicator=internalFileIndicator.substring(indexOfSlash+1);
        				}else{
        					if (indexOfBackSlash>=0){
        						internalFileIndicator=internalFileIndicator.substring(indexOfBackSlash+1);
        					}
        				}
        				attachment.setFileName(internalFileIndicator);
        				LOG.debug(internalFileIndicator);
        				attachment.setMimeType(uploadedFile.getContentType());
        				attachment.setNote(noteToSave);
        				noteToSave.getAttachments().add(attachment);
        			}
        	if(Utilities.isEmpty(noteToSave.getNoteText())&&noteToSave.getAttachments().size()==0){
        		if (form.getShowEdit()!=null && form.getShowEdit().equals("yes")) {
                    form.setNote(new Note());
                } else {
                    form.setAddText(null); 
                }
                form.setShowEdit("no");
                form.setNoteIdNumber(null);
//        		throw new Exception("Note has empty content");
                EDLXmlUtils.addGlobalErrorMessage(dom, "Note has empty content");
                return;
        	}
            getNoteService().saveNote(noteToSave);
        } 
        if (form.getShowEdit()!=null && form.getShowEdit().equals("yes")) {
            form.setNote(new Note());
        } else {
            form.setAddText(null); 
        }
        form.setShowEdit("no");
        form.setNoteIdNumber(null);
    }

	public static void addNotes(Document doc, NoteForm form) {
		Element noteForm = EDLXmlUtils.getOrCreateChildElement(doc.getDocumentElement(), "NoteForm", true);
		if (form.getShowEdit() != null) {
			Element showEdit = EDLXmlUtils.getOrCreateChildElement(noteForm, "showEdit", true);
			showEdit.appendChild(doc.createTextNode(form.getShowEdit().toLowerCase()));
		} else {
			Element showEdit = EDLXmlUtils.getOrCreateChildElement(noteForm, "showEdit", true);
			showEdit.appendChild(doc.createTextNode("no"));
		}
		if (form.getShowAdd() != null) {
			Element showAdd = EDLXmlUtils.getOrCreateChildElement(noteForm, "showAdd", true);
			showAdd.appendChild(doc.createTextNode(form.getShowAdd().toString().toLowerCase()));
		}
		if (form.getCurrentUserName() != null) {
			Element currentUserName = EDLXmlUtils.getOrCreateChildElement(noteForm, "currentUserName", true);
			currentUserName.appendChild(doc.createTextNode(form.getCurrentUserName()));
		}
		if (form.getCurrentDate() != null) {
			Element currentDate = EDLXmlUtils.getOrCreateChildElement(noteForm, "currentDate", true);
			currentDate.appendChild(doc.createTextNode(form.getCurrentDate()));
		}
		if (form.getNoteIdNumber() != null) {
			Element noteIdNumber = EDLXmlUtils.getOrCreateChildElement(noteForm, "noteIdNumber", true);
			noteIdNumber.appendChild(doc.createTextNode(form.getNoteIdNumber().toString()));
		}
		if (form.getDocId() != null) {
			Element docId = EDLXmlUtils.getOrCreateChildElement(noteForm, "docId", true);
			docId.appendChild(doc.createTextNode((form.getDocId().toString())));
		}
		if (form.getSortNotes() != null) {
			Element sortNotes = EDLXmlUtils.getOrCreateChildElement(noteForm, "sortNotes", true);
			sortNotes.appendChild(doc.createTextNode(form.getSortNotes().toString().toLowerCase()));
		}
		if (form.getSortOrder() != null) {
			Element sortOrder = EDLXmlUtils.getOrCreateChildElement(noteForm, "sortOrder", true);
			sortOrder.appendChild(doc.createTextNode(form.getSortOrder().toUpperCase()));
		}
		if (form.getNumberOfNotes() != null) {
			Element numberOfNotes = EDLXmlUtils.getOrCreateChildElement(noteForm, "numberOfNotes", true);
			numberOfNotes.appendChild(doc.createTextNode(form.getNumberOfNotes().toString()));
		}
		if (form.getAuthorizedToAdd() != null) {
			Element authorizedToAdd = EDLXmlUtils.getOrCreateChildElement(noteForm, "authorizedToAdd", true);
			authorizedToAdd.appendChild(doc.createTextNode(form.getAuthorizedToAdd().toString().toLowerCase()));
		}
		if (form.getNumberOfNotes().intValue() > 0) {
			Element notes = EDLXmlUtils.getOrCreateChildElement(noteForm, "Notes", true);
			for (Iterator i = form.getNoteList().iterator(); i.hasNext();) {
				Note noteObj = (Note) i.next();
				Element note = notes.getOwnerDocument().createElement("Note");
				notes.appendChild(note);
				// Element note = Util.getOrCreateChildElement(notes, "Note",
				// true);
				if (noteObj.getNoteId() != null) {
					Element noteId = EDLXmlUtils.getOrCreateChildElement(note, "noteId", true);
					noteId.appendChild(doc.createTextNode(noteObj.getNoteId().toString()));
				}
				if (noteObj.getFormattedCreateDate() != null) {
					Element formattedCreateDate = EDLXmlUtils.getOrCreateChildElement(note, "formattedCreateDate", true);
					formattedCreateDate.appendChild(doc.createTextNode(noteObj.getFormattedCreateDate()));
				}
				if (noteObj.getFormattedCreateTime() != null) {
					Element formattedCreateTime = EDLXmlUtils.getOrCreateChildElement(note, "formattedCreateTime", true);
					formattedCreateTime.appendChild(doc.createTextNode(noteObj.getFormattedCreateTime()));
				}
				if (noteObj.getNoteAuthorFullName() != null) {
					Element noteAuthorFullName = EDLXmlUtils.getOrCreateChildElement(note, "noteAuthorFullName", true);
					noteAuthorFullName.appendChild(doc.createTextNode(noteObj.getNoteAuthorFullName()));
				}
				if (noteObj.getNoteText() != null) {
					Element noteText = EDLXmlUtils.getOrCreateChildElement(note, "noteText", true);
					noteText.appendChild(doc.createTextNode(noteObj.getNoteText()));
				}
				if (noteObj.getEditingNote() != null) {
					Element editingNote = EDLXmlUtils.getOrCreateChildElement(note, "editingNote", true);
					editingNote.appendChild(doc.createTextNode(noteObj.getEditingNote().toString()));
				}
				if (noteObj.getAuthorizedToEdit() != null) {
					Element authorizedToEdit = EDLXmlUtils.getOrCreateChildElement(note, "authorizedToEdit", true);
					authorizedToEdit.appendChild(doc.createTextNode(noteObj.getAuthorizedToEdit().toString()));
				}
				if (!noteObj.getAttachments().isEmpty()) {
					Element attachments = EDLXmlUtils.getOrCreateChildElement(note, "attachments", true);
					for (Iterator j = noteObj.getAttachments().iterator(); j.hasNext();) {
						Attachment attachmentObj = (Attachment) j.next();
						Element attachment = EDLXmlUtils.getOrCreateChildElement(attachments, "attachment", true);
						Element attachmentId = EDLXmlUtils.getOrCreateChildElement(attachment, "attachmentId", true);
						attachmentId.appendChild(doc.createTextNode(attachmentObj.getAttachmentId().toString()));
						Element fileName = EDLXmlUtils.getOrCreateChildElement(attachment, "fileName", true);
						fileName.appendChild(doc.createTextNode(attachmentObj.getFileName()));
					}
				}
			}
		}

	}

	private static class NoteForm {
		private String showEdit;
		private Boolean showAdd;
		private Long noteIdNumber;
		private Integer numberOfNotes = new Integer(0);
		private String sortOrder = "DESCENDING";
		private Boolean sortNotes;
		private String currentUserName;
		private String currentDate;
		private Boolean authorizedToAdd;
		private List noteList;
		private String addText;
		private Long idInEdit;
		private Note note;
		private String noteText;
		private Long docId;
		private String methodToCall;
		private FileItem file;

		public NoteForm(RequestParser requestParser) {
			
			showEdit = requestParser.getParameterValue("showEdit");
			if (!Utilities.isEmpty(requestParser.getParameterValue("showAdd"))) {
				showAdd = Boolean.valueOf(requestParser.getParameterValue("showAdd"));
			}
			if (!Utilities.isEmpty(requestParser.getParameterValue("noteIdNumber"))) {
				noteIdNumber = Long.valueOf(requestParser.getParameterValue("noteIdNumber"));
			}
			methodToCall = requestParser.getParameterValue("methodToCall");
			sortOrder = "DESCENDING";
			if (!Utilities.isEmpty(requestParser.getParameterValue("sortNotes"))) {
				sortNotes = Boolean.valueOf(requestParser.getParameterValue("sortNotes"));
			}
			addText = requestParser.getParameterValue("addText");
			noteText = requestParser.getParameterValue("noteText");
			if (!Utilities.isEmpty(requestParser.getParameterValue("idInEdit"))) {
				idInEdit = Long.valueOf(requestParser.getParameterValue("idInEdit"));
			}
			if (noteIdNumber != null) {
				note = KEWServiceLocator.getNoteService().getNoteByNoteId(noteIdNumber);
			}
			if (requestParser.getUploadList() != null && ! requestParser.getUploadList().isEmpty()) {
				file = (FileItem)requestParser.getUploadList().get(0);
			}
		}

		public String getAddText() {
			return addText;
		}

		public void setAddText(String addText) {
			this.addText = addText;
		}

		public Boolean getAuthorizedToAdd() {
			return authorizedToAdd;
		}

		public void setAuthorizedToAdd(Boolean authorizedToAdd) {
			this.authorizedToAdd = authorizedToAdd;
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

		public Long getIdInEdit() {
			return idInEdit;
		}

		public void setIdInEdit(Long idInEdit) {
			this.idInEdit = idInEdit;
		}

		public Note getNote() {
			return note;
		}

		public void setNote(Note note) {
			this.note = note;
		}

		public Long getNoteIdNumber() {
			return noteIdNumber;
		}

		public void setNoteIdNumber(Long noteIdNumber) {
			this.noteIdNumber = noteIdNumber;
		}

		public List getNoteList() {
			return noteList;
		}

		public void setNoteList(List noteList) {
			this.noteList = noteList;
		}

		public String getNoteText() {
			return noteText;
		}

		public void setNoteText(String noteText) {
			this.noteText = noteText;
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

		public String getShowEdit() {
			return showEdit;
		}

		public void setShowEdit(String showEdit) {
			this.showEdit = showEdit;
		}

		public Boolean getSortNotes() {
			return sortNotes;
		}

		public void setSortNotes(Boolean sortNotes) {
			this.sortNotes = sortNotes;
		}

		public String getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
		}

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public String getMethodToCall() {
			return methodToCall;
		}

		public void setMethodToCall(String methodToCall) {
			this.methodToCall = methodToCall;
		}

		public FileItem getFile() {
			return file;
		}

		public void setFile(FileItem file) {
			this.file = file;
		}
	}
	
    /**
     * Method added for notes editing function. Called by retrieveNoteList method
     * @param allNotes
     * @param sortOrder
     * @return
     */
	
    private List sortNotes(List allNotes, String sortOrder) {
        final int returnCode = EdenConstants.Sorting.SORT_SEQUENCE_DSC.equalsIgnoreCase(sortOrder) ? -1 : 1;  

        try {
          Collections.sort(allNotes,
          new Comparator() {
            public int compare(Object o1, Object o2) {
  			Timestamp date1 = ((Note) o1).getNoteCreateDate();
  			Timestamp date2 = ((Note) o2).getNoteCreateDate();

                if (date1.before(date2)) {
                  return returnCode * -1;
                } else if (date1.after(date2)) {
                  return returnCode;
                } else {
                  return 0;
                }
            }
          });
      } catch (Throwable e) {
        LOG.error(e.getMessage(), e);
      }
      return allNotes;
    }
	
    /**
     * Method added for notes editing function. Called by retrieveNoteList method
     * @param note
     * @throws Exception
     */
	
    private void getAuthorData(Note note) throws Exception {
	    WorkflowUser workflowUser = null;
	    String id = ""; 
	    if (note != null && note.getNoteAuthorWorkflowId() != null && ! "".equalsIgnoreCase(note.getNoteAuthorWorkflowId())) {
	        workflowUser = getUserService().getWorkflowUser(new WorkflowUserId(note.getNoteAuthorWorkflowId()));
	        id = note.getNoteAuthorWorkflowId();
	    } 
	    if (workflowUser != null) {
	        note.setNoteAuthorFullName(workflowUser.getDisplayName());
	        note.setNoteAuthorEmailAddress(workflowUser.getEmailAddress());
	        note.setNoteAuthorNetworkId(workflowUser.getAuthenticationUserId().getAuthenticationId());
	    } else {
	        note.setNoteAuthorFullName(id + " (Name not Available)");
	        note.setNoteAuthorEmailAddress("Not Available");
	        note.setNoteAuthorNetworkId("Not Available");
	    }
	}	
	
	public String getCurrentDate() {
        Date currentDate = new Date();
        DateFormat dateFormat = EdenConstants.getDefaultDateFormat();
        return dateFormat.format(currentDate);
    }
	
	/**
	 * Method added for notes editing function.
	 * @return
	 */
    private NoteService getNoteService() {
		return (NoteService) KEWServiceLocator.getService(KEWServiceLocator.NOTE_SERVICE);
	}

	/**
	 * Method added for notes editing function.
	 * @return
	 */
    private UserService getUserService() {
		return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
	}

	
    /**
     * Method added for notes editing function.
     * @return
     */
    private RouteHeaderService getRouteHeaderService() {
		return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
	}
}
