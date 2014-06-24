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
package org.kuali.rice.krad.document;

import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.rules.rule.event.DocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.service.ControllerService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller service that handles document specific actions (such as workflow actions).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentControllerService extends ControllerService {

    /**
     * Handles all requests for a new document instance or to load an existing document based on the
     * given form parameters.
     *
     * @param form form instance containing the document data
     * @return ModelAndView instance for rendering the document view
     * @throws WorkflowException if a document cannot be created or loaded
     */
    ModelAndView docHandler(DocumentFormBase form) throws WorkflowException;

    /**
     * Reloads from the database the document with the doc id on the given form.
     *
     * @param form form instance containing the document id that will be reloaded
     * @return ModelAndView instance for rendering the reloaded document view
     * @throws WorkflowException if the document cannot be reloaded
     */
    ModelAndView reload(DocumentFormBase form) throws WorkflowException;

    /**
     * Recalls the document with the given id on the form from workflow.
     *
     * @param form form instance containing the document id that will be recalled
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView recall(DocumentFormBase form);

    /**
     * Saves the document instance contained on the given form.
     *
     * @param form form instance containing the document that will be saved
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView save(DocumentFormBase form);

    /**
     * Saves the document instance contained on the given form and passes the given event for rule
     * evaluation.
     *
     * @param form form instance containing the document that will be saved
     * @param saveDocumentEvent rule event that will be processed with the save operation
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView save(DocumentFormBase form, SaveDocumentEvent saveDocumentEvent);

    /**
     * Sends a complete workflow action for the document contained on the form.
     *
     * @param form form instance containing the document the complete request will be generated for
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView complete(DocumentFormBase form);

    /**
     * Sends a route workflow action for the document contained on the form.
     *
     * @param form form instance containing the document the route request will be generated for
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView route(DocumentFormBase form);

    /**
     * Sends a blanket approve workflow action for the document contained on the form.
     *
     * @param form form instance containing the document the blanket approve request will be generated for
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView blanketApprove(DocumentFormBase form);

    /**
     * Sends a approve workflow action for the document contained on the form.
     *
     * @param form form instance containing the document the approve request will be generated for
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView approve(DocumentFormBase form);

    /**
     * Sends a disapprove workflow action for the document contained on the form.
     *
     * @param form form instance containing the document the disapprove request will be generated for
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView disapprove(DocumentFormBase form);

    /**
     * Sends a fyi workflow action for the document contained on the form.
     *
     * @param form form instance containing the document the fyi request will be generated for
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView fyi(DocumentFormBase form);

    /**
     * Sends a acknowledge workflow action for the document contained on the form.
     *
     * @param form form instance containing the document the acknowledge request will be generated for
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView acknowledge(DocumentFormBase form);

    /**
     * Sends AdHoc workflow Requests for the document instance contained on the form to the AdHoc recipients
     * contained on the form.
     *
     * @param form form instance containing the document and recipients the requests will be generated for
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView sendAdHocRequests(DocumentFormBase form);

    /**
     * Redirects to the supervisor workflow view.
     *
     * @param form form instance containing the document instance
     * @return ModelAndView instance for rendering the supervisor workflow view
     */
    ModelAndView supervisorFunctions(DocumentFormBase form);

    /**
     * Invoked by the add note action to adding the note instance contained of the given form.
     *
     * @param form form instance containing the note instance
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView insertNote(DocumentFormBase form);

    /**
     * Invoked by the delete note action to delete a note instance contained on document (within the form).
     *
     * @param form form instance containing the note instance
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView deleteNote(DocumentFormBase form);

    /**
     * Invoked to download an attachment that has been uploaded for a note.
     *
     * @param form form instance containing the note (and attachment) instance
     * @param response http servlet response instance for sending back the attachment contents
     * @return ModelAndView instance for rendering the document view, or null if the response has been
     * finished
     */
    ModelAndView downloadAttachment(DocumentFormBase form, HttpServletResponse response);

    /**
     * Invoked to remove an attachment that was uploaded for the add note instance.
     *
     * @param form form instance containing the attachment.
     * @return ModelAndView instance for rendering the document view
     */
    ModelAndView cancelAttachment(DocumentFormBase form);

    /**
     * Invokes the {@link org.kuali.rice.krad.service.DocumentService} to carry out a request workflow action and adds a
     * success message, if requested a check for sensitive data is also performed.
     *
     * @param form document form instance containing the document for which the action will be taken on
     * @param action {@link org.kuali.rice.krad.uif.UifConstants.WorkflowAction} enum indicating what workflow action
     * to take
     */
    void performWorkflowAction(DocumentFormBase form, UifConstants.WorkflowAction action);

    /**
     * Invokes the {@link org.kuali.rice.krad.service.DocumentService} to carry out a request workflow action and adds a
     * success message, if requested a check for sensitive data is also performed.
     *
     * @param form document form instance containing the document for which the action will be taken on
     * @param action {@link org.kuali.rice.krad.uif.UifConstants.WorkflowAction} enum indicating what workflow action
     * to take
     * @param documentEvent rule event instance that will be evaluated with the workflow action, only currently
     * supported for the save action
     */
    void performWorkflowAction(DocumentFormBase form, UifConstants.WorkflowAction action, DocumentEvent documentEvent);
}
