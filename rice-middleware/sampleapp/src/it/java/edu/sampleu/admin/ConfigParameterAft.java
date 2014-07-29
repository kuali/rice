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
package edu.sampleu.admin;

import org.junit.Test;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigParameterAft extends AdminTmplMthdAftNavCreateNewBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Parameter&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation="
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Parameter&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Parameter
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Parameter";
    }
 
    protected void createNewEnterDetails() throws InterruptedException {
        waitAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
        selectOptionByName("document.newMaintainableObject.namespaceCode", namespaceCode);
        jiraAwareTypeByName("document.newMaintainableObject.componentCode", "ActionList");
        jiraAwareTypeByName("document.newMaintainableObject.name", "name" + uniqueString);
        jiraAwareTypeByName("document.newMaintainableObject.description", "desc" + uniqueString);
        selectOptionByName("document.newMaintainableObject.parameterTypeCode", "CONFG");
        waitAndClickByName("document.newMaintainableObject.evaluationOperatorCode");
    }

    public void testConfigParameterSearchEdit(JiraAwareFailable failable) throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        createNewEnterDetails();
        testSearchEditCancel();
        passed();
    }
    
    @Test
    public void testConfigParameterBookmark() throws Exception {
        testConfigParameterSearchEdit(this);
        passed();
    }
     
    @Test
    public void testConfigParameterNav() throws Exception {
        testConfigParameterSearchEdit(this);
        passed();
     }
}
