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
package org.kuali.rice.kns.web.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.UIFConstants;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.ViewService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.spring.form.InquiryForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * This class is the handler for inquiries of business objects. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Controller
@RequestMapping(value="/inquiry")
public class InquiryController extends UifControllerBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquiryController.class);
	
	protected ViewService viewService;
	

	@RequestMapping(params="methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") InquiryForm inquiryForm,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) {		
		return continueWithInquiry(inquiryForm, result, request, response);
	}
	
	/**
     * @param result  
	 * @param response 
     */
	@RequestMapping(params="methodToCall=continueWithInquiry")
	public ModelAndView continueWithInquiry(@ModelAttribute("KualiForm") InquiryForm inquiryForm,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		
		inquiryForm.populate(request);
		
		BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
        checkBO(bo);
        
        inquiryForm.setBo(bo);
        
        // temporary create default view
        String viewId = StringUtils.substringAfterLast(inquiryForm.getBusinessObjectClassName(), ".");
        viewId += "-InquiryView";
                
        return this.getUIFModelAndView(inquiryForm, viewId, "page1");
	}
	
	
	/**
     * throws an exception if BO fails the check.
     * @param bo the BusinessObject to check.
     * @throws UnsupportedOperationException if BO is null & no messages have been generated.
     */
    private void checkBO(BusinessObject bo) {
        if (bo == null && GlobalVariables.getMessageMap().hasNoMessages()) {
        	throw new UnsupportedOperationException("The record you have inquired on does not exist.");
        }
    }
    
	protected BusinessObject retrieveBOFromInquirable(InquiryForm inquiryForm) {
    	Inquirable kualiInquirable = inquiryForm.getInquirable();
        // retrieve the business object
        BusinessObject bo = kualiInquirable.getBusinessObject(inquiryForm.retrieveInquiryDecryptedPrimaryKeys());
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getMessageMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
        }
        return bo;
    }
	
	protected ModelAndView getUIFModelAndView(Object form, String viewId, String pageId) {
		View view = getViewService().getViewById(viewId);
		view.setCurrentPageId(pageId);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(UIFConstants.DEFAULT_MODEL_NAME, form);
		modelAndView.addObject("View", view);

		modelAndView.setViewName("View");

		return modelAndView;
	}
	
	protected ViewService getViewService() {
		if (viewService == null) {
			viewService = KNSServiceLocator.getViewService();
		}

		return this.viewService;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}
	
}
