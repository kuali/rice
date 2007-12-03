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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
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
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * Struts action for interfacing with the Notes system.
 * 
 * @see NoteService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NoteAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NoteAction.class);
    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("allNotesReport");
    }

    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        NoteForm noteForm = (NoteForm) form;
        noteForm.setShowEdit("no");
        noteForm.setNoteIdNumber(null);
        retrieveNoteList(request, noteForm);
        noteForm.setShowAdd(Boolean.TRUE);
        return start(mapping, form, request, response);
    }

    public ActionForward deleteAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	NoteForm noteForm = (NoteForm) form;
    	NoteService noteService = KEWServiceLocator.getNoteService();
    	Note note = noteService.getNoteByNoteId(noteForm.getNote().getNoteId());
    	noteService.deleteAttachment((Attachment)note.getAttachments().remove(0));
    	noteForm.setDocId(note.getRouteHeaderId());
    	noteForm.setNoteIdNumber(note.getNoteId());
    	edit(mapping, form, request, response);
    	return mapping.findForward("allNotesReport");
    }
    
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        NoteForm noteForm = (NoteForm) form;
        if ("yes".equalsIgnoreCase(noteForm.getShowEdit())) {
            noteForm.setNoteIdNumber(noteForm.getNote().getNoteId());
        } else {
            noteForm.setShowEdit("yes");
            Note noteToEdit = getNoteService().getNoteByNoteId(noteForm.getNoteIdNumber());
            noteForm.setNote(noteToEdit);
            noteForm.getNote().setNoteCreateLongDate(new Long(noteForm.getNote().getNoteCreateDate().getTime()));
        }   
        retrieveNoteList(request, noteForm);
        return start(mapping, form, request, response);
    }
    
