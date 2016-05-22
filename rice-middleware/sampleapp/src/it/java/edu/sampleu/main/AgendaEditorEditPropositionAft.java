/**
 * Copyright 2005-2016 The Kuali Foundation
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

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Checks editing propositions.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditorEditPropositionAft extends WebDriverLegacyITBase {

    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL
                    + "?channelTitle=Agenda%20Lookup&channelUrl="
                    + WebDriverUtils.getBaseUrlString()
                    + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD
                    + "org.kuali.rice.krms.impl.repository.AgendaBo"
                    + AutomatedFunctionalTestUtils.SHOW_MAINTENANCE_LINKS
                    + "&returnLocation="
                    + AutomatedFunctionalTestUtils.PORTAL_URL
                    + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText(AGENDA_LOOKUP_LINK_TEXT);
        waitForPageToLoad();
    }

    /**
     * Test that we can submit an already existing agenda with a proposition with no errors.
     *
     * @throws Exception for any test problems
     */
    protected void testNoEditSubmit() throws Exception {
        selectFrameIframePortlet();

        // search for and edit specific record
        waitAndTypeByName("lookupCriteria[id]", "T1001");
        waitAndClickButtonByExactText("Search");
        waitAndClickByLinkText("edit");

        // make sure we can submit, reload, and that we are then in final status
        submitSuccessfully();
        waitAndClickButtonByText("Reload");
        waitForTextPresent("FINAL");
    }

    @Test
    public void testAgendaEditorEditPropositionNav() throws Exception {
        testNoEditSubmit();
        passed();
    }

}