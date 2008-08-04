/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.web.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceKeyConstants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.InquiryForm;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.util.RiceConstants;

/**
 * This class handles actions for inquiries of business objects.
 */
public class KualiInquiryAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiInquiryAction.class);

    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (!(form instanceof InquiryForm)) {
            super.checkAuthorization(form, methodToCall);
        } else {
            try {
                Class businessObjectClass = Class.forName(((InquiryForm) form).getBusinessObjectClassName());
                AuthorizationType inquiryAuthType = new AuthorizationType.Inquiry(businessObjectClass);
                // check if the inquiry is allowed
                if (!getKualiModuleService().isAuthorized(GlobalVariables.getUserSession().getUniversalUser(), inquiryAuthType)) {
                    LOG.error("User not authorized for inquiry action for this object: " + businessObjectClass.getName());
                    throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), inquiryAuthType, getKualiModuleService().getResponsibleModule(businessObjectClass));
                }
            }
            catch (ClassNotFoundException ex) {
                super.checkAuthorization(form, methodToCall);
            }
        }
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE, KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
        return super.execute(mapping, form, request, response);
    }

    /**
     * Gets an inquirable impl from the impl service name parameter. Then calls lookup service to retrieve the record from the
     * key/value parameters. Finally gets a list of Rows from the inquirab
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InquiryForm inquiryForm = (InquiryForm) form;
        if (inquiryForm.getBusinessObjectClassName() == null) {
            LOG.error("Business object name not given.");
            throw new RuntimeException("Business object name not given.");
        }
        
        BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
            request.setAttribute("backLocation", request.getParameter("returnLocation"));
            return mapping.findForward("inquiryError");
        }
        
        populateSections(mapping, request, inquiryForm, bo);
        
        Inquirable kualiInquirable = inquiryForm.getInquirable();
        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Turns on (or off) the inactive record display for a maintenance collection.
     */
    public ActionForward toggleInactiveRecordDisplay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InquiryForm inquiryForm = (InquiryForm) form;
        if (inquiryForm.getBusinessObjectClassName() == null) {
            LOG.error("Business object name not given.");
            throw new RuntimeException("Business object name not given.");
        }
        
        BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
            request.setAttribute("backLocation", request.getParameter("returnLocation"));
            return mapping.findForward("inquiryError");
        }
        
        Inquirable kualiInquirable = inquiryForm.getInquirable();
        //////////////////////////////
        String collectionName = extractCollectionName(request, KNSConstants.TOGGLE_INACTIVE_METHOD);
        if (collectionName == null) {
            LOG.error("Unable to get find collection name in request.");
            throw new RuntimeException("Unable to get find collection class in request.");
        }  
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        boolean showInactive = Boolean.parseBoolean(StringUtils.substringBetween(parameterName, KNSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, "."));
        kualiInquirable.setShowInactiveRecords(collectionName, showInactive);
        //////////////////////////////
        
        populateSections(mapping, request, inquiryForm, bo);
        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    
    @Override
    public ActionForward toggleTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InquiryForm inquiryForm = (InquiryForm) form;
        if (inquiryForm.getBusinessObjectClassName() == null) {
            LOG.error("Business object name not given.");
            throw new RuntimeException("Business object name not given.");
        }
        
        BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
            request.setAttribute("backLocation", request.getParameter("returnLocation"));
            return mapping.findForward("inquiryError");
        }
        
        populateSections(mapping, request, inquiryForm, bo);
        
        Inquirable kualiInquirable = inquiryForm.getInquirable();
        
        return super.toggleTab(mapping, form, request, response);
    }

    /**
     * Convert a Request into a Map<String,String>. Technically, Request parameters do not neatly translate into a Map of Strings,
     * because a given parameter may legally appear more than once (so a Map of String[] would be more accurate.) This method should
     * be safe for business objects, but may not be reliable for more general uses.
     */
    protected String extractCollectionName(HttpServletRequest request, String methodToCall) {
        // collection name and underlying object type from request parameter
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String collectionName = null;
        if (StringUtils.isNotBlank(parameterName)) {
            collectionName = StringUtils.substringBetween(parameterName, methodToCall + ".", ".(");
        }
        return collectionName;
    }
    
    protected BusinessObject retrieveBOFromInquirable(InquiryForm inquiryForm) {
	Inquirable kualiInquirable = inquiryForm.getInquirable();
        // retrieve the business object
        BusinessObject bo = kualiInquirable.getBusinessObject(inquiryForm.retrieveInquiryDecryptedPrimaryKeys());
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
        }
        return bo;
    }
    
    protected void populateSections(ActionMapping mapping, HttpServletRequest request, InquiryForm inquiryForm, BusinessObject bo) {
	Inquirable kualiInquirable = inquiryForm.getInquirable();
	
        // get list of populated sections for display
        List sections = kualiInquirable.getSections(bo);
        inquiryForm.setSections(sections);
        kualiInquirable.addAdditionalSections(sections, bo);
        request.setAttribute(KNSConstants.INQUIRABLE_ATTRIBUTE_NAME, kualiInquirable);
    }
}