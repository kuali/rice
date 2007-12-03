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
package edu.iu.uis.eden.docsearch;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Tests the StandardGenericXMLSearchableAttribute.
 * 
 * KULWF-654: Tests the resolution to this issue by configuring a CustomActionListAttribute as well as a
 * searchable attribute.
 */
public class SearchableAttributeTest extends KEWTestCase {
    
    protected void loadTestData() throws Exception {
        loadXmlFile("SearchAttributeConfig.xml");
    }

//    private StandardGenericXMLSearchableAttribute getAttribute(String name) {
//        String attName = name;
//        if (attName == null) {
//            attName = "XMLSearchableAttribute";
//        }
//        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(attName);
//        
//        StandardGenericXMLSearchableAttribute attribute = new StandardGenericXMLSearchableAttribute();
//        attribute.setRuleAttribute(ruleAttribute);
//        return attribute;
//    }
//
    private SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String value,Boolean isLowerBoundValue,DocumentType docType) {
    	String formKey = (isLowerBoundValue == null) ? key : ((isLowerBoundValue != null && isLowerBoundValue.booleanValue()) ? SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX : SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX);
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

    @Test public void testCustomSearchableAttributesWithDataType() throws Exception {
        String documentTypeName = "SearchDocType";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        
        workflowDocument = new WorkflowDocument(new NetworkIdVO(userNetworkId), workflowDocument.getRouteHeaderId());
        DocumentRouteHeaderValue doc = KEWServiceLocator.getRouteHeaderService().getRouteHeader(workflowDocument.getRouteHeaderId());
        assertEquals("Wrong number of searchable attributes", 4, doc.getSearchableAttributeValues().size());
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
                assertEquals("The only Float attribute that should have been added has value '" + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE + "'", TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE, realValue.getSearchableAttributeValue());
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
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, null, docType));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        List searchResults = result.getSearchResults();

        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "fred", null, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();

        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeproperty", "doesntexist", null, docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException e) {}

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString(), null, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "1111111", null, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeymcfakefake", "99999999", null, docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException e) {}

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString(), null, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, "215.3548", null, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("fakeylostington", "9999.9999", null, docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException e) {}

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocSearchUtils.getDisplayValueWithDateOnly(new Timestamp(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE_IN_MILLS)), null, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "07/06/1979", null, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should be empty.", 0, searchResults.size());

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("lastingsfakerson", "07/06/2007", null, docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Search results should be throwing a validation exception for use of non-existant searchable attribute");
        } catch (WorkflowServiceErrorException e) {}
    }
    
    /**
     * Tests searching documents with searchable attributes
     * @throws WorkflowException
     */
    @Test public void testSearchAttributesAcrossDocumentTypeVersions() throws Exception {
        // first test searching for an initial version of the doc which does not have a searchable attribute
        loadXmlFile("testdoc0.xml");
        
        String documentTypeName = "SearchDoc";
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("arh14"), documentTypeName);
        DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        doc.routeDocument("routing");
        
        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);

        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setFromDateCreated("01/01/2004");
        
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("arh14"));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        assertEquals(1, result.getSearchResults().size());

        // now upload the new version with a searchable attribute
        loadXmlFile("testdoc1.xml");
        docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        
        // route a new doc
        doc = new WorkflowDocument(new NetworkIdVO("arh14"), documentTypeName);
        doc.routeDocument("routing");
        
        // with no attribute criteria, both docs should be found
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setFromDateCreated("01/01/2004");
        
        result = docSearchService.getList(user, criteria);
        assertEquals(2, result.getSearchResults().size());
        
        // search with specific SearchableAttribute value
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setFromDateCreated("01/01/2004");
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("MockSearchableAttributeKey", "Mock Searchable Attribute", null, docType));
        criteria.getSearchableAttributes().set(0, createSearchAttributeCriteriaComponent("MockSearchableAttributeKey", "MockSearchableAttributeValue", null, docType));
//        criteria.addSearchableAttribute(new KeyLabelPair("MockSearchableAttributeKey", "Mock Searchable Attribute"));
//        criteria.setSearchableAttribute(0, new KeyLabelPair("MockSearchableAttributeKey", "MockSearchableAttributeValue"));

        result = docSearchService.getList(user, criteria);
        assertEquals(1, result.getSearchResults().size());

        // search with any SearchableAttribute value
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.setFromDateCreated("01/01/2004");
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("MockSearchableAttributeKey", "Mock Searchable Attribute", null, docType));
        criteria.getSearchableAttributes().set(0, createSearchAttributeCriteriaComponent("MockSearchableAttributeKey", "", null, docType));
//        criteria.addSearchableAttribute(new KeyLabelPair("MockSearchableAttributeKey", "Mock Searchable Attribute"));
//        criteria.setSearchableAttribute(0, new KeyLabelPair("MockSearchableAttributeKey", ""));
        
        result = docSearchService.getList(user, criteria);
        // should return two because an empty value above will return any value of the 'MockSearchableAttributeKey' key including the previous document
        // that doesn't even have a record of that field being saved to the database
        assertEquals(2, result.getSearchResults().size());
    }

}
