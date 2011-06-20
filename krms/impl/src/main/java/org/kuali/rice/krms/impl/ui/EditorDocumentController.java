/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.web.spring.controller.MaintenanceDocumentController;
import org.kuali.rice.krad.web.spring.controller.UifControllerBase;
import org.kuali.rice.krad.web.spring.form.MaintenanceForm;
import org.kuali.rice.krad.web.spring.form.UifFormBase;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/krmsAgendaEditor")
public class EditorDocumentController extends MaintenanceDocumentController {

    @Override
    public MaintenanceForm createInitialForm(HttpServletRequest request) {
        return new MaintenanceForm();
    }
    
    /**
     * This overridden method does extra work on refresh to populate the context and agenda
     * 
     * @see org.kuali.rice.krad.web.spring.controller.UifControllerBase#refresh(org.kuali.rice.krad.web.spring.form.UifFormBase, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ModelAndView refresh(UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        MapUtils.verbosePrint(System.out, "actionParameters", form.getActionParameters());
        
        return super.refresh(form, result, request, response);
    }

    private BusinessObjectService getBoService() {
        return KRADServiceLocator.getBusinessObjectService();
    }

}
