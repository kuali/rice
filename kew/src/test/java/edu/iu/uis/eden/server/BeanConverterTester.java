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
package edu.iu.uis.eden.server;


import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routetemplate.TestRuleAttribute;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.BaseWorkgroup;
import edu.iu.uis.eden.workgroup.GroupId;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

public class BeanConverterTester extends KEWTestCase {

    private static final String DOCUMENT_CONTENT = EdenConstants.DOCUMENT_CONTENT_ELEMENT;
    private static final String ATTRIBUTE_CONTENT = EdenConstants.ATTRIBUTE_CONTENT_ELEMENT;
    private static final String SEARCHABLE_CONTENT = EdenConstants.SEARCHABLE_CONTENT_ELEMENT;
    private static final String APPLICATION_CONTENT = EdenConstants.APPLICATION_CONTENT_ELEMENT;

    @Test public void testConvertWorkflowGroupId() {
        BaseWorkgroup prototype = new BaseWorkgroup();
        prototype.setGroupNameId(new GroupNameId("TestWorkgroup"));
        //prototype.setDisplayName("TestWorkgroup");
        //prototype.setWorkgroupType("W");
        //assertNull(prototype.getDisplayName());
        //assertNull(prototype.getWorkgroupType());
        List list = KEWServiceLocator.getWorkgroupService().search(prototype, new HashMap<String, String>(), true);
        assertNotNull(list);
        assertTrue(list.size() > 0);
        Workgroup group = (Workgroup) list.get(0);
        assertNotNull(group);
        WorkflowGroupIdVO vo = new WorkflowGroupIdVO(group.getWorkflowGroupId().getGroupId());
        GroupId id = BeanConverter.convertWorkgroupIdVO(vo);
        assertNotNull(id);
    }

    /**
     * Tests the conversion of a String into a DocumentContentVO object which should split the
     * String into it's 3 distinct components.
     */
    @Test public void testConvertDocumentContent() throws Exception {

        // test null content
        String attributeContent = null;
        String searchableContent = null;
        String applicationContent = null;
        String xmlContent = constructContent(attributeContent, searchableContent, applicationContent);
        DocumentContentVO contentVO = BeanConverter.convertDocumentContent(xmlContent, new Long(-1234));
        assertFalse("Content cannot be empty.", Utilities.isEmpty(contentVO.getFullContent()));
        assertEquals("Attribute content is invalid.", "", contentVO.getAttributeContent());
        assertEquals("Searchable content is invalid.", "", contentVO.getSearchableContent());
        assertEquals("Application content is invalid.", "", contentVO.getApplicationContent());
        assertEquals("Should have fake document id.", new Long(-1234), contentVO.getRouteHeaderId());

        // test empty content
        attributeContent = "";
        searchableContent = "";
        applicationContent = "";
        contentVO = BeanConverter.convertDocumentContent(constructContent(attributeContent, searchableContent, applicationContent), null);
        assertContent(contentVO, attributeContent, searchableContent, applicationContent);

        // test fancy dancy content
        attributeContent = "<iEnjoyFlexContent><id>1234</id></iEnjoyFlexContent>";
        searchableContent = "<thisIdBeWarrenG>Warren G</thisIdBeWarrenG><whatsMyName>Snoop</whatsMyName>";
        applicationContent = "<thisIsTotallyRad><theCoolestContentInTheWorld qualify=\"iSaidSo\">it's marvelous!</theCoolestContentInTheWorld></thisIsTotallyRad>";
        contentVO = BeanConverter.convertDocumentContent(constructContent(attributeContent, searchableContent, applicationContent), null);
        assertContent(contentVO, attributeContent, searchableContent, applicationContent);

        attributeContent = "invalid<xml, I can't believe you would do such a thing<<<";
        try {
            contentVO = BeanConverter.convertDocumentContent(constructContent(attributeContent, searchableContent, applicationContent), null);
            fail("Parsing bad xml should have thrown an InvalidXmlException.");
        } catch (InvalidXmlException e) {
            log.info("Expected InvalidXmlException was thrown.");
            // if we got the exception we are good to go
        }

        // test an older style document
        String appSpecificXml = "<iAmAnOldSchoolApp><myDocContent type=\"custom\">is totally app specific</myDocContent><howIroll>old school, that's how I roll</howIroll></iAmAnOldSchoolApp>";
        contentVO = BeanConverter.convertDocumentContent(appSpecificXml, null);
        assertContent(contentVO, "", "", appSpecificXml);

        // test the old school (Workflow 1.6) flex document XML
        String fleXml = "<flexdoc><meinAttribute>nein</meinAttribute></flexdoc>";
        contentVO = BeanConverter.convertDocumentContent(fleXml, null);
        assertFalse("Content cannot be empty.", Utilities.isEmpty(contentVO.getFullContent()));
        assertEquals("Attribute content is invalid.", fleXml, contentVO.getAttributeContent());
        assertEquals("Searchable content is invalid.", "", contentVO.getSearchableContent());
        assertEquals("Application content is invalid.", "", contentVO.getApplicationContent());
    }

