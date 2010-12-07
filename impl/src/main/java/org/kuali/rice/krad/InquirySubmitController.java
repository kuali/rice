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
package org.kuali.rice.krad;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.krad.web.struts.form.InquirySubmitForm;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Controller
@RequestMapping("/kr/submitInquiry*")
public class InquirySubmitController {
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView start(@ModelAttribute("KualiForm") InquirySubmitForm inquiryForm,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) {		
		return new ModelAndView("InquirySubmit2", "KualiForm", inquiryForm);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView submitInquiry(@ModelAttribute("KualiForm") InquirySubmitForm inquiryForm,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) {		
		Assert.hasLength(inquiryForm.getAccount().getNumber());
		return new ModelAndView("InquirySubmit2", "KualiForm", inquiryForm);
	}

}
