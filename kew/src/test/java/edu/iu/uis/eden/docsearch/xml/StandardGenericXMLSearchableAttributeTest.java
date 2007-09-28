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
package edu.iu.uis.eden.docsearch.xml;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.docsearch.DocSearchCriteriaVO;
import edu.iu.uis.eden.docsearch.DocSearchUtils;
import edu.iu.uis.eden.docsearch.DocumentSearchResult;
import edu.iu.uis.eden.docsearch.DocumentSearchResultComponents;
import edu.iu.uis.eden.docsearch.DocumentSearchService;
import edu.iu.uis.eden.docsearch.SearchAttributeCriteriaComponent;
import edu.iu.uis.eden.docsearch.SearchableAttribute;
import edu.iu.uis.eden.docsearch.SearchableAttributeDateTimeValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeFloatValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeLongValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeStringValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeValue;
import edu.iu.uis.eden.docsearch.TestXMLSearchableAttributeDateTime;
import edu.iu.uis.eden.docsearch.TestXMLSearchableAttributeFloat;
import edu.iu.uis.eden.docsearch.TestXMLSearchableAttributeLong;
import edu.iu.uis.eden.docsearch.TestXMLSearchableAttributeString;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.KeyValueSort;

/**
 * Tests the StandardGenericXMLSearchableAttribute.
 *
 * KULWF-654: Tests the resolution to this issue by configuring a CustomActionListAttribute as well as a
 * searchable attribute.
 */
public class StandardGenericXMLSearchableAttributeTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("XmlConfig.xml");
    }

    private StandardGenericXMLSearchableAttribute getAttribute(String name) {
        String attName = name;
        if (attName == null) {
            attName = "XMLSearchableAttribute";
        }
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(attName);
        StandardGenericXMLSearchableAttribute attribute = new StandardGenericXMLSearchableAttribute();
        attribute.setRuleAttribute(ruleAttribute);
        return attribute;
    }

    private SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String value,DocumentType docType) {
    	String formKey = key;
    	String savedKey = key;
    	SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(formKey,value,savedKey);
    	Field field = getFieldByFormKey(docType, formKey);
    	if (field != null) {
        	sacc.setSearchableAttributeValue(DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType()));
        	sacc.setRangeSearch(field.isMemberOfRange());
        	sacc.setAllowWildcards(field.isAllowingWildcards());
        	sacc.setAutoWildcardBeginning(field.isAutoWildcardAtBeginning());
        	sacc.setAutoWildcardEnd(field.isAutoWildcardAtEnding());
        	sacc.setCaseSensitive(field.isCaseSensitive());
        	sacc.setSearchInclusive(field.isInclusive());
            sacc.setSearchable(field.isSearchable());
            sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
    	}
    	return sacc;
    }

    private Field getFieldByFormKey(DocumentType docType, String formKey) {
    	if (docType == null) {
    		return null;
    	}
		for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
			for (Row row : searchableAttribute.getSearchingRows()) {
				for (Field field : row.getFields()) {
					if (field.getPropertyName().equals(formKey)) {
						return field;
					}
				}
			}
		}
		return null;
    }

    @Test public void testXMLStandardSearchableAttributeWithInvalidValue() throws Exception {
        String documentTypeName = "SearchDocTypeStandardSearchDataType";
//        DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName);

        /*
         *   Below we are using the keys and values from the custom searchable attribute classes' static constants but
         *   this is only for convenience as those should always be valid values to test for.
         */
        // adding string value in what should be a long searchable attribute
        WorkflowAttributeDefinitionVO longXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStdLong");
        longXMLDef.addProperty(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "123x23");
        workflowDocument.addSearchableDefinition(longXMLDef);

        workflowDocument.setTitle("Routing style");
        try {
            workflowDocument.routeDocument("routing this document.");
            fail("Document should be unroutable with invalid searchable attribute value");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test public void testXMLStandardSearchableAttributesWithDataType() throws Exception {
        String documentTypeName = "SearchDocTypeStandardSearchDataType";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName);

        /*
         *   Below we are using the keys and values from the custom searchable attribute classes' static constants but
         *   this is only for convenience as those should always be valid values to test for.
         */
        int i = 0;
        // adding string searchable attribute
        i++;
        WorkflowAttributeDefinitionVO stringXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");
        stringXMLDef.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workflowDocument.addSearchableDefinition(stringXMLDef);
        // adding long searchable attribute
        i++;
        WorkflowAttributeDefinitionVO longXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStdLong");
        longXMLDef.addProperty(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(longXMLDef);
        // adding float searchable attribute
        i++;
        WorkflowAttributeDefinitionVO floatXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStdFloat");
        floatXMLDef.addProperty(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(floatXMLDef);
        // adding string searchable attribute
        i++;
        WorkflowAttributeDefinitionVO dateXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStdDateTime");
        dateXMLDef.addProperty(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocSearchUtils.getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE));
        workflowDocument.addSearchableDefinition(dateXMLDef);

        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        workflowDocument = new WorkflowDocument(new NetworkIdVO(userNetworkId), workflowDocument.getRouteHeaderId());
        DocumentRouteHeaderValue doc = KEWServiceLocator.getRouteHeaderService().getRouteHeader(workflowDocument.getRouteHeaderId());
        assertEquals("Wrong number of searchable attributes", i, doc.getSearchableAttributeValues().size());
        for (Iterator iter = doc.getSearchableAttributeValues().iterator(); iter.hasNext();) {
            SearchableAttributeValue attributeValue = (SearchableAttributeValue) iter.next();
            if (attributeValue instanceof SearchableAttributeStringValue) {
                SearchableAttributeStringValue realValue = (SearchableAttributeStringValue) attributeValue;
                assertEquals("The only String attribute that should have been added has key '" + TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY + "'", TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, realValue.getSearchableAttributeKey());
                assertEquals("The only String attribute that should have been added has value '" + TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE + "'", TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, realValue.getSearchableAttributeValue());
            } else if (attributeValue instanceof SearchableAttributeLongValue) {
                SearchableAttributeLongValue realValue = (SearchableAttributeLongValue) attributeValue;
                assertEquals("The only Long attribute that should have been added has key '" + TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY + "'", TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, realValue.getSearchableAttributeKey());
                assertEquals("The only Long attribute that should have been added has value '" + TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE + "'", TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE, realValue.getSearchableAttributeValue());
            } else if (attributeValue instanceof SearchableAttributeFloatValue) {
                SearchableAttributeFloatValue realValue = (SearchableAttributeFloatValue) attributeValue;
                assertEquals("The only Float attribute that should have been added has key '" + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY + "'", TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, realValue.getSearchableAttributeKey());
                assertEquals("The only Float attribute that should have been added has value '" + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY + "'", TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE, realValue.getSearchableAttributeValue());
            } else if (attributeValue instanceof SearchableAttributeDateTimeValue) {
                SearchableAttributeDateTimeValue realValue = (SearchableAttributeDateTimeValue) attributeValue;
                assertEquals("The only DateTime attribute that should have been added has key '" + TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY + "'", TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, realValue.getSearchableAttributeKey());
                Calendar testDate = Calendar.getInstance();
                testDate.setTimeInMillis(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE_IN_MILLS);
                testDate.set(Calendar.SECOND, 0);
                testDate.set(Calendar.MILLISECOND, 0);
                Calendar attributeDate = Calendar.getInstance();
                attributeDate.setTimeInMillis(realValue.getSearchableAttributeValue().getTime());
                attributeDate.set(Calendar.SECOND, 0);
                attributeDate.set(Calendar.MILLISECOND, 0);
                assertEquals("The month value for the searchable attribute is wrong",testDate.get(Calendar.MONTH),attributeDate.get(Calendar.MONTH));
                assertEquals("The date value for the searchable attribute is wrong",testDate.get(Calendar.DATE),attributeDate.get(Calendar.DATE));
                assertEquals("The year value for the searchable attribute is wrong",testDate.get(Calendar.YEAR),attributeDate.get(Calendar.YEAR));
            } else {
                fail("Searchable Attribute Value base class should be one of the four checked always");
            }
        }

        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId(userNetworkId));

        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        List searchResults = result.getSearchResults();

        assertEquals("Search results should have one document.", 1, searchResults.size());

        DocSearchCriteriaVO criteria2 = new DocSearchCriteriaVO();
        criteria2.setDocTypeFullName(documentTypeName);
        criteria2.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "fred", docType));
        DocumentSearchResultComponents result2 = docSearchService.getList(user, criteria2);
        List searchResults2 = result2.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults2.size());

        DocSearchCriteriaVO criteria3 = new DocSearchCriteriaVO();
        criteria3.setDocTypeFullName(documentTypeName);
        criteria3.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeproperty", "doesntexist", docType));
        DocumentSearchResultComponents result3 = null;
        try {
            result3 = docSearchService.getList(user, criteria3);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException e) {}

        criteria = null;
        searchResults = null;
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString(), docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria2 = null;
        searchResults2 = null;
        criteria2 = new DocSearchCriteriaVO();
        criteria2.setDocTypeFullName(documentTypeName);
        criteria2.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "1111111", docType));
        result2 = docSearchService.getList(user, criteria2);
        searchResults2 = result2.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults2.size());

        criteria3 = null;
        result3 = null;
        criteria3 = new DocSearchCriteriaVO();
        criteria3.setDocTypeFullName(documentTypeName);
        criteria3.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeymcfakefake", "99999999", docType));
        try {
            result3 = docSearchService.getList(user, criteria3);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException e) {}

        criteria = null;
        searchResults = null;
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString(), docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria2 = null;
        searchResults2 = null;
        criteria2 = new DocSearchCriteriaVO();
        criteria2.setDocTypeFullName(documentTypeName);
        criteria2.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, "215.3548", docType));
        result2 = docSearchService.getList(user, criteria2);
        searchResults2 = result2.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults2.size());

        criteria3 = null;
        result3 = null;
        criteria3 = new DocSearchCriteriaVO();
        criteria3.setDocTypeFullName(documentTypeName);
        criteria3.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeylostington", "9999.9999", docType));
        try {
            result3 = docSearchService.getList(user, criteria3);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException e) {}

        criteria = null;
        searchResults = null;
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocSearchUtils.getDisplayValueWithDateOnly(new Timestamp(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE_IN_MILLS)), docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria2 = null;
        searchResults2 = null;
        criteria2 = new DocSearchCriteriaVO();
        criteria2.setDocTypeFullName(documentTypeName);
        criteria2.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "07/06/1979", docType));
        result2 = docSearchService.getList(user, criteria2);
        searchResults2 = result2.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults2.size());

        criteria3 = null;
        result3 = null;
        criteria3 = new DocSearchCriteriaVO();
        criteria3.setDocTypeFullName(documentTypeName);
        criteria3.addSearchableAttribute(createSearchAttributeCriteriaComponent("lastingsfakerson", "07/06/2007", docType));
        try {
            result3 = docSearchService.getList(user, criteria3);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException e) {}
    }

    @Test public void testRouteDocumentWithSearchableAttribute() throws Exception {
    	String documentTypeName = "SearchDocType";
    	String key = "givenname";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), documentTypeName);
        WorkflowAttributeDefinitionVO givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");

        workflowDocument.setApplicationContent("<test></test>");

        givennameXMLDef.addProperty(key, "jack");
        workflowDocument.addSearchableDefinition(givennameXMLDef);

        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("rkirkend"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jack", docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        List searchResults = result.getSearchResults();

        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = null;
        searchResults = null;
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "fred", docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = null;
        searchResults = null;
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeproperty", "doesntexist", docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException wsee) {}
    }

    @Test public void testDocumentSearchAttributeWildcarding() throws Exception {
        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);

    	String documentTypeName = "SearchDocType";
    	String key = "givenname";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), documentTypeName);
        WorkflowAttributeDefinitionVO givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");

        workflowDocument.setApplicationContent("<test></test>");

        givennameXMLDef.addProperty(key, "jack");
        workflowDocument.addSearchableDefinition(givennameXMLDef);

        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("rkirkend"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jack", docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        List searchResults = result.getSearchResults();

        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "ja*", docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "ja", docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should have one document.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "ack", docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should have one document.", 1, searchResults.size());
    }

    @Test public void testDocumentSearchAttributeWildcardingDisallow() throws Exception {
        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);

        String documentTypeName = "SearchDocTypeStandardSearchDataType";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName);

        /*
         *   Below we are using the keys and values from the custom searchable attribute classes' static constants but
         *   this is only for convenience as those should always be valid values to test for.
         */
        WorkflowAttributeDefinitionVO longXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStdLong");
        longXMLDef.addProperty(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(longXMLDef);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId(userNetworkId));

        String validSearchValue = TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString();
        DocSearchCriteriaVO criteria = null;
        List searchResults = null;
        DocumentSearchResultComponents result = null;
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, validSearchValue, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = null;
        searchResults = null;
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "*" + validSearchValue.substring(2), docType));

        if ((new SearchableAttributeLongValue()).allowsWildcards()) {
            result = docSearchService.getList(user, criteria);
            searchResults = result.getSearchResults();
            assertEquals("Search results should be empty using wildcard '*' value.", 0, searchResults.size());
        } else {
            try {
                result = docSearchService.getList(user, criteria);
                searchResults = result.getSearchResults();
                fail("Search results should be throwing a validation exception for use of the character '*' without allowing wildcards");
            } catch (WorkflowServiceErrorException wsee) {}
        }

        criteria = null;
        searchResults = null;
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, validSearchValue.substring(0, (validSearchValue.length() - 2)), docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty trying to use assumed ending wildcard.", 0, searchResults.size());
    }

    @Test public void testDocumentSearchAttributeCaseSensitivity() throws Exception {
        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    	String documentTypeName = "SearchDocTypeCaseSensitivity";
    	String networkId = "rkirkend";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);

    	String key = "givenname";
    	WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO(networkId), documentTypeName);
        WorkflowAttributeDefinitionVO givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");
        givennameXMLDef.addProperty(key, "jack");
        workflowDocument.addSearchableDefinition(givennameXMLDef);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId(networkId));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jack", docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "JACK", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 0, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jAck", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 0, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jacK", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 0, result.getSearchResults().size());

    	key = "givenname_nocase";
        workflowDocument = new WorkflowDocument(new NetworkIdVO(networkId), documentTypeName);
        WorkflowAttributeDefinitionVO givenname_nocaseXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute_CaseInsensitive");
        givenname_nocaseXMLDef.addProperty(key, "jaCk");
        workflowDocument.addSearchableDefinition(givenname_nocaseXMLDef);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jack", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "JACK", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jaCk", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jacK", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jAc", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 0, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jA*", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "aCk", docType));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());
    }

    /**
     * Tests searching with client-generated documentContent which is just malformed XML.
     * @throws WorkflowException
     */
    @Test public void testRouteDocumentWithMalformedSearchableAttributeContent() throws WorkflowException {
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), "SearchDocType");

        workflowDocument.setApplicationContent("hey, <I'm Not ] Even & XML");

        workflowDocument.setTitle("Routing style");
        try {
            workflowDocument.routeDocument("routing this document.");
            fail("routeDocument succeeded with malformed XML");
        } catch (WorkflowException we) {
            // An exception is thrown in BeanConverter/XmlUtils.appendXml at the time of this writing
            // so I will just assume that is the expected behavior
        }
    }

    /**
     * Tests searching with client-generated documentContent which will not match what the SearchableAttribute
     * is configured to look for.  This should pass with zero search results, but should not throw an exception.
     * @throws Exception
     */
    @Test public void testRouteDocumentWithInvalidSearchableAttributeContent() throws Exception {
    	String documentTypeName = "SearchDocType";
    	String key = "givenname";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), documentTypeName);

        workflowDocument.setApplicationContent("<documentContent><searchableContent><garbage>" +
                                               "<blah>not going to match anything</blah>" +
                                               "</garbage></searchableContent></documentContent>");

        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("rkirkend"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jack", docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        List searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "fred", docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeproperty", "doesntexist", docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException wsee) {}
    }

    /**
     * Tests searching with client-generated documentContent which will not match what the SearchableAttribute
     * is configured to look for.  This should pass with zero search results, but should not throw an exception.
     * @throws Exception
     */
    @Test public void testRouteDocumentWithMoreInvalidSearchableAttributeContent() throws Exception {
    	String documentTypeName = "SearchDocType";
    	String key = "givenname";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), documentTypeName);

        workflowDocument.setApplicationContent("<documentContent><NOTsearchableContent><garbage>" +
                                               "<blah>not going to match anything</blah>" +
                                               "</garbage></NOTsearchableContent></documentContent>");

        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("rkirkend"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jack", docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        List searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "fred", docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeproperty", "doesntexist", docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException wsee) {}
    }

    @Test public void testAppendingSeachContentWithSearchableAttribute() throws Exception {
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), "SearchDocType");
        WorkflowAttributeDefinitionVO givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");

        givennameXMLDef.addProperty("givenname", "jack");
        workflowDocument.addSearchableDefinition(givennameXMLDef);

        workflowDocument.setTitle("Routing Rowdy Piper");
        workflowDocument.routeDocument("routing this document.");

        // look up document and verify the search valu is there
        DocumentRouteHeaderValue doc = KEWServiceLocator.getRouteHeaderService().getRouteHeader(workflowDocument.getRouteHeaderId());
        assertEquals("Wrong number of searchable attributes", 1, doc.getSearchableAttributeValues().size());

        workflowDocument = new WorkflowDocument(new NetworkIdVO("jhopf"), workflowDocument.getRouteHeaderId());
        givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");

        givennameXMLDef.addProperty("givenname", "jill");
        workflowDocument.addSearchableDefinition(givennameXMLDef);
        workflowDocument.approve("");

        doc = KEWServiceLocator.getRouteHeaderService().getRouteHeader(workflowDocument.getRouteHeaderId());
        assertEquals("Wrong number of searchable attributes", 2, doc.getSearchableAttributeValues().size());

        // check for jack and jill
        boolean foundJack = false;
        boolean foundJill = false;
        for (Iterator iter = doc.getSearchableAttributeValues().iterator(); iter.hasNext();) {
            SearchableAttributeValue searchableValue = (SearchableAttributeValue) iter.next();
            if (searchableValue.getSearchableAttributeDisplayValue().equals("jack")) {
                foundJack = true;
            } else if (searchableValue.getSearchableAttributeDisplayValue().equals("jill")) {
                foundJill = true;
            }
        }

        assertTrue("Didn't find searchable attribute value 'jack'", foundJack);
        assertTrue("Didn't find searchable attribute value 'jill'", foundJill);
    }

    @Test public void testNoSearchableContentAction() throws Exception {
    	RouteHeaderService routeHeaderService = KEWServiceLocator.getRouteHeaderService();
    	WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), "SearchDocType");
    	workflowDocument.saveDocument("");

    	//verify that a searchable attribute exists even though
    	DocumentRouteHeaderValue document = routeHeaderService.getRouteHeader(workflowDocument.getRouteHeaderId());
    	assertEquals("Should have a searchable attribute key", 1, document.getSearchableAttributeValues().size());
    	assertEquals("Searchable attribute key should be givenname", "givenname", ((SearchableAttributeValue)document.getSearchableAttributeValues().get(0)).getSearchableAttributeKey());


    	WorkflowDocument workflowDocument2 = new WorkflowDocument(new NetworkIdVO("rkirkend"), "SearchDocType2");
    	workflowDocument2.saveDocument("");

    	DocumentRouteHeaderValue document2 = routeHeaderService.getRouteHeader(workflowDocument2.getRouteHeaderId());
    	assertEquals("Should have a searchable attribute key", 1, document2.getSearchableAttributeValues().size());
    	assertEquals("Searchable attribute key should be givenname", "givenname", ((SearchableAttributeValue)document2.getSearchableAttributeValues().get(0)).getSearchableAttributeKey());
    }

