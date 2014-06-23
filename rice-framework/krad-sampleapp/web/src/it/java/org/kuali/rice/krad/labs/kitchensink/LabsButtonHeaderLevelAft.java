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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsButtonHeaderLevelAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page8#UifCompView-Page8
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page8#UifCompView-Page8";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Button/Header Levels");
	}
	
	@Test
    public void testButtonHeaderLevelBookmark() throws Exception {
        testButtonHeaderLevel();
        passed();
    }

    @Test
    public void testButtonHeaderLevelNav() throws Exception {
        testButtonHeaderLevel();
        passed();
    }
    
    protected void testButtonHeaderLevel() throws InterruptedException 
    {
    	//Buttons
    	waitAndClickByXpath("//button[@id='Demo-Primary-Action1']");
    	alertAccept();
    	waitAndClickByXpath("//button[@id='Demo-Primary-Action2']");
    	waitAndClickByXpath("//button[@id='Demo-Secondary-Action1']");
    	alertAccept();
    	waitAndClickByXpath("//button[@id='Demo-Secondary-Action2']");
    	assertElementPresentByXpath("//a[@id='Demo-Links-Action1']");
    	waitAndClickByXpath("//button[@id='Demo-Primary-Action3']");
    	alertAccept();
    	waitAndClickByXpath("//button[@id='Demo-Primary-Action4']");
    
    	//Header
    	assertElementPresentByXpath("//h3/span[contains(text(),'H3 Header Title')]");
    	assertElementPresentByXpath("//h4/span[contains(text(),'H4 Header Title')]");
    	assertElementPresentByXpath("//h5/span[contains(text(),'H5 Header Title')]");
    	assertElementPresentByXpath("//h6/span[contains(text(),'H6 Header Title')]");
    	assertElementPresentByXpath("//h3/a/span[contains(text(),'H3 Header Title')]");
    	assertElementPresentByXpath("//h4/a/span[contains(text(),'H4 Header Title')]");
    	assertElementPresentByXpath("//h5/a/span[contains(text(),'H5 Header Title')]");
    	assertElementPresentByXpath("//h6/a/span[contains(text(),'H6 Header Title')]");
    }
}
