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
package edu.sampleu.main.notification;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Created by sona on 10/14/14.
 */
public class AgendaEditorPropositionLabelAft extends WebDriverLegacyITBase{
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

        protected void testAgendaEditorPropositionLabel() throws Exception {
            selectFrameIframePortlet();
            waitAndTypeByName("lookupCriteria[name]","My Fabulous Agenda");
            waitAndClickByXpath("//button[contains(text(),'Search')]");
            waitAndClickByXpath("//a[contains(text(),'edit')]");
            selectFrameIframePortlet();
            waitAndClickByXpath("//a/div[contains(text(),'Rule1: stub rule lorem ipsum')]");
            waitAndClickByXpath("//button[contains(text(),'Edit Rule')]");
            selectFrameIframePortlet();
            waitForTextPresent("Campus Code = BL");
            selectByName("document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].data.proposition.id","T1000");
            waitAndClickButtonByExactText("Edit")   ;
            assertLabelWithTextPresent("Description");
            assertLabelWithTextPresent("Category");
            assertLabelWithTextPresent("Term");
            assertLabelWithTextPresent("Comparison");
            assertLabelWithTextPresent("Value");
            passed();
        }


        @Test
        public void testAgendaEditorPropositionLabelNav() throws Exception {
            testAgendaEditorPropositionLabel();
        }

}
