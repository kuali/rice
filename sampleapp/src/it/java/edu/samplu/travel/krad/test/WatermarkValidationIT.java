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
import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * tests whether the watermarks is work as expected even when they contain an apostrophe
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WatermarkValidationIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

         @Test
        /**
         * if watermarking is ok, the cancel link will bring up a confirmation if something was typed into a textbox i.e
         * the scripts will be working ok
         */
        public void testWatermarking() throws Exception {
    //        selenium.open(System.getProperty("remote.public.url"));
    //		selenium.type("name=__login_user", "quickstart");
    //		selenium.click("css=input[type=\"submit\"]");
    //		selenium.waitForPageToLoad("100000");
    		selenium.click("link=KRAD");
    		selenium.waitForPageToLoad("50000");
    		selenium.click("link=Uif Components (Kitchen Sink)");
    		selenium.waitForPageToLoad("100000");
    		Thread.sleep(2000);
            selenium.selectWindow("title=Kuali :: Uif Components");     
            selenium.focus("name=field106");
            selenium.type("name=field106", "something");
            selenium.focus("name=field110");
            selenium.type("name=field110", "something else");
            assertEquals("something", selenium.getValue("name=field106"));
            selenium.chooseCancelOnNextConfirmation();
            // 'cancel' link
            selenium.click("link=Cancel");
            // Manually tested. Selenium fails to detect confirmation window. Uncomment once its fixed.
            // assertTrue(selenium.getConfirmation().matches("^Form has unsaved data\\. Do you want to leave anyway[\\s\\S]$"));
            fail("selenium.chooseCancelOnNextConfirmation(); is not finding the Dialog see https://jira.kuali.org/browse/KULRICE-7850 "
                    + "selenium.chooseCancelOnNextConfirmation() isn't finding dialog");
                        
           
        }

    public void clearText(String field) throws Exception {
        selenium.focus(field);
        selenium.type(field, "");  
        Thread.sleep(100); 
    }
}
