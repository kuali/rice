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
package edu.sampleu.krad.compview;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *  Tests that the data attributes are rendered as expected for all controls
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifDataAttributesNavAft extends WebDriverLegacyITBase {

    public static String BOOKMARK_URL = WebDriverUtils.getBaseUrlString()+ "/kr-krad/data-attributes-test-uif-controller?viewId=dataAttributesView_selenium&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * Tests that the data attributes are rendered as expected for all controls
     */
    @Test
    public void testDataAttributesPresentInControlsNav() throws Exception{
    	open(getBookmarkUrl());
    	waitAndTypeByName("field2","");
    	fireMouseOverEventByName("field1");
    	waitForTextPresent("Required");
    	waitAndTypeByName("field1","");
    	fireMouseOverEventByName("field2");
    	waitForTextPresent("Required");
    	selectByName("field88","Vegetables");
    	waitForElementPresentByXpath("//div[@id='ui-datepicker-div' and @style='position: absolute; top: 348.1875px; width: 34em; left: 31px; z-index: 1; display: none;']");
    	waitAndClickByName("field3");
    	waitForElementPresentByXpath("//div[@id='ui-datepicker-div' and @style='position: absolute; top: 348.1875px; width: 34em; left: 31px; z-index: 1; display: block;']");
    	waitForElementPresentByXpath("//input[@type='checkbox' and @name='bField1' and @checked='checked']");
    	waitAndClickByXpath("//input[@type='radio' and @name='field5' and @value='1']");
    	waitForElementPresentByXpath("//input[@type='file' and @name='fileUpload']");
    	waitAndTypeByName("testPerson.principalName","fred");
    	waitAndTypeByName("field2","");
    	waitForTextPresent("fred, fred");
    	waitAndTypeByName("testPerson.principalName","deep");
    	waitAndTypeByName("field2","");
    	waitForTextPresent("user control not found");
    	waitForElementPresentByXpath("//a[@class='ui-spinner-button ui-spinner-up ui-corner-tr']");
    	waitForElementPresentByXpath("//a[@class='ui-spinner-button ui-spinner-down ui-corner-br']");
    	waitForElementPresentByXpath("//h4/span[contains(text(),'Image Caption Text')]");
    	waitForElementPresentByXpath("//a[@id='actionLink-noImage_attrs']");
    	waitForElementPresentByXpath("//a[@id='actionLink-imageRight_attrs']/img");
    	waitForElementPresentByXpath("//a[@id='actionLink-imageLeft_attrs']/img");
    	waitForElementPresentByXpath("//input[@type='image' and @id='imageAction_attrs']");
    	waitForElementPresentByXpath("//button[@id='buttonImageBottom_attrs']/span/img");
    	waitForElementPresentByXpath("//button[@id='buttonImageLeft_attrs']/img");
    	waitForElementPresentByXpath("//button[@id='buttonImageRight_attrs']/img");
    	waitForElementPresentByXpath("//button[@id='buttonImageTop_attrs']/span/img");
    	waitForElementPresentByXpath("//a[@href='http://www.kuali.org' and contains(text(),'Kuali Website')]");
    	waitForElementPresentByXpath("//iframe[@id='iframe_attrs']");
    }
}
