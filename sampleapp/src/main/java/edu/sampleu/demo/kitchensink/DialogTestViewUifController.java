/*
 * Copyright 2006-2012 The Kuali Foundation
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

import org.kuali.rice.krad.uif.view.DialogManager;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * a controller for the configuration test view
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/dialog-configuration-test")
public class DialogTestViewUifController extends UifControllerBase {

    @Override
    protected UifFormBase createInitialForm(HttpServletRequest request) {
        return new UifDialogTestForm();
    }

    /**
     * places a message containing apostrophes into the message map for display as a growl
     */
    @Override
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        return super.start(form, result, request, response);
    }

    /**
     * Exercises the Dialog framework.
     *
     * <p>
     * Asks a question of the user while processing a client request. Demonstrates the ability to go back
     * to the client bring up a Lightbox modal dialog.
     * </p>
     *
     * @param uiTestForm - test form
     * @param result - Spring form binding result
     * @param request - http request
     * @param response - http response
     * @return
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=save")
    public ModelAndView save(@ModelAttribute("KualiForm") UifDialogTestForm uiTestForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv;
        boolean answer = false;
        String dialogName = "myDialog";

        //exercise asking a question here
//        boolean answer = askYesOrNoQuestion("myDialog", uiTestForm,  request, response);

        //TODO: Hack by Dan
        DialogManager dm = uiTestForm.getDialogManager();
        if (dm.hasDialogBeenAnswered(dialogName)){
            answer = dm.wasDialogAnswerAffirmative(dialogName);
        } else {
            // redirect back to client to display lightbox
            dm.addDialog(dialogName, uiTestForm.getMethodToCall());
            mv = showDialog(dialogName, uiTestForm, request, response);
            return mv;
        }

        // continue on here if they answered the question
        if (answer){
            uiTestForm.setField1("The answer was Yes.");
            return getUIFModelAndView(uiTestForm);
        }
        uiTestForm.setField1("Whew, that was close");
        return getUIFModelAndView(uiTestForm, "DialogView-Page1");
    }

    /**
     * not used at this time
     *
     * @param uiTestForm
     * @param result
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(params = "methodToCall=close")
    public ModelAndView close(@ModelAttribute("KualiForm") UifDialogTestForm uiTestForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

//      TODO: Put "Are Your Sure?" dialog here
        return getUIFModelAndView(uiTestForm, "DialogView-Page1");
    }

    /**
     * Test method for a controller that invokes a dialog lightbox.
     *
     * @param form
     * @param result
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=" + "doSomething")
    public ModelAndView doSomething(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        // TODO: STUB   ***  not yet implemented
        // note: code below is test junk, just to get a skeleton in place
        StringBuffer testBucket = new StringBuffer();
        testBucket.append("blah");
        boolean okToContinue = false;
        okToContinue = super.askYesOrNoQuestion("OK_TO_CONTINUE", form, request, response);

        testBucket.append("question answer = ");
        testBucket.append(Boolean.toString(okToContinue));
        return super.refresh(form, result, request, response);
    }


}
