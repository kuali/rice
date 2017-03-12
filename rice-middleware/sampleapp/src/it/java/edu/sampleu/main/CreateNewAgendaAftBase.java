/**
 * Copyright 2005-2017 The Kuali Foundation
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
package edu.sampleu.main;

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateNewAgendaAftBase extends MainTmplMthdSTNavBase{

    /**
     * ITUtil.PORTAL + "?channelTitle=Create%20New%20Agenda&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     *  "/kr-krad/krmsAgendaEditor?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.AgendaEditor&returnLocation=" +
     *  ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Create%20New%20Agenda&channelUrl=" + WebDriverUtils
            .getBaseUrlString() +"/kr-krad/krmsAgendaEditor?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.AgendaEditor&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Create New Agenda
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Create New Agenda";
    }

    protected void testCreateNewAgenda() throws Exception {
        selectFrameIframePortlet();
        String desc = getDescriptionUnique();
        String docId = waitForAgendaDocId();
        waitAndSelectLabeled("Namespace:", "Kuali Rules Test");
        waitAndTypeLabeledInput("Name:", desc);
        fireEvent("document.newMaintainableObject.dataObject.contextName", "focus");
        waitAndTypeLabeledInput("Context:", "Context1");
        fireEvent("document.newMaintainableObject.dataObject.contextName", "blur");
        Thread.sleep(1000);
        // extra focus and blur to work around KULRICE-11534 Create New Agenda requires two blur events to fully render Type when Context is typed in (first renders label, second renders select)
        fireEvent("document.newMaintainableObject.dataObject.contextName", "focus");
        Thread.sleep(500);
        fireEvent("document.newMaintainableObject.dataObject.contextName", "blur");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.agenda.typeId");
        waitAndSelectLabeled("Type:", "Campus Agenda");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.customAttributesMap[Campus]");
        waitAndTypeLabeledInput("Campus:", "BL");
        waitAndTypeLabeledInput("label:", "Type label for " + desc);
        submitSuccessfully();
        assertDocSearch(docId, "FINAL");
    }
}
