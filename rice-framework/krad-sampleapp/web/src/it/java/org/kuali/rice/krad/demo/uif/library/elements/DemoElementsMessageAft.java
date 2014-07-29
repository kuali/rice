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
package org.kuali.rice.krad.demo.uif.library.elements;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoElementsMessageAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-MessageView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-MessageView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Message");
    }

    protected void testLibraryElementsMessageDefault() throws Exception {
        assertElementPresentByXpath("//section[@id='Demo-Message-Example1']/p[@class='uif-message uif-boxLayoutVerticalItem clearfix']");
    }
    
    protected void testLibraryElementsMessageWithExpression() throws Exception {
    	selectByName("exampleShown","Message with Expression");
    	assertElementPresentByXpath("//section[@id='Demo-Message-Example2']/p[@class='uif-message uif-boxLayoutVerticalItem clearfix']/b");
    }
    
    protected void testLibraryElementsMessageWithCustomCss() throws Exception {
    	selectByName("exampleShown","Message with Custom CSS");
    	assertElementPresentByXpath("//section[@id='Demo-Message-Example3']/p[@class='uif-message uif-boxLayoutVerticalItem clearfix badge']");
    }
    
    private void testLibraryElementMessageAll() throws Exception{
    	testLibraryElementsMessageDefault();
    	testLibraryElementsMessageWithExpression();
    	testLibraryElementsMessageWithCustomCss();
    }
    
    @Test
    public void testLibraryElementsMessageBookmark() throws Exception {
    	testLibraryElementMessageAll();
        passed();
    }

    @Test
    public void testLibraryElementsMessageNav() throws Exception {
    	testLibraryElementMessageAll();
        passed();
    }  
}
