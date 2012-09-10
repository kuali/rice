/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

/**
 * tests creating and cancelling new and edit Document Type maintenance screens 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowDocTypeIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    @Test
    /**
     * tests that a new Document Type maintenance document can be cancelled
     */
    public void testCreateNew() throws Exception {
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClick("link=Administration");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClick("link=Document Type");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClick("//img[@alt='create new']");
        waitForPageToLoad();
        assertTrue(isElementPresent("methodToCall.cancel"));
        waitAndClick("methodToCall.cancel");
        waitForPageToLoad();
        waitAndClick("methodToCall.processAnswer.button0");
        waitForPageToLoad();
    }
    
    @Test
    /**
     * tests that a Document Type maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditDocType() throws Exception {
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClick("link=Administration");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClick("link=Document Type");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        waitAndClick("link=edit");
        waitForPageToLoad();
        assertTrue(isElementPresent("methodToCall.cancel"));
        waitAndClick("methodToCall.cancel");
        waitForPageToLoad();
        waitAndClick("methodToCall.processAnswer.button0");
        waitForPageToLoad();
    }
    
    //Test to validate the requirement of Document Type Label field while submitting a document.
    @Test
    public void testCreateDocType() throws Exception {
        waitAndClick("link=Administration");
        waitForPageToLoad();
        waitAndClick("link=Document Type");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClick("css=img[alt=\"create new\"]");
        waitForPageToLoad();
        waitAndType("id=document.documentHeader.documentDescription", "Document Type description");
        waitAndType("id=document.newMaintainableObject.name", "DocType Name");
        waitAndClick("name=methodToCall.route");
        waitForPageToLoad();
        assertTrue(isTextPresent("Document Type Label is required."));
        waitAndClick("name=methodToCall.cancel");
        waitForPageToLoad();
        waitAndClick("name=methodToCall.processAnswer.button0");
        waitForPageToLoad();
        selectWindow("null");
        waitAndClick("xpath=(//input[@name='imageField'])[2]");
        waitForPageToLoad();
    }
}
