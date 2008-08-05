/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceKeyConstants;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteRecipient;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.NoteDao;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.EdenConstants;
import org.kuali.rice.kns.util.KNSConstants.NoteTypeEnum;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class is the service implementation for the Note structure.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Transactional
public class NoteServiceImpl implements NoteService {
    // set up logging
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NoteServiceImpl.class);

    private NoteDao noteDao;
    private UniversalUserService universalUserService;
    private WorkflowDocumentService workflowDocumentService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * Default constructor
     */
    public NoteServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.core.service.NoteService#saveNoteValueList(java.util.List)
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
     * @see org.kuali.core.service.NoteService#getByRemoteObjectId(java.lang.String)
     */
    public ArrayList getByRemoteObjectId(String remoteObjectId) {

        return noteDao.findByremoteObjectId(remoteObjectId);
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
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();
        tmpNote.setRemoteObjectIdentifier(bo.getObjectId());
        tmpNote.setAuthorUniversalIdentifier(kualiUser.getPersonUniversalIdentifier());
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
     * @see org.kuali.core.service.NoteService#sendNoteNotification(org.kuali.core.document.Document, org.kuali.core.bo.Note,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public void sendNoteRouteNotification(Document document, Note note, UniversalUser sender) throws UserNotFoundException, WorkflowException {
        AdHocRouteRecipient routeRecipient = note.getAdHocRouteRecipient();

        // build notification request
        UniversalUser requestedUser = universalUserService.getUniversalUserByAuthenticationUserId(routeRecipient.getId());
        String senderName = sender.getPersonFirstName() + " " + sender.getPersonLastName();
        String requestedName = requestedUser.getPersonFirstName() + " " + requestedUser.getPersonLastName();
        
        String notificationText = kualiConfigurationService.getPropertyString(RiceKeyConstants.MESSAGE_NOTE_NOTIFICATION_ANNOTATION);
        if (StringUtils.isBlank(notificationText)) {
            throw new RuntimeException("No annotation message found for note notification. Message needs added to application resources with key:" + RiceKeyConstants.MESSAGE_NOTE_NOTIFICATION_ANNOTATION);
        }
        notificationText = MessageFormat.format(notificationText, new Object[] { senderName, requestedName, note.getNoteText() });

        List<AdHocRouteRecipient> routeRecipients = new ArrayList<AdHocRouteRecipient>();
        routeRecipients.add(routeRecipient);

        workflowDocumentService.sendWorkflowNotification(document.getDocumentHeader().getWorkflowDocument(), notificationText, routeRecipients);

        // clear recipient allowing an notification to be sent to another person
        note.setAdHocRouteRecipient(new AdHocRoutePerson());
    }

    /**
     * @param universalUserService the universalUserService to set
     */
    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
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
}