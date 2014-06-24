/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.inquiry;

import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.impl.ControllerServiceImpl;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * Override of navigation controller service to check if the initial inquiry needs redirected and also to
 * retrieve the inquiry data object on the initial call.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryControllerServiceImpl extends ControllerServiceImpl {

    /**
     * Determines if the inquiry request needs to be redirected based on the module service, if not retrieves
     * the inquiry data object and sets the instance onto the form for display.
     *
     * <p>Note the inquiry data object is retrieved based on the key values passed by the request.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView start(UifFormBase form) {
        InquiryForm inquiryForm = (InquiryForm) form;
        HttpServletRequest request = form.getRequest();

        if (!inquiryForm.isRedirectedInquiry()) {
            ModelAndView redirectModelAndView = checkForModuleInquiryRedirect(inquiryForm, request);
            if (redirectModelAndView != null) {
                return redirectModelAndView;
            }
        }

        // invoke inquirable to retrieve inquiry data object
        Object dataObject = inquiryForm.getInquirable().retrieveDataObject(KRADUtils.translateRequestParameterMap(
                request.getParameterMap()));

        inquiryForm.setDataObject(dataObject);

        return super.start(inquiryForm);
    }

    /**
     * Checks for a module service that claims the inquiry class as an EBO, and if found redirects to the URL
     * given by the module service.
     *
     * @param inquiryForm form instance containing the inquiry data
     * @param request http request being handled
     * @return ModelAndView instance for redirecting to the inquiry, or null if a redirect is not needed
     */
    protected ModelAndView checkForModuleInquiryRedirect(InquiryForm inquiryForm, HttpServletRequest request) {
        Class<?> inquiryObjectClass;
        try {
            inquiryObjectClass = Class.forName(inquiryForm.getDataObjectClassName());
        } catch (ClassNotFoundException e) {
            throw new RiceRuntimeException("Unable to get class for name: " + inquiryForm.getDataObjectClassName(), e);
        }

        ModuleService responsibleModuleService =
                KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(inquiryObjectClass);
        if (responsibleModuleService != null && responsibleModuleService.isExternalizable(inquiryObjectClass)) {
            String inquiryUrl = responsibleModuleService.getExternalizableDataObjectInquiryUrl(inquiryObjectClass,
                    KRADUtils.convertRequestMapToProperties(request.getParameterMap()));

            Properties redirectUrlProps = new Properties();
            redirectUrlProps.put(UifParameters.REDIRECTED_INQUIRY, "true");

            GlobalVariables.getUifFormManager().removeSessionForm(inquiryForm);

            return getModelAndViewService().performRedirect(inquiryForm, inquiryUrl, redirectUrlProps);
        }

        return null;
    }

}
