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

import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;


/**
 * tests whether the Attribute Definition Look UP is working ok 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeDefinitionLookUpIT extends UpgradedSeleniumITBase{
    @Override
    public String getTestUrl() {
        return PORTAL;
    }
    
    @Test
    public void testAttributeDefinitionLookUp() throws Exception {
        selenium.click("link=Attribute Definition Lookup");
        selenium.waitForPageToLoad("30000");
        selenium.selectFrame("iframeportlet");
        selenium.click("css=button:contains(earch)");
        Thread.sleep(3000);
        selenium.waitForPageToLoad("30000");
        selenium.isTextPresent("Actions"); // there are no actions, but the header is the only unique text from searching
        selenium.click("link=1000");
        selenium.waitForPageToLoad("30000");

        selenium.isTextPresent("Attribute Inquiry");
        selenium.isTextPresent("KRMS Attributes");
        selenium.isTextPresent("Attribute Label");
        selenium.isTextPresent("1000");
        selenium.isTextPresent("peopleFlowId");
        selenium.isTextPresent("KR-RULE");
        selenium.isTextPresent("PeopleFlow");
//        selenium.selectFrame("name=fancybox-frame1343151577256"); // TODO parse source to get name
//        selenium.click("css=button:contains(Close)"); // looks lower case, but is upper
//        Thread.sleep(500);
//        selenium.click("css=button:contains(cancel)");

// AttributeDefinition's don't have actions (yet)
//        selenium.click("id=u80");
//        selenium.waitForPageToLoad("30000");
//        selenium.click("id=u86");
//        selenium.waitForPageToLoad("30000");
//        selenium.selectWindow("null");
//        selenium.click("xpath=(//input[@name='imageField'])[2]");
//        selenium.waitForPageToLoad("30000");
    }
}
