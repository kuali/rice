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

import org.junit.Ignore;
import org.junit.Test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * TODO vchauhan don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowCreateNewWDIT extends WebDriverLegacyITBase {
  
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=People%20Flow&channelUrl="
            + ITUtil.getBaseUrlString() + ITUtil.KRAD_LOOKUP_METHOD
            + "org.kuali.rice.kew.impl.peopleflow.PeopleFlowBo"
            + "&returnLocation=" + ITUtil.PORTAL_URL + ITUtil.SHOW_MAINTENANCE_LINKS;
   
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
    
    
    @Test
    public void testPeopleFlow() throws Exception {
      super.testPeopleFlow();
    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MainMenuLookupITBase#lookupAssertions()
     */
  
    public void lookupAssertions() {
        // nothing

    }

    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuITBase#getLinkLocator()
     */

    protected String getLinkLocator() {
        return "People Flow";
    }

    
    @Ignore // No Need to     
    @Test
 
    public void testLookUp() throws Exception {}
}
