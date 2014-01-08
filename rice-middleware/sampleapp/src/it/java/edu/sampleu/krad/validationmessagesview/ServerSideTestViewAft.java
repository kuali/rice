/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.sampleu.krad.validationmessagesview;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerSideTestViewAft extends WebDriverLegacyITBase {

    /**
     * "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91";
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=Demo-ValidationServerSide&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("ServerSide Constraint Validation Demo");
        switchToWindow("Kuali :: Validation Server-side Test View");               
    }

    //Code for KRAD Test Package.
    protected void testServerSideTestView() throws Exception {
      
       //MinMax length and value  and Required Constraint
       waitAndTypeByName("field9","a");
       waitAndTypeByName("field10","1");
       waitAndClickByXpath("//button[@id='usave']");
       Thread.sleep(4000);
       assertTextPresent("MinMax Length test: Must be between 2 and 5 characters long");
       assertTextPresent("MinMax Value test: Value must be greater than 2 and no more than 50");
       assertTextPresent(new String[]{"Required constraint", "4 errors"});
       
       //PreRequisite constraint
       waitForElementPresentByXpath("//input[@name='field7' and @disabled]");
       waitAndClickByXpath("//input[@type='checkbox' and @name='booleanField']");
       if(isElementPresentByXpath("//input[@name='field7' and @disabled]")) {
           fail("PreRequisite Constraint isn't working !");
       }
       
       //MustOccurs constraint
       waitAndTypeByName("field14","a");
       waitAndClickByXpath("//button[@id='usave']");
       Thread.sleep(4000);

    }

    @Test
    public void testServerSideTestViewBookmark() throws Exception {
        testServerSideTestView();
        passed();
    }

    @Test
    public void testServerSideTestViewNav() throws Exception {
        testServerSideTestView();
        passed();
    }
}
