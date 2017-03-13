/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.document;

import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.PessimisticLockService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.DialogResponse;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.MaterializeOption;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.form.TransactionalDocumentFormBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

/**
 * Default implementation of the transactional document controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TransactionalDocumentControllerServiceImpl extends DocumentControllerServiceImpl implements TransactionalDocumentControllerService {

    private PessimisticLockService pessimisticLockService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView copy(TransactionalDocumentFormBase form) {
        try {
            // Load any lazy loaded data before proceeding with copy
            KradDataServiceLocator.getDataObjectService().wrap(form.getDocument()).materializeReferencedObjectsToDepth(3,
                    MaterializeOption.UPDATE_UPDATABLE_REFS);

            // Clones the original and detach the data
            form.setDocument(KradDataServiceLocator.getDataObjectService().copyInstance(form.getDocument()));

            // Generate the header data after the copy
            ((Copyable) form.getDocument()).toCopy();
        } catch (WorkflowException e) {
            throw new RuntimeException("Unable to copy transactional document", e);
        }

        form.setEvaluateFlagsAndModes(true);
        form.setCanEditView(null);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Releases the pessimistic locks before continuing.
     * </p>
     */
    @Override
    public ModelAndView cancel(UifFormBase form) {
        ModelAndView modelAndView = super.cancel(form);

        releasePessimisticLocks((DocumentFormBase) form);

        return modelAndView;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Releases the pessimistic locks before continuing.
     * </p>
     */
    @Override
    public ModelAndView route(DocumentFormBase form) {
        ModelAndView modelAndView = super.route(form);

        releasePessimisticLocks(form);

        return modelAndView;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Releases the pessimistic locks before continuing.
     * </p>
     */
    @Override
    public ModelAndView approve(DocumentFormBase form) {
        ModelAndView modelAndView = super.approve(form);

        releasePessimisticLocks(form);

        return modelAndView;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Releases the pessimistic locks before continuing.
     * </p>
     */
    @Override
    public ModelAndView disapprove(DocumentFormBase form) {
        ModelAndView modelAndView = super.disapprove(form);

        releasePessimisticLocks(form);

        return modelAndView;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Releases the pessimistic locks before continuing.
     * </p>
     */
    @Override
    public ModelAndView acknowledge(DocumentFormBase form) {
        ModelAndView modelAndView = super.acknowledge(form);

        releasePessimisticLocks(form);

        return modelAndView;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Prompts for save and releases the pessimistic locks before continuing.
     * </p>
     */
    @Override
    public ModelAndView close(DocumentFormBase form) {
        Document document = form.getDocument();

        // only offer to save if it is a valid action
        if (document.getDocumentHeader().getWorkflowDocument().isValidAction(ActionType.SAVE)) {
            // initialize the dialog to prompt for save
            DialogResponse dialogResponse = form.getDialogResponse(KRADConstants.QUESTION_ACTION_CLOSE_RESPONSE);

            if (dialogResponse == null) {
                return getModelAndViewService().showDialog(KRADConstants.QUESTION_ACTION_CLOSE_RESPONSE, false, form);
            }

            // save if the user answers yes in the dialog
            if (dialogResponse.getResponseAsBoolean()) {
                // get the explanation from the document and check it for sensitive data
                String explanation = document.getDocumentHeader().getExplanation();
                ModelAndView sensitiveDataDialogModelAndView = checkSensitiveDataAndWarningDialog(explanation, form);

                // if a sensitive data warning dialog is returned then display it
                if (sensitiveDataDialogModelAndView != null) {
                    return sensitiveDataDialogModelAndView;
                }

                performWorkflowAction(form, UifConstants.WorkflowAction.SAVE);
            }
        }

        releasePessimisticLocks(form);

        return super.close(form);
    }

    /**
     * Releases the pessimistic locks for the current user.
     *
     * @param form form instance containing the transactional document data
     */
    protected void releasePessimisticLocks(DocumentFormBase form) {
        Document document = form.getDocument();

        if (!document.getPessimisticLocks().isEmpty()) {
            Person user = GlobalVariables.getUserSession().getPerson();
            getPessimisticLockService().releaseAllLocksForUser(document.getPessimisticLocks(), user);
            document.refreshPessimisticLocks();
        }
    }

    protected PessimisticLockService getPessimisticLockService() {
        if (pessimisticLockService == null) {
            pessimisticLockService = KRADServiceLocatorWeb.getPessimisticLockService();
        }

        return pessimisticLockService;
    }

    protected void setPessimisticLockService(PessimisticLockService pessimisticLockService) {
        this.pessimisticLockService = pessimisticLockService;
    }

}