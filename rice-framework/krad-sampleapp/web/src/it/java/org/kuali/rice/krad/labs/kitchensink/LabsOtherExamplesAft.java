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
public class LabsOtherExamplesAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Other Examples");
	}
	
	@Test
    public void testOtherExamplesBookmark() throws Exception {
		waitAndClickByLinkText("Other Examples");
        testOtherExamples();
        passed();
    }

    @Test
    public void testOtherExamplesNav() throws Exception {
        testOtherExamples();
        passed();
    }
    
    protected void testOtherExamples() throws InterruptedException {
    	//Fields created by Fetching Remotable Fields
    	waitForElementPresentByXpath("//input[@name='remoteFieldValuesMap[remoteField1]' and @value='Apple']");
    	assertElementPresentByXpath("//select[@name='remoteFieldValuesMap[remoteField4]']");
    	
    	//Collection Group Remote Fields
    	assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine']/tbody/tr/td[3]/div/input[@value='Apple']");
    	assertElementPresentByXpath("//button[@id='remotableFieldsCollection_add']");
    	
    	//Various configuration and feature examples
    	assertElementPresentByXpath("//input[@name='field91' and @value='Read only value']");
    	assertElementPresentByXpath("//input[@name='uiTestObject.field3' and @value='Foo-Bear']");
    	
    	//Tooltip examples
    	fireEvent("field1","focus");
        waitForToolTipPresent();
    	fireMouseOverEventByName("field2");
        waitForToolTipPresent();
    	fireMouseOverEventByName("field119");
        waitForToolTipPresent();
    	fireMouseOverEventByName("field120");
        waitForToolTipPresent();
    	
    	//Collection with tooltips
    	fireMouseOverEventByXpath("//div[@id='TableCollection1-Hover1_line1']");
        waitForToolTipPresent();
    }
}
