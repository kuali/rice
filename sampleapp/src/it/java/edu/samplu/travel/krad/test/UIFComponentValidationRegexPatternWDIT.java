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
package edu.samplu.travel.krad.test;

import edu.samplu.common.WebDriverLegacyITBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * tests that regex validation works as expected on input fields where it is configured
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UIFComponentValidationRegexPatternWDIT extends WebDriverLegacyITBase {
    
    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=UifCompView_KNS&methodToCall=start&readOnlyFields=field91";
    }

    @Test
    public void testValidCharacterConstraint() throws Exception {

        /*
         *  Timestamp pattern validation message says it allows years from 1900 - 2099 
         *  In fact it also allows 2999 as the upper limit. This needs to be sorted out.
         *  Test failing this condition is commented in the below code section for Timestamp Validation. Once resolved can be uncommented  
         *  
         */
        super.testValidCharacterConstraint();
    }
}
