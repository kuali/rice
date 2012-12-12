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
package edu.samplu.travel.krad.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import edu.samplu.common.UpgradedSeleniumITBase;

/**
 * Test verifies updates in Totals at client side.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionTotallingIT extends UpgradedSeleniumITBase {

    @Test
    public void testCollectionTotalling() throws InterruptedException {
        
        //Scenario Asserts Changes in Total at client side
        assertEquals("Total: 419", driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section1 div[role='grid'] div[data-label='Total']")).getText());
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section1 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section1]  input[name='list1[0].field1']")).clear();
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section1 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section1]  input[name='list1[0].field1']")).sendKeys("10");
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section1 div[role='grid'] div[data-label='Total']")).click();
        Thread.sleep(5000);
        assertEquals("Total: 424", driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section1 div[role='grid'] div[data-label='Total']")).getText());
  
        
        
        //Scenario Asserts Changes in Total at client side on keyUp
        assertEquals("Total: 419", driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']")).getText());
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section2 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section2] input[name='list1[0].field1']")).clear();        
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section2 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section2] input[name='list1[0].field1']")).sendKeys("9");
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']")).click();
        Thread.sleep(5000);
        assertEquals("Total: 423", driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']")).getText());
        
        
        //Asserts absence of Total in 2nd column at the footer for Demonstrating totaling on only some columns 
        assertEquals("", driver.findElement(By.xpath("//div[3]/div[3]/table/tfoot/tr/th[2]")).getText());
        //Asserts Presence of Total in 2nd column at the footer for Demonstrating totaling on only some columns 
        assertEquals("Total: 369", driver.findElement(By.xpath("//div[3]/div[3]/table/tfoot/tr/th[3]/div/fieldset/div/div[2]/div[2]")).getText());
      
        
        //Asserts Presence of Total in left most column only being one with no totaling itself 
        assertEquals("Total:", driver.findElement(By.id("u100213_span")).getText());
        assertEquals("419", driver.findElement(By.xpath("//div[4]/div[3]/table/tfoot/tr/th[2]/div/fieldset/div/div[2]/div[2]")).getText());

        //Asserts changes in value in Total and Decimal for Demonstrating multiple types of calculations for a single column (also setting average to 3 decimal places to demonstrate passing data to calculation function) 
        assertEquals("Total: 382", driver.findElement(By.xpath("//div[2]/div/fieldset/div/div[2]/div[2]")).getText());
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section6 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section6] input[name='list1[0].field4']")).clear();
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section6 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section6] input[name='list1[0].field4']")).sendKeys("11");
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']")).click();
        Thread.sleep(5000);
        assertEquals("Total: 385", driver.findElement(By.xpath("//div[2]/div/fieldset/div/div[2]/div[2]")).getText());
       
       // Assert changes in Decimal..
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section6 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section6] input[name='list1[0].field4']")).clear();
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section6 > div[role='grid'] > table > tbody div[data-parent=Demo-CollectionTotaling-Section6] input[name='list1[0].field4']")).sendKeys("15.25");
        driver.findElement(By.cssSelector("div#Demo-CollectionTotaling-Section2 div[role='grid'] div[data-label='Total']")).click();
        Thread.sleep(5000);
        assertEquals("Page Average: 11.917", driver.findElement(By.xpath("//div[2]/fieldset/div/div[2]/div")).getText());
       
    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.UpgradedSeleniumITBase#getTestUrl()
     */
    @Override
    public String getTestUrl() {
        //Returns "Group Totalling" url
        return "/kr-krad/uicomponents?viewId=Demo-CollectionTotaling&methodToCall=start";
    }

}
