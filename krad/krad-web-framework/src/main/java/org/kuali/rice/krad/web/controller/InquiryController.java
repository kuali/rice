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
package org.kuali.rice.krad.web.controller;

import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for <code>InquiryView</code> screens which handle
 * initial requests for the inquiry and actions coming from the
 * inquiry view such as export
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

    /**
     * Invoked to display the inquiry view for a data object record
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
    @RequestMapping(params = "methodToCall=start")
    @Override
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        InquiryForm inquiryForm = (InquiryForm) form;

        // initialize data object class in inquirable
        try {
            inquiryForm.getInquirable().setDataObjectClass(Class.forName(inquiryForm.getDataObjectClassName()));
        } catch (ClassNotFoundException e) {
            LOG.error("Unable to get new instance for object class: " + inquiryForm.getDataObjectClassName(), e);
            throw new RuntimeException(
                    "Unable to get new instance for object class: " + inquiryForm.getDataObjectClassName(), e);
        }

        // invoke inquirable to retrieve inquiry data object
        Object dataObject = inquiryForm.getInquirable()
                .retrieveDataObject(KRADUtils.translateRequestParameterMap(request.getParameterMap()));

        if (dataObject == null && GlobalVariables.getMessageMap().hasNoMessages()) {
            LOG.error("The record you have inquired on does not exist.");
            throw new UnsupportedOperationException("The record you have inquired on does not exist.");
        }
        inquiryForm.setDataObject(dataObject);

        return getUIFModelAndView(inquiryForm);
    }
}
