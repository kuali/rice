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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.InquiryForm;
import org.kuali.rice.KNSServiceLocator;

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
        request.setAttribute(Constants.PARAM_MAINTENANCE_VIEW_MODE, Constants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
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


        EncryptionService encryptionService = KNSServiceLocator.getEncryptionService();

        // List of encrypted values
        String encryptedString = request.getParameter(Constants.ENCRYPTED_LIST_PREFIX);
        List encryptedList = new ArrayList();
        if (StringUtils.isNotBlank(encryptedString)) {
            encryptedList = Arrays.asList(StringUtils.split(encryptedString, Constants.FIELD_CONVERSIONS_SEPERATOR));
        }

        Class businessObjectClass = Class.forName(inquiryForm.getBusinessObjectClassName());

        // build list of key values from request, if all keys not given throw error
        List boKeys = KNSServiceLocator.getPersistenceStructureService().listPrimaryKeyFieldNames(businessObjectClass);
        Map fieldValues = new HashMap();
        boolean hasRequiredKeys = true;
        for (Iterator iter = boKeys.iterator(); iter.hasNext();) {
            String element = (String) iter.next();

            if (request.getParameter(element) != null) {
                String parameter = request.getParameter(element);

                // Check if this element was encrypted, if it was decrypt it
                if (encryptedList.contains(element)) {
                    parameter = encryptionService.decrypt(parameter);
                }

                fieldValues.put(element, parameter);
            }
            else {
                hasRequiredKeys = false;
            }
        }
        if (!hasRequiredKeys) {
            LOG.error("All keys not given to lookup for bo class name " + businessObjectClass.getName());
            throw new RuntimeException("All keys not given to lookup for bo class name " + businessObjectClass.getName());
        }

        Class customInquirableClass = null;

        try {
            customInquirableClass = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObjectClass).getInquiryDefinition().getInquirableClass();
        }
        catch (Exception e) {
            LOG.error("Unable to correlate business object class with maintenance document entry");
        }

        Inquirable kualiInquirable = KNSServiceLocator.getKualiInquirable(); // get inquirable impl from Spring

        if (customInquirableClass != null) {
            Class[] defaultConstructor = new Class[] {};
            Constructor cons = customInquirableClass.getConstructor(defaultConstructor);
            kualiInquirable = (Inquirable) cons.newInstance();
        }

        // retrieve the business object
        kualiInquirable.setBusinessObjectClass(businessObjectClass);
        BusinessObject bo = kualiInquirable.getBusinessObject(fieldValues);
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KeyConstants.ERROR_INQUIRY);
            return mapping.findForward("error");
        }

        // get list of populated sections for display
        List sections = kualiInquirable.getSections(bo);

        inquiryForm.setSections(sections);

        kualiInquirable.addAdditionalSections(sections, bo);

        request.setAttribute(Constants.INQUIRABLE_ATTRIBUTE_NAME, kualiInquirable);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

}