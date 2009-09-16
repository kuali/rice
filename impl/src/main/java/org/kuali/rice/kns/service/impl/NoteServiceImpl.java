/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.NoteDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.KNSConstants.NoteTypeEnum;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the Note structure.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional
public class NoteServiceImpl implements NoteService {
    // set up logging
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NoteServiceImpl.class);

    private NoteDao noteDao;
    private PersonService personService;
    private WorkflowDocumentService workflowDocumentService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * Default constructor
     */
    public NoteServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.service.NoteService#saveNoteValueList(java.util.List)
     */
    public void saveNoteList(List notes) {
        if (notes != null) {
            for (Iterator iter = notes.iterator(); iter.hasNext();) {
                noteDao.save((Note) iter.next());
            }
        }
    }

    /**
     * Saves a Note to the DB.
     * 
     * @param Note The accounting Note object to save - can be any object that extends Note (i.e. Source and Target lines).
     */
    public Note save(Note note) throws Exception {
        noteDao.save(note);
        return note;
    }

    /**
     * Retrieves a Note by its associated object id.
     * 
     * @see org.kuali.rice.kns.service.NoteService#getByRemoteObjectId(java.lang.String)
     */
    public ArrayList getByRemoteObjectId(String remoteObjectId) {

        return noteDao.findByremoteObjectId(remoteObjectId);
    }
    
    /**
     * Retrieves a Note by note identifier.
     * 
     * @see org.kuali.rice.kns.service.NoteService#getNoteByNoteId(java.lang.Long)
     */
    public Note getNoteByNoteId(Long noteId) {
		return noteDao.getNoteByNoteId(noteId);
	}

    /**
     * Deletes a Note from the DB.
     * 
     * @param Note The Note object to delete.
     */
    public void deleteNote(Note note) throws Exception {
        noteDao.deleteNote(note);
    }

    // needed for Spring injection
    /**
     * Sets the data access object
     * 
     * @param d
     */
    public void setNoteDao(NoteDao d) {
        this.noteDao = d;
    }

    /**
     * Retrieves a data access object
     */
    public NoteDao getNoteDao() {
        return noteDao;
    }

    public Note createNote(Note note, PersistableBusinessObject bo) throws Exception {
        // TODO: Why is a deep copy being done?  Nowhere that this is called uses the given note argument
        // again after calling this method.
        Note tmpNote = (Note) ObjectUtils.deepCopy(note);
        Person kualiUser = GlobalVariables.getUserSession().getPerson();
        tmpNote.setRemoteObjectIdentifier(bo.getObjectId());
        tmpNote.setAuthorUniversalIdentifier(kualiUser.getPrincipalId());
        return tmpNote;
    }

    /**
     * This method gets the property name for the note
     * 
     * @param note
     * @return note property text
     */
    public String extractNoteProperty(Note note) {
        String propertyName = null;
        for (NoteTypeEnum nte : NoteTypeEnum.values()) {
            if (StringUtils.equals(nte.getCode(), note.getNoteTypeCode())) {
                propertyName = nte.getPath();
            }
        }
        return propertyName;
    }

    /**
     * @see org.kuali.rice.kns.service.NoteService#sendNoteNotification(org.kuali.rice.kns.document.Document, org.kuali.rice.kns.bo.Note,
     *      org.kuali.rice.kim.bo.Person)
     */
    public void sendNoteRouteNotification(Document document, Note note, Person sender) throws WorkflowException {
        AdHocRouteRecipient routeRecipient = note.getAdHocRouteRecipient();

        // build notification request
        Person requestedUser = this.getPersonService().getPersonByPrincipalName(routeRecipient.getId());
        String senderName = sender.getFirstName() + " " + sender.getLastName();
        String requestedName = requestedUser.getFirstName() + " " + requestedUser.getLastName();
        
        String notificationText = kualiConfigurationService.getPropertyString(RiceKeyConstants.MESSAGE_NOTE_NOTIFICATION_ANNOTATION);
        if (StringUtils.isBlank(notificationText)) {
            throw new RuntimeException("No annotation message found for note notification. Message needs added to application resources with key:" + RiceKeyConstants.MESSAGE_NOTE_NOTIFICATION_ANNOTATION);
        }
        notificationText = MessageFormat.format(notificationText, new Object[] { senderName, requestedName, note.getNoteText() });

        List<AdHocRouteRecipient> routeRecipients = new ArrayList<AdHocRouteRecipient>();
        routeRecipients.add(routeRecipient);

        workflowDocumentService.sendWorkflowNotification(document.getDocumentHeader().getWorkflowDocument(), notificationText, routeRecipients, KNSConstants.NOTE_WORKFLOW_NOTIFICATION_REQUEST_LABEL);

        // clear recipient allowing an notification to be sent to another person
        note.setAdHocRouteRecipient(new AdHocRoutePerson());
    }

    /**
     * @param personService the personService to set
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * @param workflowDocumentService the workflowDocumentService to set
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * @param kualiConfigurationService the kualiConfigurationService to set
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    
    protected PersonService getPersonService() {
        if ( personService == null ) {
            personService = KIMServiceLocator.getPersonService();
        }
        return personService;
    }
}
