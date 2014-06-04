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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsSchemaAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/mock?viewId=LabsSchema
     */
    public static final String BOOKMARK_URL = "/kr-krad/mock?viewId=LabsSchema";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Schema");
    }

    protected void testDemoSchema() throws InterruptedException {
    	waitForElementPresentByXpath("//section[2]/header/h3");
    	waitAndTypeByName("data[field1]","asd");
    	waitAndTypeByName("data[field2]","asd");
    	waitAndTypeByName("data[field3]","asd");
    	waitForElementPresentByXpath("//div[@data-label='Field 4']/label");
    	waitForElementPresentByXpath("//div[@data-label='Field 5']/label");
    	waitAndClickByName("booleanData[field6]");
    	waitAndClickByXpath("//input[@name='data[field7]' and @value='Opt 1']");
    	waitAndClickByXpath("//a[@class='btn btn-default icon-calendar ui-datepicker-trigger']");
    	waitAndClickByXpath("//button[contains(text(),'Today')]");
    	waitAndClickByName("data[field9]");
    	waitForElementPresentByXpath("//button[contains(text(),'calculate')]");
    	waitForElementPresentByXpath("//section[3]/div/table");
    }

    @Test
    public void testDemoSchemaBookmark() throws Exception {
    	testDemoSchema();
        passed();
    }

    @Test
    public void testDemoSchemaNav() throws Exception {
    	testDemoSchema();
        passed();
    }
}
