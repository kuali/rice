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
package edu.sampleu.krad.screenelement;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StandardLayoutDemoAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=Demo-StandardLayout&methodToCall=start
     */
    public static final String BOOKMARK_URL ="/kr-krad/uicomponents?viewId=Demo-StandardLayout&methodToCall=start";
  
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Standard Layout Demo");
        switchToWindow("Kuali :: View Title");
    }
    
    private void testStandardLayoutDemo() throws Exception{
        //Standard Sections
        waitForElementPresentByXpath("//div[@data-parent='Demo-StandardLayout-Section1']/input[@name='field1']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-StandardLayout-Section1']/input[@name='field2']");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-Section2-SubSection1' and @data-parent='Demo-StandardLayout-Section2']");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-Section2-SubSection2' and @data-parent='Demo-StandardLayout-Section2']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-StandardLayout-Section3']/input[@name='field8']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-StandardLayout-Section3']/input[@name='field9']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-StandardLayout-Section3']/div/input[@name='field10']");
        waitForElementPresentByXpath("//div[@data-parent='Demo-StandardLayout-Section3']/div/input[@name='field11']");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-Section3-SubSection1' and @data-parent='Demo-StandardLayout-Section3']");
        
        //Collection Sections
        waitAndClickByName("Demo-StandardLayout-CollectionSectionsPage");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-CollectionSection1']/section[@class='uif-collectionItem uif-boxCollectionItem uif-collectionAddItem clearfix']");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-CollectionSection1']/section[@class='uif-collectionItem uif-boxCollectionItem clearfix']");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-CollectionSection1']/section/table[@class='table table-condensed table-bordered uif-gridLayout uif-table-fixed']");
        waitForElementPresentByXpath("//div[@data-group='Demo-StandardLayout-CollectionSection2-SubCollection_line0']");
        waitForElementPresentByXpath("//div[@id='Demo-StandardLayout-Section4']/input[@name='field15']");
        waitForElementPresentByXpath("//div[@id='Demo-StandardLayout-Section4']/input[@name='field16']");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-CollectionSubSection' and @data-parent='Demo-StandardLayout-Section4']/section");
        
        //CSS Grid
        waitAndClickByName("Demo-StandardLayout-CssGridPage");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-CssGridSection1' and @data-parent='Demo-StandardLayout-CssGridPage']");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-CssGridSection2' and @data-parent='Demo-StandardLayout-CssGridPage']");
        waitForElementPresentByXpath("//section[@id='Demo-StandardLayout-CssGridSection3' and @data-parent='Demo-StandardLayout-CssGridPage']");
    }
    
    @Test
    public void testStandardLayoutDemoBookmark() throws Exception {
        testStandardLayoutDemo();
        passed();
    }

    @Test
    public void testStandardLayoutDemoNav() throws Exception {
        testStandardLayoutDemo();
        passed();
    }
}