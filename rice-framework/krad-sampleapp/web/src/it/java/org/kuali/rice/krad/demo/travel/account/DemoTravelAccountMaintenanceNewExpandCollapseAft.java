/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.demo.travel.account;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMaintenanceNewExpandCollapseAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Travel Account Maintenance (New)");
    }

    protected void testTravelAccountMaintenanceNewExpandCollapse() throws Exception {
        waitAndClickByXpath("//a/span[contains(text(),'Document Overview')]");
        waitAndClickByXpath("//a/span[contains(text(),'Account Information')]");
        waitAndClickByXpath("//a/span[contains(text(),'Sub Accounts')]");
        waitAndClickButtonByExactText("Submit");
        waitForElementPresentByXpath("//div[@style='overflow: hidden; display: block;']/div/ul/li/a[contains(text(),'Description: Required')]");
        waitForElementPresentByXpath("//div[@style='overflow: hidden; display: block;']/div/ul/li/a[contains(text(),'Travel Account Number: Required')]");
        waitForElementPresentByXpath("//div[@style='overflow: hidden; display: block;']/div/ul/li/a[contains(text(),'Travel Account Name: Required')]");
        waitForElementPresentByXpath("//div[@style='overflow: hidden; display: block;']/div/ul/li/a[contains(text(),'Travel Account Type Code: Required')]");
    }
    
    protected void testTravelAccountMaintenanceNewExpandCollapse1() throws Exception {
    	 waitAndTypeByName("document.documentHeader.documentDescription","Travel Account Maintenance New Test Document");
         String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
         waitAndTypeByName("document.newMaintainableObject.dataObject.number",randomCode);
         waitAndTypeByName("document.newMaintainableObject.dataObject.name",randomCode);
         waitAndClickByName("document.newMaintainableObject.dataObject.accountTypeCode");
    	waitForElementPresentByXpath("//section[@class='uif-disclosure tableborders wrap uif-boxLayoutVerticalItem clearfix']/div[@style='display: none; overflow: hidden;']");
    	waitAndClickByXpath("//section[@class='uif-disclosure tableborders wrap uif-boxLayoutVerticalItem clearfix']/header/h3/a");
    	waitForElementPresentByXpath("//section[@class='uif-disclosure tableborders wrap uif-boxLayoutVerticalItem clearfix']/div[@style='display: block; overflow: hidden;']");
    	waitAndClickButtonByExactText("Submit");
    	waitAndClickConfirmSubmitOk();
    	waitForElementPresentByXpath("//section[@class='uif-disclosure tableborders wrap uif-boxLayoutVerticalItem clearfix']/header/h3/a[@data-open='true']");
    }

    @Test
    public void testDemoTravelAccountMaintenanceNewExpandCollapseBookmark() throws Exception {
        testTravelAccountMaintenanceNewExpandCollapse();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceNewExpandCollapseNav() throws Exception {
        testTravelAccountMaintenanceNewExpandCollapse();
        passed();
    }
    
    @Test
    public void testDemoTravelAccountMaintenanceNewExpandCollapse1Bookmark() throws Exception {
        testTravelAccountMaintenanceNewExpandCollapse1();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceNewExpandCollapse1Nav() throws Exception {
        testTravelAccountMaintenanceNewExpandCollapse1();
        passed();
    }
}