//    public ActionForward attachFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//    	
//    	
//    	return start(mapping, form, request, response);
//    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        NoteForm noteForm = (NoteForm) form;
        Note noteToSave = null;
        if (noteForm.getShowEdit().equals("yes")) {
            noteToSave = noteForm.getNote();
            noteToSave.setNoteCreateDate(new Timestamp(noteToSave.getNoteCreateLongDate().longValue()));
        } else {
            noteToSave = new Note();
            noteToSave.setNoteId(null);
            noteToSave.setRouteHeaderId(noteForm.getDocId());
            noteToSave.setNoteCreateDate(new Timestamp((new Date()).getTime()));
            noteToSave.setNoteAuthorWorkflowId(getUserSession(request).getWorkflowUser().getWorkflowUserId().getWorkflowId());
            noteToSave.setNoteText(noteForm.getAddText());
        }
        CustomNoteAttribute customNoteAttribute = null;
        DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(noteToSave.getRouteHeaderId());
        boolean canEditNote = false;
        boolean canAddNotes = false;
        if (routeHeader != null) {
            customNoteAttribute = routeHeader.getCustomNoteAttribute();
            if (customNoteAttribute != null) {
                customNoteAttribute.setUserSession(getUserSession(request));
                canAddNotes = customNoteAttribute.isAuthorizedToAddNotes();
                canEditNote = customNoteAttribute.isAuthorizedToEditNote(noteToSave);
            }
        }
        
        if ((noteForm.getShowEdit().equals("yes") && canEditNote) ||
                (!noteForm.getShowEdit().equals("yes") && canAddNotes)) {
        	FormFile uploadedFile = (FormFile)noteForm.getFile();
        	if (uploadedFile != null && StringUtils.isNotBlank(uploadedFile.getFileName())) {
        		Attachment attachment = new Attachment();
        		attachment.setAttachedObject(uploadedFile.getInputStream());
        		attachment.setFileName(uploadedFile.getFileName());
        		attachment.setMimeType(uploadedFile.getContentType());
        		attachment.setNote(noteToSave);
        		noteToSave.getAttachments().add(attachment);
        	}
            getNoteService().saveNote(noteToSave);
        } 
        if (noteForm.getShowEdit().equals("yes")) {
            noteForm.setNote(new Note());
        } else {
            noteForm.setAddText(null); 
        }
        noteForm.setShowEdit("no");
        noteForm.setNoteIdNumber(null);
        retrieveNoteList(request, noteForm);
        return start(mapping, form, request, response);
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        NoteForm noteForm = (NoteForm) form;
        Note existingNote = getNoteService().getNoteByNoteId(noteForm.getNoteIdNumber());
        getNoteService().deleteNote(existingNote);
        noteForm.setShowEdit("no");
        noteForm.setNoteIdNumber(null);
        retrieveNoteList(request, noteForm);
        return start(mapping, form, request, response);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        NoteForm noteForm = (NoteForm) form;
        noteForm.setShowEdit("no");
        noteForm.setNote(new Note());
        noteForm.setNoteIdNumber(null);
        retrieveNoteList(request, noteForm);
        return start(mapping, form, request, response);
    }
        
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return start(mapping, form, request, response);
    }
    

    
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        NoteForm noteForm = (NoteForm) form;
        noteForm.setCurrentUserName(getUserSession(request).getWorkflowUser().getDisplayName());
        noteForm.setCurrentDate(getCurrentDate());
        if (! "workflowReport".equalsIgnoreCase(noteForm.getMethodToCall()) && ! "add".equalsIgnoreCase(noteForm.getMethodToCall()) && ! "cancel".equalsIgnoreCase(noteForm.getMethodToCall()) && ! "edit".equalsIgnoreCase(noteForm.getMethodToCall()) && ! "delete".equalsIgnoreCase(noteForm.getMethodToCall()) && ! "save".equalsIgnoreCase(noteForm.getMethodToCall())) {
            retrieveNoteList(request, noteForm);
        }
        noteForm.setShowAttachments(new Boolean(Utilities.getApplicationConstant(EdenConstants.APP_CONST_SHOW_ATTACHMENTS)));
        return null;
    }

    private void retrieveNoteList(HttpServletRequest request, NoteForm noteForm) throws Exception {
        if (noteForm.getDocId() != null) {
//            List allNotes = getNoteService().getNotesByRouteHeaderId(noteForm.getDocId());
        	
            CustomNoteAttribute customNoteAttribute = null;
            DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(noteForm.getDocId());
            
            List allNotes = routeHeader.getNotes();
            boolean canAddNotes = false;
            if (routeHeader != null) {
                customNoteAttribute = routeHeader.getCustomNoteAttribute();
                if (customNoteAttribute != null) {
                    customNoteAttribute.setUserSession(getUserSession(request));
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
                singleNote.setAuthorizedToEdit(new Boolean(canEditNote));
                if (noteForm.getNoteIdNumber() != null && (noteForm.getNoteIdNumber().intValue() == singleNote.getNoteId().intValue())) {
                    singleNote.setEditingNote(Boolean.TRUE);
                }
            }
            if (noteForm.getSortNotes() != null && noteForm.getSortNotes().booleanValue()) { 
                if (EdenConstants.Sorting.SORT_SEQUENCE_DSC.equalsIgnoreCase(noteForm.getSortOrder())) {
                    noteForm.setSortOrder(EdenConstants.Sorting.SORT_SEQUENCE_ASC);
                    noteForm.setSortNotes(new Boolean(false));
                } else {
                    noteForm.setSortOrder(EdenConstants.Sorting.SORT_SEQUENCE_DSC);
                    noteForm.setSortNotes(new Boolean(false));
                }
            } else {
                noteForm.setSortOrder(noteForm.getSortOrder());
            }
            noteForm.setNoteList(sortNotes(allNotes, noteForm.getSortOrder())); 
            noteForm.setNumberOfNotes(new Integer(allNotes.size()));
            noteForm.setAuthorizedToAdd(new Boolean(canAddNotes));
            noteForm.setShowAdd(Boolean.TRUE);
            if (! canAddNotes) {
                noteForm.setShowAdd(Boolean.FALSE);
            } else if (noteForm.getNoteList().size() == 0) {
                noteForm.setShowAdd(Boolean.FALSE);
            }
        }
    }
  
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
   
    private NoteService getNoteService() {
        return (NoteService) KEWServiceLocator.getService(KEWServiceLocator.NOTE_SERVICE);
    }

    private UserService getUserService() {
        return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    }
    
    private RouteHeaderService getRouteHeaderService() {
        return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }

}
