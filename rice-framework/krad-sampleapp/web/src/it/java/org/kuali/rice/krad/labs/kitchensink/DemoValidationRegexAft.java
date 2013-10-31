/*
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DemoValidationRegexAft extends DemoKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&formKey=4e87b0ca-c718-49c2-ac6d-f86e8dbabf6c&cacheKey=ca03hvydzk027i3l2hw0ldkuik&pageId=UifCompView-Page4#UifCompView-Page4";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Validation - Regex");
	}
	
	@Test
    public void testValidationRegexBookmark() throws Exception {
        testValidationRegex();
        passed();
    }

    @Test
    public void testValidationRegexNav() throws Exception {
        testValidationRegex();
        passed();
    }
    
    protected void testValidationRegex() throws InterruptedException 
    {
    	waitAndTypeByName("field50","1qqqqq.qqqqqq");
    	waitAndTypeByName("field51","-1.0E");
    	waitAndTypeByName("field77","1.2");
    	waitAndTypeByName("field52","asddffgghj");
    	waitAndTypeByName("field53"," :_");
    	waitAndTypeByName("field54","as");
    	waitAndTypeByName("field84","kuali.org");
    	waitAndTypeByName("field55","1234");
    	waitAndTypeByName("field75","aws");
    	waitAndTypeByName("field82","12");
    	waitAndTypeByName("field83","24");
    	waitAndTypeByName("field57","1599");
    	waitAndTypeByName("field58","0");
    	waitAndTypeByName("field61","360001");
    	waitAndTypeByName("field62","@#");
    	waitAndTypeByName("field63","2a#");
    	waitAndTypeByName("field64","1@");
    	waitAndTypeByName("field76","a2");
    	waitAndTypeByName("field65","a e");
    	waitAndTypeByName("field66","sdfa");
    	waitAndTypeByName("field67","1234-a");
    	waitAndTypeByName("field68","4.a");
    	assertElementPresentByXpath("//input[@name='field50' and @class='uif-textControl validChar-field500 dirty error']");
    	assertElementPresentByXpath("//input[@name='field51' and @class='uif-textControl validChar-field510 dirty error']");
    	assertElementPresentByXpath("//input[@name='field77' and @class='uif-textControl validChar-field770 dirty error']");
    	assertElementPresentByXpath("//input[@name='field52' and @class='uif-textControl validChar-field520 dirty error']");
    	assertElementPresentByXpath("//input[@name='field53' and @class='uif-textControl validChar-field530 dirty error']");
    	assertElementPresentByXpath("//input[@name='field54' and @class='uif-textControl validChar-field540 dirty error']");
    	assertElementPresentByXpath("//input[@name='field84' and @class='uif-textControl validChar-field840 dirty error']");
    	assertElementPresentByXpath("//input[@name='field55' and @class='uif-textControl validChar-field550 dirty error']");
    	assertElementPresentByXpath("//input[@name='field75' and @class='uif-textControl validChar-field750 dirty error']");
    	assertElementPresentByXpath("//input[@name='field82' and @class='uif-textControl validChar-field820 dirty error']");
    	assertElementPresentByXpath("//input[@name='field83' and @class='uif-textControl validChar-field830 dirty error']");
    	assertElementPresentByXpath("//input[@name='field57' and @class='uif-textControl validChar-field570 dirty error']");
    	assertElementPresentByXpath("//input[@name='field58' and @class='uif-textControl validChar-field580 dirty error']");
    	assertElementPresentByXpath("//input[@name='field61' and @class='uif-textControl validChar-field610 dirty error']");
    	assertElementPresentByXpath("//input[@name='field62' and @class='uif-textControl validChar-field620 dirty error']");
    	assertElementPresentByXpath("//input[@name='field63' and @class='uif-textControl validChar-field630 dirty error']");
    	assertElementPresentByXpath("//input[@name='field64' and @class='uif-textControl validChar-field640 dirty error']");
    	assertElementPresentByXpath("//input[@name='field76' and @class='uif-textControl validChar-field760 dirty error']");
    	assertElementPresentByXpath("//input[@name='field65' and @class='uif-textControl validChar-field650 dirty error']");
    	assertElementPresentByXpath("//input[@name='field66' and @class='uif-textControl validChar-field660 dirty error']");
    	assertElementPresentByXpath("//input[@name='field67' and @class='uif-textControl validChar-field670 dirty error']");
    	waitAndTypeByName("field67","");
    	assertElementPresentByXpath("//input[@name='field68' and @class='uif-textControl validChar-field680 dirty error']");
    }
}