    /**
     * Tests the conversion of a DocumentContentVO object into an XML String.  Includes generating content
     * for any attributes which are on the DocumentContentVO object.
     *
     * TODO there is some crossover between this test and the DocumentContentTest, do we really need both of them???
     */
    @Test public void testBuildUpdatedDocumentContent() throws Exception {
        String startContent = "<"+DOCUMENT_CONTENT+">";
        String endContent = "</"+DOCUMENT_CONTENT+">";

        /*
         * 	// test no content, this should return null which indicates an unchanged document content VO
         * //RouteHeaderVO routeHeaderVO = new RouteHeaderVO();
         */

        // test no content, this should return empty document content
        DocumentContentVO contentVO = new DocumentContentVO();
        //routeHeaderVO.setDocumentContent(contentVO);
        String content = BeanConverter.buildUpdatedDocumentContent(contentVO);
        assertEquals("Invalid content conversion.", EdenConstants.DEFAULT_DOCUMENT_CONTENT, content);

        // test simple case, no attributes
        String attributeContent = "<attribute1><id value=\"3\"/></attribute1>";
        String searchableContent = "<searchable1><data>hello</data></searchable1>";
        contentVO = new DocumentContentVO();
        contentVO.setAttributeContent(constructContent(ATTRIBUTE_CONTENT, attributeContent));
        contentVO.setSearchableContent(constructContent(SEARCHABLE_CONTENT, searchableContent));
        content = BeanConverter.buildUpdatedDocumentContent(contentVO);
        String fullContent = startContent+constructContent(ATTRIBUTE_CONTENT, attributeContent)+constructContent(SEARCHABLE_CONTENT, searchableContent)+endContent;
        assertEquals("Invalid content conversion.", StringUtils.deleteWhitespace(fullContent), StringUtils.deleteWhitespace(content));

        // now, add an attribute
        String testAttributeContent = new TestRuleAttribute().getDocContent();
        WorkflowAttributeDefinitionVO attributeDefinition = new WorkflowAttributeDefinitionVO(TestRuleAttribute.class.getName());
        contentVO.addAttributeDefinition(attributeDefinition);
        content = BeanConverter.buildUpdatedDocumentContent(contentVO);
        fullContent = startContent+
            constructContent(ATTRIBUTE_CONTENT, attributeContent+testAttributeContent)+
            constructContent(SEARCHABLE_CONTENT, searchableContent)+
            endContent;
        assertEquals("Invalid content conversion.", StringUtils.deleteWhitespace(fullContent), StringUtils.deleteWhitespace(content));
    }

    private String constructContent(String type, String content) {
        if (Utilities.isEmpty(content)) {
            return "";
        }
        return "<"+type+">"+content+"</"+type+">";
    }

    private String constructContent(String attributeContent, String searchableContent, String applicationContent) {
        return "<"+DOCUMENT_CONTENT+">"+
            constructContent(ATTRIBUTE_CONTENT, attributeContent)+
            constructContent(SEARCHABLE_CONTENT, searchableContent)+
            constructContent(APPLICATION_CONTENT, applicationContent)+
            "</"+DOCUMENT_CONTENT+">";
    }

    private void assertContent(DocumentContentVO contentVO, String attributeContent, String searchableContent, String applicationContent) {
        if (Utilities.isEmpty(attributeContent)) {
        	attributeContent = "";
        } else {
            attributeContent = "<"+ATTRIBUTE_CONTENT+">"+attributeContent+"</"+ATTRIBUTE_CONTENT+">";
        }
        if (Utilities.isEmpty(searchableContent)) {
        	searchableContent = "";
        } else {
            searchableContent = "<"+SEARCHABLE_CONTENT+">"+searchableContent+"</"+SEARCHABLE_CONTENT+">";
        }
        assertFalse("Content cannot be empty.", Utilities.isEmpty(contentVO.getFullContent()));
        assertEquals("Attribute content is invalid.", attributeContent, contentVO.getAttributeContent());
        assertEquals("Searchable content is invalid.", searchableContent, contentVO.getSearchableContent());
        assertEquals("Application content is invalid.", applicationContent, contentVO.getApplicationContent());
        assertEquals("Incorrect number of attribute definitions.", 0, contentVO.getAttributeDefinitions().length);
        assertEquals("Incorrect number of searchable attribute definitions.", 0, contentVO.getSearchableDefinitions().length);
    }

}
