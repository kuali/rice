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
package org.kuali.rice.krad.web.controller;

import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

/**
 * Controller for <code>InquiryView</code> screens which handle initial requests for the inquiry and
 * actions coming from the inquiry view such as export
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/inquiry")
public class InquiryController extends UifControllerBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquiryController.class);

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected InquiryForm createInitialForm(HttpServletRequest request) {
        return new InquiryForm();
    }

    /**
     * Invoked to request an inquiry view for a data object class
     *
     * <p>
     * Checks if the data object is externalizable and we need to redirect to the appropriate inquiry URL, else
     * continues with the inquiry view display
     * </p>
     *
     * <p>
     * Data object class name and values for a primary or alternate key set must
     * be sent in the request
     * </p>
     *
     * <p>
     * Invokes the inquirable to perform the query for the data object record, if not found
     * an exception will be thrown. If found the object is set on the form and then the view
     * is rendered
     * </p>
     */
    @Override
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) {
        InquiryForm inquiryForm = (InquiryForm) form;

        // if request is not a redirect, determine if we need to redirect for an externalizable object inquiry
        if (!inquiryForm.isRedirectedInquiry()) {
            Class<?> inquiryObjectClass;
            try {
                inquiryObjectClass = Class.forName(inquiryForm.getDataObjectClassName());
            } catch (ClassNotFoundException e) {
                throw new RiceRuntimeException("Unable to get class for name: " + inquiryForm.getDataObjectClassName(),
                        e);
            }

            ModuleService responsibleModuleService =
                    KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(inquiryObjectClass);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(inquiryObjectClass)) {
                String inquiryUrl = responsibleModuleService.getExternalizableDataObjectInquiryUrl(inquiryObjectClass,
                        KRADUtils.convertRequestMapToProperties(request.getParameterMap()));

                Properties redirectUrlProps = new Properties();
                redirectUrlProps.put(UifParameters.REDIRECTED_INQUIRY, "true");

                // clear current form from session
                GlobalVariables.getUifFormManager().removeSessionForm(form);

                return performRedirect(form, inquiryUrl, redirectUrlProps);
            }
        }

        // invoke inquirable to retrieve inquiry data object
        Object dataObject = inquiryForm.getInquirable().retrieveDataObject(KRADUtils.translateRequestParameterMap(
                request.getParameterMap()));

        inquiryForm.setDataObject(dataObject);

        return super.start(form, request, response);
    }

}
