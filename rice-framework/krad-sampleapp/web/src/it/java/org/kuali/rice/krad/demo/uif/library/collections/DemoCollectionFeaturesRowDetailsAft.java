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
public class DemoCollectionFeaturesRowDetailsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TableLayoutDetailsView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-TableLayoutDetailsView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Row Details");
    }

    protected void testCollectionFeaturesRowDetails() throws Exception {
      if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section1']/div/table/tbody/tr[@class='detailsRow']")) {
        fail("Row Details Present");
      }
      waitAndClickButtonByText("Open/Close Row Details");
      assertElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section1']/div/table/tbody/tr[@class='detailsRow']");
      waitAndClickButtonByText("Open/Close Row Details");
      if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section1']/div/table/tbody/tr[@class='detailsRow']")) {
        fail("Row Details Present");
      }
      waitAndClickButtonByText("Open/Close Row Details");
    }
    
    protected void testCollectionFeaturesRowDetailsAjaxRetrival() throws Exception {
        selectByName("exampleShown","Ajax Retrieval");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='dataTables_wrapper']/table/tbody/tr[@class='detailsRow']")) {
          fail("Row Details Present");
        }
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section2_detLink_line0']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='dataTables_wrapper']/table/tbody/tr[@class='detailsRow']");
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section2_detLink_line0']");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='dataTables_wrapper']/table/tbody/tr[@class='detailsRow']")) {
          fail("Row Details Present");
        }
    }
    
    protected void testCollectionFeaturesRowDetailsTableSubCollection() throws Exception {
        selectByName("exampleShown","W/ Table SubCollection");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section3']/div/table/tbody/tr[@class='detailsRow']")) {
            fail("Row Details Present");
        }
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section3_detLink_line0']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section3']/div/table/tbody/tr[@class='detailsRow']/td/div/section/div/div/table");
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section3_detLink_line0']");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section3']/div/table/tbody/tr[@class='detailsRow']/td/div/section/div/div/table")) {
            fail("Row Details Present");
        }
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section3_detLink_line0']");
    }
    
    protected void testCollectionFeaturesRowDetailsStackedSubCollection() throws Exception {
        selectByName("exampleShown","W/ Stacked SubCollection");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section4']/div/table/tbody/tr[@class='detailsRow']")) {
            fail("Row Details Present");
        }
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section4_detLink_line0']");
        waitForElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section4']/div/table/tbody/tr[@class='detailsRow']/td/div/div[2]/div[2]/table");
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section4_detLink_line0']");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section4']/div/table/tbody/tr[@class='detailsRow']")) {
            fail("Row Details Present");
        }
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section4_detLink_line0']");
    }
    
    protected void testCollectionFeaturesRowDetailsNestedDetails() throws Exception {
        selectByName("exampleShown","Nested Details");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section5']/div/table/tbody/tr[@class='detailsRow']")) {
            fail("Row Details Present");
        }
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section5_detLink_line0']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section5']/div/table/tbody/tr[@class='detailsRow']/td/div/section/div/table/tbody/tr/td/div/fieldset/div/a");
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section5_detLink_line0']");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section5']/div/table/tbody/tr[@class='detailsRow']/td/div/section/div/table")) {
            fail("Row Details Present");
        }
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section5_detLink_line0']");
    }
    
    protected void testCollectionFeaturesRowDetailsOpenedDetails() throws Exception {
        selectByName("exampleShown","Opened Details");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section6']/div/table/tbody/tr[@class='detailsRow']");
        waitAndClickByXpath("//a[@id='Demo-TableLayoutDetails-Section6_detLink_add']");
        assertElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section6']/div/table/tbody/tr[@class='detailsRow']/td");
        waitAndClickByXpath("//div[@id='Demo-TableLayoutDetails-Section6']/button");
        if(isElementPresentByXpath("//div[@id='Demo-TableLayoutDetails-Section6']/div/table/tbody/tr[@class='detailsRow']/td")) {
          fail("Row Details Present");
        }
      }
    
    @Test
    public void testCollectionFeaturesRowDetailsBookmark() throws Exception {
        testCollectionFeaturesRowDetails();
        //testCollectionFeaturesRowDetailsAjaxRetrival();   //Commented as this feature is not present on the environment
        testCollectionFeaturesRowDetailsTableSubCollection();
        testCollectionFeaturesRowDetailsStackedSubCollection();
        testCollectionFeaturesRowDetailsNestedDetails();
        testCollectionFeaturesRowDetailsOpenedDetails();
        passed();
    }

    @Test
    public void testCollectionFeaturesRowDetailsNav() throws Exception {
        testCollectionFeaturesRowDetails();
        //testCollectionFeaturesRowDetailsAjaxRetrival();	//Commented as this feature is not present on the environment
        testCollectionFeaturesRowDetailsTableSubCollection();
        testCollectionFeaturesRowDetailsStackedSubCollection();
        testCollectionFeaturesRowDetailsNestedDetails();
        testCollectionFeaturesRowDetailsOpenedDetails();
        passed();
    }  
}
