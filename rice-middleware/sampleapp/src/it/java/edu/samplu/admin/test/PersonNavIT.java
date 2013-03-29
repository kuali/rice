/**
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

package edu.samplu.admin.test;

import java.util.List;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PersonNavIT extends AdminTmplMthdSTNavBase {

    String docId;
    String personName;

    @Override
    protected String getLinkLocator() {
        return "Person";
    }
    
    @Test
    public void testPerson() throws Exception {
        
        //Create New Person
        super.gotoCreateNew();
        List<String> params;
        params=super.testCreateNewPerson(docId, personName);
        
        //LookUp Person
        selectTopFrame();
        super.gotoMenuLinkLocator();
        params=super.testLookUpPerson(params.get(0), params.get(1));

        //Verify Person
        super.testVerifyPerson(params.get(0), params.get(1));
    }
}