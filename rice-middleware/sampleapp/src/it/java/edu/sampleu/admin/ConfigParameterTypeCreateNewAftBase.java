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

import org.apache.commons.lang.RandomStringUtils;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigParameterTypeCreateNewAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Parameter%20Type&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterTypeBo&docFormKey=88888888&returnLocation="
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Parameter%20Type&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterTypeBo&docFormKey=88888888&returnLocation="+
            AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Parameter Type
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Parameter Type";
    }

    public void testConfigParameterTypeCreateNewBookmark(JiraAwareFailable failable) throws Exception {
        testConfigParameterTypeCreateNew();
        passed();
    }

    public void testConfigParameterTypeCreateNewNav(JiraAwareFailable failable) throws Exception {
        testConfigParameterTypeCreateNew();
        passed();
    }    
    
    public void testConfigParameterTypeCreateNew() throws Exception
    {
        selectFrameIframePortlet();
        waitAndClickByXpath(CREATE_NEW_XPATH);        
        waitAndTypeByName("document.documentHeader.documentDescription","Test description of parameter type create " + AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomCharsNot9Digits());
        waitAndTypeByName("document.newMaintainableObject.code", RandomStringUtils.randomAlphabetic(5));
        waitAndTypeByName("document.newMaintainableObject.name","Test1ParmTypeCode" + AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomChars());
        waitAndClickByName("methodToCall.route");
        checkForDocError();
        waitAndClickByName("methodToCall.close");
//         waitAndClickByName("methodToCall.processAnswer.button1");
    }
}
