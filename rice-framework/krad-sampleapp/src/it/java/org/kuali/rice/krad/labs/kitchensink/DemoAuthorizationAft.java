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

public class DemoAuthorizationAft extends DemoKitchenSinkBase {

    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&formKey=a9ce648c-c2e6-408f-ab0c-7628f99ee6a0&cacheKey=3pg1hrr9uoq94kw8jub6j&pageId=UifCompView-Page9#UifCompView-Page9";
    private static final String GROUP_EDITING_ASSERT_MSG="Editing of Group can be done in restricted access !";
    private static final String SECURE_FIELDS_ASSERT_MSG="Secure Fields present in restricted access !";
    private static final String SECURE_ACTIONS_ASSERT_MSG="Editing of Group can be done in restricted access !";
    private static final String SECURE_WIDGET_ASSERT_MSG="Secure Widget field is present restricted access !";
    private static final String SECURE_LINE_VIEW_ASSERT_MSG="Line View Fields is present restricted access !";
    private static final String SECURE_LINE_EDIT_ASSERT_MSG="Line Editing Fields is present restricted access !";
    private static final String SECURE_LINE_FIELDS_ASSERT_MSG="Line Fields is present restricted access !";
    private static final String SECURE_LINE_ACTION_ASSERT_MSG="Line Action Button is present restricted access !";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Authorization");
	}
	
	@Test
    public void testAuthorizationBookmark() throws Exception {
        testAuthorization();
        passed();
    }

    @Test
    public void testAuthorizationNav() throws Exception {
        testAuthorization();
        passed();
    }
    
    protected void testAuthorization() throws InterruptedException 
    {
    	//Group Editing
    	assertElementNotPresentByXpath("//div[@data-parent='UifCompView-SecureGroupEdit']/input",GROUP_EDITING_ASSERT_MSG);
    	
    	//Secure Fields
    	assertElementNotPresentByXpath("//div[@data-parent='UifCompView-SecureFields' and @data-label='Field 3']/input",SECURE_FIELDS_ASSERT_MSG);
    	
    	//Secure Actions
    	assertElementNotPresentByXpath("//div[@class='uif-verticalBoxLayout clearfix']/button[contains(text(),'Save')]", SECURE_ACTIONS_ASSERT_MSG);
    	
    	//Secure Widget
    	assertElementNotPresentByXpath("//div[@class='UifCompView-SecureWidgets']/input",SECURE_WIDGET_ASSERT_MSG);
   
    	//Secure Line View
    	assertElementNotPresentByXpath("//div[@class='UifCompView-Page9']/table/tbody/tr[2]",SECURE_LINE_VIEW_ASSERT_MSG);
    
    	//Secure Line Edit
    	assertElementNotPresentByXpath("//div[@id='UifCompView-SecureLineEdit']/table/tbody/tr[2]/td[2]/input",SECURE_LINE_EDIT_ASSERT_MSG);
    
    	//Secure Line Fields
    	assertElementNotPresentByXpath("//div[@id='UifCompView-SecureLineFields']/table/tbody/tr[2]/td[3]/input",SECURE_LINE_FIELDS_ASSERT_MSG);
    	
    	//Secure Line Fields
    	assertElementNotPresentByXpath("//div[@id='UifCompView-SecureLineActions']/table/tbody/tr[2]/td[7]/div/fieldset/button",SECURE_LINE_ACTION_ASSERT_MSG);
    }
    
    private void assertElementNotPresentByXpath(String locator, String message)
    {
    	if(isElementPresentByXpath(locator))
    	{
    		fail(message);
    	}
    }
}
