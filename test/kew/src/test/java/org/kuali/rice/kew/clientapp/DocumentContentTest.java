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
package org.kuali.rice.kew.clientapp;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kew.dto.DocumentContentDTO;

import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.TestRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.custommonkey.xmlunit.XMLAssert;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests that client interaction with document content behaves approriately.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentContentTest extends KEWTestCase {

    private static final String DOCUMENT_CONTENT = KEWConstants.DOCUMENT_CONTENT_ELEMENT;
    private static final String ATTRIBUTE_CONTENT = KEWConstants.ATTRIBUTE_CONTENT_ELEMENT;
    private static final String SEARCHABLE_CONTENT = KEWConstants.SEARCHABLE_CONTENT_ELEMENT;
    private static final String APPLICATION_CONTENT = KEWConstants.APPLICATION_CONTENT_ELEMENT;
    
    @Test public void testDocumentContent() throws Exception {
        String startContent = "<"+DOCUMENT_CONTENT+">";
        String endContent = "</"+DOCUMENT_CONTENT+">";
        String emptyContent1 = startContent+endContent;
        String emptyContent2 = "<"+DOCUMENT_CONTENT+"/>";
        
        WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
        
        // test no content prior to server creation
        assertEquals("Content should be empty.", "", document.getApplicationContent());
        assertEquals("Content should be empty.", "", document.getAttributeContent());
        assertEquals("Content should be empty.", "", document.getDocumentContent().getSearchableContent());
        String fullContent = document.getDocumentContent().getFullContent();
        assertTrue("Invalid content conversion.", fullContent.equals(emptyContent1) || fullContent.equals(emptyContent2));
        
        // test content after server creation
        document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
        // this will create the document on the server
        document.saveRoutingData();
        assertNotNull(document.getDocumentId());
        // the document id on the document content should be there now
        assertEquals("Incorrect document id.", document.getDocumentId(), document.getDocumentContent().getDocumentId());
        assertEquals("Content should be empty.", "", document.getApplicationContent());
        assertEquals("Content should be empty.", "", document.getAttributeContent());
        assertEquals("Content should be empty.", "", document.getDocumentContent().getSearchableContent());
        fullContent = document.getDocumentContent().getFullContent();
        assertTrue("Invalid content conversion.", fullContent.equals(emptyContent1) || fullContent.equals(emptyContent2));
        // verify the content on the actual document stored in the database
        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getDocumentId());
        assertTrue("Invalid initial content.", routeHeader.getDocContent().equals(emptyContent1) || routeHeader.getDocContent().equals(emptyContent2));
        
        // test simple case, no attributes
        document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
        String attributeContent = "<attribute1><id value=\"3\"/></attribute1>";
        String searchableContent = "<searchable1><data>hello</data></searchable1>";
        DocumentContentDTO contentVO = document.getDocumentContent();
        contentVO.setAttributeContent(constructContent(ATTRIBUTE_CONTENT, attributeContent));
        contentVO.setSearchableContent(constructContent(SEARCHABLE_CONTENT, searchableContent));
        document.saveRoutingData();
        // now reload the document
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
        String expectedContent = startContent+constructContent(ATTRIBUTE_CONTENT, attributeContent)+constructContent(SEARCHABLE_CONTENT, searchableContent)+endContent;
        fullContent = document.getDocumentContent().getFullContent();
        assertEquals("Invalid content conversion.", StringUtils.deleteWhitespace(expectedContent), StringUtils.deleteWhitespace(fullContent));
        
        // now, add an attribute and then clear it, document content should remain the same
        String testAttributeContent = new TestRuleAttribute().getDocContent();
        WorkflowAttributeDefinitionDTO attributeDefinition = new WorkflowAttributeDefinitionDTO(TestRuleAttribute.class.getName());
        document.addAttributeDefinition(attributeDefinition);
        document.clearAttributeDefinitions();
        document.saveRoutingData();
        fullContent = document.getDocumentContent().getFullContent();
        assertEquals("Invalid content conversion.", StringUtils.deleteWhitespace(expectedContent), StringUtils.deleteWhitespace(fullContent));
        
        // now really add an attribute and save the content
        document.addAttributeDefinition(attributeDefinition);
        document.saveRoutingData();
        fullContent = document.getDocumentContent().getFullContent();
        expectedContent = startContent+
            constructContent(ATTRIBUTE_CONTENT, attributeContent+testAttributeContent)+
            constructContent(SEARCHABLE_CONTENT, searchableContent)+
            endContent;
        assertEquals("Invalid content conversion.", StringUtils.deleteWhitespace(expectedContent), StringUtils.deleteWhitespace(fullContent));

        // let's reload the document and try appending a couple of attributes for good measure, this will test appending to existing content on non-materialized document content
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
        document.addAttributeDefinition(attributeDefinition);
        document.addAttributeDefinition(attributeDefinition);
        document.saveRoutingData();
        fullContent = document.getDocumentContent().getFullContent();
        expectedContent = startContent+
            constructContent(ATTRIBUTE_CONTENT, attributeContent+testAttributeContent+testAttributeContent+testAttributeContent)+
            constructContent(SEARCHABLE_CONTENT, searchableContent)+
            endContent;
        assertEquals("Invalid content conversion.", StringUtils.deleteWhitespace(expectedContent), StringUtils.deleteWhitespace(fullContent));
        
        // now let's try clearing the attribute content
        document.clearAttributeContent();
        expectedContent = startContent+constructContent(SEARCHABLE_CONTENT, searchableContent)+endContent;
        fullContent = document.getDocumentContent().getFullContent();
        assertEquals("Invalid content conversion.", StringUtils.deleteWhitespace(expectedContent), StringUtils.deleteWhitespace(fullContent));
        // now save it and make sure it comes back from the server the same way
        document.saveRoutingData();
        fullContent = document.getDocumentContent().getFullContent();
        assertEquals("Invalid content conversion.", StringUtils.deleteWhitespace(expectedContent), StringUtils.deleteWhitespace(fullContent));
        
        // Test backward compatibility with old-school document content, this document content could look something
        // like <myRadContent>abcd</myRadContent>, when converted to the new form, it should come out like
        // <documentContent><applicationContent><myRadContent>abcd</myRadContent></applicationContent></documentContent>
        String myRadContent = "<myRadContent>abcd</myRadContent>";
        document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
        DocumentRouteHeaderValue documentValue = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getDocumentId());
        documentValue.setDocContent(myRadContent);
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(documentValue);
        // reload the client document and check that the XML has been auto-magically converted
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
        String expected = startContent+constructContent(APPLICATION_CONTENT, myRadContent)+endContent;
        fullContent = document.getDocumentContent().getFullContent();
        assertEquals("Backward compatibility failure.", StringUtils.deleteWhitespace(expected), StringUtils.deleteWhitespace(fullContent));
    }
    
    private String constructContent(String type, String content) {
        if (content == null) {
            return "";
        }
        return "<"+type+">"+content+"</"+type+">";
    }
    
    /**
     * Tests that the lazy loading of document content is functioning properly.
     */
    @Test public void testLazyContentLoading() throws Exception {
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
        assertFalse("Content should not be loaded yet.", isContentLoaded(document));
        
        // save the document, the content should still not be loaded
        document.setTitle("Test Title");
        document.saveRoutingData();
        assertFalse("Content should not be loaded yet.", isContentLoaded(document));
        
        // now get the document content, this should result in the content being loaded
        DocumentContentDTO content = document.getDocumentContent();
        assertNotNull("Content should be non-null.", content);
        assertTrue("Content should now be loaded.", isContentLoaded(document));
        
        // create a new document, try saving it, and make sure the content has not been loaded
        document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
        document.saveDocument("");
        assertFalse("Content should not be loaded yet.", isContentLoaded(document));
        
        // set some content on the document
        String applicationContent = "<myTestContent/>";
        document.setApplicationContent(applicationContent);
        assertTrue("Content should now be loaded.", isContentLoaded(document));
        document.saveRoutingData();
        
        // reload the document
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
        assertFalse("Content should not be loaded yet.", isContentLoaded(document));

        XMLAssert.assertXMLEqual("Invalid application content", applicationContent, document.getApplicationContent());
        assertTrue("Content should now be loaded.", isContentLoaded(document));
        
    }
    
    private boolean isContentLoaded(WorkflowDocument document) throws Exception {
    	Field contentField = document.getClass().getDeclaredField("documentContent");
    	contentField.setAccessible(true);
    	return contentField.get(document) != null;
    }
    
    /**
     * Tests that document content is reloaded from the database after every call (such as Approve, etc.)
     * so as to verify that the document content stored on the WorkflowDocument will not go stale in between
     * calls.
     */
    @Test public void testDocumentContentConsistency() throws Exception {
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
    	String appContent = "<app>content</app>";
    	document.setApplicationContent(appContent);
    	document.saveRoutingData();
        XMLAssert.assertXMLEqual(appContent, document.getApplicationContent());
    	
    	// load the document and modify the content
    	WorkflowDocument document2 = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
    	XMLAssert.assertXMLEqual(appContent, document2.getApplicationContent());
    	String appContent2 = "<app>content2</app>";
    	document2.setApplicationContent(appContent2);
    	XMLAssert.assertXMLEqual(appContent2, document2.getApplicationContent());
    	document2.saveRoutingData();
    	
    	// the original document should not notice these changes yet
    	XMLAssert.assertXMLEqual(appContent, document.getApplicationContent());
    	// but if we saveRoutingData, we should see the new value
    	document.saveRoutingData();
    	XMLAssert.assertXMLEqual(appContent2, document.getApplicationContent());
    	
    	// also verify that just setting the content, but not saving it, doesn't get persisted
    	document2.setApplicationContent("<bad>content</bad>");
    	document2 = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document2.getDocumentId());
    	XMLAssert.assertXMLEqual(appContent2, document.getApplicationContent());
    }
    
    /**
     * Tests modification of the DocumentContentVO object directly.
     */
    @Test public void testManualDocumentContentModification() throws Exception {
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
    	document.saveRoutingData();
    	
    	// fetch it from WorkflowInfo
    	DocumentContentDTO content = new WorkflowInfo().getDocumentContent(document.getDocumentId());
    	assertTrue("Should contain default content, was " + content.getFullContent(), KEWConstants.DEFAULT_DOCUMENT_CONTENT.equals(content.getFullContent()) ||
    			KEWConstants.DEFAULT_DOCUMENT_CONTENT2.equals(content.getFullContent()));
    	
    	String appContent = "<abcdefg>hijklm n o p</abcdefg>";
    	content.setApplicationContent(appContent);
    	assertFalse(isContentLoaded(document));
    	document.saveDocumentContent(content);
    	assertTrue(isContentLoaded(document));
    	
    	// test that the content on the document is the same as the content we just set
    	XMLAssert.assertXMLEqual(appContent, document.getApplicationContent());
    	
    	// fetch the document fresh and make sure the content is correct
    	document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
    	XMLAssert.assertXMLEqual(appContent, document.getApplicationContent());
    	
    }
}
