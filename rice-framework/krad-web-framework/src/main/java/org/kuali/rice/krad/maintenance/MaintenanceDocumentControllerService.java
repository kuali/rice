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

import org.kuali.rice.krad.document.DocumentControllerService;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller service that extends {@link org.kuali.rice.krad.document.DocumentControllerService} and adds
 * methods specific to maintenance documents.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MaintenanceDocumentControllerService extends DocumentControllerService {

    /**
     * Sets up a new maintenance document for an edit action on the data object identified by the form
     * parameters.
     *
     * @param form form instance containing the maintenance data
     * @return ModelAndView instance for rendering the edit maintenance view
     */
    ModelAndView setupMaintenanceEdit(MaintenanceDocumentForm form);

    /**
     * Sets up a new maintenance document for a copy action on the data object identified by the form
     * parameters.
     *
     * @param form form instance containing the maintenance data
     * @return ModelAndView instance for rendering the copy maintenance view
     */
    ModelAndView setupMaintenanceCopy(MaintenanceDocumentForm form);

    /**
     * Sets up a new maintenance document for a new with existing action on the data object identified by the form
     * parameters.
     *
     * @param form form instance containing the maintenance data
     * @return ModelAndView instance for rendering the new maintenance view
     */
    ModelAndView setupMaintenanceNewWithExisting(MaintenanceDocumentForm form);

    /**
     * Sets up a new maintenance document for a delete action on the data object identified by the form
     * parameters.
     *
     * @param form form instance containing the maintenance data
     * @return ModelAndView instance for rendering the delete maintenance view
     */
    ModelAndView setupMaintenanceDelete(MaintenanceDocumentForm form);

    /**
     * Invoked to setup a new maintenance document for the maintenance data contained on the form
     * and the given maintenance action.
     *
     * @param form form instance containing the maintenance data
     * @param maintenanceAction type of maintenance action being requested (new, edit, copy, delete)
     */
    void setupMaintenanceDocument(MaintenanceDocumentForm form, String maintenanceAction);

    /**
     * When the maintenance data object is a {@link org.kuali.rice.krad.bo.PersistableAttachment} or
     * {@link org.kuali.rice.krad.bo.PersistableAttachmentList}, streams the selected attachment back to the
     * response.
     *
     * @param form form instance containing the maintenance data
     * @param response Http response for returning the attachment contents
     */
    void downloadDataObjectAttachment(MaintenanceDocumentForm form, HttpServletResponse response);
}
