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
package edu.samplu.admin.test;

import java.util.List;

import org.junit.Test;

/**
 * tests the Component section in Rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentNavIT extends AdminMenuNavITBase {
    String docId;
    String componentName;
    String componentCode;

    @Override
    protected String getLinkLocator() {
        return "Component";
    }

    @Test
    public void testComponentParameter() throws Exception {

        //Create New
        super.gotoCreateNew();
        List<String> params;
        params=super.testCreateNewComponent(docId, componentName,componentCode);
    
        //Lookup
        super.gotoMenuLinkLocator();
        selectFrameIframePortlet();
        params=super.testLookUpComponent(params.get(0), params.get(1),params.get(2));

        //edit
        params=super.testEditComponent(params.get(0), params.get(1),params.get(2));
        
        //Verify if its edited
        super.gotoMenuLinkLocator();
        params=super.testVerifyEditedComponent(params.get(0), params.get(1),params.get(2));

        //copy
        params=super.testCopyComponent(params.get(0), params.get(1),params.get(2));
        
        //Verify if its copied
        super.gotoMenuLinkLocator();
        super.testVerifyCopyComponent(params.get(0), params.get(1), params.get(2));
    }
}
