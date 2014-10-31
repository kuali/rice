/*
 * Copyright 2006-2014 The Kuali Foundation
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
package edu.sampleu.admin;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class PersonAftBase extends AdminTmplMthdAftNavCreateNewBase {

    protected void createNewEnterDetails() throws InterruptedException {
        waitAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
        jiraAwareTypeByName("document.principalName", "pn" + uniqueString);
        selectByName("newAffln.affiliationTypeCode","Affiliate");
        selectByName("newAffln.campusCode","BL - BLOOMINGTON");
        waitAndClickByName("newAffln.dflt");
        waitAndClickByName("methodToCall.addAffln.anchor");
        waitAndClickByName("methodToCall.toggleTab.tabContact");
        selectByName("newName.nameCode","Other");
        selectByName("newName.namePrefix","Mr");
        jiraAwareTypeByName("newName.firstName","Deep");
        jiraAwareTypeByName("newName.middleName","D");
        jiraAwareTypeByName("newName.lastName","Moteria");
        waitAndClickByName("newName.dflt");
        waitAndClickByName("methodToCall.addName.anchor");
    }

    /**
     * {@inheritDoc}
     * Person
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Person";
    }

    protected void saveAndClose() throws InterruptedException {
        checkForDocError();
        waitAndClickByXpath(SAVE_XPATH);
        waitForTextPresent("Document was successfully saved");
        waitAndClickByName("methodToCall.close");
//         waitAndClickByName("methodToCall.processAnswer.button1");
    }

    protected void saveAndReload() throws InterruptedException {
        checkForDocError();
        waitAndClickByXpath(SAVE_XPATH);
        waitForTextPresent("Document was successfully saved");
        waitAndClickByName("methodToCall.reload");
//         waitAndClickByName("methodToCall.processAnswer.button1");
    }

    protected void submitAndClose() throws InterruptedException {
        checkForDocError();
        waitAndClickByName("methodToCall.route");
        waitForTextPresent("Document was successfully submitted");
        waitAndClickByName("methodToCall.close");
//         waitAndClickByName("methodToCall.processAnswer.button1");
    }

    /**
     * submits the doc and asserts that it was successfully submitted
     * does not close the document
     *
     * @throws InterruptedException
     */
    protected void submit() throws InterruptedException {
        checkForDocError();
        waitAndClickByName("methodToCall.route");
        waitForTextPresent("Document was successfully submitted");
    }

    /**
     * recalls a document.
     * closes the page when done.
     *
     * @param cancel if true, performs recall and cancel. if false, performs recall to action list
     *
     * @throws InterruptedException
     */
    protected void recall(boolean cancel) throws InterruptedException {
        waitAndClickByName("methodToCall.recall");
        waitForTextPresent("the reason below");
        waitAndTypeByName("reason", "Oops!");
        if (cancel){
            // recall and cancel
            waitAndClickByName("methodToCall.processAnswer.button1");
            waitForTextPresent("RECALLED");
            waitAndClickByName("methodToCall.close");
        } else {
            // recall to action list
            waitAndClickByName("methodToCall.processAnswer.button0");
            waitForTextPresent("SAVED");
            waitAndClickByName("methodToCall.close");
            waitAndClickByName("methodToCall.processAnswer.button1");
        }
    }

}