//    protected String getAltAppContextFile() {
//    	if (this.getName().equals("testIndexingRequeue")) {
//    		return "edu/iu/uis/eden/docsearch/xml/SearchableAttributeProcessorSpring.xml";
//    	} else {
//    		return null;
//    	}
//
//    }

    //this is testing that the code being called in the event of an OptimisticLockException happening in the
    //SearchableAttributeProcessor is working.  An actual OptimisticLockException is not being created because it's
    //too much of a pain in this single threaded testing env.
//    public void testIndexingRequeue() throws Exception {
//    	Long routeHeaderId = new Long(1);
//
//        PersistedMessage searchIndexingWork = new PersistedMessage();
//        searchIndexingWork.setProcessorClassName(SearchableAttributeProcessor.class.getName());
//        searchIndexingWork.setRouteHeaderId(routeHeaderId);
//        searchIndexingWork.setQueuePriority(new Integer(6));
//        searchIndexingWork.setQueueStatus("Q");
//        searchIndexingWork.setQueueDate(new Timestamp(System.currentTimeMillis()));
//        SpringServiceLocator.getRouteQueueService().save(searchIndexingWork);
//
//        new SearchableAttributeProcessor().requeueIndexing(searchIndexingWork);
//
//        Collection queueEntries = SpringServiceLocator.getRouteQueueService().findAll();
//        assertEquals("should have 2 queue entries", 2, queueEntries.size());
//        for (Iterator iter = queueEntries.iterator(); iter.hasNext();) {
//			PersistedMessage queueEntry = (PersistedMessage) iter.next();
//			//all entries should have certain similarities
//			assertEquals("Wrong routeHeaderid", searchIndexingWork.getRouteHeaderId(), queueEntry.getRouteHeaderId());
//			assertEquals("Wrong queue status", searchIndexingWork.getQueueStatus(), queueEntry.getQueueStatus());
//			assertEquals("Wrong queue priority", searchIndexingWork.getQueuePriority(), queueEntry.getQueuePriority());
//		}
//    }

    @Test public void testClearingSearchContentWithSearchableAttribute() throws Exception {
    	RouteHeaderService routeHeaderService = KEWServiceLocator.getRouteHeaderService();

    	WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), "SearchDocType");
        WorkflowAttributeDefinitionVO givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");

        givennameXMLDef.addProperty("givenname", "jack");
        workflowDocument.addSearchableDefinition(givennameXMLDef);

        workflowDocument.setTitle("Routing Rowdy Piper");
        workflowDocument.routeDocument("routing this document.");

        // look up document and verify the search valu is there
        DocumentRouteHeaderValue doc = routeHeaderService.getRouteHeader(workflowDocument.getRouteHeaderId());
        assertEquals("Wrong number of searchable attributes", 1, doc.getSearchableAttributeValues().size());

        workflowDocument = new WorkflowDocument(new NetworkIdVO("jhopf"), workflowDocument.getRouteHeaderId());
        workflowDocument.clearSearchableContent();

        givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");
        givennameXMLDef.addProperty("givenname", "dukeboys");
        workflowDocument.addSearchableDefinition(givennameXMLDef);

        givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");
        givennameXMLDef.addProperty("givenname", "luke duke");
        workflowDocument.addSearchableDefinition(givennameXMLDef);

        givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttribute");
        givennameXMLDef.addProperty("givenname", "bo duke");
        workflowDocument.addSearchableDefinition(givennameXMLDef);

        workflowDocument.approve("");

        doc = routeHeaderService.getRouteHeader(workflowDocument.getRouteHeaderId());
        assertEquals("Wrong number of searchable attributes", 3, doc.getSearchableAttributeValues().size());

        // check for jack and jill
        boolean foundLuke = false;
        boolean foundBo = false;
        boolean foundDukeBoys = false;
        for (Iterator iter = doc.getSearchableAttributeValues().iterator(); iter.hasNext();) {
            SearchableAttributeValue searchableValue = (SearchableAttributeValue) iter.next();
            if (searchableValue.getSearchableAttributeDisplayValue().equals("dukeboys")) {
                foundDukeBoys = true;
            } else if (searchableValue.getSearchableAttributeDisplayValue().equals("luke duke")) {
                foundLuke = true;
            } else if (searchableValue.getSearchableAttributeDisplayValue().equals("bo duke")) {
                foundBo = true;
            }
        }

        assertTrue("Didn't find searchable attribute value 'luke duke'", foundLuke);
        assertTrue("Didn't find searchable attribute value 'bo duke'", foundBo);
        assertTrue("Didn't find searchable attribute value 'dukeboys'", foundDukeBoys);
    }

    /*
     * Test method for 'edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute.getSearchContent()'
     */
    @Test public void testGetSearchContent() throws Exception {
        StandardGenericXMLSearchableAttribute attribute = getAttribute("XMLSearchableAttribute");
        String keyName = "givenname";
        String value = "jack";
        Map paramMap = new HashMap();
        paramMap.put(keyName, value);
        attribute.setParamMap(paramMap);
        String searchContent = attribute.getSearchContent();
        assertTrue("searchContent was not found.", searchContent != null && searchContent.length() > 0);
        XPath xpath = XPathFactory.newInstance().newXPath();
        Element foundDocContent = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(searchContent)))).getDocumentElement();
        String findStuff = "//putWhateverWordsIwantInsideThisTag/" + keyName + "/value";
        assertTrue("Search content does not contain correct value for field '" + keyName + "'.", value.equals(xpath.evaluate(findStuff, foundDocContent, XPathConstants.STRING)));

        attribute = getAttribute("XMLSearchableAttributeStdLong");
        keyName = "testLongKey";
        value = "123458";
        paramMap = new HashMap();
        paramMap.put(keyName, value);
        attribute.setParamMap(paramMap);
        searchContent = attribute.getSearchContent();
        assertTrue("searchContent was not found.", searchContent != null && searchContent.length() > 0);
        xpath = XPathFactory.newInstance().newXPath();
        foundDocContent = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(searchContent)))).getDocumentElement();
        findStuff = "//putWhateverWordsIwantInsideThisTag/" + keyName + "/value";
        assertTrue("Search content does not contain correct value for field '" + keyName + "'.", value.equals(xpath.evaluate(findStuff, foundDocContent, XPathConstants.STRING)));

        attribute = getAttribute("XMLSearchableAttributeStdFloat");
        keyName = "testFloatKey";
        value = "2568.204";
        paramMap = new HashMap();
        paramMap.put(keyName, value);
        attribute.setParamMap(paramMap);
        searchContent = attribute.getSearchContent();
        assertTrue("searchContent was not found.", searchContent != null && searchContent.length() > 0);
        xpath = XPathFactory.newInstance().newXPath();
        foundDocContent = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(searchContent)))).getDocumentElement();
        findStuff = "//putWhateverWordsIwantInsideThisTag/" + keyName + "/value";
        assertTrue("Search content does not contain correct value for field '" + keyName + "'.", value.equals(xpath.evaluate(findStuff, foundDocContent, XPathConstants.STRING)));

        attribute = getAttribute("XMLSearchableAttributeStdCurrency");
        keyName = "testCurrencyKey";
        value = "2248.20";
        paramMap = new HashMap();
        paramMap.put(keyName, value);
        attribute.setParamMap(paramMap);
        searchContent = attribute.getSearchContent();
        assertTrue("searchContent was not found.", searchContent != null && searchContent.length() > 0);
        xpath = XPathFactory.newInstance().newXPath();
        foundDocContent = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(searchContent)))).getDocumentElement();
        findStuff = "//putWhateverWordsIwantInsideThisTag/" + keyName + "/value";
        assertTrue("Search content does not contain correct value for field '" + keyName + "'.", value.equals(xpath.evaluate(findStuff, foundDocContent, XPathConstants.STRING)));

        attribute = getAttribute("XMLSearchableAttributeStdDateTime");
        keyName = "testDateTimeKey";
        value = DocSearchUtils.getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE);
        paramMap = new HashMap();
        paramMap.put(keyName, value);
        attribute.setParamMap(paramMap);
        searchContent = attribute.getSearchContent();
        assertTrue("searchContent was not found.", searchContent != null && searchContent.length() > 0);
        xpath = XPathFactory.newInstance().newXPath();
        foundDocContent = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(searchContent)))).getDocumentElement();
        findStuff = "//putWhateverWordsIwantInsideThisTag/" + keyName + "/value";
        assertTrue("Search content does not contain correct value for field '" + keyName + "'.", value.equals(xpath.evaluate(findStuff, foundDocContent, XPathConstants.STRING)));
    }

    /*
     * Test method for 'edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute.getSearchStorageValues(String)'
     */
    @Test public void testGetSearchStorageValues() {
    	String attributeName = "XMLSearchableAttribute";
    	String keyName = "givenname";
    	String value = "jack";
    	String documentcontent = "<documentContent>" + "<searchableContent>" + "<putWhateverWordsIwantInsideThisTag>" + "<" + keyName + ">" + "<value>" + value + "</value>" + "</" + keyName + ">" + "</putWhateverWordsIwantInsideThisTag>" + "</searchableContent>" + "</documentContent>";
        StandardGenericXMLSearchableAttribute attribute = getAttribute(attributeName);
        List values = attribute.getSearchStorageValues(documentcontent);
        assertEquals("Number of search attribute values is wrong",1,values.size());
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            assertEquals("Key of attribute is wrong",keyName,searchAttValue.getSearchableAttributeKey());
            assertEquals("Value of attribute is wrong",value,searchAttValue.getSearchableAttributeDisplayValue());
        }

    	attributeName = "XMLSearchableAttributeStdLong";
        keyName = "testLongKey";
        value = "123458";
    	documentcontent = "<documentContent>" + "<searchableContent>" + "<putWhateverWordsIwantInsideThisTag>" + "<" + keyName + ">" + "<value>" + value + "</value>" + "</" + keyName + ">" + "</putWhateverWordsIwantInsideThisTag>" + "</searchableContent>" + "</documentContent>";
        attribute = getAttribute(attributeName);
        values = attribute.getSearchStorageValues(documentcontent);
        assertEquals("Number of search attribute values is wrong",1,values.size());
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            assertEquals("Key of attribute is wrong",keyName,searchAttValue.getSearchableAttributeKey());
            assertEquals("Value of attribute is wrong",value,searchAttValue.getSearchableAttributeDisplayValue());
        }

        attributeName = "XMLSearchableAttributeStdFloat";
        keyName = "testFloatKey";
        value = "2568.204154796";
        documentcontent = "<documentContent>" + "<searchableContent>" + "<putWhateverWordsIwantInsideThisTag>" + "<" + keyName + ">" + "<value>" + value + "</value>" + "</" + keyName + ">" + "</putWhateverWordsIwantInsideThisTag>" + "</searchableContent>" + "</documentContent>";
        attribute = getAttribute(attributeName);
        values = attribute.getSearchStorageValues(documentcontent);
        assertEquals("Number of search attribute values is wrong",1,values.size());
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            assertEquals("Key of attribute is wrong",keyName,searchAttValue.getSearchableAttributeKey());
            assertEquals("Value of attribute is wrong",insertCommasIfNeeded(value, 3),searchAttValue.getSearchableAttributeDisplayValue());
        }

        Map<String,String> currencyParameterMap = new HashMap<String,String>();
        currencyParameterMap.put("displayFormatPattern", "#,###.00");
        attributeName = "XMLSearchableAttributeStdCurrency";
        keyName = "testCurrencyKey";
        value = "2238.2";
        documentcontent = "<documentContent>" + "<searchableContent>" + "<putWhateverWordsIwantInsideThisTag>" + "<" + keyName + ">" + "<value>" + value + "</value>" + "</" + keyName + ">" + "</putWhateverWordsIwantInsideThisTag>" + "</searchableContent>" + "</documentContent>";
        attribute = getAttribute(attributeName);
        values = attribute.getSearchStorageValues(documentcontent);
        assertEquals("Number of search attribute values is wrong",1,values.size());
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            assertEquals("Key of attribute is wrong",keyName,searchAttValue.getSearchableAttributeKey());
            assertEquals("Value of attribute is wrong",insertCommasIfNeeded(value + "0", 3),searchAttValue.getSearchableAttributeDisplayValue(currencyParameterMap));
        }

        attributeName = "XMLSearchableAttributeStdCurrency";
        keyName = "testCurrencyKey";
        value = "2157238.2";
        documentcontent = "<documentContent>" + "<searchableContent>" + "<putWhateverWordsIwantInsideThisTag>" + "<" + keyName + ">" + "<value>" + value + "</value>" + "</" + keyName + ">" + "</putWhateverWordsIwantInsideThisTag>" + "</searchableContent>" + "</documentContent>";
        attribute = getAttribute(attributeName);
        values = attribute.getSearchStorageValues(documentcontent);
        assertEquals("Number of search attribute values is wrong",1,values.size());
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            assertEquals("Key of attribute is wrong",keyName,searchAttValue.getSearchableAttributeKey());
            assertEquals("Value of attribute is wrong",insertCommasIfNeeded(value + "0", 3),searchAttValue.getSearchableAttributeDisplayValue(currencyParameterMap));
        }

    	attributeName = "XMLSearchableAttributeStdDateTime";
        keyName = "testDateTimeKey";
        value = DocSearchUtils.getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE);
        documentcontent = "<documentContent>" + "<searchableContent>" + "<putWhateverWordsIwantInsideThisTag>" + "<" + keyName + ">" + "<value>" + value + "</value>" + "</" + keyName + ">" + "</putWhateverWordsIwantInsideThisTag>" + "</searchableContent>" + "</documentContent>";
        attribute = getAttribute(attributeName);
        values = attribute.getSearchStorageValues(documentcontent);
        assertEquals("Number of search attribute values is wrong",1,values.size());
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            assertEquals("Key of attribute is wrong",keyName,searchAttValue.getSearchableAttributeKey());
            assertEquals("Value of attribute is wrong",value,searchAttValue.getSearchableAttributeDisplayValue());
        }

        // test for kuali xstream formatted dates
        value = "2007-02-20";
        String returnValue = "02/20/2007";
        documentcontent = "<documentContent>" + "<searchableContent>" + "<putWhateverWordsIwantInsideThisTag>" + "<" + keyName + ">" + "<value>" + value + "</value>" + "</" + keyName + ">" + "</putWhateverWordsIwantInsideThisTag>" + "</searchableContent>" + "</documentContent>";
        attribute = getAttribute(attributeName);
        values = attribute.getSearchStorageValues(documentcontent);
        assertEquals("Number of search attribute values is wrong",1,values.size());
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            SearchableAttributeValue searchAttValue = (SearchableAttributeValue) iter.next();
            assertEquals("Key of attribute is wrong",keyName,searchAttValue.getSearchableAttributeKey());
            assertEquals("Value of attribute is wrong",returnValue,searchAttValue.getSearchableAttributeDisplayValue());
        }
    }

    private String insertCommasIfNeeded(String value, int interval) {
        int indexOfDecimal = value.indexOf(".");
        String decimalPointOn = value.substring(indexOfDecimal);
        String temp = value.substring(0, indexOfDecimal);
        StringBuffer builtValue = new StringBuffer();
        if (temp.length() <= interval) {
            builtValue.append(temp);
        } else {
            int counter = 0;
            for (int i = temp.length() - 1; (i >= 0); i--) {
                if (counter == interval) {
                    builtValue.insert(0, ",");
                    counter = 0;
                }
                counter++;
                builtValue.insert(0, temp.substring(i, i+1));
            }
        }
        return (builtValue.append(decimalPointOn)).toString();
    }

    /*
     * Test method for 'edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute.getSearchingRows()'
     */
    @Test public void testGetSearchingRows() {
        StandardGenericXMLSearchableAttribute searchAttribute = getAttribute(null);
        assertTrue("Invalid number of search rows", searchAttribute.getSearchingRows().size() == 1);

        //we really just want this to load without exploding
        List searchRows = getAttribute("BlankDropDownSearchAttribute").getSearchingRows();
        assertEquals("Invalid number of search rows", 1, searchRows.size());
        Row row = (Row) searchRows.get(0);
        Field field = row.getField(0);
        assertEquals("Should be 5 valid values", 5, field.getFieldValidValues().size());

        assertEquals("Default value is not correct", "AMST", field.getPropertyValue());
    }

    /*
     * Test method for 'edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute.validateUserSearchInputs(Map)'
     */
    @Test  public void testValidateUserSearchInputs() {
        StandardGenericXMLSearchableAttribute searchAttribute = getAttribute("XMLSearchableAttribute");
        Map paramMap = new HashMap();
        paramMap.put(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "jack");
        List validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "jack.jack");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        WorkflowAttributeValidationError error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertEquals("Validation error should match xml attribute message", "Invalid first name", error.getMessage());
        paramMap.clear();
        paramMap.put(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "jack*jack");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 0, validationErrors.size());

        searchAttribute = getAttribute("XMLSearchableAttributeStdLong");
        paramMap = new HashMap();
        paramMap.put(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString());
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString() + ".33");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        paramMap.clear();
        paramMap.put(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "jack*jack");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));

        searchAttribute = getAttribute("XMLSearchableAttributeStdFloat");
        paramMap = new HashMap();
        paramMap.put(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString());
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString() + "a");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        paramMap.clear();
        paramMap.put(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString() + "*");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));

        searchAttribute = getAttribute("XMLSearchableAttributeStdCurrency");
        String key = "testCurrencyKey";
        Float value = Float.valueOf("5486.25");
        paramMap = new HashMap();
        paramMap.put(key, value.toString());
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(key, value.toString() + "a");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        paramMap.clear();
        paramMap.put(key, value.toString() + "*");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));

        searchAttribute = getAttribute("XMLSearchableAttributeStdDateTime");
        paramMap = new HashMap();
        paramMap.put(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocSearchUtils.getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE));
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "001/5/08");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        paramMap.clear();
        paramMap.put(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "01/02/20*");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
    }

    /**
     * Tests the XStreamSafeEvaluator against searchable attributes to resolve EN-63 and KULWF-723.
     *
     * This test is pretty much just a copy of testRouteDocumentWithSearchableAttribute using a
     * different document type which defines the same xpath expression, only with embedded
     * XStream "reference" attributes in the XML.
     */
    @Test public void testRouteDocumentWithXStreamSearchableAttribute() throws Exception {
    	String documentTypeName = "SearchDocType";
    	String key = "givenname";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);

        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), "SearchDocTypeXStream");
        WorkflowAttributeDefinitionVO givennameXMLDef = new WorkflowAttributeDefinitionVO("XMLXStreamSearchableAttribute");

        workflowDocument.setApplicationContent("<test></test>");

        givennameXMLDef.addProperty("givenname", "jack");
        workflowDocument.addSearchableDefinition(givennameXMLDef);

        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("rkirkend"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jack", docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        List searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "fred", docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeproperty", "doesntexist", docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException wsee) {}
    }


    /**
     * Tests the resolution to issues EN-95, KULWF-757, KULOWF-52 whereby the use of a quickfinder is causing
     * NullPointers when searching for documents.
     */
    @Test public void testSearchableAttributeWithQuickfinder() throws Exception {
    	String documentTypeName = "AttributeWithQuickfinderDocType";
    	String key = "chart";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
    	 WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), documentTypeName);

    	 // define the chart for the searchable attribute
    	 WorkflowAttributeDefinitionVO chartDef = new WorkflowAttributeDefinitionVO("SearchableAttributeWithQuickfinder");
         chartDef.addProperty(key, "BL");
         document.addSearchableDefinition(chartDef);

         // save the document
         document.setTitle("Routin' with style");
         document.saveDocument("Savin' this document.");

         // prepare to search
         DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
         UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
         WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("rkirkend"));

         // execute the search by our chart, we should see one result
         DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
         criteria.setDocTypeFullName(documentTypeName);
         criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "BL", docType));
         DocumentSearchResultComponents results = docSearchService.getList(user, criteria);
         List searchResults = results.getSearchResults();
         assertEquals("Search results should have one document.", 1, searchResults.size());
         DocumentSearchResult result = (DocumentSearchResult)searchResults.get(0);
         KeyValueSort kvs = result.getResultContainer(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID);
         assertEquals("Wrong document in search results.", document.getRouteHeaderId(), kvs.getSortValue());

         // search with no searchable attribute criteria, should return our document as well
         criteria = new DocSearchCriteriaVO();
         criteria.setDocTypeFullName(documentTypeName);
         results = docSearchService.getList(user, criteria);
         searchResults = results.getSearchResults();
         assertEquals("Search results should have one document.", 1, searchResults.size());
         result = (DocumentSearchResult)searchResults.get(0);
         kvs = result.getResultContainer(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID);
         assertEquals("Wrong document in search results.", document.getRouteHeaderId(), kvs.getSortValue());

    }

    /**
     * Tests that the hidding of fields and columns works properly to resolve EN-53.
     *
     * TODO this is currently commented out because we can't test this properly through the unit
     * test since the filtering of the column actually happens in the web-tier.  Shoudl this be
     * the case?  Maybe we need to re-examine when we refactor document search.
     */
    @Test public void testSearchableAttributeWithHiddens() throws Exception {
    	// for the following document, the chart field should not show up in the result set and the org field
    	// should not show up in the criteriaw
    	String docType = "AttributeWithHiddensDocType";
    	DocumentType documentType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(docType);

    	String attributeName = "SearchableAttributeWithHiddens";
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), docType);

   	 	// define the chart for the searchable attribute
   	 	WorkflowAttributeDefinitionVO chartDef = new WorkflowAttributeDefinitionVO(attributeName);
        chartDef.addProperty("chart", "BL");
        chartDef.addProperty("org", "ARSC");
        chartDef.addProperty("dollar", "24");
        document.addSearchableDefinition(chartDef);

        // save the document
        document.setTitle("Routin' with style");
        document.saveDocument("Savin' this document.");

        // prepare to search
        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("rkirkend"));

        // execute the search by our chart, we should see one result
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(docType);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("chart", "BL", documentType));
        DocumentSearchResultComponents results = docSearchService.getList(user, criteria);
        List searchResults = results.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        DocumentSearchResult result = (DocumentSearchResult)searchResults.get(0);
        KeyValueSort kvs = result.getResultContainer(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID);
        assertEquals("Wrong document in search results.", document.getRouteHeaderId(), kvs.getSortValue());
        // also check that the chart field is not in the result set and the org field is
        kvs = null;
        kvs = result.getResultContainer("chart");
        assertNull("The chart column should not be in the result set!",kvs.getValue());
        kvs = null;
        kvs = result.getResultContainer("org");
        assertNotNull("The org column should be in the result set", kvs);
        assertEquals("Wrong org code.", "ARSC", kvs.getValue());
        kvs = null;
        kvs = result.getResultContainer("dollar");
        assertNotNull("The dollar column should be in the result set", kvs);
        assertEquals("Wrong dollar code.", "24", kvs.getValue());
    }

    @Test public void testSetApplicationContentXMLRoutedDocument() throws Exception {
        String documentTypeName = "SearchDocType";
        String key = "givenname";
        DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO("rkirkend"), documentTypeName);
        workflowDocument.setApplicationContent("<documentContent><searchableContent><putWhateverWordsIwantInsideThisTag>" +
                                               "<givenname><value>jack</value></givenname>" +
                                               "</putWhateverWordsIwantInsideThisTag></searchableContent></documentContent>");

        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");

        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("rkirkend"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "jack", docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        List searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 1, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(key, "fred", docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeproperty", "doesntexist", docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException wsee) {}
    }

    @Ignore("Implement Me")
    @Test public void testDisplayTypeMultiboxSearch() throws Exception {

    }

}