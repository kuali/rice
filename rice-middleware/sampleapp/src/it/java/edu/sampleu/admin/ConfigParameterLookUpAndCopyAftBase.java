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

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigParameterLookUpAndCopyAftBase extends AdminTmplMthdAftNavBase {

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

    public void testConfigParameterLookUpAndCopyBookmark(JiraAwareFailable failable) throws Exception {
        testConfigParameterLookUpAndCopy();
        passed();
    }

    public void testConfigParameterLookUpAndCopyNav(JiraAwareFailable failable) throws Exception {
        testConfigParameterLookUpAndCopy();
        passed();
    }    
    
    public void testConfigParameterLookUpAndCopy() throws Exception
    {
        selectFrameIframePortlet();
        waitAndTypeByName("name","*email*");
        waitAndClickSearchSecond();
        waitAndClickByXpath("//a[@title='copy Parameter withParameter Name=EMAIL_NOTIFICATION_TEST_ADDRESS Application ID=KUALI Namespace Code=KR-WKFLW Component=ActionList ']");
        waitAndTypeByName("document.documentHeader.documentDescription","Test description of parameter copy " + AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomCharsNot9Digits());
        selectByName("document.newMaintainableObject.namespaceCode","KR-WKFLW - Workflow");
        waitAndTypeByName("document.newMaintainableObject.componentCode","ActionList");
        waitAndTypeByName("document.newMaintainableObject.applicationId","KUALI");
        waitAndTypeByName("document.newMaintainableObject.name","EMAIL_NOTIFICATION_TEST_ADDRESS_COPY_" + AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomChars());
        waitAndClickByName("methodToCall.route");
        checkForDocError();
        waitAndClickByName("methodToCall.close");
////         waitAndClickByName("methodToCall.processAnswer.button1");
    }
}
