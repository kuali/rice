/**
 * Copyright 2005-2018 The Kuali Foundation
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
package edu.sampleu.demo.kitchensink;

import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the guest user access.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/guestviews")
public class UifGuestController extends UifControllerBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected UifComponentsTestForm createInitialForm() {
        return new UifComponentsTestForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(UifFormBase form) {
        if (!form.getViewId().equals("UifGuestUserView")) {
            throw new RuntimeException("Guest user not allowed to acces this view : " + form.getViewId());
        }

        return super.start(form);
    }
}