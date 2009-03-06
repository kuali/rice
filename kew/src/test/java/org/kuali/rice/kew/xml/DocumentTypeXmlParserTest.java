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
// Created on Jun 16, 2006

package org.kuali.rice.kew.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.exception.InvalidWorkgroupException;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;


public class DocumentTypeXmlParserTest extends KEWTestCase {
    private List testDoc(String docName, Class expectedException) throws Exception {
        DocumentTypeXmlParser parser = new DocumentTypeXmlParser();
        try {
            List docTypes = parser.parseDocumentTypes(getClass().getResourceAsStream(docName + ".xml"));
            if (expectedException != null) {
                fail(docName + " successfully loaded");
            }
            return docTypes;
        } catch (Exception e) {
            if (expectedException == null || !(expectedException.isAssignableFrom(e.getClass()))) {
                throw e;
            } else {
                log.error(docName + " exception: " + e);
                return new ArrayList();
            }
        }
    }

    @Test public void testLoadDocWithVariousActivationTypes() throws Exception {
        testDoc("ValidActivationTypes", null);
    }

    @Test public void testLoadDocWithInvalidActivationType() throws Exception {
        testDoc("BadActivationType", IllegalArgumentException.class);
    }

    @Test public void testLoadDocWithValidPolicyNames() throws Exception {
        testDoc("ValidPolicyNames", null);
    }
    
    @Test public void testLoadDocWithValidRuleSelector() throws Exception {
        testDoc("ValidRuleSelector", null);
    }

    @Test public void testLoadDocWithDuplicatePolicyName() throws Exception {
        testDoc("DuplicatePolicyName", InvalidXmlException.class);
    }

    @Test public void testLoadDocWithBadPolicyName() throws Exception {
        testDoc("BadPolicyName", IllegalArgumentException.class);
    }

    @Test public void testLoadDocWithBadNextNode() throws Exception {
        testDoc("BadNextNode", InvalidXmlException.class);
    }

    @Test public void testLoadDocWithNoDocHandler() throws Exception {
        testDoc("NoDocHandler", null);
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName("DocumentTypeXmlParserTestDoc1");
        assertTrue("Doc type unresolved doc handler should be empty.", StringUtils.isBlank(documentType.getUnresolvedDocHandlerUrl()));
        assertTrue("Doc type doc handler should be empty.", StringUtils.isBlank(documentType.getDocHandlerUrl()));
    }

    @Test public void testLoadDocWithBadExceptionWG() throws Exception {
        testDoc("BadExceptionWorkgroup", InvalidWorkgroupException.class);
    }

    @Test public void testLoadDocWithBadSuperUserWG() throws Exception {
        testDoc("BadSuperUserWorkgroup", InvalidWorkgroupException.class);
    }

    @Test public void testLoadDocWithBadBlanketApproveWG() throws Exception {
        testDoc("BadBlanketApproveWorkgroup", InvalidWorkgroupException.class);
    }

    @Test public void testLoadDocWithBadRuleTemplate() throws Exception {
        testDoc("BadRuleTemplate", InvalidXmlException.class);
    }

    @Test public void testLoadDocWithInvalidParent() throws Exception {
        testDoc("InvalidParent", InvalidXmlException.class);
    }
    
    @Test public void testLoadDocWithOrphanedNodes() throws Exception {
    	testDoc("OrphanedNodes", InvalidXmlException.class);
    }
    
    @Test public void testBlanketApprovePolicy() throws Exception {
    	testDoc("BlanketApprovePolicy", null);
    	
    	// on BlanketApprovePolicy1 anyone can blanket approve
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("pzhang"), "BlanketApprovePolicy1");
    	document.saveRoutingData();
    	assertTrue(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));

    	// on BlanketApprovePolicy2 no-one can blanket approve
    	document = new WorkflowDocument(new NetworkIdDTO("pzhang"), "BlanketApprovePolicy2");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
    	// on BlanketApprovePolicy3 no-one can blanket approve
    	document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "BlanketApprovePolicy3");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
    	// on BlanketApprovePolicy4 TestWorkgroup can blanket approve
    	/*document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "BlanketApprovePolicy4");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));*/
    	
    	// on Blanket ApprovePolicy 5, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdDTO("pzhang"), "BlanketApprovePolicy5");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
