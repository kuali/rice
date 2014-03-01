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
package edu.sampleu.admin.workflow;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentOperationAft extends WebDriverLegacyITBase {

    /**
     *   AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Document%20Operation&channelUrl="+ WebDriverUtils
     *   .getBaseUrlString()+"/kew/DocumentOperation.do";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Document%20Operation&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kew/DocumentOperation.do";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Document Operation");
    }

    protected void testDocumentOperation() throws Exception { 
        selectFrameIframePortlet();
        waitAndTypeByName("documentId","2381");
        waitAndClickByName("methodToCall.getDocument");
        waitForElementPresentByXpath("//input[@src='images/buttonsmall_save.gif']");
        assertTextPresent(new String[] {"Document Actions", "Queue Document", "Queue Action Invocation", "Document ID:",
                "2443", "Route Node Instance ID:", "2443"});
    }

    @Test
    public void testDocumentOperationBookmark() throws Exception {
        testDocumentOperation();
        passed();
    }

    @Test
    public void testDocumentOperationNav() throws Exception {
        testDocumentOperation();
        passed();
    }
}
