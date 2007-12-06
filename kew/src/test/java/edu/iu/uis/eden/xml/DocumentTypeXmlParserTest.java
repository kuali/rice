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

package edu.iu.uis.eden.xml;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.InvalidWorkgroupException;
import edu.iu.uis.eden.exception.InvalidXmlException;

public class DocumentTypeXmlParserTest extends KEWTestCase {
    private void testDoc(String docName, Class expectedException) throws Exception {
        DocumentTypeXmlParser parser = new DocumentTypeXmlParser();
        try {
            parser.parseDocumentTypes(getClass().getResourceAsStream(docName + ".xml"));
            if (expectedException != null) {
                fail(docName + " successfully loaded");
            }
        } catch (Exception e) {
            if (expectedException == null || !(expectedException.isAssignableFrom(e.getClass()))) {
                throw e;
            } else {
                log.error(docName + " exception: " + e);
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
        testDoc("NoDocHandler", InvalidXmlException.class);
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
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("pzhang"), "BlanketApprovePolicy1");
    	document.saveRoutingData();
    	assertTrue(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));

    	// on BlanketApprovePolicy2 no-one can blanket approve
    	document = new WorkflowDocument(new NetworkIdVO("pzhang"), "BlanketApprovePolicy2");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
    	// on BlanketApprovePolicy3 no-one can blanket approve
    	document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "BlanketApprovePolicy3");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
    	// on BlanketApprovePolicy4 TestWorkgroup can blanket approve
    	/*document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "BlanketApprovePolicy4");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));*/
    	
    	// on Blanket ApprovePolicy 5, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdVO("pzhang"), "BlanketApprovePolicy5");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
//   	 on Blanket ApprovePolicy 6, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdVO("pzhang"), "BlanketApprovePolicy6");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
//   	 on Blanket ApprovePolicy 7, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdVO("pzhang"), "BlanketApprovePolicy7");
    	document.saveRoutingData();
    	assertTrue(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertTrue(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
//   	 on BlanketApprovePolicy_Override_NONE, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdVO("pzhang"), "BlanketApprovePolicy_Override_NONE");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	
//   	 on BlanketApprovePolicy_Override_ANY, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdVO("pzhang"), "BlanketApprovePolicy_Override_ANY");
    	document.saveRoutingData();
    	assertTrue(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertTrue(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));

//  	 on BlanketApprovePolicy_Override_ANY, BlanketApprovePolicy is not allowed since no elements are defined on any document types in the hierarchy
    	document = new WorkflowDocument (new NetworkIdVO("pzhang"), "BlanketApprovePolicy_NoOverride");
    	document.saveRoutingData();
    	assertFalse(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
    	document = new WorkflowDocument (new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertTrue(document.isActionCodeValidForDocument(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
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
}