//   	 on Blanket ApprovePolicy 6, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdDTO("pzhang"), "BlanketApprovePolicy6");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
//   	 on Blanket ApprovePolicy 7, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdDTO("pzhang"), "BlanketApprovePolicy7");
    	document.saveRoutingData();
    	assertTrue(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertTrue(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
//   	 on BlanketApprovePolicy_Override_NONE, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdDTO("pzhang"), "BlanketApprovePolicy_Override_NONE");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
//   	 on BlanketApprovePolicy_Override_ANY, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdDTO("pzhang"), "BlanketApprovePolicy_Override_ANY");
    	document.saveRoutingData();
    	assertTrue(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertTrue(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));

//  	 on BlanketApprovePolicy_Override_ANY, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdDTO("pzhang"), "BlanketApprovePolicy_NoOverride");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
    	assertTrue(document.isActionCodeValidForDocument(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    }
    
    @Test public void testReportingWorkgroupName() throws Exception {
    	testDoc("ReportingWorkgroupName", null);
    	
    	DocumentType documentType1 = KEWServiceLocator.getDocumentTypeService().findByName("ReportingWorkgroupName1");
    	assertNotNull("Should have a reporting workgroup.", documentType1.getReportingWorkgroup());
    	assertEquals("Should be WorkflowAdmin reporting workgroup", "WorkflowAdmin", documentType1.getReportingWorkgroup().getGroupName());
    		
    	DocumentType documentType2 = KEWServiceLocator.getDocumentTypeService().findByName("ReportingWorkgroupName2");
    	assertNull("Should not have a reporting workgroup.", documentType2.getReportingWorkgroup());
    }
    
    @Test public void testCurrentDocumentNotMaxVersionNumber() throws Exception {
        String fileNameToIngest = "VersionNumberCheck";
        String documentTypeName = "VersionCheckDocument";
        testDoc(fileNameToIngest, null);
        testDoc(fileNameToIngest, null);
        testDoc(fileNameToIngest, null);
        
        DocumentType originalCurrentDocType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        assertNotNull("Should have found document for doc type '" + documentTypeName + "'",originalCurrentDocType);
        assertNotNull("Doc Type should have previous doc type id",originalCurrentDocType.getPreviousVersionId());
        assertEquals("Doc Type should be current",Boolean.TRUE,originalCurrentDocType.getCurrentInd());
        DocumentType previousDocType1 = KEWServiceLocator.getDocumentTypeService().findById(originalCurrentDocType.getPreviousVersionId());
        assertNotNull("Should have found document for doc type '" + documentTypeName + "' and previous version " + originalCurrentDocType.getPreviousVersionId(),previousDocType1);
        assertNotNull("Doc Type should have previous doc type id",previousDocType1.getPreviousVersionId());
        DocumentType firstDocType = KEWServiceLocator.getDocumentTypeService().findById(previousDocType1.getPreviousVersionId());
        assertNotNull("Should have found document for doc type '" + documentTypeName + "' and previous version " + previousDocType1.getPreviousVersionId(),firstDocType);
        assertNull("Doc type retrieved should have been first doc type",firstDocType.getPreviousVersionId());
        
        // reset the current document to the previous one to replicate bug conditions
        originalCurrentDocType.setCurrentInd(Boolean.FALSE);
        KEWServiceLocator.getDocumentTypeService().save(originalCurrentDocType);
        firstDocType.setCurrentInd(Boolean.TRUE);
        KEWServiceLocator.getDocumentTypeService().save(firstDocType);
        DocumentType newCurrentDocType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        assertNotNull("Should have found document for doc type '" + documentTypeName + "'",newCurrentDocType);
        assertEquals("Version of new doc type should match that of first doc type", firstDocType.getVersion(), newCurrentDocType.getVersion());
        
        // ingest the doc type again and verify correct version number
        try {
            testDoc(fileNameToIngest, null);
        } catch (Exception e) {
            fail("File should have ingested correctly" + e.getLocalizedMessage());
        }
        
        DocumentType currentDocType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        assertNotNull("Should have found document for doc type '" + documentTypeName + "'",currentDocType);
        assertEquals("Doc Type should be current",Boolean.TRUE,currentDocType.getCurrentInd());
        assertNotNull("Doc Type should have previous doc type id",currentDocType.getPreviousVersionId());
        assertEquals("New current document should have version 1 greater than ", Integer.valueOf(originalCurrentDocType.getVersion().intValue() + 1), currentDocType.getVersion());
        previousDocType1 = KEWServiceLocator.getDocumentTypeService().findById(currentDocType.getPreviousVersionId());
        assertNotNull("Should have found document for doc type '" + documentTypeName + "' and previous version " + newCurrentDocType.getPreviousVersionId(),previousDocType1);
        assertFalse("Doc Type should be current",previousDocType1.getCurrentInd());
        assertNull("Doc type retrieved should not have previous doc type",previousDocType1.getPreviousVersionId());
    }
    
    @Test public void testLoadDocWithOrderedAttributes() throws Exception {
        List documentTypes = testDoc("ValidActivationTypes", null);
        assertEquals("Should only be one doc type parsed", 1, documentTypes.size());
        DocumentType docType = (DocumentType) documentTypes.get(0);
        for (int i = 0; i < docType.getDocumentTypeAttributes().size(); i++) {
            DocumentTypeAttribute attribute = docType.getDocumentTypeAttributes().get(i);
            assertEquals("Invalid Index Number", i+1, attribute.getOrderIndex());
        }
        
        DocumentType docTypeFresh = KEWServiceLocator.getDocumentTypeService().findByName("DocumentTypeXmlParserTestDoc_ValidActivationTypes");
        assertEquals("Should be 3 doc type attributes", 3, docTypeFresh.getDocumentTypeAttributes().size());
        int index = 0;
        DocumentTypeAttribute attribute = docTypeFresh.getDocumentTypeAttributes().get(index);
        assertEquals("Invalid Index Number", index+1, attribute.getOrderIndex());
        assertEquals("Invalid attribute name for order value " + index+1, "TestRuleAttribute2", attribute.getRuleAttribute().getName());
        
        index = 1;
        attribute = docTypeFresh.getDocumentTypeAttributes().get(index);
        assertEquals("Invalid Index Number", index+1, attribute.getOrderIndex());
        assertEquals("Invalid attribute name for order value " + index+1, "TestRuleAttribute3", attribute.getRuleAttribute().getName());

        index = 2;
        attribute = docTypeFresh.getDocumentTypeAttributes().get(index);
        assertEquals("Invalid Index Number", index+1, attribute.getOrderIndex());
        assertEquals("Invalid attribute name for order value " + index+1, "TestRuleAttribute", attribute.getRuleAttribute().getName());
    }
    
    @Test public void testLoadDocWithNoLabel() throws Exception {
    	List documentTypes = testDoc("DocTypeWithNoLabel", null);
    	assertEquals("Should have parsed 1 document type", 1, documentTypes.size());
    	
    	DocumentType documentType = (DocumentType)documentTypes.get(0);
    	assertEquals("Document type has incorrect name", "DocumentTypeXmlParserTestDoc_DocTypeWithNoLabel", documentType.getName());
    	assertEquals("Document type has incorrect label", KEWConstants.DEFAULT_DOCUMENT_TYPE_LABEL, documentType.getLabel());
    	
    	// now test a DocumentType ingestion with no label for a DocumentType that has a previous version
    	// in this case we use TestDocumentType3 which should have been ingested from DefaultTestData.xml
    	DocumentType testDocType3 = KEWServiceLocator.getDocumentTypeService().findByName("TestDocumentType3");
    	assertNotNull("TestDocumentType3 should exist.", testDocType3);
    	// the current label for TestDocumentType3 should be TestDocumentType
    	String expectedLabel = "TestDocumentType";
    	assertEquals("Incorrect label", expectedLabel, testDocType3.getLabel());
    	
    	// now let's ingest a new version without the label, it should maintain the original label and not
    	// end up with a value of Undefined
    	documentTypes = testDoc("DocTypeWithNoLabelPreviousVersion", null);
    	assertEquals("Should have parsed 1 document type", 1, documentTypes.size());
    	testDocType3 = (DocumentType)documentTypes.get(0);
    	assertEquals("Document type has incorrect name", "TestDocumentType3", testDocType3.getName());
    	assertEquals("Document type has incorrect label", expectedLabel, testDocType3.getLabel());
    }

    @Test public void testLoadRoutePathOnlyAdjustsDocument() throws Exception {
        List documentTypes = testDoc("RoutePathAdjustment1", null);
        assertEquals("Incorrect parsed document type count", 1, documentTypes.size());
        DocumentType docType1 = (DocumentType) documentTypes.get(0);
        List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(docType1, true);
        assertEquals("Incorrect document route node count", 1, routeNodes.size());
        assertEquals("Expected Route Node Name is incorrect", "First", ((RouteNode)routeNodes.get(0)).getRouteNodeName());

        documentTypes = testDoc("RoutePathAdjustment2", null);
        assertEquals("Incorrect parsed document type count", 1, documentTypes.size());
        DocumentType docType2 = (DocumentType) documentTypes.get(0);
        routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(docType2, true);
        assertEquals("Incorrect document route node count", 2, routeNodes.size());
        assertEquals("Expected Route Node Name is incorrect", "First", ((RouteNode)routeNodes.get(0)).getRouteNodeName());
        assertEquals("Expected Route Node Name is incorrect", "Second", ((RouteNode)routeNodes.get(1)).getRouteNodeName());
    }

    /**
     * Checks if a child document can be processed when it precedes its parent.
     * 
     * @throws Exception
     */
    @Test public void testLoadDocWithOneChildPrecedingParent() throws Exception {
    	List<?> docTypeList;
    	// Test a case where there is a single child document preceding its parent.
    	docTypeList = testDoc("ChildParentTestConfig1_Reordered", null);
    	assertEquals("There should be 5 document types.", 5, docTypeList.size());
    }
    
    /**
     * Checks if a child routing document can be processed when it precedes its parent.
     * 
     * @throws Exception
     */
    @Test public void testRouteDocWithOneChildPrecedingParent() throws Exception {
    	List<?> docTypeList;
    	this.loadXmlFile("ChildParentTestConfig1_Reordered.xml");
    	// Test a case where there is a single router child document preceding its parent.
    	docTypeList = testDoc("ChildParentTestConfig1_Routing", null);
    	assertEquals("There should be 5 document types.", 5, docTypeList.size());
    }
    
    /**
     * Checks if the child-parent resolution works with a larger inheritance tree.
     * 
     * @throws Exception
     */
    @Test public void testLoadDocWithLargerChildPrecedenceInheritanceTree() throws Exception {
    	List<?> docTypeList;
    	// Test a case where there are multiple inheritance tree layers to resolve.
    	docTypeList = testDoc("ChildParentTestConfig1_Reordered2", null);
    	assertEquals("There should be 10 document types.", 10, docTypeList.size());
    }
    
    /**
     * Checks if the child-parent resolution works with a larger inheritance tree and a mix of standard & routing documents.
     * 
     * @throws Exception
     */
    @Test public void testRouteDocWithLargerChildPrecedenceInheritanceTree() throws Exception {
    	List<?> docTypeList;
    	this.loadXmlFile("ChildParentTestConfig1_Routing2_Prep.xml");
    	// Test a case where there are multiple inheritance tree layers to resolve.
    	docTypeList = testDoc("ChildParentTestConfig1_Routing2", null);
    	assertEquals("There should be 10 document types.", 10, docTypeList.size());
    }
    
}