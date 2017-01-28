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
package org.kuali.rice.krad.labs;

import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.controller.MethodAccessible;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.FileControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Basic controller for the lab views
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/labs")
public class KradLabsController extends UifControllerBase {

    @Override
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(UifFormBase form) {

        if (form.getViewId().equals("Labs-BootstrapMultiSelect")) {
            GlobalVariables.getMessageMap().putWarning("multiSelectField2", "validation.equals");
        }
            return super.start(form);
     }

    @Override
    protected KradLabsForm createInitialForm() {
        return new KradLabsForm();
    }

    @RequestMapping(params = "methodToCall=jsonExample")
    public ModelAndView jsonExample(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        form.setRequestJsonTemplate("/templates/jsonSample.ftl");

        return getModelAndView(form);
    }

    @Autowired
    @Qualifier("demoFileControllerService")
    @Override
    public void setFileControllerService(FileControllerService fileControllerService) {
        super.setFileControllerService(fileControllerService);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=saveField")
    public ModelAndView saveField(UifFormBase uifForm) throws Exception {
        GlobalVariables.getMessageMap().putError("dataField3", "serverTestError");
        // Hook method for saving individual fields
        return refresh(uifForm);
    }

    @MethodAccessible
    @RequestMapping(params = "methodToCall=dialogMessage")
    public ModelAndView dialogMessage(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
                HttpServletRequest request, HttpServletResponse response)  throws Exception {
        GlobalVariables.getMessageMap().putError("HierarchyMessages", "serverTestError");
        GlobalVariables.getMessageMap().putError("inputField1", "serverTestError");
        // Hook method for saving individual fields
        return getModelAndView(form);
    }
}
