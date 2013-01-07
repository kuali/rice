/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.krad.configview;

import edu.samplu.common.WebDriverLegacyITBase;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * Selenium test that tests collections
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionsLegacyIT extends WebDriverLegacyITBase {
	
	
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=ConfigurationTestView-Collections&methodToCall=start";
    }

    /**
     * Test adding a column of values to the Default Tests Table Layout
     */
    @Test
    public void testDefaultTestsTableLayout() throws Exception{
        
        assertTableLayout();        
        waitAndTypeByName("newCollectionLines['list1'].field1", "asdf1");
        waitAndTypeByName("newCollectionLines['list1'].field2", "asdf2");
        waitAndTypeByName("newCollectionLines['list1'].field3", "asdf3");
        waitAndTypeByName("newCollectionLines['list1'].field4", "asdf4");
        waitAndClickByXpath("//button[contains(.,'add')]"); // the first button is the one we want
                
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if ( getAttributeByName("newCollectionLines['list1'].field1","value").equals("")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }

        Assert.assertEquals("",getAttributeByName("newCollectionLines['list1'].field1","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4","value"));
        Assert.assertEquals("asdf1", getAttributeByName("list1[0].field1","value"));
        Assert.assertEquals("asdf2", getAttributeByName("list1[0].field2","value"));
        Assert.assertEquals("asdf3", getAttributeByName("list1[0].field3","value"));
        Assert.assertEquals("asdf4", getAttributeByName("list1[0].field4","value"));
        
        Assert.assertTrue(isElementPresentByXpath("//div[@id='Collections-Base-TableLayout_disclosureContent']/div/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
        passed();
    }

    
    private void assertTableLayout() {
        Assert.assertTrue(driver.getPageSource().contains("Table Layout"));
        Assert.assertTrue(driver.getPageSource().contains("Field 1"));
        Assert.assertTrue(driver.getPageSource().contains("Field 2"));
        Assert.assertTrue(driver.getPageSource().contains("Field 3"));
        Assert.assertTrue(driver.getPageSource().contains("Field 4"));
        Assert.assertTrue(driver.getPageSource().contains("Actions"));
    }

    /**
     * Test adding a column of values to the Add Blank Line Tests Table Layout
     */
    @Test
    public void testAddBlankLine() throws Exception {
        waitAndClickByLinkText("Add Blank Line");
        waitAndClickByXpath("//button[contains(.,'Add Line')]");
        Thread.sleep(3000); //  TODO a wait until the loading.gif isn't visible woudl be better
        assertElementPresentByName("list1[0].field1");
     
        assertTableLayout();
        Assert.assertEquals("", getAttributeByName("list1[0].field1","value"));
        Assert.assertEquals("", getAttributeByName("list1[0].field2","value"));
        Assert.assertEquals("", getAttributeByName("list1[0].field3","value"));
        Assert.assertEquals("", getAttributeByName("list1[0].field4","value"));
        Assert.assertEquals("5", getAttributeByName("list1[1].field1","value"));
        Assert.assertEquals("6", getAttributeByName("list1[1].field2","value"));
        Assert.assertEquals("7", getAttributeByName("list1[1].field3","value"));
        Assert.assertEquals("8", getAttributeByName("list1[1].field4","value"));
        
        
        Assert.assertEquals("Total: 419", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());     
        waitAndTypeByName("list1[0].field1", "1");
        waitAndTypeByName("list1[0].field2", "1");
        waitAndTypeByName("list1[0].field3", "1");
        waitAndTypeByName("list1[0].field4", "1");
        Assert.assertEquals("Total: 420", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());   
        passed();
        
    }

  
    /**
     * Test action column placement in table layout collections
     */
    @Test
    public void testActionColumnPlacement() throws Exception {

        //Lack of proper locators its not possible to uniquely identify/locate this elements without use of ID's.
        //This restricts us to use the XPath to locate elements from the dome. 
        //This test is prone to throw error in case of any changes in the dom Html graph.
        
        
        
        waitAndClickByLinkText("Column Sequence");
        Thread.sleep(2000);
        //waitAndClick("css=div.jGrowl-close");
        // check if actions column RIGHT by default
        //Assert.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-collection1']//tr[2]/td[6]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (isElementPresentByXpath("//tr[2]/td[6]/div/fieldset/div/div[2]/button")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        Assert.assertTrue(isElementPresentByXpath("//tr[2]/td[6]/div/fieldset/div/div[2]/button"));

        // check if actions column is LEFT
        //Assert.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-collection2']//tr[2]/td[1]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (isElementPresentByXpath("//div[2]/div[2]/div[2]/table/tbody/tr[2]/td/div/fieldset/div/div[2]/button")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        Assert.assertTrue(isElementPresentByXpath("//div[2]/div[2]/div[2]/table/tbody/tr[2]/td/div/fieldset/div/div[2]/button"));
        
        // check if actions column is 3rd in a sub collection
        //Assert.assertTrue(isElementPresent("//div[@id='ConfigurationTestView-subCollection2_line0']//tr[2]/td[3]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= 60) Assert.fail("timeout");
            try { if (isElementPresentByXpath("//tr[2]/td[3]/div/fieldset/div/div[2]/button")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        Assert.assertTrue(isElementPresentByXpath("//tr[2]/td[3]/div/fieldset/div/div[2]/button"));
        passed();
        
    }
    
    
    @Test
	public void testAddViaLightbox() throws Exception {
		
		waitAndClickByLinkText("Add Via Lightbox");
		Assert.assertEquals("Total: 419", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
	    waitAndClickByXpath("//button[contains(.,'Add Line')]");
        Thread.sleep(3000);
        waitAndTypeByXpath("//form/div/table/tbody/tr/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[2]/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[3]/td/div/input", "1");
        waitAndTypeByXpath("//form/div/table/tbody/tr[4]/td/div/input", "1");
        waitAndClickByXpath("//button[@id='uif-addLine_add']");
        Thread.sleep(3000);
		Assert.assertEquals("Total: 420", driver.findElement(By.xpath("//fieldset/div/div[2]/div[2]")).getText());
        passed();
	}
	
	@Test
	public void testColumnSequence() throws Exception {
		
		waitAndClickByLinkText("Column Sequence");
		Thread.sleep(3000);
		waitAndTypeByName("newCollectionLines['list1'].field1", "1");
		waitAndTypeByName("newCollectionLines['list1'].field2", "1");
		waitAndTypeByName("newCollectionLines['list1'].field3", "1");
		waitAndTypeByName("newCollectionLines['list1'].field4", "1");
		waitAndClick(By.id("uif-addLine_add"));
		Thread.sleep(3000);
				
		//Check if row has been added really or not
		Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field1","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4","value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field1","value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field2","value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field3","value"));
        Assert.assertEquals("1", getAttributeByName("list1[0].field4","value"));
        
        //Check for the added if delete is present or not
		Assert.assertTrue(isElementPresentByXpath("//div[@id='Collections-ColumnSequence-TableDefault_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
		passed();
	}

	@Test
	public void testSequencerow() throws Exception {
		waitAndClickByLinkText("Save Row");
		Thread.sleep(3000);
		waitAndTypeByName("newCollectionLines['list1'].field1", "1");
		waitAndTypeByName("newCollectionLines['list1'].field2", "1");
		waitAndTypeByName("newCollectionLines['list1'].field3", "1");
		waitAndTypeByName("newCollectionLines['list1'].field4", "1");
				
		waitAndClickByXpath("//button[contains(.,'add')]");
		Thread.sleep(3000);
		
		//Check if row has been added really or not
		Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field1","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field2","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field3","value"));
        Assert.assertEquals("", getAttributeByName("newCollectionLines['list1'].field4","value"));
        Assert.assertEquals("1",getAttributeByName("list1[0].field1","value"));
        Assert.assertEquals("1",getAttributeByName("list1[0].field2","value"));
        Assert.assertEquals("1",getAttributeByName("list1[0].field3","value"));
        Assert.assertEquals("1",getAttributeByName("list1[0].field4","value"));
        
        //Check for the added if delete is present or not
		Assert.assertTrue(isElementPresentByXpath("//div[@id='Collections-SaveRow-Table_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button"));
		Assert.assertTrue(isElementPresentByXpath("//div[@id='Collections-SaveRow-Table_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button[@class='uif-action uif-secondaryActionButton uif-smallActionButton uif-saveLineAction']"));
		passed();
	}

    
}
