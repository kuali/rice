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
package org.kuali.rice.krad.web.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.spring.form.InquiryForm;
import org.kuali.rice.krad.web.spring.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * This class is the handler for inquiries of business objects.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/inquiry")
public class InquiryController extends UifControllerBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquiryController.class);
	
	@Override
    protected InquiryForm createInitialForm(HttpServletRequest request) {
        return new InquiryForm();
    }

    @RequestMapping(params = "methodToCall=start")
    @Override
	public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
        InquiryForm inquiryForm = (InquiryForm) form;
        
		return continueWithInquiry(inquiryForm, result, request, response);
	}

	/**
	 * @param result
	 * @param response
	 */
	@RequestMapping(params = "methodToCall=continueWithInquiry")
	public ModelAndView continueWithInquiry(@ModelAttribute("KualiForm") InquiryForm inquiryForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		Object bo = retrieveBOFromInquirable(inquiryForm);
		checkBO(bo);

		inquiryForm.setBo(bo);

		return getUIFModelAndView(inquiryForm);
	}

	/**
	 * throws an exception if BO fails the check.
	 * 
	 * @param bo
	 *            the BusinessObject to check.
	 * @throws UnsupportedOperationException
	 *             if BO is null & no messages have been generated.
	 */
	private void checkBO(Object bo) {
		if (bo == null && GlobalVariables.getMessageMap().hasNoMessages()) {
			throw new UnsupportedOperationException("The record you have inquired on does not exist.");
		}
	}

	protected Object retrieveBOFromInquirable(InquiryForm inquiryForm) {
		Inquirable kualiInquirable = inquiryForm.getInquirable();

		// retrieve the business object
		Object  inquiryObject = kualiInquirable.getDataObject(inquiryForm.retrieveInquiryDecryptedPrimaryKeys());
		if (inquiryObject == null) {
			LOG.error("No records found in inquiry action.");
			GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
		}

		return inquiryObject;
	}

}
