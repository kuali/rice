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

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigComponentCreateNewAftBase extends ConfigComponentAftBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Component&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation="+
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */    
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Component&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation="+
            AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    public void testConfigComponentCreateNewBookmark() throws Exception {
        testConfigComponentCreateNew();
        passed();
    }

    public void testConfigComponentCreateNewNav() throws Exception {
        testConfigComponentCreateNew();
        passed();
    }

    public void testConfigComponentCreateNewSaveBookmark() throws Exception {
        testConfigComponentCreateNewSave();
        passed();
    }

    public void testConfigComponentCreateNewSaveNav() throws Exception {
        testConfigComponentCreateNewSave();
        passed();
    }

    public void testConfigComponentCreateNewDuplicateEntryBookmark() throws Exception {
        testConfigComponentCreateNewDuplicateEntry();
        passed();
    }
    
     public void testConfigComponentCreateNewDuplicateEntryNav() throws Exception {
         testConfigComponentCreateNewDuplicateEntry();
         passed();
     }
 

    public void testConfigComponentCreateNew() throws Exception {
        String docId = testCreateNew();
        submitAndClose();
        assertDocSearch(docId, "FINAL");
        passed();
    }

    public void testConfigComponentCreateNewDuplicateEntry() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByXpath("//img[@alt='create new']");
        selectFrameIframePortlet();
        String tempValue=AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.documentHeader.documentDescription","Description for Duplicate");
        selectByName("document.newMaintainableObject.namespaceCode","KR-BUS - Service Bus");
        waitAndTypeByName("document.newMaintainableObject.code","COMPCODE");
        waitAndTypeByName("document.newMaintainableObject.name","Component Name "+tempValue);
        waitAndClickByXpath("//input[@name='methodToCall.route']");
        waitForTextPresent("Document was successfully submitted.");
        selectTopFrame();
        waitAndClickAdministration();
        waitAndClickByLinkText("Component");
        selectFrameIframePortlet();
        waitAndClickByXpath("//img[@alt='create new']");
        selectFrameIframePortlet();
        waitAndTypeByName("document.documentHeader.documentDescription","Description for Duplicate");
        selectByName("document.newMaintainableObject.namespaceCode","KR-BUS - Service Bus");
        waitAndTypeByName("document.newMaintainableObject.code","COMPCODE");
        waitAndTypeByName("document.newMaintainableObject.name","Component Name "+tempValue);
        waitAndClickByXpath("//input[@name='methodToCall.route']");
        waitForTextPresent("This document cannot be Saved or Routed because a record with the same primary key already exists.");
        passed();
    }
    
    public void testConfigComponentCreateNewSave() throws Exception {
        String docId = testCreateNew();
        saveAndClose();
        assertDocSearch(docId, DOC_STATUS_SAVED);
        passed();
    }
}
