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

import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.service.ControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @Override
    @Autowired
    @Qualifier("inquiryControllerService")
    public void setControllerService(ControllerService controllerService) {
        super.setControllerService(controllerService);
    }
}
