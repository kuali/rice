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
package org.kuali.rice.krad.maintenance;

import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.PersistableAttachment;
import org.kuali.rice.krad.bo.PersistableAttachmentList;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.document.DocumentControllerServiceImpl;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.MaintenanceDocumentService;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Default implementation of the maintenance document controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceDocumentControllerServiceImpl extends DocumentControllerServiceImpl implements MaintenanceDocumentControllerService {

    private MaintenanceDocumentService maintenanceDocumentService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView start(UifFormBase form) {
        setupMaintenanceDocument((MaintenanceDocumentForm) form, KRADConstants.MAINTENANCE_NEW_ACTION);

        return super.start(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadDocument(DocumentFormBase form) throws WorkflowException {
        super.loadDocument(form);

        MaintenanceDocumentForm maintenanceForm = (MaintenanceDocumentForm) form;
        maintenanceForm.setMaintenanceAction(
                (maintenanceForm.getDocument()).getNewMaintainableObject().getMaintenanceAction());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDocument(DocumentFormBase form) throws WorkflowException {
        setupMaintenanceDocument((MaintenanceDocumentForm) form, KRADConstants.MAINTENANCE_NEW_ACTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView setupMaintenanceEdit(MaintenanceDocumentForm form) {
        setupMaintenanceDocument(form, KRADConstants.MAINTENANCE_EDIT_ACTION);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView setupMaintenanceCopy(MaintenanceDocumentForm form) {
        setupMaintenanceDocument(form, KRADConstants.MAINTENANCE_COPY_ACTION);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView setupMaintenanceNewWithExisting(MaintenanceDocumentForm form) {
        setupMaintenanceDocument(form, KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView setupMaintenanceDelete(MaintenanceDocumentForm form) {
        GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES, RiceKeyConstants.MESSAGE_DELETE);

        setupMaintenanceDocument(form, KRADConstants.MAINTENANCE_DELETE_ACTION);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupMaintenanceDocument(MaintenanceDocumentForm form, String maintenanceAction) {
        MaintenanceDocument document = form.getDocument();

        if (document == null) {
            createMaintenanceDocument(form, maintenanceAction);

            document = form.getDocument();
        }

        form.setMaintenanceAction(maintenanceAction);

        // invoke maintenance document service to setup the object for maintenance
        getMaintenanceDocumentService().setupMaintenanceObject(document, maintenanceAction,
                form.getRequest().getParameterMap());

        // for new maintainable check if a maintenance lock exists and if so warn the user
        if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
            MaintenanceUtils.checkForLockingDocument(document, false);
        }

        // retrieve notes topic display flag from data dictionary and add to document
        // TODO: should be in the view as permission
        DocumentEntry entry = KRADServiceLocatorWeb.getDocumentDictionaryService().getMaintenanceDocumentEntry(
                document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        document.setDisplayTopicFieldInNotes(entry.getDisplayTopicFieldInNotes());
    }

    /**
     * Helper method to create a new maintenance document instance and set the instance on the given form.
     *
     * @param form form instance to pull data object from and set new document instance on
     * @param maintenanceAction type of maintenance action being requested
     */
    protected void createMaintenanceDocument(MaintenanceDocumentForm form, String maintenanceAction) {
        MaintenanceDocument document = getMaintenanceDocumentService().setupNewMaintenanceDocument(
                form.getDataObjectClassName(), form.getDocTypeName(), maintenanceAction);

        form.setDocument(document);
        form.setDocTypeName(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void downloadDataObjectAttachment(MaintenanceDocumentForm form, HttpServletResponse response) {
        MaintenanceDocument document = form.getDocument();
        Object dataObject = document.getDocumentDataObject();

        if (dataObject instanceof PersistableAttachment) {
            PersistableAttachment attachment = (PersistableAttachment) dataObject;

            byte[] attachmentContent = attachment.getAttachmentContent();
            if (attachmentContent != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(attachmentContent);
                try {
                    KRADUtils.addAttachmentToResponse(response, inputStream, attachment.getContentType(),
                            attachment.getFileName(), attachmentContent.length);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to retrieve attachment contents", e);
                }
            }
        } else if (dataObject instanceof PersistableAttachmentList) {
            PersistableAttachmentList<PersistableAttachment> attachmentList =
                    (PersistableAttachmentList<PersistableAttachment>) dataObject;
            PersistableAttachmentList<PersistableAttachment> attachmentListBo =
                    (PersistableAttachmentList<PersistableAttachment>) document.getNewMaintainableObject()
                            .getDataObject();

            PersistableAttachment attachment = (PersistableAttachment) attachmentListBo.getAttachments().get(
                    Integer.parseInt(form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX)));

            byte[] attachmentContent = attachment.getAttachmentContent();
            if (attachmentContent != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(attachmentContent);
                try {
                    KRADUtils.addAttachmentToResponse(response, inputStream, attachment.getContentType(),
                            attachment.getFileName(), attachmentContent.length);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to retrieve attachment contents", e);
                }
            }
        }
    }

    protected MaintenanceDocumentService getMaintenanceDocumentService() {
        return maintenanceDocumentService;
    }

    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }
}
