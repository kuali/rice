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
package edu.sampleu.travel;

import org.kuali.rice.krad.lookup.LookupController;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.web.controller.MethodAccessible;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/lookup")
public class DemoLookupController extends LookupController {

    @Override
    @MethodAccessible
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(UifFormBase form) {
        return super.start(form);
    }
}
