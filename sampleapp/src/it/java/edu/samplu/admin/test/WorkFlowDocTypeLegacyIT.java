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

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * tests creating and cancelling new and edit Document Type maintenance screens 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowDocTypeLegacyIT extends WebDriverLegacyITBase{
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    @Test
    /**
     * tests that a new Document Type maintenance document can be cancelled
     */
    public void testCreateNew() throws Exception {
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClickByLinkText("Administration");
        waitForPageToLoad();
        Thread.sleep(5000);
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClickByLinkText("Document Type");
        waitForPageToLoad();
        Thread.sleep(5000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClickByXpath("//img[@alt='create new']");
        waitAndClickByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.processAnswer.button0");
        waitForPageToLoad();
        passed();
    }
    
    @Test
    /**
     * tests that a Document Type maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditDocType() throws Exception {
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClickByLinkText("Administration");
        waitForPageToLoad();
        Thread.sleep(5000);
        assertEquals("Kuali Portal Index", getTitle());
        waitAndClickByLinkText("Document Type");
        waitForPageToLoad();
        Thread.sleep(5000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        waitAndClickByLinkText("edit");
        waitAndClickByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.processAnswer.button0");
        passed();
    }
    
    //Test to validate the requirement of Document Type Label field while submitting a document.
    @Test
    public void testCreateDocType() throws Exception {
        waitAndClickByLinkText("Administration");
        waitForPageToLoad();
        waitAndClickByLinkText("Document Type");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClick("img[alt=\"create new\"]","");
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Document Type description");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "DocType Name");
        waitAndClickByName("methodToCall.route");
        waitForPageToLoad();
        assertTextPresent("Document Type Label is required.");
        waitAndClickByName("methodToCall.cancel");
        waitAndClickByName("methodToCall.processAnswer.button0");
        waitForPageToLoad();
        //selectWindow("null");
        driver.switchTo().defaultContent();
        Thread.sleep(5000);
        waitAndClickByXpath("(//input[@name='imageField'])[2]");
        Thread.sleep(5000);
        passed();
    }
}
