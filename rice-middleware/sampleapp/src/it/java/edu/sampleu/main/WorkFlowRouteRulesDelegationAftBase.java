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

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesDelegationAftBase extends MainTmplMthdSTNavBase{

    /**
     * ITUtil.PORTAL + "?channelTitle=Routing%20Rules%20Delegation&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     * "/kr/lookup.do?businessObjectClassName=org.kuali.rice.kew.rule.RuleDelegationBo&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK + "&showMaintenanceLinks=true";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Routing%20Rules%20Delegation&channelUrl=" + WebDriverUtils
            .getBaseUrlString() +
            "/kr/lookup.do?businessObjectClassName=org.kuali.rice.kew.rule.RuleDelegationBo&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK + "&showMaintenanceLinks=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Routing Rules Delegation
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Routing Rules Delegation";
    }

    public void testWorkFlowRouteRulesDelegationBookmark(JiraAwareFailable failable) throws Exception {
        testCreateNewRRDTravelRequestDestRouting();
        passed();
    }

    public void testWorkFlowRouteRulesDelegationNav(JiraAwareFailable failable) throws Exception {
        testCreateNewRRDTravelRequestDestRouting();
        passed();
    }
}
