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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsHelpAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-HelpView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-HelpView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Help");
    }

    protected void testWidgetsTooltipHelp() throws Exception {
    	fireMouseOverEventByXpath("//label[@id='Demo-Help-Field1_label']");
    	waitForElementPresentByXpath("//div[@class='popover top in']");
    	fireMouseOverEventByXpath("//label[@id='Demo-Help-Field2_label']");
    	waitForElementPresentByXpath("//div[@class='popover top in']");
    	fireMouseOverEventByXpath("//label[@id='Demo-Help-Field3_label']");
    	waitForElementPresentByXpath("//div[@class='popover top in']");
    	fireMouseOverEventByXpath("//label[@id='Demo-Help-Override-Tooltip_label']");
    	waitForElementPresentByXpath("//div[@class='popover top in']");
    	fireMouseOverEventByXpath("//label[@id='Demo-Help-Override-On-Focus-Tooltip_label']");
    	waitForElementPresentByXpath("//div[@class='popover top in']");
    	fireMouseOverEventByXpath("//label[@id='Demo-Help-Checkbox_label']");
    	waitForElementPresentByXpath("//div[@class='popover top in']");
    	fireMouseOverEventByXpath("//a[@data-linkfor='Demo-Help-DisplayOnlyFields_disclosureContent']");
    	waitForElementPresentByXpath("//div[@class='popover top in']");
    }

    protected void testMissingTooltipHelp() throws Exception {
       selectByName("exampleShown","Missing Tooltip Help");
       fireEvent("dataField1", "focus");
       waitForElementNotPresent(By.xpath("//div[@class='popover top in']"));
    }
    
    protected void testExternalHelp() throws Exception {
    	selectByName("exampleShown","External Help");
    	waitAndClickByXpath("//div[@id='Demo-Help-Section3_disclosureContent']/div/div/div/button[@title='Help for Field Label']");
    	switchToWindow("Kuali Foundation");
    	switchToWindow("Kuali ::");
    	waitAndClickByXpath("//div[@id='Demo-Help-Section3_disclosureContent']/div[2]/div/div/button[@title='Help for Field Label']");
    	switchToWindow("Kuali Foundation");
    	switchToWindow("Kuali ::");
    	waitAndClickByXpath("//div[@id='Demo-Help-Section3_disclosureContent']/div[3]/div/div/button[@title='Help for Field Label']");
    	switchToWindow("Kuali Foundation");
    	switchToWindow("Kuali ::");
    }
    
    private void testAllHelp() throws Exception {
    	testWidgetsTooltipHelp();
        testMissingTooltipHelp();
	    testExternalHelp();
	    passed();
    }

    @Test
    public void testWidgetsHelpBookmark() throws Exception {
    	testAllHelp();
    }

    @Test
    public void testWidgetsHelpNav() throws Exception {
    	testAllHelp();
    }
}
