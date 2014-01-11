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
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoCollectionSequenceAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionSequenceView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionSequenceView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Collection Features", "Sequence Column");
    }

    protected void changeSequenceView() throws Exception {
    	
    	//Auto Sequence
        assert(isOptionSelected("exampleShown", "Demo-CollectionSequence-Example1"));
        testAutoSequence();
        
        //Field Sequence
        selectOptionByName("exampleShown","Demo-CollectionSequence-Example2");
        waitForPageToLoad();
        assert(isOptionSelected("exampleShown", "Demo-CollectionSequence-Example2"));
        testFieldSequence();
        
        //No Sequence
        selectOptionByName("exampleShown","Demo-CollectionSequence-Example3");
        waitForPageToLoad();
        assert(isOptionSelected("exampleShown", "Demo-CollectionSequence-Example3"));
        testNoSequence();
    }

    protected void testCollectionSequenceExamples() throws Exception {
        changeSequenceView();
    }

    private boolean isOptionSelected(String dropDownName, String optionValue) {
        WebElement select = driver.findElement(By.name(dropDownName));
        List<WebElement> options = select.findElements(By.tagName("option"));
        for (WebElement option: options) {
            if (option.getAttribute("selected")!=null) {
                return true;
            }
        }
        return false;
    }
    
    private void testAutoSequence() throws InterruptedException{
    	for(short i=1; i<10 ; i++)
    	{
    		if(i==2)
    			i++;
    		waitForElementPresentByXpath("//div[@data-parent='Demo-CollectionSequence-Example1']/div[@class='uif-disclosureContent']/div/table/tbody/tr/td/div/span[contains(text(),'"+i+"')]");
    	}
    }
    
    private void testFieldSequence() throws InterruptedException{
    	//Cant test all in loop as the value differs a lot.
    	waitForElementPresentByXpath("//div[@data-parent='Demo-CollectionSequence-Example2']/div[@class='uif-disclosureContent']/div/table/tbody/tr/td/div[contains(text(),'3')]");
    }
    
    private void testNoSequence() throws InterruptedException{
    	waitForElementNotPresent(By.xpath("//div[@data-parent='Demo-CollectionSequence-Example3']/div[@class='uif-disclosureContent']/div/table/tbody/tr/td/div/span"));
    }

    @Test
    public void testCollectionSequenceBookmark() throws Exception {
        testCollectionSequenceExamples();
        passed();
    }

    @Test
    public void testCollectionSequenceNav() throws Exception {
        testCollectionSequenceExamples();
        passed();
    }
}
