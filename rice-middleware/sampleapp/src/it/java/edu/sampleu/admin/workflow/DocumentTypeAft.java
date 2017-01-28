/**
 * Copyright 2005-2017 The Kuali Foundation
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
package edu.sampleu.admin.workflow;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentTypeAft extends WebDriverLegacyITBase {

    /**
     *   AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Document%20Operation&channelUrl="+ WebDriverUtils
     *   .getBaseUrlString()+"/kew/DocumentOperation.do";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Document%20Type&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.doctype.bo.DocumentType&returnLocation="+AutomatedFunctionalTestUtils.PORTAL+"&hideReturnLink=true&docFormKey=88888888";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Document Type");
    }

    protected String testDocumentType() throws Exception { 
        selectFrameIframePortlet();
        waitAndClickByXpath("//img[@alt='create new']");
        selectFrameIframePortlet();
        String docId = findElement(By.xpath("//table[@summary='document header: general information']/tbody/tr/td")).getText();
        
        waitAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
        jiraAwareTypeByName("document.newMaintainableObject.name", "name" + uniqueString);
        jiraAwareTypeByName("document.newMaintainableObject.label", "name" + uniqueString);
        waitAndClickSubmit();
        waitForTextPresent("Document was successfully submitted.");
        return docId;
    }

    @Test
    public void testDocumentTypeBookmark() throws Exception {
        testDocumentType();
        passed();
    }

    @Test
    public void testDocumentTypeNav() throws Exception {
        testDocumentType();
        passed();
    }
}
