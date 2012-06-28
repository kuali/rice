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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import com.thoughtworks.selenium.*;

/**
 * tests whether the "Create New Agenda" is working ok 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateNewAgendaIT {

    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @Test
    public void testCreateNewAgenda() throws Exception {
        selenium.open(System.getProperty("remote.public.url"));
        selenium.type("name=__login_user", "admin");
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Create New Agenda");
        selenium.waitForPageToLoad("30000");
        selenium.selectFrame("iframeportlet");
        selenium.select("name=document.newMaintainableObject.dataObject.namespace", "label=Kuali Rules Test");
        selenium.type("name=document.newMaintainableObject.dataObject.agenda.name", "Agenda Name 1");
        selenium.click("id=u244");
        Thread.sleep(2000);
        selenium.selectFrame("relative=up");
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (selenium.isElementPresent("id=fancybox-frame"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        selenium.selectFrame("fancybox-frame");
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (selenium.isElementPresent("id=u80"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        selenium.click("id=u80");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=return value");
        Thread.sleep(2000);
        selenium.waitForPopUp("iframeportlet", "30000");
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (selenium.isElementPresent("id=u260_attribute"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        selenium.select("id=u260_attribute", "label=Campus Agenda");
        Thread.sleep(2000);
        selenium.click("id=u588");
        selenium.selectFrame("relative=up");
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (selenium.isElementPresent("id=fancybox-frame"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        selenium.selectFrame("id=fancybox-frame");
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (selenium.isElementPresent("id=u80"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        selenium.click("id=u80");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=return value");
        Thread.sleep(2000);
        selenium.waitForPopUp("iframeportlet", "30000");
        for (int second = 0;; second++) {
            if (second >= 60)
                Assert.fail("timeout");
            try {
                if (selenium.isElementPresent("id=u135"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        selenium.click("id=u135");
        selenium.click("id=u156");
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        selenium.click("xpath=(//input[@name='imageField'])[2]");
        selenium.waitForPageToLoad("30000");
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
