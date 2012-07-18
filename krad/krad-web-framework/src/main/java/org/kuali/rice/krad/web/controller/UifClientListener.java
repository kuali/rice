/**
 * Copyright 2005-2012 The Kuali Foundation
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

import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller that receives various ajax requests from the client to manager server side state
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/listener")
public class UifClientListener extends UifControllerBase {

    @Override
    protected UifFormBase createInitialForm(HttpServletRequest request) {
        return new UifFormBase();
    }

    /**
     * Invoked from the client when the user is leaving a view (by the portal tabs or other mechanism) to clear
     * the form from session storage
     *
     * @param formKey - key of form that should be cleared
     * @return String json success string
     */
    @RequestMapping(params = "methodToCall=clearForm")
    public
    @ResponseBody
    String clearForm(@RequestParam("formKey") String formKey, HttpServletRequest request,
            HttpServletResponse response) {

        // clear form from session
        GlobalVariables.getUifFormManager().removeFormWithHistoryFormsByKey(formKey);

        return "{status:success}";
    }

}
