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
package edu.sampleu.kew;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoutingIdentityManagementDocumentTypeHierarchyAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Routing%20Report&channelUrl="
     * + WebDriverUtils.getBaseUrlString() + "/kew/RoutingReport.do?returnLocation=" +
     * AutomatedFunctionalTestUtils.PORTAL;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL
            + "?channelTitle=Routing%20and%20Identity%20Management%20Document%20Type%20Hierarchy&channelUrl="
            + WebDriverUtils.getBaseUrlString()
            + "/kew/RuleQuickLinks.do";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickMainMenu();
        waitAndClickByLinkText("Routing and Identity Management Document Type Hierarchy");
    }

    protected void testRoutingIdentityManagementDocumentTypeHierarchy() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByXpath("//input[@id='tab-SendNotificationRequest-imageToggle']");
        assertTextPresent("RequestsNode");
        assertTextPresent("ReviewersNode");
        waitAndClickByXpath("//input[@id='tab-eDocExample1ParentDocument-imageToggle']");
        assertTextPresent("eDoc.Example1.Node1");
        waitAndClickByXpath("//input[@id='tab-KualiDocument-imageToggle']");
        assertTextPresent("Rice Document (RiceDocument)");
        assertTextPresent("Edit Document Type");
        assertTextPresent("View Document Configuration");
        assertTextPresent("Agenda Maintenance Document (AgendaMaintenanceDocument)");
    }

    @Test
    public void testRoutingIdentityManagementDocumentTypeHierarchyBookmark() throws Exception {
        testRoutingIdentityManagementDocumentTypeHierarchy();
        passed();
    }

    @Test
    public void testRoutingIdentityManagementDocumentTypeHierarchyNav() throws Exception {
        testRoutingIdentityManagementDocumentTypeHierarchy();
        passed();
    }
}
