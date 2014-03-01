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
package edu.sampleu.krad.screenelement;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReadOnlyFieldsDemoAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=Demo-ReadOnlyTestView&methodToCall=start
     */
    public static final String BOOKMARK_URL ="/kr-krad/uicomponents?viewId=Demo-ReadOnlyTestView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("ReadOnly fields Demo");
        switchToWindow("Kuali :: ReadOnly Test");
    }
    
    private void testReadOnlyFieldsDemo() throws Exception{
        waitAndTypeByName("field2","Hi");
        waitAndTypeByName("field3","KualiRice!");
        selectByName("field4","Option 1");
        waitAndClickByName("field117","2");
        waitAndClickByName("field115","3");
        waitAndTypeByName("date1","02/17/2014");
        selectByName("field116","Option 3");
        waitAndClickButtonByText("Make ReadOnly");
        String [] assertText = {"Hi","KualiRice!","Option 1","02/17/2014","Option 3"};
        assertTextPresent(assertText);
    }
    
    @Test
    public void testReadOnlyFieldsDemoBookmark() throws Exception {
        testReadOnlyFieldsDemo();
        passed();
    }

    @Test
    public void testReadOnlyFieldsDemoNav() throws Exception {
        testReadOnlyFieldsDemo();
        passed();
    }
    
}
