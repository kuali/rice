/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.kuali.rice.krad.web.controller.TransactionalDocumentControllerBase;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/approval")
public class TravelAuthorizationController extends TransactionalDocumentControllerBase {

    @Override
    protected TravelAuthorizationForm createInitialForm(HttpServletRequest request) {
        return new TravelAuthorizationForm();
    }

    /**
     * Showcase the dialog feature by confirming with the user that he really wants to route the document.
     */
    @Override
    public ModelAndView route(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        String dialog = "TravelAuthorization-RouteConfirmationDialog";
        if (!hasDialogBeenAnswered(dialog, form)) {
            return showDialog(dialog, form, request, response);
        }
        boolean dialogAnswer = getBooleanDialogResponse(dialog, form, request, response);
        if (dialogAnswer) {
            return super.route(form, result, request, response);
        } else {
            resetDialogStatus(dialog, form);
            return getUIFModelAndView(form);
        }

    }
}
