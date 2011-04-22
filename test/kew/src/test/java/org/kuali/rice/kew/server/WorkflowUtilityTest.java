/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.server;


import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.core.api.parameter.Parameter;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.TestXMLSearchableAttributeDateTime;
import org.kuali.rice.kew.docsearch.TestXMLSearchableAttributeFloat;
import org.kuali.rice.kew.docsearch.TestXMLSearchableAttributeLong;
import org.kuali.rice.kew.docsearch.TestXMLSearchableAttributeString;
import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultRowDTO;

import org.kuali.rice.kew.dto.ReportActionToTakeDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.dto.RuleDTO;
import org.kuali.rice.kew.dto.RuleExtensionDTO;
import org.kuali.rice.kew.dto.RuleReportCriteriaDTO;
import org.kuali.rice.kew.dto.RuleResponsibilityDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowUtility;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.util.KNSConstants;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class WorkflowUtilityTest extends KEWTestCase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowUtilityTest.class);

    private WorkflowUtility workflowUtility;

    @Override
	public void setUp() throws Exception {
		super.setUp();
		setWorkflowUtility(KEWServiceLocator.getWorkflowUtilityService());
	}

	protected void loadTestData() throws Exception {
        loadXmlFile("WorkflowUtilityConfig.xml");
    }

	public WorkflowUtility getWorkflowUtility() {
		return this.workflowUtility;
	}

	public void setWorkflowUtility(WorkflowUtility workflowUtility) {
		this.workflowUtility = workflowUtility;
	}

	@Test
    public void testFindByAppId() throws WorkflowException{
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.setAppDocId("123456789");
        document.routeDocument("");
    	DocumentDetailDTO doc=getWorkflowUtility().getDocumentDetailFromAppId(SeqSetup.DOCUMENT_TYPE_NAME, "123456789");
    	
    	assertNotNull(doc);
    	
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.setAppDocId("123456789");
        document.routeDocument("");

        try{
        	getWorkflowUtility().getDocumentDetailFromAppId(SeqSetup.DOCUMENT_TYPE_NAME, "123456789");
        	assertTrue(false);
        }catch(WorkflowException e){
        	assertTrue(true);
        }
        
        try{
        	getWorkflowUtility().getDocumentDetailFromAppId("notExist", "wrong");
        	assertTrue(false);
        }catch(WorkflowException e){
        	assertTrue(true);
        }
        
        try{
        	getWorkflowUtility().getDocumentDetailFromAppId("notExist", null);
        	assertTrue(false);
        }catch(RuntimeException e){
        	assertTrue(true);
        }
    	
        try{
        	getWorkflowUtility().getDocumentDetailFromAppId(null, null);
        	assertTrue(false);
        }catch(RuntimeException e){
        	assertTrue(true);
        }
        
    }

    @Test public void testGetActionsRequested() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        assertActionsRequested("ewestfal", document.getRouteHeaderId());
        assertActionsRequested("bmcgough", document.getRouteHeaderId());
        assertActionsRequested("rkirkend", document.getRouteHeaderId());
    }

    protected void assertActionsRequested(String principalName, Long documentId) throws Exception {
    	AttributeSet attrSet = getWorkflowUtility().getActionsRequested(getPrincipalIdForName(principalName), documentId);
    	assertNotNull("Actions requested should be populated", attrSet);
    	assertFalse("Actions requested should be populated with at least one entry", attrSet.isEmpty());
    	assertEquals("Wrong number of actions requested", 4, attrSet.size());
    }

    @Test public void testIsUserInRouteLog() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), false));
        assertFalse("User should not be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), false));
        assertFalse("User should not be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("temay"), false));
        assertFalse("User should not be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("temay"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), true));

        // test that we can run isUserInRouteLog on a SAVED document
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.saveDocument("");
        assertTrue(document.stateIsSaved());
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), false));
        assertFalse("User should not be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), false));
        assertFalse("User should not be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), false));
        assertFalse("User should not be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), false));
        assertFalse("User should not be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("temay"), false));
        assertFalse("User should not be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), false));

        // now look all up in the future of this saved document
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("temay"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), true));
    }

    @Test public void testIsUserInRouteLogAfterReturnToPrevious() throws Exception {
	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());

        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());

        // bmcgough and rkirkend should be in route log
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertFalse("User should NOT be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), false));
        // Phil of the future
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), true));
        TestUtilities.assertAtNode(document, "WorkflowDocument");

        document.returnToPreviousNode("", "AdHoc");
        TestUtilities.assertAtNode(document, "AdHoc");
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());

        document.approve("");

        // we should be back where we were
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        TestUtilities.assertAtNode(document, "WorkflowDocument");

        // now verify that is route log authenticated still works
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertFalse("User should NOT be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), true));

        // let's look at the revoked node instances

        List revokedNodeInstances = KEWServiceLocator.getRouteNodeService().getRevokedNodeInstances(KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId()));
        assertNotNull(revokedNodeInstances);
        assertEquals(2, revokedNodeInstances.size());

        // let's approve past this node and another
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        document.approve("");
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        // should be at WorkflowDocument2
        document = new WorkflowDocument(getPrincipalIdForName("pmckown"), document.getRouteHeaderId());
        TestUtilities.assertAtNode(document, "WorkflowDocument2");
        assertTrue(document.isApprovalRequested());
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), true));

        // now return back to WorkflowDocument
        document.returnToPreviousNode("", "WorkflowDocument");
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        // Phil should no longer be non-future route log authenticated
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertFalse("User should NOT be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), false));
        assertTrue("User should be authenticated.", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), true));
    }
    
    @Test
    public void testIsUserInRouteLogWithSplits() throws Exception {
    	loadXmlFile("WorkflowUtilitySplitConfig.xml");
    	
    	// initialize the split node to both branches
    	TestSplitNode.setLeftBranch(true);
    	TestSplitNode.setRightBranch(true);
    	
    	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("admin"), "UserInRouteLog_Split");
        document.routeDocument("");
        
        // document should be in ewestfal action list
        document = TestUtilities.switchByPrincipalName("ewestfal", document);
        assertTrue("should have approve", document.isApprovalRequested());
        TestUtilities.assertAtNode(document, "BeforeSplit");
        
        // now let's run some simulations
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("natjohns"), true));
        assertFalse("should NOT be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("user1"), true));
        
        // now let's activate only the left branch and make sure the split is properly executed
        TestSplitNode.setRightBranch(false);
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertFalse("should NOT be in route log because right branch is not active", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("natjohns"), true));
        
        // now let's do a flattened evaluation, it should hit both branches
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLogWithOptionalFlattening(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true, true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLogWithOptionalFlattening(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true, true));
        assertTrue("should be in route log because we've flattened nodes", getWorkflowUtility().isUserInRouteLogWithOptionalFlattening(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), true, true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLogWithOptionalFlattening(document.getRouteHeaderId(), getPrincipalIdForName("natjohns"), true, true));
        
        // now let's switch to the right branch
        TestSplitNode.setRightBranch(true);
        TestSplitNode.setLeftBranch(false);
        
        assertFalse("should NOT be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertFalse("should NOT be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("natjohns"), true));

        // now let's switch back to the left branch and approve it
        TestSplitNode.setLeftBranch(true);
        TestSplitNode.setRightBranch(false);
        
        // now let's approve it so that we're inside the right branch of the split
        document.approve("");
        // shoudl be at SplitLeft1 node
        TestUtilities.assertAtNode(document, "SplitLeft1");
        
        document = TestUtilities.switchByPrincipalName("rkirkend", document);
        assertTrue("should have an approve request", document.isApprovalRequested());
        
        // now let's run the simulation so we can test running from inside a split branch
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), true));
        assertFalse("should NOT be in route log because right branch is not active", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("jhopf"), true));
        assertTrue("should be in route log", getWorkflowUtility().isUserInRouteLog(document.getRouteHeaderId(), getPrincipalIdForName("natjohns"), true));
    }
    
    public abstract interface ReportCriteriaGenerator { public abstract ReportCriteriaDTO buildCriteria(WorkflowDocument workflowDoc) throws Exception; public boolean isCriteriaRouteHeaderBased();}

    private class ReportCriteriaGeneratorUsingXML implements ReportCriteriaGenerator {
        public ReportCriteriaDTO buildCriteria(WorkflowDocument workflowDoc) throws Exception {
            ReportCriteriaDTO criteria = new ReportCriteriaDTO(workflowDoc.getDocumentType());
            criteria.setXmlContent(workflowDoc.getDocumentContent().getApplicationContent());
            return criteria;
        }
        public boolean isCriteriaRouteHeaderBased() {
            return false;
        }
    }

    private class ReportCriteriaGeneratorUsingRouteHeaderId implements ReportCriteriaGenerator {
        public ReportCriteriaDTO buildCriteria(WorkflowDocument workflowDoc) throws Exception {
            ReportCriteriaDTO criteria = new ReportCriteriaDTO(workflowDoc.getRouteHeaderId());
            return criteria;
        }
        public boolean isCriteriaRouteHeaderBased() {
            return true;
        }
    }

    @Test public void testDocumentWillHaveApproveOrCompleteRequestAtNode_RouteHeaderId() throws Exception {
        runDocumentWillHaveApproveOrCompleteRequestAtNode(SeqSetup.DOCUMENT_TYPE_NAME,new ReportCriteriaGeneratorUsingRouteHeaderId());
    }

    @Test public void testDocumentWillHaveApproveOrCompleteRequestAtNode_XmlContent() throws Exception {
        runDocumentWillHaveApproveOrCompleteRequestAtNode(SeqSetup.DOCUMENT_TYPE_NAME,new ReportCriteriaGeneratorUsingXML());
    }

    @Test public void testDocumentWillHaveApproveOrCompleteRequestAtNode_ForceAction_RouteHeaderId() throws Exception {
        runDocumentWillHaveApproveOrCompleteRequestAtNode_ForceAction("SimulationTestDocumenType_ForceAction",new ReportCriteriaGeneratorUsingRouteHeaderId());
    }

    @Test public void testDocumentWillHaveApproveOrCompleteRequestAtNode_ForceAction_XmlContent() throws Exception {
        runDocumentWillHaveApproveOrCompleteRequestAtNode_ForceAction("SimulationTestDocumenType_ForceAction",new ReportCriteriaGeneratorUsingXML());
    }

    private void runDocumentWillHaveApproveOrCompleteRequestAtNode_ForceAction(String documentType, ReportCriteriaGenerator generator) throws Exception {
      /*
        name="WorkflowDocument"
          -  rkirkend - Approve - false
        name="WorkflowDocument2"
          -  rkirkend - Approve - false
        name="WorkflowDocument3"
          -  rkirkend - Approve - true
        name="WorkflowDocument4"
          -  rkirkend - Approve - false
          -  jitrue   - Approve - true
      */
        ReportCriteriaDTO reportCriteriaDTO = generator.buildCriteria(new WorkflowDocument(getPrincipalIdForName("ewestfal"), documentType));
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument2");
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("bmcgough"));
        assertTrue("Document should have at least one unfulfilled approve/complete request",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{getPrincipalIdForName("bmcgough")});
        assertFalse("Document should not have any unfulfilled approve/complete requests",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(new WorkflowDocument(getPrincipalIdForName("ewestfal"), documentType));
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument4");
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("bmcgough"));
        ReportActionToTakeDTO[] actionsToTake = new ReportActionToTakeDTO[2];
        actionsToTake[0] = new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD,getPrincipalIdForName("rkirkend"),"WorkflowDocument3");
        actionsToTake[1] = new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD,getPrincipalIdForName("jitrue"),"WorkflowDocument4");
        reportCriteriaDTO.setActionsToTake(actionsToTake);
        assertFalse("Document should not have any unfulfilled approve/complete requests",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(new WorkflowDocument(getPrincipalIdForName("ewestfal"), documentType));
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("pmckown"));
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument4");
        actionsToTake = new ReportActionToTakeDTO[2];
        actionsToTake[0] = new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD,getPrincipalIdForName("rkirkend"),"WorkflowDocument3");
        actionsToTake[1] = new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD,getPrincipalIdForName("jitrue"),"WorkflowDocument4");
        reportCriteriaDTO.setActionsToTake(actionsToTake);
        assertFalse("Document should not have any unfulfilled approve/complete requests",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), documentType);
        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("rkirkend"));
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument");
        assertFalse("Document should not have any approve/complete requests",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("rkirkend"));
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument2");
        assertFalse("Document should not have any approve/complete requests",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("rkirkend"));
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{getPrincipalIdForName("rkirkend")});
        assertFalse("Document should not have any approve/complete requests for user rkirkend",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        document.routeDocument("");
        assertEquals("Document should be enroute", KEWConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());
        assertEquals("Document route node is incorrect", "WorkflowDocument3", document.getNodeNames()[0]);
        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument4");
        assertTrue("At least one unfulfilled approve/complete request should have been generated",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{getPrincipalIdForName("rkirkend")});
        assertTrue("At least one unfulfilled approve/complete request should have been generated for rkirkend",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument4");
        assertTrue("At least one unfulfilled approve/complete request should have been generated",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        // if rkirkend approvers the document here it will move to last route node and no more simulations need to be run
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        document.approve("");
        assertEquals("Document should be enroute", KEWConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());
        assertEquals("Document route node is incorrect", "WorkflowDocument4", document.getNodeNames()[0]);
    }

    private void runDocumentWillHaveApproveOrCompleteRequestAtNode(String documentType,ReportCriteriaGenerator generator) throws Exception {
      /*
        name="WorkflowDocument"
          -  bmcgough - Approve - false
          -  rkirkend - Approve - false
        name="WorkflowDocument2"
          -  pmckown - Approve - false
        name="Acknowledge1"
          -  temay - Ack - false
        name="Acknowledge2"
          -  jhopf - Ack - false
      */
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), documentType);

        ReportCriteriaDTO reportCriteriaDTO = generator.buildCriteria(document);
//        ReportCriteriaDTO reportCriteriaDTO = new ReportCriteriaDTO(document.getRouteHeaderId());
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument2");
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("bmcgough"));
        assertTrue("Document should have one unfulfilled approve/complete request",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{getPrincipalIdForName("bmcgough")});
        assertFalse("Document should not have any unfulfilled approve/complete requests",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument2");
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("bmcgough"));
        ReportActionToTakeDTO[] actionsToTake = new ReportActionToTakeDTO[1];
