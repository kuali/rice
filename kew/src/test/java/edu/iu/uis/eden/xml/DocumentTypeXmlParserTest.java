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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.clientapp.WorkflowDocument;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.exception.InvalidWorkgroupException;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.xml.DocumentTypeXmlParser;
import org.kuali.workflow.test.KEWTestCase;


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
    	assertEquals("Should be WorkflowAdmin reporting workgroup", "WorkflowAdmin", documentType1.getReportingWorkgroup().getGroupNameId().getNameId());
    		
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

}