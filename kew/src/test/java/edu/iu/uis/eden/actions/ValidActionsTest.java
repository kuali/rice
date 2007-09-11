/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package edu.iu.uis.eden.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.ValidActionsVO;

public class ValidActionsTest extends KEWTestCase {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ValidActionsTest.class);

    public static final String DOCUMENT_TYPE_NAME = "BlanketApproveSequentialTest";

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Ignore
    @Test public void testValidActions() throws Exception {
        WorkflowDocument document = null;
        String networkId = null;
        document = new WorkflowDocument(new NetworkIdVO("user1"), DOCUMENT_TYPE_NAME);
        Long routeHeaderId = document.getRouteHeaderId();

        networkId = "rkirkend";
        document = this.checkActions(networkId, routeHeaderId, 
                new String[]{EdenConstants.ACTION_TAKEN_SU_CANCELED_CD,EdenConstants.ACTION_TAKEN_SU_APPROVED_CD}, 
                new String[]{EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD,EdenConstants.ACTION_TAKEN_ROUTED_CD,EdenConstants.ACTION_TAKEN_SAVED_CD,EdenConstants.ACTION_TAKEN_CANCELED_CD});
        // check for super user action "c", "a"
        // check for blanket approve "B"
        // check for no route "O"
        // check for no save "S"

        networkId = "pmckown";
        document = this.checkActions(networkId, routeHeaderId, 
                new String[]{}, 
                new String[]{EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD,EdenConstants.ACTION_TAKEN_SU_CANCELED_CD,EdenConstants.ACTION_TAKEN_SU_APPROVED_CD,EdenConstants.ACTION_TAKEN_ROUTED_CD,EdenConstants.ACTION_TAKEN_SAVED_CD,EdenConstants.ACTION_TAKEN_CANCELED_CD});
        // check for no super user action "c", "a"
        // check for blanket approve "B"
        // check for no route "O"
        // check for no save "S"

        networkId = "user1";
        document = this.checkActions(networkId, routeHeaderId, 
                new String[]{EdenConstants.ACTION_TAKEN_ROUTED_CD,EdenConstants.ACTION_TAKEN_SAVED_CD,EdenConstants.ACTION_TAKEN_CANCELED_CD}, 
                new String[]{EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD,EdenConstants.ACTION_TAKEN_SU_CANCELED_CD,EdenConstants.ACTION_TAKEN_SU_APPROVED_CD});
        // check for no blanket approve "B"
        // check for no super user actions "c", "a"
        // check for routable "O"
        // check for savable "S"
        document.saveDocument("");

        networkId = "rkirkend";
        document = this.checkActions(networkId, routeHeaderId, 
                new String[]{EdenConstants.ACTION_TAKEN_SU_CANCELED_CD,EdenConstants.ACTION_TAKEN_SU_APPROVED_CD}, 
                new String[]{EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD,EdenConstants.ACTION_TAKEN_ROUTED_CD,EdenConstants.ACTION_TAKEN_SAVED_CD,EdenConstants.ACTION_TAKEN_CANCELED_CD});
        // check for super user action "c", "a"
        // check for blanket approve "B"
        // check for no route "O"
        // check for no save "S"

        networkId = "pmckown";
        document = this.checkActions(networkId, routeHeaderId, 
                new String[]{}, 
                new String[]{EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD,EdenConstants.ACTION_TAKEN_SU_CANCELED_CD,EdenConstants.ACTION_TAKEN_SU_APPROVED_CD,EdenConstants.ACTION_TAKEN_ROUTED_CD,EdenConstants.ACTION_TAKEN_SAVED_CD,EdenConstants.ACTION_TAKEN_CANCELED_CD});
        // check for no super user action "c", "a"
        // check for blanket approve "B"
        // check for no route "O"
        // check for no save "S"

        networkId = "user1";
        document = this.checkActions(networkId, routeHeaderId, 
                new String[]{EdenConstants.ACTION_TAKEN_ROUTED_CD,EdenConstants.ACTION_TAKEN_SAVED_CD,EdenConstants.ACTION_TAKEN_CANCELED_CD}, 
                new String[]{EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD,EdenConstants.ACTION_TAKEN_SU_CANCELED_CD,EdenConstants.ACTION_TAKEN_SU_APPROVED_CD});
        // check for no blanket approve "B"
        // check for no super user actions "c", "a"
        // check for routable "O"
        // check for savable "S"
        document.routeDocument("");

        networkId = "user1";
        document = this.checkActions(networkId, routeHeaderId, 
                new String[]{EdenConstants.ACTION_TAKEN_SAVED_CD}, 
                new String[]{EdenConstants.ACTION_TAKEN_ROUTED_CD,EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD,EdenConstants.ACTION_TAKEN_SU_CANCELED_CD,EdenConstants.ACTION_TAKEN_SU_APPROVED_CD,EdenConstants.ACTION_TAKEN_CANCELED_CD});
        // check for no blanket approve "B"
        // check for no super user actions "c", "a"
        // check for no routable "O"
        // check for no savable "S"

        networkId = "rkirkend";
        document = this.checkActions(networkId, routeHeaderId, 
                new String[]{EdenConstants.ACTION_TAKEN_SAVED_CD,EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD,EdenConstants.ACTION_TAKEN_SU_CANCELED_CD,EdenConstants.ACTION_TAKEN_SU_APPROVED_CD,EdenConstants.ACTION_TAKEN_APPROVED_CD}, 
                new String[]{EdenConstants.ACTION_TAKEN_ROUTED_CD});
        // check for super user action "c", "a"
        // check for blanket approve "B"
        // check for approve "A"
        // check for no route "O"
        // check for no save "S"

        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        document.approve("");

        document = new WorkflowDocument(new NetworkIdVO("pmckown"), document.getRouteHeaderId());
        document.approve("");

        // SHOULD NOW BE ONLY ACKNOWLEDGED

        document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
        // test for Processed Status on document
        document.acknowledge("");
        document = new WorkflowDocument(new NetworkIdVO("temay"), document.getRouteHeaderId());
        document.acknowledge("");
    }

    private WorkflowDocument checkActions(String networkId,Long routeHeaderId,String[] validActionsAllowed,String[] invalidActionsNotAllowed) throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(networkId), routeHeaderId);
        ValidActionsVO validActions = document.getRouteHeader().getValidActions();
        Set validActionsSet = (validActions.getValidActionCodesAllowed() != null) ? new HashSet(Arrays.asList(validActions.getValidActionCodesAllowed())) : new HashSet();

        for (int i = 0; i < validActionsAllowed.length; i++) {
            String actionAllowed = validActionsAllowed[i];
            if (!validActionsSet.contains(actionAllowed)) {
                fail("Action '" + EdenConstants.ACTION_TAKEN_CD.get(actionAllowed) + "' should be allowed for user " + networkId);
            }
        }

        for (int j = 0; j < invalidActionsNotAllowed.length; j++) {
            String actionDisallowed = invalidActionsNotAllowed[j];
            if (validActionsSet.contains(actionDisallowed)) {
                fail("Action '" + EdenConstants.ACTION_TAKEN_CD.get(actionDisallowed) + "' should not be allowed for user " + networkId);
            }
        }

        return document;
    }
}
