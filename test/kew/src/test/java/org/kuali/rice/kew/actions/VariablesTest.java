/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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

package org.kuali.rice.kew.actions;

import org.junit.Test;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;

import org.kuali.rice.kew.engine.node.Branch;
import org.kuali.rice.kew.engine.node.BranchState;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;

import static org.junit.Assert.*;

/**
 * Test case that tests setting and getting variables, both programmatically
 * and via the "SetVar" node; stolen directly from ApproveActionTest.testPreapprovals
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class VariablesTest extends KEWTestCase {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VariablesTest.class);

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    private void dumpInfoAboutDoc(WorkflowDocument doc) throws WorkflowException {
        LOG.info("\tDoc: class=" + doc.getDocumentType() + " title=" + doc.getTitle() + " status=" + doc.getStatusDisplayValue());
        LOG.info("\tActionRequests:");
        for (ActionRequestDTO ar: doc.getActionRequests()) {
            LOG.info("\t\tId: " + ar.getActionRequestId() + " PrincipalId: " + ar.getPrincipalId() + " ActionRequested: " + ar.getActionRequested() + " ActionTaken: " + (ar.getActionTaken() != null ? ar.getActionTaken().getActionTaken() : null) + " NodeName: " + ar.getNodeName() + " Status:" + ar.getStatus());
        }
        LOG.info("\tActionTakens:");
        for (ActionTakenDTO at: doc.getActionsTaken()) {
            LOG.info("\t\tId: " + at.getActionTakenId() + " PrincipalId: " + at.getPrincipalId() + " ActionTaken: " + at.getActionTaken());
        }
        LOG.info("\tNodeNames:");
        for (String name: doc.getNodeNames()) {
            LOG.info("\t\t" + name);
        }
    }

    public void dumpBranch(Branch b) {
        LOG.info("Branch: " + b.getBranchId() + " " + b.getName());
        for (BranchState bs: b.getBranchState()) {
            LOG.info(bs.getBranchStateId() + " " + bs.getKey() + " " + bs.getValue());
        }
    }

    @Test public void testVariables() throws Exception {
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), "VariablesTest");
        doc.routeDocument("");

        //rock some preapprovals and other actions...
        doc = new WorkflowDocument(getPrincipalIdForName("ewestfal"), doc.getRouteHeaderId());
        dumpInfoAboutDoc(doc);
        doc.setVariable("myexcellentvariable", "righton");
        doc.approve("");
        assertEquals("startedVariableValue", doc.getVariable("started"));
        assertEquals("startedVariableValue", doc.getVariable("copiedVar"));

        doc = new WorkflowDocument(getPrincipalIdForName("user2"), doc.getRouteHeaderId());
        assertEquals("righton", doc.getVariable("myexcellentvariable"));
        doc.setVariable("vartwo", "two");
        doc.setVariable("myexcellentvariable", "ichangedit");
        doc.acknowledge("");

        doc = new WorkflowDocument(getPrincipalIdForName("user3"), doc.getRouteHeaderId());
        assertEquals("ichangedit", doc.getVariable("myexcellentvariable"));
        assertEquals("two", doc.getVariable("vartwo"));
        doc.setVariable("another", "another");
        doc.setVariable("vartwo", null);
        doc.complete("");

        //approve as the person the doc is routed to so we can move the documen on and hopefully to final
        doc = new WorkflowDocument(getPrincipalIdForName("user1"), doc.getRouteHeaderId());
        assertEquals("ichangedit", doc.getVariable("myexcellentvariable"));
        assertEquals(null, doc.getVariable("vartwo"));
        assertEquals("another", doc.getVariable("another"));
        doc.approve("");

        assertEquals("endedVariableValue", doc.getVariable("ended"));
        assertNotNull(doc.getVariable("google"));
        LOG.info(doc.getVariable("google"));
        assertEquals("documentContentendedVariableValue", doc.getVariable("xpath"));
        LOG.info(doc.getVariable("xpath"));

        assertEquals("aNewStartedVariableValue", doc.getVariable("started"));

        assertTrue("the document should be final", doc.stateIsFinal());
    }
}
