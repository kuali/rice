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
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * tests the inquiry feature in rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }
    
    @Test
    public void testInquiry() throws Exception {
        waitAndClickByLinkText("KRAD");
        waitAndClickByLinkText("Travel Account Lookup");
        selectFrame("iframeportlet");
        waitAndTypeByName("lookupCriteria[number]", "a1");
        waitAndClickByXpath("//*[@alt='Direct Inquiry']");
        selectTopFrame();
        Thread.sleep(5000);
        WebElement iframe1= driver.findElement(By.xpath("//iframe[@class='fancybox-iframe']"));
        driver.switchTo().frame(iframe1);
        assertEquals("Travel Account Inquiry", getTextByXpath("//h1/span").trim());
        assertElementPresentByLinkText("a1");
        waitAndClickByXpath("//button[@id='u13']");
        selectFrame("iframeportlet");
        
        waitAndClickByXpath("//button[@id='u19']");
        Thread.sleep(2000);
        waitAndClickByXpath("//*[@alt='Direct Inquiry']");
        Alert a1= driver.switchTo().alert();
        assertEquals("Please enter a value in the appropriate field.",a1.getText());
        a1.accept();
        switchToWindow("null");
        selectFrame("iframeportlet");
        
        //No Direct Inquiry Option for Fiscal Officer.
        waitAndTypeByName("lookupCriteria[foId]", "1");
        waitAndClickByXpath("//*[@id='u229']");
        selectTopFrame();
        Thread.sleep(5000);
        WebElement iframe2= driver.findElement(By.xpath("//iframe[@class='fancybox-iframe']"));
        driver.switchTo().frame(iframe2);
        assertEquals("Fiscal Officer Lookup", getTextByXpath("//h1/span").trim());
        assertEquals("1",getAttributeByName("lookupCriteria[id]", "value"));
        waitAndClickByXpath("//div[contains(button, 'Search')]/button[3]");
        selectFrame("iframeportlet");
        
        
        selectOptionByName("lookupCriteria[extension.accountTypeCode]", "CAT");
        waitAndClickByXpath("//fieldset[@id='u232_fieldset']/input[@alt='Search Field']");
        selectTopFrame();
        Thread.sleep(5000);
        WebElement iframe3= driver.findElement(By.xpath("//iframe[@class='fancybox-iframe']"));
        driver.switchTo().frame(iframe3);
        assertEquals("Travel Account Type Lookup", getTextByXpath("//h1/span").trim());
        assertEquals("CAT",getAttributeByName("lookupCriteria[accountTypeCode]", "value"));
        waitAndClickByXpath("//div[contains(button, 'Search')]/button[3]");
        selectFrame("iframeportlet");

    }
}
