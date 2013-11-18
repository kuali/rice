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
package edu.sampleu.travel;

import com.thoughtworks.selenium.SeleneseTestBase;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionTotallingAft extends WebDriverLegacyITBase {

    /**
     * "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&readOnlyFields=field91";
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=Demo-CollectionTotaling&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByXpath(KUALI_COLLECTION_TOTALLING_XPATH);
        switchToWindow(KUALI_COLLECTION_TOTALLING_WINDOW_XPATH);               
    }

    //Code for KRAD Test Package.
    protected void testCollectionTotalling() throws Exception {
        //Scenario Asserts Changes in Total at client side
        assertEquals("333",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-message']"));
        clearTextByXpath("//div[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tbody/tr[2]/td[2]/div/input[@name='list1[0].field1']");
        waitAndTypeByXpath("//div[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tbody/tr[2]/td[2]/div/input[@name='list1[0].field1']","10");
        waitAndClickByXpath("//div[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-message']");
        Thread.sleep(2000);
        assertEquals("338",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-message']"));        
        
        //Scenario Asserts Changes in Total at client side on keyUp
        assertEquals("333",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section2']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-message']"));
        clearTextByXpath("//div[@id='Demo-CollectionTotaling-Section2']/div[@role='grid']/table/tbody/tr[2]/td[2]/div/input[@name='list1[0].field1']");
        waitAndTypeByXpath("//div[@id='Demo-CollectionTotaling-Section2']/div[@role='grid']/table/tbody/tr[2]/td[2]/div/input[@name='list1[0].field1']","10");
        Thread.sleep(2000);
        assertEquals("338",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section2']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-message']"));        
        
        //Totalling Flexibility
        assertEquals("82",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section3']/div[@role='grid']/table/tfoot/tr/th[3]/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-message']"));

        //Left Total Labels
        assertEquals("Page Total:",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section4']/div[@role='grid']/table/tfoot/tr/th/div/div[@class='uif-verticalBoxLayout']/span[@class='uif-label']/label"));
        
        //Hide Footer
        assertElementPresentByXpath("//div[@id='Demo-CollectionTotaling-Section5']/div[@role='grid']/table/tfoot/tr[@style='display: none;']");
        
        //Multiple Calculations
        assertEquals("Page Total:",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section6']/div[@role='grid']/table/tfoot/tr/th[5]/div/div[@class='uif-verticalBoxLayout']/div/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-label']/label"));
        assertEquals("Page Average:",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section6']/div[@role='grid']/table/tfoot/tr/th[5]/div/div[@class='uif-verticalBoxLayout']/div[2]/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-label']/label"));
        assertEquals("Page Min:",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section6']/div[@role='grid']/table/tfoot/tr/th[5]/div/div[@class='uif-verticalBoxLayout']/div[3]/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-label']/label"));
        assertEquals("Page Max:",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section6']/div[@role='grid']/table/tfoot/tr/th[5]/div/div[@class='uif-verticalBoxLayout']/div[4]/fieldset/div/div[@class='uif-verticalBoxLayout']/div[@data-role='pageTotal']/span[@class='uif-label']/label"));
        
        //Group Totaling
        assertEquals("Group Total:",getTextByXpath("//div[@id='Demo-CollectionTotaling-Section7']/div[@role='grid']/table/tbody/tr[7]/td/div/span/label"));
    }

    @Test
    public void testCollectionTotallingBookmark() throws Exception {
        testCollectionTotalling();
        passed();
    }

    @Test
    public void testCollectionTotallingNav() throws Exception {
        testCollectionTotalling();
        passed();
    }
}
