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

import edu.samplu.common.ITUtil;
import org.junit.Test;

/**
 * tests the Component section in Rice.
 * @deprecated
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentNavIT extends AdminTmplMthdSTNavBase {
    String docId;
    String componentName;
    String componentCode;

    @Override
    protected String getLinkLocator() {
        return "Component";
    }

    @Test
    public void testComponentParameter() throws Exception {
        gotoCreateNew();
        componentName = "TestName" + ITUtil.DTS_TWO;
        componentCode = "TestCode" + ITUtil.DTS_TWO;

        docId = testCreateNewComponent(componentName, componentCode);

        //Lookup
        gotoMenuLinkLocator();
        selectFrameIframePortlet();
        testLookUpComponent(docId, componentName, componentCode);

        testEditComponent(docId, componentName, componentCode);
        
        //Verify if its edited
        gotoMenuLinkLocator();
        testVerifyEditedComponent(docId, componentName, componentCode);

        testCopyComponent(docId, componentName + "copy", componentCode + "copy");
        
        gotoMenuLinkLocator();
        testVerifyCopyComponent(docId, componentName + "copy", componentCode + "copy");
    }
}
