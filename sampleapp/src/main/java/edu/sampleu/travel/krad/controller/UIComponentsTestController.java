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
package edu.sampleu.travel.krad.controller;

import edu.sampleu.travel.krad.form.UILayoutTestForm;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/uicomponents")
public class UIComponentsTestController extends UifControllerBase {

    @Override
    protected Class<UILayoutTestForm> formType() {
        return UILayoutTestForm.class;
    }

	@Override
	@RequestMapping(params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
	    UILayoutTestForm uiTestForm = (UILayoutTestForm) form;

		return super.start(uiTestForm, result, request, response);
	}

	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=save")
	public ModelAndView save(@ModelAttribute("KualiForm") UILayoutTestForm uiTestForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		return getUIFModelAndView(uiTestForm, uiTestForm.getViewId(), "page2");
	}
	
	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=close")
	public ModelAndView close(@ModelAttribute("KualiForm") UILayoutTestForm uiTestForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		return getUIFModelAndView(uiTestForm, uiTestForm.getViewId(), "page1");
	}

    /**
     * Handles menu navigation between view pages
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=navigate")
    public ModelAndView navigate(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String pageId = form.getActionParamaterValue(UifParameters.NAVIGATE_TO_PAGE_ID);

        if(pageId.equals("uifpage7")){
            GlobalVariables.getMessageMap().putError("gField1", "serverTestError");
            GlobalVariables.getMessageMap().putError("gField1", "serverTestError2");
            GlobalVariables.getMessageMap().putError("gField2", "serverTestError");
            GlobalVariables.getMessageMap().putError("gField3", "serverTestError");
            GlobalVariables.getMessageMap().putWarning("gField1", "serverTestWarning");
            GlobalVariables.getMessageMap().putWarning("gField2", "serverTestWarning");
            GlobalVariables.getMessageMap().putInfo("gField2", "serverTestInfo");
            GlobalVariables.getMessageMap().putInfo("gField3", "serverTestInfo");
        }
        // only refreshing page
        form.setRenderFullView(false);

        return getUIFModelAndView(form, form.getViewId(), pageId);
    }
}
