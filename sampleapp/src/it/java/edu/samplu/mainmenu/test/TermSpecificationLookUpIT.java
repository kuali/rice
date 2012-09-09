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

import edu.samplu.common.ITUtil;
import edu.samplu.common.MainMenuLookupITBase;

/**
 * tests that user 'admin' can display the Term Specification lookup screen, search,
 * initiate an Term Specification maintenance document via an edit action on the search results and
 * finally cancel the maintenance document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TermSpecificationLookUpIT extends MainMenuLookupITBase {
    @Override
    public String getLinkLocator() {
        return "link=Term Specification Lookup";
    }
    
    @Override
    public void lookupAssertions() {
        ITUtil.assertTrue(selenium.isTextPresent("Context"), "Text Context not present");
    }
}
