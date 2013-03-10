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
package edu.samplu.travel.krad.test;

import static org.junit.Assert.*;

import org.junit.Test;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * Test verifies updates in Totals at client side.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionTotallingWDIT extends WebDriverLegacyITBase {
    
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.UpgradedSeleniumITBase#getTestUrl()
     */
    @Override
    public String getTestUrl() {
        //Returns "Group Totalling" url
        return "/kr-krad/uicomponents?viewId=Demo-CollectionTotaling&methodToCall=start";
    }

    @Test
    public void testCollectionTotalling() throws Exception {
        
        super.testCollectionTotalling();
    }  

}
