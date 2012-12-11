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
package edu.samplu.mainmenu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateSampleEDocLiteLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }
    @Test
    public void testUntitled() throws Exception {    
        assertEquals("Kuali Portal Index", getTitle());
        // click on the main menu eDoc Lite link
        waitAndClickByLinkText("eDoc Lite");
        waitForPageToLoad();
        Thread.sleep(3000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @alt='search']");
        waitForPageToLoad();
        // click on the create new.
        waitAndClickByLinkText("Create Document");
        waitForPageToLoad();
        Thread.sleep(3000);
        String docId = getTextByXpath("//table/tbody/tr[4]/td[@class='datacell1']");
        waitAndTypeByName("userName", "Viral Chauhan");
        waitAndTypeByName("rqstDate", "12/03/2020");
        checkByName("fundedBy");
        waitAndTypeByName("addText","Note Added.");
        waitAndClickByXpath("//td[@class='datacell']/div/img");
        waitForPageToLoad();
        waitAndClickByXpath("//input[@value='submit']");
        assertEquals(Boolean.FALSE, isElementPresentByXpath("//input[@value='submit']"));
        assertEquals(Boolean.FALSE, isElementPresentByXpath("//input[@value='save']"));
        assertEquals(Boolean.FALSE, isElementPresentByXpath("//input[@value='cancel']"));
        waitForPageToLoad();
        selectTopFrame();
        waitAndClickByXpath("//img[@alt='doc search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @alt='search']");
        waitForPageToLoad();
        isElementPresent(By.linkText(docId));
        
    }
}
