/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.mainmenu.test;

import org.junit.Test;
import org.openqa.selenium.By;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;


/**
 * tests whether the Category Look UP is working ok 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CategoryLookUpWDIT extends WebDriverLegacyITBase{
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Category%20Lookup&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.CategoryBo&showMaintenanceLinks=true&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
    
    @Override
    public String getTestUrl() {
        return TEST_URL;
        
    }
    
    @Test
    public void testCategoryLookUp() throws Exception {
       super.testCategoryLookUp();
    }
}
