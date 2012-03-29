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
package edu.samplu.travel.krad.test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.uif.UifConstants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *  Tests that the data attributes are rendered as expected for all controls and widgets
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifDataAttributesIT {
    private Selenium selenium;
    private  Log log = LogFactory.getLog(getClass());

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @Test
    /**
     * Tests that the data attributes are rendered as expected for all controls
     */
    public void testDataAttributesPresent() throws Exception {

        selenium.open(System.getProperty("remote.public.url"));
        assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("50000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.open(
                "/kr-dev/kr-krad/data-attributes-test-uif-controller?viewId=dataAttributesView_selenium&methodToCall=start");
        selenium.waitForPageToLoad("50000");

        //create a map that will specify in which html tag to look for simple attributes
        Map<String, String[]> tagAndElements = new HashMap<String, String[]>();
        // elements whose simple attributes are set in the wrapping div
        String[] divWrappedElements = {"textInputField", "textAreaInputField", "datePicker", "checkBox", "radioButton", "fileUpload"};
        // elements whose simple attributes are set in an anchor tag
        String[] anchorElements = {"navigationLink", "actionLink-noImage", "actionLink-imageRight", "actionLink-imageLeft"};
        String[] imgElements = {"imageField"};
        String[] buttonElements = {"buttonTextOnly", "buttonImageBottom", "buttonImageLeft", "buttonImageTop", "buttonImageRight"};
        String[] inputElements = {"imageAction"};
        
        tagAndElements.put("div", divWrappedElements);
        tagAndElements.put("a", anchorElements);
        tagAndElements.put("img", imgElements);
        tagAndElements.put("button", buttonElements);
        tagAndElements.put("input", inputElements);

        for (String tag: tagAndElements.keySet()) {
            String[] elementIds = tagAndElements.get(tag);
            for (int i=0; i<elementIds.length; i++) {
                String tagId = elementIds[i];
                // String controlId = beanIds[i] + UifConstants.IdSuffixes.CONTROL;
                // check for complex attributes
                String complexAttributesXpath="//input[(@type='hidden') and (@data-for='"+ tagId + "')]";
                assertTrue(tagId + ": complex data attributes script not found", selenium.isElementPresent(complexAttributesXpath));

                String scriptValue = selenium.getAttribute(complexAttributesXpath + "@value");
                assertNotNull("script value is null",scriptValue);
                // log.info("scriptValue for " + divId + " is " + scriptValue);
                assertTrue(tagId + ": script does not contain expected code",
                        scriptValue.contains("jQuery('#" + tagId + "').data('capitals', {kenya:'nairobi', uganda:'kampala', tanzania:'dar'});"
                                + "jQuery('#" + tagId + "').data('intervals', {short:2, medium:5, long:13});"));

                // check for simple attributes
                String simpleAttributesXpath="//" + tag + "[(@id='" + tagId + "') and (@data-iconTemplateName='cool-icon-%s.png') and (@data-transitions='3')]";
                assertTrue(tagId + " does not have simple data attributes present", selenium.isElementPresent(simpleAttributesXpath));
            }
        }
        // testing for https://groups.google.com/a/kuali.org/group/rice.usergroup.krad/browse_thread/thread/1e501d07c1141aad#
        String styleValue = selenium.getAttribute("//span[@id='textInputField_label_span']@style");
        // log.info("styleValue is " + styleValue);
        assertTrue("textInputField label does not contain expected style", styleValue.replace(" ", "").contains("color:red"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
    

}
