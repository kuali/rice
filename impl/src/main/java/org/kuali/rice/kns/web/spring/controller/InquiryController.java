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

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.spring.KradBooleanBinder;
import org.kuali.rice.kns.web.struts.form.InquiryForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * This is a description of what this class does - swgibson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Controller
@RequestMapping(value="/inquiry")
public class InquiryController {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquiryController.class);
	
//	@ModelAttribute("KualiForm")
//	public InquiryForm prepareForm() {
//		InquiryForm form = new InquiryForm();
//		form.setUsingSpring(true);
//		
//		return form;
//	}
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(boolean.class, new KradBooleanBinder());
    }

	@RequestMapping(params="methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") InquiryForm inquiryForm,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) {		
		return continueWithInquiry(inquiryForm, result, request, response);
	}
	
	@RequestMapping(params="methodToCall=continueWithInquiry")
	public ModelAndView continueWithInquiry(@ModelAttribute("KualiForm") InquiryForm inquiryForm,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		
		inquiryForm.populate(request);
		
		BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
        checkBO(bo);
        
        populateSections(request, inquiryForm, bo);
        
        return new ModelAndView("test/KualiDirectInquiry", "KualiForm", inquiryForm);
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
    
    /**
     * Returns a section list with one empty section and one row.
     * 
     * @return list of sections
     */
    private List<Section> getEmptySections(String title) {
    	final Row row = new Row(Collections.<Field>emptyList());
    	
    	final Section section = new Section(Collections.singletonList(row));
		section.setErrorKey("*");
		section.setSectionTitle(title != null ? title : "");
		section.setNumberOfColumns(0);
		
		return Collections.singletonList(section);
    }
    
    protected void populateSections(HttpServletRequest request, InquiryForm inquiryForm, BusinessObject bo) {
    	Inquirable kualiInquirable = inquiryForm.getInquirable();
    	
    	if (bo != null) {
    		// get list of populated sections for display
    		List sections = kualiInquirable.getSections(bo);
        	inquiryForm.setSections(sections);
        	kualiInquirable.addAdditionalSections(sections, bo);
    	} else {
    		inquiryForm.setSections(getEmptySections(kualiInquirable.getTitle()));
    	}

        request.setAttribute(KNSConstants.INQUIRABLE_ATTRIBUTE_NAME, kualiInquirable);
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
	
}
