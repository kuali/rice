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
package edu.sampleu.admin;

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigParameterAft extends AdminTmplMthdAftNavCopyBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Parameter&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation="
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Parameter&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    public static String[] inputVerifyDetails;

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

    @Override
    protected void createNewEnterDetails() throws InterruptedException {
        inputDetails();

        selectOptionByName("document.newMaintainableObject.namespaceCode", inputVerifyDetails[6]);
    }

    private void inputDetails() throws InterruptedException {
        inputVerifyDetails = new String[] {
                getDescriptionUnique(),
                "ActionList",
                "name" + uniqueString,
                "desc" + uniqueString,
                "Config",
                "Allowed",
                namespaceCode
        };

// can we generalize to figure out the input type and what to do with it?
//        for (String[] input: inputVerifyDetails) {
//            input(input[0], input[1]);
//        }

        waitAndTypeByName("document.documentHeader.documentDescription", inputVerifyDetails[0]);
        jiraAwareTypeByName("document.newMaintainableObject.componentCode", inputVerifyDetails[1]);
        jiraAwareTypeByName("document.newMaintainableObject.name", inputVerifyDetails[2]);
        jiraAwareTypeByName("document.newMaintainableObject.description", inputVerifyDetails[3]);
        jiraAwareTypeByName(("document.newMaintainableObject.parameterTypeCode"), inputVerifyDetails[4]);
//        selectOptionText(By.name("document.newMaintainableObject.parameterTypeCode"), inputVerifyDetails[4]);
        waitAndClickByName("document.newMaintainableObject.evaluationOperatorCode", inputVerifyDetails[5]);
    }

    @Override
    protected void blanketApproveAssert(String docId) throws InterruptedException {
        super.blanketApproveAssert(docId);
        waitAndClickLinkContainingText(docId);

        driver.switchTo().window((String)driver.getWindowHandles().toArray()[1]);

        assertTextPresent(inputVerifyDetails);
        screenshot();
    }

    @Override
    protected void createNewLookupDetails() throws InterruptedException {
        inputDetails();

        assertEquals("", getTextByName(CANCEL_NAME));

        String componentLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.coreservice.impl.component.ComponentBo!!).(((code:document.newMaintainableObject.componentCode,namespaceCode:document.newMaintainableObject.namespaceCode,))).((`document.newMaintainableObject.componentCode:code,document.newMaintainableObject.namespaceCode:namespaceCode,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(componentLookUp);
        waitAndClickSearch();
        waitAndClickReturnValue();
    }
}
