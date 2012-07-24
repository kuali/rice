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
        selenium.click("id=u80");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=u86");
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        selenium.click("xpath=(//input[@name='imageField'])[2]");
        selenium.waitForPageToLoad("30000");
        
    }
}
