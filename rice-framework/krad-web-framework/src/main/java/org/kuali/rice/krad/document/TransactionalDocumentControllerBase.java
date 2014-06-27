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

import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.TransactionalDocumentFormBase;
import org.kuali.rice.krad.web.service.ControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Base controller for KRAD transactional document views.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see DocumentControllerBase
 */
public abstract class TransactionalDocumentControllerBase extends DocumentControllerBase {

    /**
     * @see TransactionalDocumentControllerService#copy(org.kuali.rice.krad.web.form.TransactionalDocumentFormBase)
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_COPY)
    public ModelAndView copy(TransactionalDocumentFormBase form) {
        return getControllerService().cancelAttachment(form);
    }

    @Override
    protected TransactionalDocumentControllerService getControllerService() {
        return (TransactionalDocumentControllerService) super.getControllerService();
    }

    @Override
    @Autowired
    @Qualifier("transactionalDocumentControllerService")
    public void setControllerService(ControllerService controllerService) {
        super.setControllerService(controllerService);
    }

}
