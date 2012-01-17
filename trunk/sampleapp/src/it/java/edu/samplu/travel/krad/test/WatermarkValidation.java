/*
 * Copyright 2006-2011 The Kuali Foundation
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *tests whether the watermarking is working ok
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WatermarkValidation {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");//System.getProperty("remote.public.url")
        selenium.start();
    }

    @Test
    /**
     * if watermarking is ok, the cancel link will bring up a confirmation if something was typed into a textbox i.e
     * the scripts will be working ok
     */
    public void testWatermarking() throws Exception {
        selenium.open("/kr-dev/portal.do");
		selenium.type("name=__login_user", "quickstart");
		selenium.click("css=input[type=\"submit\"]");
		selenium.waitForPageToLoad("100000");
		selenium.click("link=KRAD");
		selenium.waitForPageToLoad("50000");
		selenium.click("link=Uif Components (Kitchen Sink)");
		selenium.waitForPageToLoad("100000");
        selenium.selectFrame("iframeportlet");
		//selenium.selectWindow("name=iframeportlet");
        selenium.focus("id=257");
		selenium.type("id=257", "something");
        selenium.focus("id=371");
        selenium.type("id=371", "something else");
        assertEquals("something", selenium.getValue("xpath=//*[@id=\"257\"]"));
		selenium.chooseCancelOnNextConfirmation();
		selenium.click("id=10");
        //selenium.
        //Thread.sleep(1000);
		assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
    
    public void clearText(String field) throws Exception {
        selenium.focus(field);
        selenium.type(field, "");  
        Thread.sleep(100); 
    }
}
