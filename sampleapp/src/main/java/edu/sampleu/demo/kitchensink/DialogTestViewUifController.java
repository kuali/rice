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

        // dialog names
        String dialog1 = "myDialog";
        String dialog2 = "myRadioButtonDialog";
        String dialog3 = "myRestart";

        // local copies of dialog answers
        boolean whichStory = false;
        String whichVersion;
        boolean doRestart = false;

        //exercise asking a question here
//        boolean answer = askYesOrNoQuestion("dialog1", uiTestForm,  request, response);

        DialogManager dm = uiTestForm.getDialogManager();

        //TODO: Hack by Dan
        if (dm.hasDialogBeenAnswered(dialog1)){
            whichStory = dm.wasDialogAnswerAffirmative(dialog1);
        } else {
            // redirect back to client to display lightbox
            dm.addDialog(dialog1, uiTestForm.getMethodToCall());
            mv = showDialog(dialog1, uiTestForm, request, response);
            return mv;
        }

        // continue on here if they answered the question
        if (whichStory){
            uiTestForm.setField1("You Selected: Go Dog, Go!");

            // popup a 2nd consecutive dialog
            if (dm.hasDialogBeenAnswered(dialog2)){
                whichVersion = dm.getDialogExplanation(dialog2);
            } else {
                // redirect back to client to display lightbox
                dm.addDialog(dialog2, uiTestForm.getMethodToCall());
                mv = showDialog(dialog2, uiTestForm, request, response);
                return mv;
            }

            // do something different for each possible response
            if(whichVersion.contains("A")){
                uiTestForm.setField1("You selected the G rated version, by P.D. Eastman. Here we go...");
            } else if(whichVersion.contains("B")){
                uiTestForm.setField1("You selected the the PG rated version, Go Snoop Dogg, Go!  by Dr. Dre.....");
            } else if (whichVersion.contains("C")){
                uiTestForm.setField1("For the X Rated version, your credit card will be charged $29.99");
            } else {
                // if they didn't make a proper choice, ask them again
                uiTestForm.setField1("You need to select a version");
                dm.resetDialogStatus(dialog2);
                return getUIFModelAndView(uiTestForm, "DialogView-Page1");
            }
        } else {
            uiTestForm.setField1("You Selected: Green Eggs And Ham");

            // in this case, return to client and wait for them to submit again before showing next dialog
            if (!dm.hasDialogBeenDisplayed(dialog2)){
                dm.addDialog(dialog2,uiTestForm.getMethodToCall());
                return getUIFModelAndView(uiTestForm, "DialogView-Page1");
            }

            // cluncky approach to invoking 2nd dialog
            if (dm.hasDialogBeenAnswered(dialog2)){
                whichVersion = dm.getDialogAnswer(dialog2);
            } else {
                // redirect back to client to display lightbox
                dm.addDialog(dialog2, uiTestForm.getMethodToCall());
                mv = showDialog(dialog2, uiTestForm, request, response);
                return mv;
            }

            // display which story the user has selected
            if(whichVersion.contains("A")){
                uiTestForm.setField1("You selected the G rated version, by Dr. Seuss. Here we go...");
            } else if(whichVersion.contains("B")){
                uiTestForm.setField1("You selected the the PG rated version, read by Sarah Silverman.....");
            } else if (whichVersion.contains("C")){
                uiTestForm.setField1("For the X Rated version, your credit card will be charged $29.99");
            } else {
                uiTestForm.setField1("You need to select a version");
                dm.resetDialogStatus(dialog2);
                return getUIFModelAndView(uiTestForm, "DialogView-Page1");
            }

        }

        // Wait at the client page for another page submit
        if (!dm.hasDialogBeenDisplayed(dialog3)) {
                dm.addDialog(dialog3,uiTestForm.getMethodToCall());
                return getUIFModelAndView(uiTestForm, "DialogView-Page1");
        };

        // Ask them if they want to start over
        if (dm.hasDialogBeenAnswered(dialog3)){
            doRestart = dm.wasDialogAnswerAffirmative(dialog3);
        } else {
            // redirect back to client to display lightbox
            dm.addDialog(dialog3, uiTestForm.getMethodToCall());
            mv = showDialog(dialog3, uiTestForm, request, response);
            return mv;
        }

        // clear the dialog manager entries, so when we come back, we'll ask all the questions again
        if (doRestart){
            dm.removeDialog(dialog1);
            dm.removeDialog(dialog2);
            dm.removeDialog(dialog3);
            uiTestForm.setField1("Ok, let's start over.");
        }

        // we're done, go to the next page
        return getUIFModelAndView(uiTestForm, "DialogView-Page2");
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
