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
package edu.sampleu.admin;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class AdminTmplMthdAftNavBlanketAppBase extends AdminTmplMthdAftNavCreateNewBase {

    @Override
    protected String getMenuLinkLocator() {
        return AdminTmplMthdAftNavBase.ADMIN_LOCATOR;
    }

    @Override
    protected String getCreateNewLinkLocator() {
        return AdminTmplMthdAftNavBase.CREATE_NEW_LOCATOR;
    }

    protected String testBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = verifyDocInitiated();
        assertBlanketApproveButtonsPresent();
        createNewLookupDetails();
        blanketApproveTest(docId);
        return docId;
    }

    @Test
    public void testBlanketAppBookmark() throws Exception {
        testBlanketApprove();
        passed();
    }

    @Test
    public void testBlanketAppNav() throws Exception {
        testBlanketApprove();
        passed();
    }
}