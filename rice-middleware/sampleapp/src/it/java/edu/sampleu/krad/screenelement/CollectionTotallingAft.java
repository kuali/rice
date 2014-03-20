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
        Integer preValue=Integer.parseInt(getTextByXpath("//section[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div/p"));
        clearTextByXpath("//section[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tbody/tr[2]/td[2]/div/input[@name='list1[0].field1']");
        waitAndTypeByXpath("//section[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tbody/tr[2]/td[2]/div/input[@name='list1[0].field1']","0");
        waitAndClickByXpath("//section[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div/p");
        Integer postValue=Integer.parseInt(getTextByXpath("//section[@id='Demo-CollectionTotaling-Section1']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div/p"));
        Thread.sleep(2000);
        if(postValue>preValue) {
            jiraAwareFail("Totalling not working !");
        }        
        
        //Scenario Asserts Changes in Total at client side on keyUp
        Integer preValueClient=Integer.parseInt(getTextByXpath("//section[@id='Demo-CollectionTotaling-Section2']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div/p"));
        clearTextByXpath("//section[@id='Demo-CollectionTotaling-Section2']/div[@role='grid']/table/tbody/tr[2]/td[2]/div/input[@name='list1[0].field1']");
        waitAndTypeByXpath("//section[@id='Demo-CollectionTotaling-Section2']/div[@role='grid']/table/tbody/tr[2]/td[2]/div/input[@name='list1[0].field1']","0");
        Integer postValueClient=Integer.parseInt(getTextByXpath("//section[@id='Demo-CollectionTotaling-Section2']/div[@role='grid']/table/tfoot/tr/th[2]/div/fieldset/div/div/p"));
        Thread.sleep(2000);        
        if(postValueClient>preValueClient) {
            jiraAwareFail("Totalling not working !");
        }  
        
        //Totalling Flexibility
        assertElementPresentByXpath("//section[@id='Demo-CollectionTotaling-Section3']/div[@role='grid']/table/tfoot/tr/th[3]/div/fieldset/div/div/p");

        //Left Total Labels
        assertElementPresentByXpath("//section[@id='Demo-CollectionTotaling-Section4']/div[@role='grid']/table/tfoot/tr/th/div/label");
        
        //Multiple Calculations
        assertEquals("Page Total:",getTextByXpath("//section[@id='Demo-CollectionTotaling-Section6']/div[@role='grid']/table/tfoot/tr/th[5]/div/div/fieldset/div/div/label"));
        assertEquals("Page Average:",getTextByXpath("//section[@id='Demo-CollectionTotaling-Section6']/div[@role='grid']/table/tfoot/tr/th[5]/div/div[2]/fieldset/div/div/label"));
        assertEquals("Page Min:",getTextByXpath("//section[@id='Demo-CollectionTotaling-Section6']/div[@role='grid']/table/tfoot/tr/th[5]/div/div[3]/fieldset/div/div/label"));
        assertEquals("Page Max:",getTextByXpath("//section[@id='Demo-CollectionTotaling-Section6']/div[@role='grid']/table/tfoot/tr/th[5]/div/div[4]/fieldset/div/div/label"));
        
        //Group Totaling
        assertEquals("Group Total:",getTextByXpath("//section[@id='Demo-CollectionTotaling-Section7']/div[@role='grid']/table/tbody/tr[7]/td/div/label"));
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
