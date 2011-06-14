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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.core.util.type.KualiPercent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.spring.controller.UifControllerBase;
import org.kuali.rice.krad.web.spring.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.sampleu.travel.bo.FiscalOfficer;
import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.krad.form.UITestForm;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/uitest")
public class UITestController extends UifControllerBase {

	@Override
    protected UITestForm createInitialForm(HttpServletRequest request) {
        return new UITestForm();
    }

	@Override
	@RequestMapping(params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
	    UITestForm uiTestForm = (UITestForm) form;
		
		// populate model for testing
		TravelAccount travelAccount = new TravelAccount();
		travelAccount.setNumber("101");
		travelAccount.setName("Account 101");

		FiscalOfficer fiscalOfficer = new FiscalOfficer();
		fiscalOfficer.setId(new Long(1));

		List<TravelAccount> officerAccounts = new ArrayList<TravelAccount>();

		TravelAccount travelAccount2 = new TravelAccount();
		travelAccount2.setNumber("102");
		travelAccount2.setName("Account 102");
		travelAccount2.setSubAccount("34");
		travelAccount2.setSubAccountName("G34 So");
		travelAccount2.setSubsidizedPercent(new KualiPercent(45.0));
		officerAccounts.add(travelAccount2);

		TravelAccount travelAccount3 = new TravelAccount();
		travelAccount3.setNumber("103");
		travelAccount3.setName("Account 103");
		officerAccounts.add(travelAccount3);

		TravelAccount travelAccount4 = new TravelAccount();
		travelAccount4.setNumber("104");
		travelAccount4.setName("Account 104");
		travelAccount4.setSubAccount("i84n");
		travelAccount4.setSubAccountName("Supplies");
		travelAccount4.setSubsidizedPercent(new KualiPercent(10));
		officerAccounts.add(travelAccount4);

		fiscalOfficer.setAccounts(officerAccounts);
		travelAccount.setFiscalOfficer(fiscalOfficer);
		
		// build sub-collections
		travelAccount2.setFiscalOfficer(fiscalOfficer);
		travelAccount3.setFiscalOfficer(fiscalOfficer);
		travelAccount4.setFiscalOfficer(fiscalOfficer);

		uiTestForm.setTravelAccount1(travelAccount);
		uiTestForm.setTravelAccount2(travelAccount);
		uiTestForm.setTravelAccount3(travelAccount);
		uiTestForm.setTravelAccount4(travelAccount);
		
		uiTestForm.setField5("a14");
		
		uiTestForm.setField1("Field1");
		uiTestForm.setField2("Field2");

		return super.start(uiTestForm, result, request, response);
	}

	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=save")
	public ModelAndView save(@ModelAttribute("KualiForm") UITestForm uiTestForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
		//For testing server side errors:
		if(uiTestForm.getField2().equals("server_error")){
			GlobalVariables.getMessageMap().putError("field2", "serverTestError");
			GlobalVariables.getMessageMap().putError("field2", "serverTestError2");
			GlobalVariables.getMessageMap().putWarning("field2", "serverTestWarning");
			GlobalVariables.getMessageMap().putInfo("field2", "serverTestInfo");
			GlobalVariables.getMessageMap().putInfo("field3", "serverTestInfo");
			GlobalVariables.getMessageMap().putError("field13", "serverTestError");
			GlobalVariables.getMessageMap().putWarning("field4", "serverTestWarning");
			GlobalVariables.getMessageMap().putWarning("TEST_WARNING", "serverTestError3");
			GlobalVariables.getMessageMap().putError("TEST_ERROR", "serverTestError3");
			GlobalVariables.getMessageMap().putError("vField5", "serverTestError");
			GlobalVariables.getMessageMap().putError("vField6", "serverTestError");
			//GlobalVariables.getMessageMap().clearErrorMessages();
			return getUIFModelAndView(uiTestForm, uiTestForm.getViewId(), uiTestForm.getPageId());
		}
		
		return getUIFModelAndView(uiTestForm, uiTestForm.getViewId(), "page2");
	}
	
	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=close")
	public ModelAndView close(@ModelAttribute("KualiForm") UITestForm uiTestForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		return getUIFModelAndView(uiTestForm, uiTestForm.getViewId(), "page1");
	}

}
