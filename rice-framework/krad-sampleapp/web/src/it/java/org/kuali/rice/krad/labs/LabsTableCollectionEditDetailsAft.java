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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsTableCollectionEditDetailsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=LabsAppContainerFixed
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=Lab-TableCollectionEditDetails";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	waitAndClickByLinkText("Table Collection Edit Details");
    }

    protected void testLabsTableCollectionEditDetails() throws InterruptedException {
        //Enter data in fields, add line; verify correct data is added in new line.
        waitAndTypeByName("newCollectionLines['collection1'].field2","999");
        waitAndTypeByName("newCollectionLines['collection1'].field4","999");
        waitAndClickByXpath("//button[contains(text(),'Add')]");
        waitForElementPresentByXpath("//span[contains(text(),'999')]");

        //Delete new line; verify line is gone from top of list.
        waitAndClickByXpath("//button[contains(text(),'Delete')]");
        waitForElementNotPresent(By.xpath("//span[contains(text(),'999')]"));

        //Edit an existing line; change values in fields; save; verify new values are retained.
        waitAndClickByXpath("//button[contains(text(),'Edit in Dialog')]");
        clearTextByName("dialogDataObject.field2");
        waitAndTypeByName("dialogDataObject.field2","999");
        clearTextByName("dialogDataObject.field4");
        waitAndTypeByName("dialogDataObject.field4","999");
        waitAndClickByXpath("//button[contains(text(),'Save Changes')]");
        waitForElementPresentByXpath("//span[contains(text(),'999')]");
        waitForElementPresentByXpath("//pre[contains(text(),'999')]");

        //Edit an existing line; change values; click No to cancel; verify new values are not retained.
        waitAndClickByXpath("//button[contains(text(),'Edit in Dialog')]");
        waitForElementVisibleBy(By.name("dialogDataObject.field2"));
        clearTextByName("dialogDataObject.field2");
        waitAndTypeByName("dialogDataObject.field2","111");
        clearTextByName("dialogDataObject.field4");
        waitAndTypeByName("dialogDataObject.field4","111");
        waitAndClickByXpath("//button[contains(text(),'No')]");
        waitForElementPresentByXpath("//span[contains(text(),'999')]");
        waitForElementPresentByXpath("//pre[contains(text(),'999')]");
        waitForElementNotPresent(By.xpath("//span[contains(text(),'111')]"));
        waitForElementNotPresent(By.xpath("//pre[contains(text(),'111')]"));
    }

    @Test
    public void testLabsTableCollectionEditDetailsBookmark() throws Exception {
    	testLabsTableCollectionEditDetails();
        passed();
    }

    @Test
    public void testLabsTableCollectionEditDetailsNav() throws Exception {
    	testLabsTableCollectionEditDetails();
        passed();
    }
}