//        actionsToTake[0] = new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD,getPrincipalIdForName("rkirkend"),"WorkflowDocument");
        actionsToTake[0] = new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD,getPrincipalIdForName("pmckown"),"WorkflowDocument2");
        reportCriteriaDTO.setActionsToTake(actionsToTake);
        assertFalse("Document should not have any unfulfilled approve/complete requests",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument2");
        actionsToTake = new ReportActionToTakeDTO[2];
        actionsToTake[0] = new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD,getPrincipalIdForName("bmcgough"),"WorkflowDocument");
        actionsToTake[1] = new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD,getPrincipalIdForName("rkirkend"),"WorkflowDocument");
        reportCriteriaDTO.setActionsToTake(actionsToTake);
        reportCriteriaDTO.setRoutingPrincipalId(getPrincipalIdForName("pmckown"));
        assertFalse("Document should not have any unfulfilled approve/complete requests",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), documentType);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());

        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        document.approve("");

        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setTargetNodeName("WorkflowDocument2");
        assertTrue("Document should have one unfulfilled approve/complete request",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        document = new WorkflowDocument(getPrincipalIdForName("pmckown"), document.getRouteHeaderId());
        document.approve("");
        assertTrue(document.stateIsProcessed());

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setTargetNodeName("Acknowledge1");
        assertFalse("Document should not have any unfulfilled approve/complete requests when in processed status",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ,KEWConstants.ACTION_REQUEST_COMPLETE_REQ}, false));

        reportCriteriaDTO = generator.buildCriteria(document);
        reportCriteriaDTO.setTargetNodeName("Acknowledge1");
        assertTrue("Document should have one unfulfilled Ack request when in final status",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ}, false));
        if (generator.isCriteriaRouteHeaderBased()) {
            assertFalse("Document should have no unfulfilled Ack request generated when in final status",getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ}, true));
        }

        // if temay acknowledges the document here it will move to processed and no more simulations would need to be tested
        document = new WorkflowDocument(getPrincipalIdForName("temay"), document.getRouteHeaderId());
        document.acknowledge("");
        assertTrue(document.stateIsProcessed());
    }

    @Test public void testIsLastApprover() throws Exception {
        // test the is last approver in route level against our sequential document type
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.saveRoutingData();

        // the initial "route level" should have no requests initially so it should return false
        assertFalse("Should not be last approver.", getWorkflowUtility().isLastApproverInRouteLevel(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), new Integer(0)));
        assertFalse("Should not be last approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.ADHOC_NODE));

        // app specific route a request to a workgroup at the initial node (TestWorkgroup)
		String groupId = getGroupIdForName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "TestWorkgroup");
        document.adHocRouteDocumentToGroup(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "", groupId, "", false);
        assertTrue("Should be last approver.", getWorkflowUtility().isLastApproverInRouteLevel(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), new Integer(0)));
        assertTrue("Should be last approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.ADHOC_NODE));

        // app specific route a request to a member of the workgroup (jitrue)
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "", getPrincipalIdForName("jitrue"), "", false);
        // member of the workgroup with the user request should be last approver
        assertTrue("Should be last approver.", getWorkflowUtility().isLastApproverInRouteLevel(document.getRouteHeaderId(), getPrincipalIdForName("jitrue"), new Integer(0)));
        assertTrue("Should be last approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("jitrue"), SeqSetup.ADHOC_NODE));
        // other members of the workgroup will not be last approvers because they don't satisfy the individuals request (ewestfal)
        assertFalse("Should not be last approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.ADHOC_NODE));

        // route the document, should stay at the adhoc node until those requests have been completed
        document.routeDocument("");
        document = new WorkflowDocument(getPrincipalIdForName("jitrue"), document.getRouteHeaderId());
        assertEquals("Document should be at adhoc node.", SeqSetup.ADHOC_NODE, document.getNodeNames()[0]);
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document.approve("");

        // document should now be at the WorkflowDocument node with a request to bmcgough and rkirkend
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        // since there are two requests, neither should be last approver
        assertFalse("Should not be last approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Should not be last approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        document.approve("");

        // request to rirkend has been satisfied, now request to bmcgough is only request remaining at level so he should be last approver
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        assertTrue("Should be last approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        document.approve("");

    }

    /**
     * This method tests how the isLastApproverAtNode method deals with force action requests, there is an app constant
     * with the value specified in KEWConstants.IS_LAST_APPROVER_ACTIVATE_FIRST which dictates whether or not to simulate
     * activation of initialized requests before running the method.
     *
     * Tests the fix to issue http://fms.dfa.cornell.edu:8080/browse/KULWF-366
     */
    @Test public void testIsLastApproverActivation() throws Exception {
        // first test without the parameter set
        Parameter lastApproverActivateParameter = CoreFrameworkServiceLocator.getParameterService().getParameter(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.FEATURE_DETAIL_TYPE, KEWConstants.IS_LAST_APPROVER_ACTIVATE_FIRST_IND);
        assertNotNull("last approver parameter should exist.", lastApproverActivateParameter);
        assertTrue("initial parameter value should be null or empty.", StringUtils.isBlank(lastApproverActivateParameter.getValue()));
        String originalParameterValue = lastApproverActivateParameter.getValue();
        
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.LAST_APPROVER_DOCUMENT_TYPE_NAME);
        document.routeDocument("");

        // at the first node (WorkflowDocument) we should have a request to rkirkend, bmcgough and to ewestfal with forceAction=true,
        assertEquals("We should be at the WorkflowDocument node.", SeqSetup.WORKFLOW_DOCUMENT_NODE, document.getNodeNames()[0]);
        assertFalse("ewestfal should have not have approve because it's initiated", document.isApprovalRequested());
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertFalse("rkirkend should not have approve because it's initiated", document.isApprovalRequested());
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue("bmcgough should have approve", document.isApprovalRequested());
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be 3 pending requests.", 3, actionRequests.size());
        // the requests to bmcgough should be activated, the request to rkirkend should be initialized,
        // and the request to ewestfal should be initialized and forceAction=true
        boolean foundBmcgoughRequest = false;
        boolean foundRkirkendRequest = false;
        boolean foundEwestfalRequest = false;
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            String netId = getPrincipalNameForId(actionRequest.getPrincipalId());
            if ("bmcgough".equals(netId)) {
                assertTrue("Request to bmcgough should be activated.", actionRequest.isActive());
                foundBmcgoughRequest = true;
            } else if ("rkirkend".equals(netId)) {
                assertTrue("Request to rkirkend should be initialized.", actionRequest.isInitialized());
                foundRkirkendRequest = true;
            } else if ("ewestfal".equals(netId)) {
                assertTrue("Request to ewestfal should be initialized.", actionRequest.isInitialized());
                assertTrue("Request to ewestfal should be forceAction.", actionRequest.getForceAction().booleanValue());
                foundEwestfalRequest = true;
            }
        }
        assertTrue("Did not find request to bmcgough.", foundBmcgoughRequest);
        assertTrue("Did not find request to rkirkend.", foundRkirkendRequest);
        assertTrue("Did not find request to ewestfal.", foundEwestfalRequest);

        // at this point, neither bmcgough, rkirkend nor ewestfal should be the last approver
        assertFalse("Bmcgough should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Rkirkend should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Ewestfal should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as bmcgough
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        document.approve("");

        // still, neither rkirkend nor ewestfal should be "final approver"
        // at this point, neither bmcgough, rkirkend nor ewestfal should be the last approver
        assertFalse("Rkirkend should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Ewestfal should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as rkirkend
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        // should be one pending activated to ewestfal now
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be 1 pending requests.", 1, actionRequests.size());
        ActionRequestValue actionRequest = (ActionRequestValue)actionRequests.get(0);
        assertTrue("Should be activated.", actionRequest.isActive());

        // ewestfal should now be the final approver
        assertTrue("Ewestfal should be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as ewestfal to send to next node
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue("ewestfal should have approve request", document.isApprovalRequested());
        document.approve("");

        // should be at the workflow document 2 node
        assertEquals("Should be at the WorkflowDocument2 Node.", SeqSetup.WORKFLOW_DOCUMENT_2_NODE, document.getNodeNames()[0]);
        // at this node there should be two requests, one to ewestfal with forceAction=false and one to pmckown,
        // since we haven't set the application constant, the non-force action request won't be activated first so pmckown
        // will not be the final approver
        assertFalse("Pmckown should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), SeqSetup.WORKFLOW_DOCUMENT_2_NODE));
        assertFalse("Ewestfal should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_2_NODE));
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be 2 action requests.", 2, actionRequests.size());

        // Now set up the app constant that checks force action properly and try a new document
        String parameterValue = "Y";
        Parameter.Builder b = Parameter.Builder.create(lastApproverActivateParameter);
        b.setValue(parameterValue);
        CoreFrameworkServiceLocator.getParameterService().updateParameter(b.build());

        lastApproverActivateParameter = CoreFrameworkServiceLocator.getParameterService().getParameter(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.FEATURE_DETAIL_TYPE, KEWConstants.IS_LAST_APPROVER_ACTIVATE_FIRST_IND);
        assertNotNull("Parameter should not be null.", lastApproverActivateParameter);
        assertEquals("Parameter should be Y.", parameterValue, lastApproverActivateParameter.getValue());


        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.LAST_APPROVER_DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        // on this document type approval progression will go as follows:
        // Workflow Document   (Sequential): bmcgough (1, fa=false),  rkirkend (2, fa=false), ewestfal (3, fa=true)
        // Workflow Document 2 (Sequential): pmckown (1, fa=false), ewestfal (2, fa=false)

        // at this point, neither bmcgough, rkirkend nor ewestfal should be the last approver
        assertFalse("Bmcgough should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Rkirkend should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Ewestfal should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as bmcgough
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        document.approve("");

        // now there is just a request to rkirkend and ewestfal, since ewestfal is force action true, neither should be final approver
        assertFalse("Rkirkend should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Ewestfal should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // verify that ewestfal does not have permissions to approve the document yet since his request has not yet been activated
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertFalse("Ewestfal should not have permissions to approve", document.isApprovalRequested());
        
        // approve as rkirkend
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue("Ewestfal should now have permission to approve", document.isApprovalRequested());
        
        // ewestfal should now be the final approver
        assertTrue("Ewestfal should now be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as ewestfal to send it to the next node
        document.approve("");

        TestUtilities.assertAtNode(document, SeqSetup.WORKFLOW_DOCUMENT_2_NODE);
        List<ActionRequestValue> requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("We should have 2 requests here.", 2, requests.size());
        
        // now, there are requests to pmckown and ewestfal here, the request to ewestfal is forceAction=false and since ewestfal
        // routed the document, this request should be auto-approved.  However, it's priority is 2 so it is activated after the
        // request to pmckown which is the situation we are testing
        assertTrue("Pmckown should be the last approver at this node.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("pmckown"), SeqSetup.WORKFLOW_DOCUMENT_2_NODE));
        assertFalse("Ewestfal should not be the final approver.", getWorkflowUtility().isLastApproverAtNode(document.getRouteHeaderId(), getPrincipalIdForName("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_2_NODE));

        // if we approve as pmckown, the document should go into acknowledgement and become processed
        document = new WorkflowDocument(getPrincipalIdForName("pmckown"), document.getRouteHeaderId());
        document.approve("");
        assertTrue("Document should be processed.", document.stateIsProcessed());

        // set parameter value back to it's original value
        Parameter.Builder b2 = Parameter.Builder.create(lastApproverActivateParameter);
        b2.setValue("");
        CoreFrameworkServiceLocator.getParameterService().updateParameter(b2.build());
    }

    @Test public void testIsFinalApprover() throws Exception {
        // for this document, pmckown should be the final approver
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        assertFinalApprover(document);
    }

    @Test public void testIsFinalApproverChild() throws Exception {
        // 12-13-2005: HR ran into a bug where this method was not correctly locating the final approver node when using a document type whic
        // inherits the route from a parent, so we will incorporate this into the unit test to prevent regression
        WorkflowDocument childDocument = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.CHILD_DOCUMENT_TYPE_NAME);
        assertFinalApprover(childDocument);
    }

    /**
     * Factored out so as not to duplicate a bunch of code between testIsFinalApprover and testIsFinalApproverChild.
     */
    private void assertFinalApprover(WorkflowDocument document) throws Exception {
        document.routeDocument("");

        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue("Document should be enroute.", document.stateIsEnroute());
        assertTrue("Should have approve request.", document.isApprovalRequested());

        // bmcgough is not the final approver
        assertFalse("Should not be final approver.", getWorkflowUtility().isFinalApprover(document.getRouteHeaderId(), getPrincipalIdForName("bmcgough")));
        // approve as bmcgough
        document.approve("");

        // should be to Ryan now, who is also not the final approver on the document
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be enroute.", document.stateIsEnroute());
        assertTrue("Should have approve request.", document.isApprovalRequested());
        assertFalse("Should not be final approver.", getWorkflowUtility().isFinalApprover(document.getRouteHeaderId(), getPrincipalIdForName("rkirkend")));
        document.approve("");

        // should be to Phil now, who *IS* the final approver on the document
        document = new WorkflowDocument(getPrincipalIdForName("pmckown"), document.getRouteHeaderId());
        assertTrue("Document should be enroute.", document.stateIsEnroute());
        assertTrue("Should have approve request.", document.isApprovalRequested());
        assertTrue("Should be final approver.", getWorkflowUtility().isFinalApprover(document.getRouteHeaderId(), getPrincipalIdForName("pmckown")));

        // now adhoc an approve to temay, phil should no longer be the final approver
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, SeqSetup.WORKFLOW_DOCUMENT_2_NODE,
                "", getPrincipalIdForName("temay"), "", true);
        assertFalse("Should not be final approver.", getWorkflowUtility().isFinalApprover(document.getRouteHeaderId(), getPrincipalIdForName("pmckown")));
        assertFalse("Should not be final approver.", getWorkflowUtility().isFinalApprover(document.getRouteHeaderId(), getPrincipalIdForName("temay")));

        // now approve as temay and then adhoc an ack to jeremy
        document = new WorkflowDocument(getPrincipalIdForName("temay"), document.getRouteHeaderId());
        assertTrue("SHould have approve request.", document.isApprovalRequested());
        document.approve("");

        // phil should be final approver again
        assertTrue("Should be final approver.", getWorkflowUtility().isFinalApprover(document.getRouteHeaderId(), getPrincipalIdForName("pmckown")));
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, SeqSetup.WORKFLOW_DOCUMENT_2_NODE,
                "", getPrincipalIdForName("jhopf"), "", true);
        document = new WorkflowDocument(getPrincipalIdForName("jhopf"), document.getRouteHeaderId());
        assertTrue("Should have acknowledge request.", document.isAcknowledgeRequested());

        // now there should be an approve to phil and an ack to jeremy, so phil should be the final approver and jeremy should not
        assertTrue("Should be final approver.", getWorkflowUtility().isFinalApprover(document.getRouteHeaderId(), getPrincipalIdForName("pmckown")));
        assertFalse("Should not be final approver.", getWorkflowUtility().isFinalApprover(document.getRouteHeaderId(), getPrincipalIdForName("jhopf")));

        // after approving as phil, the document should go processed
        document = new WorkflowDocument(getPrincipalIdForName("pmckown"), document.getRouteHeaderId());
        document.approve("");
        assertTrue("Document should be processed.", document.stateIsProcessed());
    }
    
    @Test public void testGetPrincipalIdsInRouteLog() throws Exception {
    	
    	Set<String> NonSITMembers = new HashSet<String>(
    			Arrays.asList(
						new String[] {
								getPrincipalIdForName("user1"), 
								getPrincipalIdForName("user2"), 
								getPrincipalIdForName("user3"), 
								getPrincipalIdForName("dewey")}
				)
    	);
    	
    	Set<String> WorkflowAdminMembers = new HashSet<String>(
    			Arrays.asList(
    					new String[] {
    							getPrincipalIdForName("ewestfal"), 
    							getPrincipalIdForName("rkirkend"), 
    							getPrincipalIdForName("jhopf"), 
    							getPrincipalIdForName("bmcgough"), 
    							getPrincipalIdForName("shenl"), 
    							getPrincipalIdForName("quickstart")
    					}
    			)
    	);
    	
    	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), RouteLogTestSetup.DOCUMENT_TYPE_NAME);
		document.routeDocument("");

		// just look at the current node
		Set<String> principalIds = new HashSet<String>(
				Arrays.asList(
						getWorkflowUtility().getPrincipalIdsInRouteLog(document.getRouteHeaderId(), false)
				)
		);
		// should contain ewestfal and NonSIT group members
		assertTrue(principalIds.contains(getPrincipalIdForName("ewestfal")));
		assertTrue(principalIds.containsAll(NonSITMembers));
		
		// should NOT contain jitrue and WorkflowAdmin group members as they are in the rule for the future node
		assertFalse(principalIds.contains(getPrincipalIdForName("jitrue")));
		assertFalse(principalIds.containsAll(WorkflowAdminMembers));
		
		// this time look at future nodes too
		principalIds = new HashSet<String>(
				Arrays.asList(
						getWorkflowUtility().getPrincipalIdsInRouteLog(document.getRouteHeaderId(), true)
				)
		);
		
		// should contain ewestfal and NonSIT group members
		assertTrue(principalIds.contains(getPrincipalIdForName("ewestfal")));
		assertTrue(principalIds.containsAll(NonSITMembers));

		// should also contain jitrue and WorkflowAdmin group members
		assertTrue(principalIds.contains(getPrincipalIdForName("jitrue")));
		assertTrue(principalIds.containsAll(WorkflowAdminMembers));
    }

    @Test public void testRoutingReportOnDocumentType() throws Exception {
    	ReportCriteriaDTO criteria = new ReportCriteriaDTO("SeqDocType");
    	criteria.setRuleTemplateNames(new String[] { "WorkflowDocumentTemplate" });
    	DocumentDetailDTO documentDetail = getWorkflowUtility().routingReport(criteria);
    	assertNotNull(documentDetail);
    	assertEquals("Should have been 2 requests generated.", 2, documentDetail.getActionRequests().length);

    	// let's try doing both WorkflowDocumentTemplate and WorkflowDocumentTemplate2 together
    	criteria.setRuleTemplateNames(new String[] { "WorkflowDocumentTemplate", "WorkflowDocument2Template" });
    	documentDetail = getWorkflowUtility().routingReport(criteria);
    	assertEquals("Should have been 3 requests generated.", 3, documentDetail.getActionRequests().length);

    	boolean foundRkirkend = false;
    	boolean foundBmcgough = false;
    	boolean foundPmckown = false;
    	for (int index = 0; index < documentDetail.getActionRequests().length; index++) {
			ActionRequestDTO actionRequest = documentDetail.getActionRequests()[index];
			String netId = getPrincipalNameForId(actionRequest.getPrincipalId());
			if (netId.equals("rkirkend")) {
				foundRkirkend = true;
				assertEquals(SeqSetup.WORKFLOW_DOCUMENT_NODE, actionRequest.getNodeName());
			} else if (netId.equals("bmcgough")) {
				foundBmcgough = true;
				assertEquals(SeqSetup.WORKFLOW_DOCUMENT_NODE, actionRequest.getNodeName());
			} else if (netId.equals("pmckown")) {
				foundPmckown = true;
				assertEquals(SeqSetup.WORKFLOW_DOCUMENT_2_NODE, actionRequest.getNodeName());
			}
		}
    	assertTrue("Did not find request for rkirkend", foundRkirkend);
    	assertTrue("Did not find request for bmcgough", foundBmcgough);
    	assertTrue("Did not find request for pmckown", foundPmckown);

    }

    @Test public void testRoutingReportOnRouteHeaderId() throws Exception {
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("user1"), "SeqDocType");

        ReportCriteriaDTO criteria = new ReportCriteriaDTO(doc.getRouteHeaderId());
        criteria.setRuleTemplateNames(new String[] { "WorkflowDocumentTemplate" });
        DocumentDetailDTO documentDetail = getWorkflowUtility().routingReport(criteria);
        assertNotNull(documentDetail);
        assertEquals("Route header id returned should be the same as the one passed in", doc.getRouteHeaderId(), documentDetail.getRouteHeaderId());
        assertEquals("Wrong number of action requests generated", 2, documentDetail.getActionRequests().length);

        // let's try doing both WorkflowDocumentTemplate and WorkflowDocumentTemplate2 together
        criteria.setRuleTemplateNames(new String[] { "WorkflowDocumentTemplate", "WorkflowDocument2Template" });
        documentDetail = getWorkflowUtility().routingReport(criteria);
        assertEquals("Should have been 3 requests generated.", 3, documentDetail.getActionRequests().length);

        boolean foundRkirkend = false;
        boolean foundBmcgough = false;
        boolean foundPmckown = false;
        for (int index = 0; index < documentDetail.getActionRequests().length; index++) {
            ActionRequestDTO actionRequest = documentDetail.getActionRequests()[index];
            String netId = getPrincipalNameForId(actionRequest.getPrincipalId());
            if (netId.equals("rkirkend")) {
                foundRkirkend = true;
                assertEquals(SeqSetup.WORKFLOW_DOCUMENT_NODE, actionRequest.getNodeName());
            } else if (netId.equals("bmcgough")) {
                foundBmcgough = true;
                assertEquals(SeqSetup.WORKFLOW_DOCUMENT_NODE, actionRequest.getNodeName());
            } else if (netId.equals("pmckown")) {
                foundPmckown = true;
                assertEquals(SeqSetup.WORKFLOW_DOCUMENT_2_NODE, actionRequest.getNodeName());
            }
        }
        assertTrue("Did not find request for rkirkend", foundRkirkend);
        assertTrue("Did not find request for bmcgough", foundBmcgough);
        assertTrue("Did not find request for pmckown", foundPmckown);

    }
    
    protected void verifyEmptyArray(String qualifier, Object[] array) {
    	assertNotNull("Array should not be empty", array);
        assertEquals("Number of " + qualifier + "s Returned Should be 0",0,array.length);
    }

    @Test public void testRuleReportGeneralFunction() throws Exception {
        RuleReportCriteriaDTO ruleReportCriteria = null;
        this.ruleExceptionTest(ruleReportCriteria, "Sending in null RuleReportCriteriaDTO should throw Exception");

        ruleReportCriteria = new RuleReportCriteriaDTO();
        this.ruleExceptionTest(ruleReportCriteria, "Sending in empty RuleReportCriteriaDTO should throw Exception");

        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setResponsiblePrincipalId("hobo_man");
        this.ruleExceptionTest(ruleReportCriteria, "Sending in an invalid principle ID should throw Exception");

        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setResponsibleGroupId("-1234567");
        this.ruleExceptionTest(ruleReportCriteria, "Sending in an invalid Workgroup ID should throw Exception");

        ruleReportCriteria = new RuleReportCriteriaDTO();
        RuleExtensionDTO ruleExtensionVO = new RuleExtensionDTO("key","value");
        ruleReportCriteria.setRuleExtensionVOs(new RuleExtensionDTO[]{ruleExtensionVO});
        this.ruleExceptionTest(ruleReportCriteria, "Sending in one or more RuleExtentionVO objects with no Rule Template Name should throw Exception");

        RuleDTO[] rules = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 3",3,rules.length);

        rules = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setActionRequestCodes(new String[]{KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ});
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setResponsiblePrincipalId(getPrincipalIdForName("temay"));
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        verifyEmptyArray("Rule", rules);

        rules = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setActionRequestCodes(new String[]{KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ});
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 1",1,rules.length);
        // check the rule returned
        RuleDTO ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have force action set to false",Boolean.FALSE,ruleVO.getForceAction());
        assertEquals("Number of Rule Responsibilities returned is incorrect",2,ruleVO.getRuleResponsibilities().length);
        RuleResponsibilityDTO responsibilityVO = null;
        for (int i = 0; i < ruleVO.getRuleResponsibilities().length; i++) {
            responsibilityVO = ruleVO.getRuleResponsibilities()[i];
            String responsibilityPrincipalName = getPrincipalNameForId(responsibilityVO.getPrincipalId());
            if ("temay".equals(responsibilityPrincipalName)) {
                assertEquals("Rule user is not correct","temay",responsibilityPrincipalName);
                assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
                assertEquals("Rule should be Ack Request",KEWConstants.ACTION_REQUEST_APPROVE_REQ,responsibilityVO.getActionRequestedCd());
            } else if ("ewestfal".equals(responsibilityPrincipalName)) {
                assertEquals("Rule user is not correct","ewestfal",responsibilityPrincipalName);
                assertEquals("Rule priority is incorrect",Integer.valueOf(2),responsibilityVO.getPriority());
                assertEquals("Rule should be Ack Request",KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ,responsibilityVO.getActionRequestedCd());
            } else {
                fail("Network ID of user for this responsibility is neither temay or ewestfal");
            }
        }

        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setResponsiblePrincipalId(getPrincipalIdForName("temay"));
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules returned is not correct",2,rules.length);
        for (int i = 0; i < rules.length; i++) {
            ruleVO = rules[i];
            if (RuleTestGeneralSetup.RULE_TEST_TEMPLATE_1.equals(ruleVO.getRuleTemplateName())) {
                assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
                assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_1,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_1,ruleVO.getRuleTemplateName());
                assertEquals("Rule did not have force action set to true",Boolean.TRUE,ruleVO.getForceAction());
                assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
                responsibilityVO = ruleVO.getRuleResponsibilities()[0];
                assertEquals("Rule user is incorrect","temay",getPrincipalNameForId(responsibilityVO.getPrincipalId()));
                assertEquals("Rule priority is incorrect",Integer.valueOf(3),responsibilityVO.getPriority());
                assertEquals("Rule action request is incorrect",KEWConstants.ACTION_REQUEST_APPROVE_REQ,responsibilityVO.getActionRequestedCd());
            } else if (RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2.equals(ruleVO.getRuleTemplateName())) {
                assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
                assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2,ruleVO.getRuleTemplateName());
                assertEquals("Rule did not have force action set to false",Boolean.FALSE,ruleVO.getForceAction());
                assertEquals("Number of Rule Responsibilities returned is incorrect",2,ruleVO.getRuleResponsibilities().length);
                responsibilityVO = null;
                for (int l = 0; l < ruleVO.getRuleResponsibilities().length; l++) {
                    responsibilityVO = ruleVO.getRuleResponsibilities()[l];
                    String responsibilityPrincipalName = getPrincipalNameForId(responsibilityVO.getPrincipalId());
                    if ("temay".equals(responsibilityPrincipalName)) {
                        assertEquals("Rule user is not correct","temay",responsibilityPrincipalName);
                        assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
                        assertEquals("Rule should be Ack Request",KEWConstants.ACTION_REQUEST_APPROVE_REQ,responsibilityVO.getActionRequestedCd());
                    } else if ("ewestfal".equals(responsibilityPrincipalName)) {
                        assertEquals("Rule user is not correct","ewestfal",responsibilityPrincipalName);
                        assertEquals("Rule priority is incorrect",Integer.valueOf(2),responsibilityVO.getPriority());
                        assertEquals("Rule should be Ack Request",KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ,responsibilityVO.getActionRequestedCd());
                    } else {
                        fail("Network ID of user for this responsibility is neither temay or ewestfal");
                    }
                }
            } else {
                fail("Rule Template of returned rule is not of type " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_1 + " nor " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2);
            }
        }

        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setResponsibleGroupId(RuleTestGeneralSetup.RULE_TEST_GROUP_ID);
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 1",1,rules.length);
        ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_3,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_3,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have force action set to true",Boolean.TRUE,ruleVO.getForceAction());
        assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
        responsibilityVO = ruleVO.getRuleResponsibilities()[0];
        Group ruleTestGroup = KIMServiceLocator.getIdentityManagementService().getGroup(responsibilityVO.getGroupId());
        assertEquals("Rule workgroup id is incorrect",RuleTestGeneralSetup.RULE_TEST_GROUP_ID, ruleTestGroup.getGroupId());
        assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
        assertEquals("Rule action request is incorrect",KEWConstants.ACTION_REQUEST_FYI_REQ,responsibilityVO.getActionRequestedCd());

        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setResponsiblePrincipalId(getPrincipalIdForName("user1"));
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 1",1,rules.length);
        ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_3,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_3,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have force action set to true",Boolean.TRUE,ruleVO.getForceAction());
        assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
        responsibilityVO = ruleVO.getRuleResponsibilities()[0];
        assertEquals("Rule workgroup id is incorrect",RuleTestGeneralSetup.RULE_TEST_GROUP_ID, ruleTestGroup.getGroupId());
        assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
        assertEquals("Rule action request is incorrect",KEWConstants.ACTION_REQUEST_FYI_REQ,responsibilityVO.getActionRequestedCd());
    }

    /**
     * Tests specific rule scenario relating to standard org review routing
     *
     * @throws Exception
     */
    @Test public void testRuleReportOrgReviewTest() throws Exception {
        loadXmlFile("WorkflowUtilityRuleReportConfig.xml");
        RuleReportCriteriaDTO ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleReportCriteria.setResponsiblePrincipalId(getPrincipalIdForName("user1"));
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        RuleDTO[] rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",2,rules.length);

        ruleReportCriteria = null;
        rules = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleReportCriteria.setResponsiblePrincipalId(getPrincipalIdForName("user1"));
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",1,rules.length);

        ruleReportCriteria = null;
        rules = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        RuleExtensionDTO ruleExtensionVO = new RuleExtensionDTO(RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME,"BA");
        RuleExtensionDTO ruleExtensionVO2 = new RuleExtensionDTO(RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME,"FMOP");
        RuleExtensionDTO[] ruleExtensionVOs = new RuleExtensionDTO[] {ruleExtensionVO,ruleExtensionVO2};
        ruleReportCriteria.setRuleExtensionVOs(ruleExtensionVOs);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",2,rules.length);

        ruleReportCriteria = null;
        rules = null;
        ruleExtensionVO = null;
        ruleExtensionVO2 = null;
        ruleExtensionVOs = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleReportCriteria.setResponsiblePrincipalId(getPrincipalIdForName("ewestfal"));
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",1,rules.length);
        RuleDTO ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME,RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE,RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have force action set to true",Boolean.TRUE,ruleVO.getForceAction());
        assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
        RuleResponsibilityDTO responsibilityVO = ruleVO.getRuleResponsibilities()[0];
        Group ruleTestGroup2 = KIMServiceLocator.getIdentityManagementService().getGroup(responsibilityVO.getGroupId());
        assertEquals("Rule workgroup name is incorrect",RuleTestOrgReviewSetup.RULE_TEST_WORKGROUP2,ruleTestGroup2.getGroupName());
        assertEquals("Rule priority is incorrect",Integer.valueOf(4),responsibilityVO.getPriority());
        assertEquals("Rule action request is incorrect",KEWConstants.ACTION_REQUEST_FYI_REQ,responsibilityVO.getActionRequestedCd());
        ruleExtensionVOs = ruleVO.getRuleExtensions();
        assertEquals("Number of Rule Extensions Returned Should be 2",2,ruleExtensionVOs.length);
        for (int i = 0; i < ruleExtensionVOs.length; i++) {
            RuleExtensionDTO extensionVO = ruleExtensionVOs[i];
            // if rule key is chartCode.... should equal UA
            // else if rule key is orgCode.... should equal VPIT
            // otherwise error
            if (RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME.equals(extensionVO.getKey())) {
                assertEquals("Rule Extension for key '" + RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME + "' is incorrect","UA",extensionVO.getValue());
            } else if (RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME.equals(extensionVO.getKey())) {
                assertEquals("Rule Extension for key '" + RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME + "' is incorrect","VPIT",extensionVO.getValue());
            } else {
                fail("Rule Extension has attribute key that is neither '" + RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME +
                        "' nor '" + RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME + "'");
            }
        }

        ruleReportCriteria = null;
        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleReportCriteria.setResponsiblePrincipalId(getPrincipalIdForName("user1"));
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",2,rules.length);

        ruleReportCriteria = null;
        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleExtensionVO = null;
        ruleExtensionVO2 = null;
        ruleExtensionVOs = null;
        ruleReportCriteria = new RuleReportCriteriaDTO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleExtensionVO = new RuleExtensionDTO(RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME,"UA");
        ruleExtensionVO2 = new RuleExtensionDTO(RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME,"FMOP");
        ruleExtensionVOs = new RuleExtensionDTO[] {ruleExtensionVO,ruleExtensionVO2};
        ruleReportCriteria.setRuleExtensionVOs(ruleExtensionVOs);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = getWorkflowUtility().ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",1,rules.length);
        ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME,RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE,RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have force action set to true",Boolean.TRUE,ruleVO.getForceAction());
        assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
        responsibilityVO = ruleVO.getRuleResponsibilities()[0];
        ruleTestGroup2 = KIMServiceLocator.getIdentityManagementService().getGroup(responsibilityVO.getGroupId());
        assertEquals("Rule workgroup name is incorrect",RuleTestOrgReviewSetup.RULE_TEST_WORKGROUP, ruleTestGroup2.getGroupName());
        assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
        assertEquals("Rule action request is incorrect",KEWConstants.ACTION_REQUEST_APPROVE_REQ,responsibilityVO.getActionRequestedCd());
    }

    @Test public void testGetUserActionItemCount() throws Exception {
        String principalId = getPrincipalIdForName("ewestfal");
        WorkflowDocument document = new WorkflowDocument(principalId, SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());

        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(0), getWorkflowUtility().getUserActionItemCount(principalId));
        principalId = getPrincipalIdForName("bmcgough");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(1), getWorkflowUtility().getUserActionItemCount(principalId));
        principalId = getPrincipalIdForName("rkirkend");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(1), getWorkflowUtility().getUserActionItemCount(principalId));

        TestUtilities.assertAtNode(document, "WorkflowDocument");
        document.returnToPreviousNode("", "AdHoc");
        TestUtilities.assertAtNode(document, "AdHoc");
        // verify count after return to previous
        principalId = getPrincipalIdForName("ewestfal");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        // expect one action item for approval request
        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(1), getWorkflowUtility().getUserActionItemCount(principalId));
        principalId = getPrincipalIdForName("bmcgough");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());
        assertTrue(document.isFYIRequested());
        // expect one action item for fyi action request
        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(1), getWorkflowUtility().getUserActionItemCount(principalId));
        principalId = getPrincipalIdForName("rkirkend");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());
        // expect no action items
        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(0), getWorkflowUtility().getUserActionItemCount(principalId));

        principalId = getPrincipalIdForName("ewestfal");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        document.approve("");
        TestUtilities.assertAtNode(document, "WorkflowDocument");

        // we should be back where we were
        principalId = getPrincipalIdForName("ewestfal");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());
        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(0), getWorkflowUtility().getUserActionItemCount(principalId));
        principalId = getPrincipalIdForName("bmcgough");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(1), getWorkflowUtility().getUserActionItemCount(principalId));
        principalId = getPrincipalIdForName("rkirkend");
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        assertEquals("Count is incorrect for user " + principalId, Integer.valueOf(1), getWorkflowUtility().getUserActionItemCount(principalId));
    }

    @Test public void testGetActionItems() throws Exception {
        String initiatorNetworkId = "ewestfal";
        String user1NetworkId = "bmcgough";
        String user2NetworkId ="rkirkend";
        String initiatorPrincipalId = getPrincipalIdForName(initiatorNetworkId);
        String user1PrincipalId = getPrincipalIdForName(user1NetworkId);
        String user2PrincipalId = getPrincipalIdForName(user2NetworkId);
        String principalId = getPrincipalIdForName(initiatorNetworkId);
        String docTitle = "this is the doc title";
        WorkflowDocument document = new WorkflowDocument(principalId, SeqSetup.DOCUMENT_TYPE_NAME);
        document.setTitle(docTitle);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());

        ActionItemDTO[] actionItems = getWorkflowUtility().getAllActionItems(document.getRouteHeaderId());
        assertEquals("Incorrect number of action items returned",2,actionItems.length);
        for (ActionItemDTO actionItem : actionItems) {
            assertEquals("Action Item should be Approve request", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
            assertEquals("Action Item has incorrect doc title", docTitle, actionItem.getDocTitle());
            assertTrue("User should be one of '" + user1NetworkId + "' or '" + user2NetworkId + "'", user1PrincipalId.equals(actionItem.getPrincipalId()) || user2PrincipalId.equals(actionItem.getPrincipalId()));
        }

        principalId = getPrincipalIdForName(user2NetworkId);
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        TestUtilities.assertAtNode(document, "WorkflowDocument");
        document.returnToPreviousNode("", "AdHoc");
        TestUtilities.assertAtNode(document, "AdHoc");
        // verify count after return to previous
        actionItems = getWorkflowUtility().getAllActionItems(document.getRouteHeaderId());
        assertEquals("Incorrect number of action items returned",2,actionItems.length);
        for (ActionItemDTO actionItem : actionItems) {
            assertEquals("Action Item has incorrect doc title", docTitle, actionItem.getDocTitle());
            assertTrue("Action Items should be Approve or FYI requests only", KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionItem.getActionRequestCd()) || KEWConstants.ACTION_REQUEST_FYI_REQ.equals(actionItem.getActionRequestCd()));
            if (KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionItem.getActionRequestCd())) {
                assertTrue("User should be '" + initiatorNetworkId + "'", initiatorPrincipalId.equals(actionItem.getPrincipalId()));
            } else if (KEWConstants.ACTION_REQUEST_FYI_REQ.equals(actionItem.getActionRequestCd())) {
                assertTrue("User should be  '" + user1NetworkId + "'", user1PrincipalId.equals(actionItem.getPrincipalId()));
            }
        }

        principalId = getPrincipalIdForName(initiatorNetworkId);
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document.approve("");
        TestUtilities.assertAtNode(document, "WorkflowDocument");

        // we should be back where we were
        actionItems = getWorkflowUtility().getAllActionItems(document.getRouteHeaderId());
        assertEquals("Incorrect number of action items returned",2,actionItems.length);
        for (ActionItemDTO actionItem : actionItems) {
            assertEquals("Action Item should be Approve request", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
            assertEquals("Action Item has incorrect doc title", docTitle, actionItem.getDocTitle());
            assertTrue("User should be one of '" + user1NetworkId + "' or '" + user2NetworkId + "'", user1PrincipalId.equals(actionItem.getPrincipalId()) || user2PrincipalId.equals(actionItem.getPrincipalId()));
        }
    }

    @Test public void testGetActionItems_ActionRequestCodes() throws Exception {
        String initiatorNetworkId = "ewestfal";
        String user1NetworkId = "bmcgough";
        String user2NetworkId ="rkirkend";
        String initiatorPrincipalId = getPrincipalIdForName(initiatorNetworkId);
        String user1PrincipalId = getPrincipalIdForName(user1NetworkId);
        String user2PrincipalId = getPrincipalIdForName(user2NetworkId);
        String principalId = getPrincipalIdForName(initiatorNetworkId);
        String docTitle = "this is the doc title";
        WorkflowDocument document = new WorkflowDocument(principalId, SeqSetup.DOCUMENT_TYPE_NAME);
        document.setTitle(docTitle);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());

        ActionItemDTO[] actionItems = getWorkflowUtility().getActionItems(document.getRouteHeaderId(), new String[]{KEWConstants.ACTION_REQUEST_COMPLETE_REQ});
        verifyEmptyArray("Action Item", actionItems);
        actionItems = getWorkflowUtility().getActionItems(document.getRouteHeaderId(), new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ});
        assertEquals("Incorrect number of action items returned",2,actionItems.length);
        for (ActionItemDTO actionItem : actionItems) {
            assertEquals("Action Item should be Approve request", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
            assertEquals("Action Item has incorrect doc title", docTitle, actionItem.getDocTitle());
            assertTrue("User should be one of '" + user1NetworkId + "' or '" + user2NetworkId + "'", user1PrincipalId.equals(actionItem.getPrincipalId()) || user2PrincipalId.equals(actionItem.getPrincipalId()));
        }

        principalId = getPrincipalIdForName(user2NetworkId);
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        TestUtilities.assertAtNode(document, "WorkflowDocument");
        document.returnToPreviousNode("", "AdHoc");
        TestUtilities.assertAtNode(document, "AdHoc");
        // verify count after return to previous
        actionItems = getWorkflowUtility().getActionItems(document.getRouteHeaderId(), new String[]{KEWConstants.ACTION_REQUEST_COMPLETE_REQ});
        verifyEmptyArray("Action Item", actionItems);
        actionItems = getWorkflowUtility().getActionItems(document.getRouteHeaderId(), new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ});
        assertEquals("Incorrect number of action items returned",1,actionItems.length);
        actionItems = getWorkflowUtility().getActionItems(document.getRouteHeaderId(), new String[]{KEWConstants.ACTION_REQUEST_FYI_REQ});
        assertEquals("Incorrect number of action items returned",1,actionItems.length);
        actionItems = getWorkflowUtility().getActionItems(document.getRouteHeaderId(), new String[]{KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_APPROVE_REQ});
        assertEquals("Incorrect number of action items returned",2,actionItems.length);
        for (ActionItemDTO actionItem : actionItems) {
            assertEquals("Action Item has incorrect doc title", docTitle, actionItem.getDocTitle());
            assertTrue("Action Items should be Approve or FYI requests only", KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionItem.getActionRequestCd()) || KEWConstants.ACTION_REQUEST_FYI_REQ.equals(actionItem.getActionRequestCd()));
            if (KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionItem.getActionRequestCd())) {
                assertTrue("User should be '" + initiatorNetworkId + "'", initiatorPrincipalId.equals(actionItem.getPrincipalId()));
            } else if (KEWConstants.ACTION_REQUEST_FYI_REQ.equals(actionItem.getActionRequestCd())) {
                assertTrue("User should be  '" + user1NetworkId + "'", user1PrincipalId.equals(actionItem.getPrincipalId()));
            } else {
                fail("Should not have found action request with requested action '" + KEWConstants.ACTION_REQUEST_CD.get(actionItem.getActionRequestCd()) + "'");
            }
        }

        principalId = getPrincipalIdForName(initiatorNetworkId);
        document = new WorkflowDocument(principalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document.approve("");
        TestUtilities.assertAtNode(document, "WorkflowDocument");

        // we should be back where we were
        actionItems = getWorkflowUtility().getActionItems(document.getRouteHeaderId(), new String[]{KEWConstants.ACTION_REQUEST_COMPLETE_REQ});
        verifyEmptyArray("Action Item", actionItems);
        actionItems = getWorkflowUtility().getActionItems(document.getRouteHeaderId(), new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ});
        assertEquals("Incorrect number of action items returned",2,actionItems.length);
        for (ActionItemDTO actionItem : actionItems) {
            assertEquals("Action Item should be Approve request", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
            assertEquals("Action Item has incorrect doc title", docTitle, actionItem.getDocTitle());
            assertTrue("User should be one of '" + user1NetworkId + "' or '" + user2NetworkId + "'", user1PrincipalId.equals(actionItem.getPrincipalId()) || user2PrincipalId.equals(actionItem.getPrincipalId()));
        }
    }

    /**
     * This method routes two test documents of the type specified.  One has the given title and another has a dummy title.
     */
    private void setupPerformDocumentSearchTests(String documentTypeName, String expectedRouteNodeName, String docTitle) throws WorkflowException {
        String userNetworkId = "ewestfal";
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalIdForName(userNetworkId), documentTypeName);
        workflowDocument.setTitle("Respect my Authoritah");
        workflowDocument.routeDocument("routing this document.");
        if (StringUtils.isNotBlank(expectedRouteNodeName)) {
        	assertEquals("Document is not at expected routeNodeName", expectedRouteNodeName, workflowDocument.getNodeNames()[0]);
        }

        userNetworkId = "rkirkend";
        workflowDocument = new WorkflowDocument(getPrincipalIdForName(userNetworkId), documentTypeName);
        workflowDocument.setTitle(docTitle);
        workflowDocument.routeDocument("routing this document.");
        if (StringUtils.isNotBlank(expectedRouteNodeName)) {
        	assertEquals("Document is not at expected routeNodeName", expectedRouteNodeName, workflowDocument.getNodeNames()[0]);
        }
    }

    @Test public void testPerformDocumentSearch_WithUser_CustomThreshold() throws Exception {
        runTestPerformDocumentSearch_CustomThreshold(getPrincipalIdForName("user2"));
    }

    @Test public void testPerformDocumentSearch_NoUser_CustomThreshold() throws Exception {
    	runTestPerformDocumentSearch_CustomThreshold(null);
    }

    private void runTestPerformDocumentSearch_CustomThreshold(String principalId) throws Exception {
        String documentTypeName = SeqSetup.DOCUMENT_TYPE_NAME;
        String docTitle = "Routing Style";
        setupPerformDocumentSearchTests(documentTypeName, null, docTitle);

        DocumentSearchCriteriaDTO criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        DocumentSearchResultDTO result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        List<DocumentSearchResultRowDTO> searchResults = result.getSearchResults();
        assertEquals("Search results should have two documents.", 2, searchResults.size());

        int threshold = 1;
        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setThreshold(Integer.valueOf(threshold));
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        assertTrue("Search results should signify search went over the given threshold: " + threshold, result.isOverThreshold());
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", threshold, searchResults.size());
    }

    @Test public void testPerformDocumentSearch_WithUser_BasicCriteria() throws Exception {
        runTestPerformDocumentSearch_BasicCriteria(getPrincipalIdForName("user2"));
    }

    @Test public void testPerformDocumentSearch_NoUser_BasicCriteria() throws Exception {
    	runTestPerformDocumentSearch_BasicCriteria(null);
    }

    private void runTestPerformDocumentSearch_BasicCriteria(String principalId) throws Exception {
        String documentTypeName = SeqSetup.DOCUMENT_TYPE_NAME;
        String docTitle = "Routing Style";
        setupPerformDocumentSearchTests(documentTypeName, null, docTitle);
        String userNetworkId = "delyea";
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalIdForName(userNetworkId), documentTypeName);
        workflowDocument.setTitle("Get Outta Dodge");
        workflowDocument.routeDocument("routing this document.");

        DocumentSearchCriteriaDTO criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setDocTitle(docTitle);
        DocumentSearchResultDTO result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        List<DocumentSearchResultRowDTO> searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setInitiator("rkirkend");
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setInitiator("user1");
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have two documents.", 3, searchResults.size());
        // now verify that the search returned the proper document id
        boolean foundValidDocId = false;
        for (DocumentSearchResultRowDTO documentSearchResultRowVO : searchResults) {
			for (KeyValue keyValueVO : documentSearchResultRowVO.getFieldValues()) {
				if ( (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID.equals(keyValueVO.getKey())) &&
					 (StringUtils.equals(workflowDocument.getRouteHeaderId().toString(), keyValueVO.getValue())) ) {
					foundValidDocId = true;
					break;
				}
			}
		}
        assertTrue("Should have found document search result with specified document id",foundValidDocId);
    }

    @Test public void testPerformDocumentSearch_WithUser_RouteNodeSearch() throws Exception {
        runTestPerformDocumentSearch_RouteNodeSearch(getPrincipalIdForName("user2"));
    }

    @Test public void testPerformDocumentSearch_NoUser_RouteNodeSearch() throws Exception {
    	runTestPerformDocumentSearch_RouteNodeSearch(null);
    }

    private void runTestPerformDocumentSearch_RouteNodeSearch(String principalId) throws Exception {
        String documentTypeName = SeqSetup.DOCUMENT_TYPE_NAME;
        setupPerformDocumentSearchTests(documentTypeName, SeqSetup.WORKFLOW_DOCUMENT_NODE, "Doc Title");

        // test exception thrown when route node specified and no doc type specified
        DocumentSearchCriteriaDTO criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocRouteNodeName(SeqSetup.ADHOC_NODE);
        try {
            getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
            fail("Exception should have been thrown when specifying a route node name but no document type name");
        } catch (Exception e) {}

        // test exception thrown when route node specified does not exist on document type
        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setDocRouteNodeName("Yo homes, smell ya later!");
        try {
            getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
            fail("Exception should have been thrown when specifying a route node name that does not exist on the specified document type name");
        } catch (Exception e) {}

        runPerformDocumentSearch_RouteNodeSearch(principalId, SeqSetup.ADHOC_NODE, documentTypeName, 0, 0, 2);
        runPerformDocumentSearch_RouteNodeSearch(principalId, SeqSetup.WORKFLOW_DOCUMENT_NODE, documentTypeName, 0, 2, 0);
        runPerformDocumentSearch_RouteNodeSearch(principalId, SeqSetup.WORKFLOW_DOCUMENT_2_NODE, documentTypeName, 2, 0, 0);
    }

    @Test public void testPerformDocumentSearch_RouteNodeSpecial() throws RemoteException, WorkflowException {
        String documentTypeName = "DocumentWithSpecialRouteNodes";
        setupPerformDocumentSearchTests(documentTypeName, "Level1", "Doc Title");
        runPerformDocumentSearch_RouteNodeSearch(null, "Level5", documentTypeName, 0, 0, 2);
        runPerformDocumentSearch_RouteNodeSearch(null, "Level1", documentTypeName, 0, 2, 0);
        runPerformDocumentSearch_RouteNodeSearch(null, "Level3", documentTypeName, 2, 0, 0);

    }

    private void runPerformDocumentSearch_RouteNodeSearch(String principalId, String routeNodeName, String documentTypeName, int countBeforeNode, int countAtNode, int countAfterNode) throws RemoteException, WorkflowException {
        DocumentSearchCriteriaDTO criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setDocRouteNodeName(routeNodeName);
        criteria.findDocsAtExactSpecifiedRouteNode();
        DocumentSearchResultDTO result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        List<DocumentSearchResultRowDTO> searchResults = result.getSearchResults();
        assertEquals("Wrong number of search results when checking default node qualifier.", countAtNode, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setDocRouteNodeName(routeNodeName);
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Wrong number of search results when checking docs at exact node.", countAtNode, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setDocRouteNodeName(routeNodeName);
        criteria.findDocsBeforeSpecifiedRouteNode();
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Wrong number of search results when checking docs before node.", countBeforeNode, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setDocRouteNodeName(routeNodeName);
        criteria.findDocsAfterSpecifiedRouteNode();
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Wrong number of search results when checking docs after node.", countAfterNode, searchResults.size());
    }

    @Test public void testPerformDocumentSearch_WithUser_SearchAttributes() throws Exception {
    	runTestPerformDocumentSearch_SearchAttributes(getPrincipalIdForName("user2"));
    }

    @Test public void testPerformDocumentSearch_NoUser_SearchAttributes() throws Exception {
    	runTestPerformDocumentSearch_SearchAttributes(null);
    }

    private void runTestPerformDocumentSearch_SearchAttributes(String principalId) throws Exception {
        String documentTypeName = SeqSetup.DOCUMENT_TYPE_NAME;
        String docTitle = "Routing Style";
        setupPerformDocumentSearchTests(documentTypeName, null, docTitle);

        DocumentSearchCriteriaDTO criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY,TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE)));
        DocumentSearchResultDTO result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        List<DocumentSearchResultRowDTO> searchResults = result.getSearchResults();
        assertEquals("Search results should have two documents.", 2, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName); 
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY,"fred")));
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue("fakeproperty", "doesntexist")));
        try {
            result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (Exception e) {}

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString())));
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have two documents.", 2, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "1111111")));
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue("fakeymcfakefake", "99999999")));
        try {
            result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (Exception e) {}

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString())));
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have two documents.", 2, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, "215.3548")));
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue("fakeylostington", "9999.9999")));
        try {
            result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (Exception e) {}

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocSearchUtils.getDisplayValueWithDateOnly(new Timestamp(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE_IN_MILLS)))));
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have two documents.", 2, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "07/06/1979")));
        result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocumentSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setSearchAttributeValues(Collections.singletonList(new ConcreteKeyValue("lastingsfakerson","07/06/2007")));
        try {
            result = getWorkflowUtility().performDocumentSearchWithPrincipal(principalId, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (Exception e) {}
    }

    @Test public void testGetSearchableAttributeDateTimeValuesByKey() throws Exception {
        String documentTypeName = SeqSetup.DOCUMENT_TYPE_NAME;
        String userNetworkId = "ewestfal";
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalIdForName(userNetworkId), documentTypeName);
        workflowDocument.setTitle("Respect my Authoritah");
        workflowDocument.routeDocument("routing this document.");
        userNetworkId = "rkirkend";
        WorkflowDocument workflowDocument2 = new WorkflowDocument(getPrincipalIdForName(userNetworkId), documentTypeName);
        workflowDocument2.setTitle("Routing Style");
        workflowDocument2.routeDocument("routing this document.");

        Timestamp[] timestamps = getWorkflowUtility().getSearchableAttributeDateTimeValuesByKey(workflowDocument.getRouteHeaderId(), TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY);
        assertNotNull("Timestamps should not be null", timestamps);
        assertTrue("Timestamps should not be empty", 0 != timestamps.length);
        verifyTimestampToSecond(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE_IN_MILLS, timestamps[0].getTime());

        timestamps = getWorkflowUtility().getSearchableAttributeDateTimeValuesByKey(workflowDocument2.getRouteHeaderId(), TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY);
        assertNotNull("Timestamps should not be null", timestamps);
        assertTrue("Timestamps should not be empty", 0 != timestamps.length);
        verifyTimestampToSecond(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE_IN_MILLS, timestamps[0].getTime());
    }
    
    protected void verifyTimestampToSecond(Long originalTimeInMillis, Long testTimeInMillis) throws Exception {
        Calendar testDate = Calendar.getInstance();
        testDate.setTimeInMillis(originalTimeInMillis);
        testDate.set(Calendar.MILLISECOND, 0);
        Calendar attributeDate = Calendar.getInstance();
        attributeDate.setTimeInMillis(testTimeInMillis);
        attributeDate.set(Calendar.MILLISECOND, 0);
        assertEquals("The month value for the searchable attribute is wrong",testDate.get(Calendar.MONTH),attributeDate.get(Calendar.MONTH));
        assertEquals("The date value for the searchable attribute is wrong",testDate.get(Calendar.DATE),attributeDate.get(Calendar.DATE));
        assertEquals("The year value for the searchable attribute is wrong",testDate.get(Calendar.YEAR),attributeDate.get(Calendar.YEAR));
        assertEquals("The hour value for the searchable attribute is wrong",testDate.get(Calendar.HOUR),attributeDate.get(Calendar.HOUR));
        assertEquals("The minute value for the searchable attribute is wrong",testDate.get(Calendar.MINUTE),attributeDate.get(Calendar.MINUTE));
        assertEquals("The second value for the searchable attribute is wrong",testDate.get(Calendar.SECOND),attributeDate.get(Calendar.SECOND));
    }

    private void ruleExceptionTest(RuleReportCriteriaDTO ruleReportCriteria, String message) {
        try {
            getWorkflowUtility().ruleReport(ruleReportCriteria);
            fail(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RuleTestGeneralSetup {
        public static final String DOCUMENT_TYPE_NAME = "RuleTestDocType";
        public static final String RULE_TEST_TEMPLATE_1 = "WorkflowDocumentTemplate";
        public static final String RULE_TEST_TEMPLATE_2 = "WorkflowDocument2Template";
        public static final String RULE_TEST_TEMPLATE_3 = "WorkflowDocument3Template";
        public static final String RULE_TEST_GROUP_ID = "3003"; // the NonSIT group
    }

    private class RuleTestOrgReviewSetup {
        public static final String DOCUMENT_TYPE_NAME = "OrgReviewTestDocType";
        public static final String RULE_TEST_TEMPLATE = "OrgReviewTemplate";
        public static final String RULE_TEST_WORKGROUP = "Org_Review_Group";
        public static final String RULE_TEST_WORKGROUP2 = "Org_Review_Group_2";
        public static final String RULE_TEST_CHART_CODE_NAME = "chartCode";
        public static final String RULE_TEST_ORG_CODE_NAME = "orgCode";
    }

    private class SeqSetup {
        public static final String DOCUMENT_TYPE_NAME = "SeqDocType";
        public static final String LAST_APPROVER_DOCUMENT_TYPE_NAME = "SeqLastApproverDocType";
        public static final String CHILD_DOCUMENT_TYPE_NAME = "SeqChildDocType";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
        public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
        public static final String ACKNOWLEDGE_1_NODE = "Acknowledge1";
        public static final String ACKNOWLEDGE_2_NODE = "Acknowledge2";
    }
    
    private class RouteLogTestSetup {
        public static final String DOCUMENT_TYPE_NAME = "UserAndGroupTestDocType";
        public static final String RULE_TEST_TEMPLATE_1 = "WorkflowDocumentTemplate";
        public static final String RULE_TEST_TEMPLATE_2 = "WorkflowDocument2Template";
        public static final String RULE_TEST_GROUP_ID = "3003"; // the NonSIT group
    }

}
