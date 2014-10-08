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
package edu.sampleu.main;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * test that checks that rules can be added to agendas
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditorAndOrToggleSyncAft extends WebDriverLegacyITBase {
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

    protected void testAgendaEditorAddRuleWithSimpleProposition() throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("lookupCriteria[name]","SimpleAgendaCompoundProp");
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        waitAndClickByXpath("//a[contains(text(),'edit')]");
        selectFrameIframePortlet();
        waitAndClickByXpath("//a/p[contains(text(),'CmpdTestRule: For testing compound props')]");
        waitAndClickByXpath("//button[contains(text(),'Edit Rule')]");
        selectFrameIframePortlet();
        waitForTextPresent("( Campus Code = Muir AND Campus Code = Revelle AND Campus Code = Warren )");
        selectByName("document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[1].data.proposition.compoundOpCode","OR");
        selectByName("document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[3].data.proposition.compoundOpCode","OR");
        waitForTextNotPresent("( Campus Code = Muir AND Campus Code = Revelle AND Campus Code = Warren )");
        waitForTextPresent("( Campus Code = Muir OR Campus Code = Revelle OR Campus Code = Warren )");
        passed();
    }

    /**
     * test that a rule can be added to an agenda
     */
    @Test
    public void testAgendaEditorAddRuleNav() throws Exception {
        testAgendaEditorAddRuleWithSimpleProposition();
    }

}

