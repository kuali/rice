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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentAuthorizationException;
import org.kuali.rice.kns.exception.UnknownDocumentIdException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.spring.controller.KualiTransactionalDocumentControllerBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.document.TravelDocument2;
import edu.sampleu.travel.krad.form.TravelDocumentKradForm;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Controller
@RequestMapping(value="/travelDocument2*")
public class TravelDocumentController extends KualiTransactionalDocumentControllerBase {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TravelDocumentController.class);

    @RequestMapping(params="methodToCall=insertAccount")
    public ModelAndView insertAccount(@ModelAttribute("KualiForm") TravelDocumentKradForm travelForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        TravelDocumentForm2 travelForm = (TravelDocumentForm2) form;
        TravelAccount travAcct = (TravelAccount) KNSServiceLocator.getBusinessObjectService().retrieve(travelForm.getTravelAccount());
        // Make sure a travel account was actually retrieved.
        if (travAcct == null) {
        	GlobalVariables.getMessageMap().putError("travelAccount.number", RiceKeyConstants.ERROR_CUSTOM, "Invalid travel account number");
        	throw new ValidationException("Invalid travel account number");
        }
        // Insert the travel account into the list, if the list does not already contain it.
        boolean containsNewAcct = false;
        for (Iterator<TravelAccount> travAcctIter = ((TravelDocument2) travelForm.getDocument()).getTravelAccounts().iterator(); travAcctIter.hasNext();) {
        	if (travAcctIter.next().getNumber().equals(travAcct.getNumber())) {
        		containsNewAcct = true;
        		break;
        	}
        }
        if (!containsNewAcct) {
        	((TravelDocument2) travelForm.getDocument()).getTravelAccounts().add(travAcct);
        }
        travelForm.setTravelAccount(new TravelAccount());
//        return mapping.findForward(RiceConstants.MAPPING_BASIC);
        return new ModelAndView("test/KualiDirectInquiry", "KualiForm", travelForm);
    }

	@RequestMapping(params="methodToCall=deleteAccount")
    public ModelAndView deleteAccount(@ModelAttribute("KualiForm") TravelDocumentKradForm travelForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	// Remove the travel account at the index specified in the "methodToCall" parameter.
//    	TravelDocumentForm2 travelForm = (TravelDocumentForm2) form;
    	String strIndex = StringUtils.substringBetween((String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE),
        		KNSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
    	if (StringUtils.isNotBlank(strIndex)) {
    		((TravelDocument2) travelForm.getDocument()).getTravelAccounts().remove(Integer.parseInt(strIndex));
    	}
//        return mapping.findForward(RiceConstants.MAPPING_BASIC);
        return new ModelAndView("test/KualiDirectInquiry", "KualiForm", travelForm);
    }

	@RequestMapping(params="methodToCall=refresh")
    public ModelAndView refresh(@ModelAttribute("KualiForm") TravelDocumentKradForm travelForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        return mapping.findForward(RiceConstants.MAPPING_BASIC);
        return new ModelAndView("test/KualiDirectInquiry", "KualiForm", travelForm);
    }

}
