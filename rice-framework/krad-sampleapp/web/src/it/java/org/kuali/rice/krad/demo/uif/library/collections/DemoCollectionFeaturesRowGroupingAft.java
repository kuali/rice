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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoCollectionFeaturesRowGroupingAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TableLayoutGroupingView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-TableLayoutGroupingView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Row Grouping");
    }

    protected void testCollectionFeaturesRowGroupingBaseFunctionality() throws Exception {
        waitForElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section1']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow a']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section1']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-group='a']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section1']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow b']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section1']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-group='b']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section1']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow c']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section1']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-group='c']");
    }
    
    protected void testCollectionFeaturesRowGrouping2FieldGrouping() throws Exception {
        selectByName("exampleShown", "2 Field Grouping");
        waitForElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section2']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow 2001-fall']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section2']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-groupvalue='2001-fall']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section2']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow 2001-spring']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section2']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-groupvalue='2001-spring']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section2']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow 2002-fall']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section2']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-groupvalue='2002-fall']");
    }
    
    protected void testCollectionFeaturesRowGroupingOmitGroupField() throws Exception {
        selectByName("exampleShown", "Omit Group Field");
        waitForElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section3']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow a']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section3']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-group='a']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section3']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow b']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section3']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-group='b']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section3']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow c']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section3']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-group='c']");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section3']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-groupvalue]"))
        {
            fail("Group Field Not Omited.");
        }
    }
    
    protected void testCollectionFeaturesRowGroupingAddToGrouped() throws Exception {
        selectByName("exampleShown", "Add to Grouped");
        waitForElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section4']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow a']");
//        assertElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section4']/div[@class='dataTables_wrapper']/table/tbody/tr[@data-group='a']/td/div/span[contains(text(), 'A')]");
    }
    
    protected void testCollectionFeaturesRowGroupingPrefixOption() throws Exception {
        selectByName("exampleShown", "Prefix option");
        waitForElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section5']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow a']/td[contains(text(),'Lines with value A')]");
    }
    
    protected void testCollectionFeaturesRowGroupingCustomGroupTitle() throws Exception {
        selectByName("exampleShown", "Custom Group title");
        waitForElementPresentByXpath("//div[@id='Demo-TableLayoutGrouping-Section6']/div[@class='dataTables_wrapper']/table/tbody/tr[@class='uif-groupRow letter-a-in-item']/td[contains(text(),'Letter A in item')]");
    }
    
    @Test
    public void testCollectionFeaturesRowGroupingBookmark() throws Exception {
        testCollectionFeaturesRowGroupingBaseFunctionality();
        testCollectionFeaturesRowGrouping2FieldGrouping();
        testCollectionFeaturesRowGroupingOmitGroupField();
        testCollectionFeaturesRowGroupingAddToGrouped();
        testCollectionFeaturesRowGroupingPrefixOption();
        testCollectionFeaturesRowGroupingCustomGroupTitle();
        passed();
    }

    @Test
    public void testCollectionFeaturesRowGroupingNav() throws Exception {
        testCollectionFeaturesRowGroupingBaseFunctionality();
        testCollectionFeaturesRowGrouping2FieldGrouping();
        testCollectionFeaturesRowGroupingOmitGroupField();
        testCollectionFeaturesRowGroupingAddToGrouped();
        testCollectionFeaturesRowGroupingPrefixOption();
        testCollectionFeaturesRowGroupingCustomGroupTitle();
        passed();
    }  
}
