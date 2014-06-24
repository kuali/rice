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

import org.kuali.rice.krad.document.DocumentControllerBase;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.kuali.rice.krad.web.service.ControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller that handles requests for a {@link org.kuali.rice.krad.uif.view.MaintenanceDocumentView}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = KRADConstants.ControllerMappings.MAINTENANCE)
public class MaintenanceDocumentController extends DocumentControllerBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected MaintenanceDocumentForm createInitialForm() {
        return new MaintenanceDocumentForm();
    }

    /**
     * @see MaintenanceDocumentControllerService#setupMaintenanceEdit(org.kuali.rice.krad.web.form.MaintenanceDocumentForm)
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_EDIT)
    public ModelAndView setupMaintenanceEdit(MaintenanceDocumentForm form) {
        return getControllerService().setupMaintenanceEdit(form);
    }

    /**
     * @see MaintenanceDocumentControllerService#setupMaintenanceCopy(org.kuali.rice.krad.web.form.MaintenanceDocumentForm)
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_COPY)
    public ModelAndView setupMaintenanceCopy(MaintenanceDocumentForm form) {
        return getControllerService().setupMaintenanceCopy(form);
    }

    /**
     * @see MaintenanceDocumentControllerService#setupMaintenanceNewWithExisting(org.kuali.rice.krad.web.form.MaintenanceDocumentForm)
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_NEW_WITH_EXISTING)
    public ModelAndView setupMaintenanceNewWithExisting(MaintenanceDocumentForm form) {
        return getControllerService().setupMaintenanceNewWithExisting(form);
    }

    /**
     * @see MaintenanceDocumentControllerService#setupMaintenanceDelete(org.kuali.rice.krad.web.form.MaintenanceDocumentForm)
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_DELETE)
    public ModelAndView maintenanceDelete(MaintenanceDocumentForm form) {
        return getControllerService().setupMaintenanceDelete(form);
    }

    /**
     * @see MaintenanceDocumentControllerService#downloadDataObjectAttachment(org.kuali.rice.krad.web.form.MaintenanceDocumentForm,
     * javax.servlet.http.HttpServletResponse)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=downloadDataObjectAttachment")
    public ModelAndView downloadDataObjectAttachment(MaintenanceDocumentForm form, HttpServletResponse response) {
        getControllerService().downloadDataObjectAttachment(form, response);

        return null;
    }

    @Override
    protected MaintenanceDocumentControllerService getControllerService() {
        return (MaintenanceDocumentControllerService) super.getControllerService();
    }

    @Override
    @Autowired
    @Qualifier("maintenanceDocumentControllerService")
    public void setControllerService(ControllerService controllerService) {
        super.setControllerService(controllerService);
    }

}
