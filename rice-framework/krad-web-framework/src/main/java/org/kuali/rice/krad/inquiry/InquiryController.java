/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.service.ControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for inquiry views which handle initial requests for the inquiry and
 * actions coming from the inquiry view such as export.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = KRADConstants.ControllerMappings.INQUIRY)
public class InquiryController extends UifControllerBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected InquiryForm createInitialForm() {
        return new InquiryForm();
    }

    /**
     * @see org.kuali.rice.krad.inquiry.InquiryControllerService#downloadDataObjectAttachment(org.kuali.rice.krad.web.form.InquiryForm,
     * javax.servlet.http.HttpServletResponse)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=downloadDataObjectAttachment")
    public ModelAndView downloadDataObjectAttachment(InquiryForm form, HttpServletResponse response) {
        getControllerService().downloadDataObjectAttachment(form, response);

        return null;
    }

    /**
     * @see org.kuali.rice.krad.inquiry.InquiryControllerService#downloadCustomDataObjectAttachment(org.kuali.rice.krad.web.form.InquiryForm,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=downloadCustomDataObjectAttachment")
    public ModelAndView downloadCustomDataObjectAttachment(InquiryForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        getControllerService().downloadCustomDataObjectAttachment(form, request, response);

        return null;
    }

    @Override
    protected InquiryControllerService getControllerService() {
        return (InquiryControllerService) super.getControllerService();
    }

    @Override
    @Autowired
    @Qualifier("inquiryControllerService")
    public void setControllerService(ControllerService controllerService) {
        super.setControllerService(controllerService);
    }
}
