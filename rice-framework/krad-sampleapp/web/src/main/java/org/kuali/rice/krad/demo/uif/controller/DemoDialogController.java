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
package org.kuali.rice.krad.demo.uif.controller;

import org.kuali.rice.krad.demo.uif.form.KradSampleAppForm;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.form.DialogResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the dialog demo view.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/dialog")
public class DemoDialogController extends KradSampleAppController {

    protected static final String DEMO_DUPLICATE_DIALOG = "Demo-DialogGroup-ServerResponse1";
    protected static final String DEMO_DISAPPROVE_CONFIRM = "Demo-DialogGroup-ServerResponse2";
    protected static final String DEMO_DISAPPROVE_SURVEY = "Demo-DialogGroup-ServerResponse3";

    @RequestMapping(params = "methodToCall=save")
    public ModelAndView save(@ModelAttribute("KualiForm") KradSampleAppForm form) throws Exception {
        // typically there would be conditional logic that triggers the dialog
        DialogResponse duplicateDialogResponse = form.getDialogResponse(DEMO_DUPLICATE_DIALOG);
        if (duplicateDialogResponse == null) {
            return showDialog(DEMO_DUPLICATE_DIALOG, true, form);
        }

        boolean verifiedDuplicate = duplicateDialogResponse.getResponseAsBoolean();
        if (verifiedDuplicate) {
            // continue with save
            GlobalVariables.getMessageMap().putInfoForSectionId("demoDialogEx8", "demo.dialogs.saveConfirmation");
        }

        return getModelAndView(form);
    }

    @RequestMapping(params = "methodToCall=disapprove")
    public ModelAndView disapprove(@ModelAttribute("KualiForm") KradSampleAppForm form) throws Exception {
        // typically there would be conditional logic that triggers the dialog
        DialogResponse disapproveConfirm = form.getDialogResponse(DEMO_DISAPPROVE_CONFIRM);
        if (disapproveConfirm == null) {
            return showDialog(DEMO_DISAPPROVE_CONFIRM, true, form);
        }

        // since the dialog was a confirmation, we don't need to check the answer, if they selected no the
        // request will not be resent
        String disapproveExplanation = disapproveConfirm.getExplanation();

        DialogResponse disapproveSurvey = form.getDialogResponse(DEMO_DISAPPROVE_SURVEY);
        if (disapproveSurvey == null) {
            return showDialog(DEMO_DISAPPROVE_SURVEY, false, form);
        }

        String surveyResponse = disapproveSurvey.getResponse();

        GlobalVariables.getMessageMap().putInfoForSectionId("demoDialogEx9", "demo.dialogs.disapprove",
                disapproveExplanation, surveyResponse);

        return getModelAndView(form);
    }
}
