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
public class ConfigNamespaceCreateNewAft extends AdminTmplMthdAftNavCreateNewBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Namespace&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="+
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */   
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Namespace&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="+
            AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    protected void createNewEnterDetails() throws InterruptedException {
        waitAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
        jiraAwareTypeByName("document.newMaintainableObject.code", "code" + uniqueString);
        jiraAwareTypeByName("document.newMaintainableObject.name", "name" + uniqueString);
    }

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Namespace
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Namespace";
    }

    public void testConfigNamespaceCreateNewBookmark(JiraAwareFailable failable) throws Exception {
        testConfigNamespaceCreateNew();
        passed();
    }

    public void testConfigNamespaceCreateNewNav(JiraAwareFailable failable) throws Exception {
        testConfigNamespaceCreateNew();
        passed();
    }    
    
    public void testConfigNamespaceCreateNew() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
//        waitAndClickByXpath(CREATE_NEW_XPATH);
        String docId = waitForDocId();

        waitAndTypeByName("document.documentHeader.documentDescription", "Test description of Namespace create new " + AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomCharsNot9Digits());
        waitAndTypeByName("document.newMaintainableObject.code", "KR-SYS3" + AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomChars());
        waitAndTypeByName("document.newMaintainableObject.name","Enterprise Infrastructure 3");
        waitAndTypeByName("document.newMaintainableObject.applicationId","RICE");

        addAdHocRecipientsPerson(new String[]{getUserName(), "A"});

//        waitAndClickByName("methodToCall.route");
        waitAndClickSave();
        waitAndClickSubmit();
        assertActionList(docId, "A", "ENROUTE");
        assertOutbox(docId, "FINAL");
        checkForDocError();

        assertDocSearch(docId, DOC_STATUS_FINAL);

//        waitAndClickByName("methodToCall.close");
////         waitAndClickByName("methodToCall.processAnswer.button1");
    }

    @Test
    public void testConfigNamespaceCreateNewBookmark() throws Exception {
        testConfigNamespaceCreateNewBookmark(this);
    }

    @Test
    public void testConfigNamespaceCreateNewNav() throws Exception {
        testConfigNamespaceCreateNewNav(this);
    }
}
