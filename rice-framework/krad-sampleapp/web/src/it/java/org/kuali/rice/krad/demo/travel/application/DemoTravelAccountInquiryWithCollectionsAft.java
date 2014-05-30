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
package org.kuali.rice.krad.demo.travel.application;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountInquiryWithCollectionsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/inquiry?methodToCall=start&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&viewName=original
     */
    public static final String BOOKMARK_URL = "/kr-krad/inquiry?methodToCall=start&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&viewName=original";
    
    /**
     * Search
     */
    public static final String SEARCH = "Search";
    
    /**
     * Clear Values
     */
    public static final String CLEAR_VALUES = "Clear Values";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Travel Account Inquiry with Collections");
    }

    protected void testTravelPerDiemLookUp() throws Exception {
    	String travelAccountInfo []={"Travel Account Number:","a14","Travel Account Name:","Travel Account 14",
    			"Account Type:","CAT - Clearing","Date Created:","Subsidized Percent:","Fiscal Officer:","fran",
                "Fiscal Officer Name:","fran, fran"};
    	assertTextPresent(travelAccountInfo);
    	assertSubAccount("A","Sub Account A");
    	assertSubAccount("B","Sub Account B");
    	assertSubAccount("C","Sub Account C");
    	assertSubAccount("D","Sub Account D");
    	assertSubAccount("E","Sub Account E");
    	assertSubAccount("F","Sub Account F");
    	assertSubAccount("G","Sub Account G");
    	assertSubAccount("H","Sub Account H");
    	assertSubAccount("I","Sub Account Eye");
    	assertSubAccount("J","Sub Account J");
    	assertSubAccount("K","Sub Account K");
    	assertSubAccount("L","Sub Account L");
    	assertSubAccount("M","Sub Account M");
    	assertSubAccount("N","Sub Account N");
    	assertSubAccount("SUB123","Sub Account 123");
    }
    
    private void assertSubAccount(String subAccountNumber,String subAccountName) throws Exception
    {
    	String subAccountInfo[]={"Travel Sub Account Number:",subAccountNumber,"Sub Account Name:",subAccountName};
    	assertTextPresent(subAccountInfo);
    }

    @Test
    public void testTravelPerDiemLookUpBookmark() throws Exception {
        testTravelPerDiemLookUp();
        passed();
    }

    @Test
    public void testTravelPerDiemLookUpNav() throws Exception {
        testTravelPerDiemLookUp();
        passed();
    }
}
