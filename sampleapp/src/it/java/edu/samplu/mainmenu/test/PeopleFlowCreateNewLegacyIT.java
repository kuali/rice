/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.samplu.mainmenu.test;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import com.thoughtworks.selenium.Selenium;

import edu.samplu.common.AdminMenuITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.MainMenuLookupITBase;
import edu.samplu.common.MainMenuLookupLegacyITBase;
import edu.samplu.common.MenuITBase;
import edu.samplu.common.UpgradedSeleniumITBase;

/**
 * TODO vchauhan don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowCreateNewLegacyIT extends MainMenuLookupLegacyITBase {
  
     
    
    @Test
    public void testPeopleFlow() throws Exception {
        
        //Click Main Menu and Create New
        gotoMenuLinkLocator();
        waitAndClick(By.linkText(getCreateNewLinkLocator()));
        //Save docId
        waitForElementPresent("div[data-headerfor='PeopleFlow-MaintenanceView'] div[data-label='Document Number'] > span");
        String docId = getText("div[data-headerfor='PeopleFlow-MaintenanceView'] div[data-label='Document Number'] > span");
         
        
        driver.findElement(By.name("document.documentHeader.documentDescription")).clear();
        driver.findElement(By.name("document.documentHeader.documentDescription")).sendKeys("Description for Document");
        new Select(driver.findElement(By.name("document.newMaintainableObject.dataObject.namespaceCode"))).selectByVisibleText("KUALI - Kuali Systems");
        driver.findElement(By.name("document.newMaintainableObject.dataObject.name")).clear();
        driver.findElement(By.name("document.newMaintainableObject.dataObject.name")).sendKeys("Document Name" + ITUtil.DTS);
        
        //Add Row1
        driver.findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        driver.findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).sendKeys("kr");
        driver.findElement(By.cssSelector("button[data-loadingmessage='Adding Line...']")).click();
        Thread.sleep(3000);
        //Add Row2
        driver.findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        driver.findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).sendKeys("admin");
        driver.findElement(By.cssSelector("button[data-loadingmessage='Adding Line...']")).click();
        Thread.sleep(3000);
        
        //Blanket approve
        driver.findElement(By.cssSelector("div[data-parent='PeopleFlow-MaintenanceView'] > div.uif-footer button~button~button")).click();
        Thread.sleep(5000);
        //Close the Doc
        driver.findElement(By.id("uif-close")).click();
        Thread.sleep(3000);
        
        
        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        driver.findElement(By.cssSelector("img[alt=\"doc search\"]")).click();
        Thread.sleep(5000);
        driver.switchTo().frame("iframeportlet");

        driver.findElement(By.cssSelector("td.infoline > input[name=\"methodToCall.search\"]")).click();
        Thread.sleep(5000);
        assertEquals("FINAL", driver.findElement(By.xpath("//table[@id='row']/tbody/tr/td[4]")).getText());
        driver.switchTo().defaultContent();
        driver.findElement(By.name("imageField")).click();

    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MainMenuLookupITBase#lookupAssertions()
     */
    @Override
    public void lookupAssertions() {
        // nothing

    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuITBase#getLinkLocator()
     */
    @Override
    protected String getLinkLocator() {
        return "People Flow";
    }

    
    @Ignore // No Need to     
    @Test
    @Override
    public void testLookUp() throws Exception {}
}
