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
package org.kuali.rice.krad.labs.lookups;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupAutomaticPassingOfKeysAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=search&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelSubAccount&travelAccountNumber=a14&lookupCriteria[travelAccountNumber]=a14
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=search&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelSubAccount&travelAccountNumber=a14&lookupCriteria[travelAccountNumber]=a14";
   
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Automatic passing of keys");
    }

    @Test
    public void testLabsLookupAutomaticPassingOfKeysBookmark() throws Exception {
        testLabsLookupAutomaticPassingOfKeys();
        passed();
    }

    @Test
    public void testLabsLookupAutomaticPassingOfKeysNav() throws Exception {
        testLabsLookupAutomaticPassingOfKeys();
        passed();
    }

    protected void testLabsLookupAutomaticPassingOfKeys()throws Exception {
       waitForElementPresentByXpath("//input[@name='lookupCriteria[travelAccountNumber]' and @value='a14']");
       String results [][]={{"a14","A","Sub Account A"},
    		   {"a14","B","Sub Account B"},
    		   {"a14","C","Sub Account C"},
    		   {"a14","D","Sub Account D"},
    		   {"a14","E","Sub Account E"},
    		   {"a14","F","Sub Account F"},
    		   {"a14","G","Sub Account G"},
    		   {"a14","H","Sub Account H"},
    		   {"a14","I","Sub Account Eye"},
    		   {"a14","J","Sub Account J"},
       };
       assertTextPresent(results);
    }
}
