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
package edu.sampleu.admin.config.namespace.pending.approvals.workgroup;

import edu.sampleu.admin.config.namespace.pending.PendingBase;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FyiPendingApprovalsAft extends PendingBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Namespace&channelUrl="+WebDriverUtils.getBaseUrlString()+ITUtil..KNS_LOOKUP_METHOD
     * +"org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="
     * +ITUtil.PORTAL_URL+ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Namespace&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD
            + "org.kuali.rice.coreservice.impl.namespace.NamespaceBo" + "&docFormKey=88888888&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void testFYIPendingApprovals() throws Exception {
        fillInNamespaceOverview("Test Namespace 4", "SUACTION4", "SUACTION4", "KUALI");

        String[][] groupsActions = new String[][] {{"group1", "ACKNOWLEDGE"}, {"TestGroup2"}};
        fillInAdHocGroups(groupsActions);

        String docId = submitAndLookupDoc();
        assertSuperGroup(docId);
    }

    @Test
    public void testFYIPendingApprovalsBookmark() throws Exception {
        testFYIPendingApprovals();
        passed();
    }

    @Test
    public void testFYIPendingApprovalsNav() throws Exception {
        testFYIPendingApprovals();
        passed();
    }
}
