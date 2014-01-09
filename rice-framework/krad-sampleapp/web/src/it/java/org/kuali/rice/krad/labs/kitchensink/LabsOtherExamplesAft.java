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

public class LabsOtherExamplesAft extends LabsKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&formKey=f158ddb9-f5d4-4af6-8d2b-7a23b8ede984&cacheKey=gk3l8kqyb1jpa64mdoswce&pageId=UifCompView-Page10#UifCompView-Page10";

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
    	fireMouseOverEventByXpath("//input[@name='field1']");
    	assertTextPresent("This tooltip is triggered by focus or and mouse over.");
    	fireMouseOverEventByXpath("//input[@name='field2']");
    	assertTextPresent("This is a tool-tip with different position and tail options");
    	fireMouseOverEventByXpath("//select[@name='field119']");
    	assertTextPresent("This is a Select Control tooltip");
    	fireMouseOverEventByXpath("//select[@name='field120']");
    	assertTextPresent("This is a Multi Select Control tooltip");
    	
    	//Collection with tooltips
    	fireMouseOverEventByXpath("//span[@data-parent='TableCollection1-Hover1_add']");
    	assertTextPresent(" and ");
    }
}
