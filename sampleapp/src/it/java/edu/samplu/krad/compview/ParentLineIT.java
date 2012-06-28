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

package edu.samplu.krad.compview;

import com.thoughtworks.selenium.DefaultSelenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

/**
 * tests that the parent line variable is available in a sub collection
 *
 * <p>configuration done in /edu/sampleu/demo/kitchensink/UifComponentsViewP7.xml on bean id="subCollection1"</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ParentLineIT {
    private DefaultSelenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*chrome",  System.getProperty("remote.public.url"));//"http://localhost:8080/"
        selenium.start();
    }

    @Test
    /**
     * tests that the size of a sub collection is correctly displayed using the parentLine el variable
     */
    public void testSubCollectionSize() throws Exception {
        selenium.open("/kr-dev/portal.do");
        selenium.type("name=__login_user", "admin");
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=KRAD");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Uif Components (Kitchen Sink)");
        selenium.waitForPageToLoad("30000");
        selenium.selectFrame("iframeportlet");
        // click on collections page link
        selenium.click("id=u961");
        // Thread.sleep(30000);
        // wait for collections page to load by checking the presence of a sub collection line item
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("id=u1089_line0_line0_control")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        // verify that sub collection sizes are displayed as expected
        assertEquals("SubCollection - (3 lines)", selenium.getText("id=u1030_line0"));
        assertEquals("SubCollection - (2 lines)", selenium.getText("id=u1030_line1"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
