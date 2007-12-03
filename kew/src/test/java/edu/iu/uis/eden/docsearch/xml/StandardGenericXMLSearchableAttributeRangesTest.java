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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.docsearch.DocSearchCriteriaVO;
import edu.iu.uis.eden.docsearch.DocSearchUtils;
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
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Tests the StandardGenericXMLSearchableAttribute.
 *
 * KULWF-654: Tests the resolution to this issue by configuring a CustomActionListAttribute as well as a
 * searchable attribute.
 */
public class StandardGenericXMLSearchableAttributeRangesTest extends KEWTestCase {

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

    private SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String value,Boolean isLowerBoundValue,DocumentType docType) {
    	String formKey = (isLowerBoundValue == null) ? key : ((isLowerBoundValue != null && isLowerBoundValue.booleanValue()) ? SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + key : SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + key);
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

    /*
     * Test method for 'edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute.getSearchingRows()'
     */
    @Test public void testGetSearchingRowsUsingRangeSearches() {
        StandardGenericXMLSearchableAttribute searchAttribute = getAttribute("XMLSearchableAttributeStringRange");
        List searchRows = searchAttribute.getSearchingRows();
        if ((new SearchableAttributeStringValue()).allowsRangeSearches()) {
        	fail("Cannot perform range search on string field at database level");
//            assertEquals("Invalid number of search rows", 2, searchRows.size());
//            for (int i = 1; i <= searchRows.size(); i++) {
//				Row row = (Row)searchRows.get(i - 1);
//	            assertEquals("Invalid number of fields for search row " + i, 1, row.getFields().size());
//	            assertTrue("Field is not the member of a range",row.getField(0).isMemberOfRange());
//			}
        } else {
            assertEquals("Invalid number of search rows", 1, searchRows.size());
            Row row = (Row) searchRows.get(0);
            assertEquals("Invalid number of fields for search row", 1, row.getFields().size());
            assertFalse("Field is the member of a range when ranges are not allowed",row.getField(0).isMemberOfRange());
        }

        searchAttribute = getAttribute("XMLSearchableAttributeStdLongRange");
        // search def :  rangeSearch=true
        // range def  :
        // upper def  :
        // lower def  :
        searchRows = searchAttribute.getSearchingRows();
        if ((new SearchableAttributeLongValue()).allowsRangeSearches()) {
            assertEquals("Invalid number of search rows", 2, searchRows.size());
            for (int i = 1; i <= searchRows.size(); i++) {
				Row row = (Row)searchRows.get(i - 1);
	            assertEquals("Invalid number of fields for search row " + i, 1, row.getFields().size());
	            Field field = row.getField(0);
	            assertTrue("Field should be the member of a range",field.isMemberOfRange());
	            assertTrue("Field should not be inclusive",field.isInclusive());
	            assertFalse("Field should not be using datepicker", field.isUsingDatePicker());
			}
        } else {
            assertEquals("Invalid number of search rows", 1, searchRows.size());
            Row row = (Row) searchRows.get(0);
            assertEquals("Invalid number of fields for search row", 1, row.getFields().size());
            Field field = row.getField(0);
            assertFalse("Field is the member of a range when ranges are not allowed",field.isMemberOfRange());
            assertFalse("Field is inclusive when ranges are not allowed",field.isInclusive());
            assertFalse("Field should not be using datepicker", field.isUsingDatePicker());
        }

        searchAttribute = getAttribute("XMLSearchableAttributeStdFloatRange");
        // search def :
        // range def  :  inclusive=false
        // upper def  :  label=ending
        // lower def  :  label=starting
        searchRows = searchAttribute.getSearchingRows();
        if ((new SearchableAttributeFloatValue()).allowsRangeSearches()) {
            assertEquals("Invalid number of search rows", 2, searchRows.size());
            for (int i = 1; i <= searchRows.size(); i++) {
				Row row = (Row)searchRows.get(i - 1);
	            assertEquals("Invalid number of fields for search row " + i, 1, row.getFields().size());
	            Field field = row.getField(0);
	            assertTrue("Upper and Lower Fields should be members of a range",field.isMemberOfRange());
	            assertFalse("Upper and Lower Fields should not be inclusive",field.isInclusive());
	            String labelValue = null;
	            if (field.getPropertyName().startsWith(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
	            	labelValue = "starting";
	            } else if (field.getPropertyName().startsWith(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX)) {
	            	labelValue = "ending";
	            } else {
	            	fail("Field should have prefix consistent with upper or lower bound of a range");
	            }
	            assertEquals("Field label is incorrect.", labelValue, field.getFieldLabel());
	            assertFalse("Field should not be using datepicker", field.isUsingDatePicker());
			}
        } else {
            assertEquals("Invalid number of search rows", 1, searchRows.size());
            Row row = (Row) searchRows.get(0);
            assertEquals("Invalid number of fields for search row", 1, row.getFields().size());
            Field field = row.getField(0);
            assertFalse("Field is the member of a range when ranges are not allowed",field.isMemberOfRange());
            assertFalse("Field should not be using datepicker", field.isUsingDatePicker());
        }

        searchAttribute = getAttribute("XMLSearchableAttributeStdDateTimeRange");
        // search def :  datePicker=false
        // range def  :  inclusive=false
        // upper def  :  inclusvie=true - datePicker=true
        // lower def  :
        searchRows = searchAttribute.getSearchingRows();
        if ((new SearchableAttributeDateTimeValue()).allowsRangeSearches()) {
            assertEquals("Invalid number of search rows", 2, searchRows.size());
            for (int i = 0; i < searchRows.size(); i++) {
				Row row = (Row)searchRows.get(i);
	            assertTrue("Invalid number of fields for search row", row.getFields().size() > 0);
	            Field field = row.getField(0);
	            assertTrue("Field should be the member of a range search", field.isMemberOfRange());
	            if (field.getPropertyName().startsWith(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
	            	// this is the lower bound row
	            	assertFalse("Upper Field should not be using datepicker field", field.isUsingDatePicker());
	            	assertFalse("Upper Field should not be inclusive", field.isInclusive());
	            } else if (field.getPropertyName().startsWith(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX)) {
	            	// this is the upper bound row
	            	assertTrue("Upper Field should be using datepicker field", field.isUsingDatePicker());
	            	assertTrue("Upper Field should not be inclusive", field.isInclusive());
	            	assertEquals("Row should have two fields (including the datepicker field)", 2, row.getFields().size());
	            	assertEquals("Second field in row  should be of type datepicker", Field.DATEPICKER, row.getField(1).getFieldType());
	            } else {
	            	fail("Field should have prefix consistent with upper or lower bound of a range");
	            }
			}
        } else {
            assertEquals("Invalid number of search rows", 1, searchRows.size());
            Row row = (Row) searchRows.get(0);
            // check to make sure our datepicker field didn't make it to the search rows
            assertEquals("Invalid number of fields", 1, row.getFields().size());
            assertFalse("Field is the member of a range when ranges are not allowed",row.getField(0).isMemberOfRange());
        }
    }

    /*
     * Test method for 'edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute.validateUserSearchInputs(Map)'
     */
    @Test  public void testValidateUserSearchRangeInputs() {
    	// upper bound and lower bound fields should be using same validation... we just altername which formKey we use here
        StandardGenericXMLSearchableAttribute searchAttribute = getAttribute("XMLSearchableAttributeStringRange");
        Map paramMap = new HashMap();
        paramMap.put(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "jack");
        List validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "jack.jack");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        WorkflowAttributeValidationError error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertEquals("Validation error should match xml attribute message", "Invalid first name", error.getMessage());
        paramMap.clear();
        paramMap.put(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "jack*jack");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 0, validationErrors.size());

        searchAttribute = getAttribute("XMLSearchableAttributeStdLongRange");
        paramMap = new HashMap();
        paramMap.put(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString());
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString() + ".33");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        paramMap.clear();
        paramMap.put(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "jack*jack");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));

        searchAttribute = getAttribute("XMLSearchableAttributeStdFloatRange");
        paramMap = new HashMap();
        paramMap.put(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString());
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString() + "a");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        paramMap.clear();
        paramMap.put(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString() + "*");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));

        searchAttribute = getAttribute("XMLSearchableAttributeStdDateTimeRange");
        paramMap = new HashMap();
        paramMap.put(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocSearchUtils.getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE));
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should not have returned an error.", 0, validationErrors.size());
        paramMap.clear();
        paramMap.put(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "001/5/08");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        paramMap.clear();
        paramMap.put(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "01/02/20*");
        validationErrors = searchAttribute.validateUserSearchInputs(paramMap);
        assertEquals("Validation should return a single error message.", 1, validationErrors.size());
        error = (WorkflowAttributeValidationError) validationErrors.get(0);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
    }

    /**
     * Test searching by searchable attributes that use ranges
     */
    @Test public void testSearchableAttributeRanges() throws Exception {
        String documentTypeName = "SearchDocTypeRangeSearchDataType";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName);

        /*
         *   Below we are using the keys and values from the custom searchable attribute classes' static constants but
         *   this is only for convenience as those should always be valid values to test for.
         */
        // adding string searchable attribute
        WorkflowAttributeDefinitionVO stringXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStringRange");
        stringXMLDef.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workflowDocument.addSearchableDefinition(stringXMLDef);
        // adding long searchable attribute
        WorkflowAttributeDefinitionVO longXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStdLongRange");
        longXMLDef.addProperty(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(longXMLDef);
        // adding float searchable attribute
        WorkflowAttributeDefinitionVO floatXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStdFloatRange");
        floatXMLDef.addProperty(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(floatXMLDef);
        // adding string searchable attribute
        WorkflowAttributeDefinitionVO dateXMLDef = new WorkflowAttributeDefinitionVO("XMLSearchableAttributeStdDateTimeRange");
        dateXMLDef.addProperty(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocSearchUtils.getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE));
        workflowDocument.addSearchableDefinition(dateXMLDef);

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

        // begin string attribute value testing
        DocSearchCriteriaVO criteria = null;
        List searchResults = null;
        DocumentSearchResultComponents result = null;

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, Boolean.TRUE, docType));
        if ((new SearchableAttributeStringValue()).allowsRangeSearches()) {
			fail("Cannot search by range on a String field at the database level");
        } else {
            try {
                result = docSearchService.getList(user, criteria);
    			fail("Searching by range for field using key '" + TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY + "' should throw exception");
    		} catch (Exception e) {}
        }


        // begin long attribute value testing
        // inclusive = true
        String searchAttributeLongKey = TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY;
        Long searchAttributeLongValue = TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE;

        Long longValueToUse = null;
        // test lower bound only
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        longValueToUse = searchAttributeLongValue;
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, longValueToUse.toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() - 1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, longValueToUse.toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() + 1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, longValueToUse.toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());

        // test upper bound only
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        longValueToUse = searchAttributeLongValue;
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, longValueToUse.toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() - 1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, longValueToUse.toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() + 1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, longValueToUse.toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        // test both bounds
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue()).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue()).toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() + 2).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() + 4).toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() - 4).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() - 2).toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() - 2).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() + 2).toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() + 2).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() - 2).toString(), Boolean.FALSE, docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Error should have been thrown for invalid range");
        } catch (WorkflowServiceErrorException e) {}

        // begin float attribute value testing
        // inclusive = false
        String searchAttributeFloatKey = TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY;
        BigDecimal searchAttributeFloatValue = TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE;

        BigDecimal floatValueToUse = null;
        // test lower bound only
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue;
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, floatValueToUse.toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue.subtract(BigDecimal.ONE);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, floatValueToUse.toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue.add(BigDecimal.ONE);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, floatValueToUse.toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());

        // test upper bound only
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue;
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, floatValueToUse.toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue.subtract(BigDecimal.ONE);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, floatValueToUse.toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue.add(BigDecimal.ONE);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, floatValueToUse.toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        // test both bounds
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, searchAttributeFloatValue.toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, searchAttributeFloatValue.toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, (searchAttributeFloatValue.add(new BigDecimal(2))).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, (searchAttributeFloatValue.add(new BigDecimal(4))).toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, (searchAttributeFloatValue.subtract(new BigDecimal(4))).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, (searchAttributeFloatValue.subtract(new BigDecimal(2))).toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, (searchAttributeFloatValue.subtract(new BigDecimal(2))).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, (searchAttributeFloatValue.add(new BigDecimal(2))).toString(), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, (searchAttributeFloatValue.add(new BigDecimal(2))).toString(), Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeFloatKey, (searchAttributeFloatValue.subtract(new BigDecimal(2))).toString(), Boolean.FALSE, docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Error should have been thrown for invalid range");
        } catch (WorkflowServiceErrorException e) {}

        // begin datetime attribute value testing
        // inclusive = ?
        String searchAttributeDateTimeKey = TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY;
        Calendar searchAttributeDateTimeValue = Utilities.convertTimestamp(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE);

        Calendar calendarValueToUse = null;
        // test lower bound only
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        String valueToSearch = DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(calendarValueToUse));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, valueToSearch, Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, -1);
        valueToSearch = DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(calendarValueToUse));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, valueToSearch, Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, 1);
        valueToSearch = DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(calendarValueToUse));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, valueToSearch, Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, "", Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());

        // test upper bound only
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(calendarValueToUse)), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, -1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(calendarValueToUse)), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, 1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, "", Boolean.TRUE, docType));
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(calendarValueToUse)), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());

        // test both bounds
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        Calendar lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(lowerBoundValue)), Boolean.TRUE, docType));
        Calendar upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(upperBoundValue)), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, 2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(lowerBoundValue)), Boolean.TRUE, docType));
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, 4);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(upperBoundValue)), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, -4);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(lowerBoundValue)), Boolean.TRUE, docType));
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, -2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(upperBoundValue)), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 0, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, -2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(lowerBoundValue)), Boolean.TRUE, docType));
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, 2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(upperBoundValue)), Boolean.FALSE, docType));
        result = docSearchService.getList(user, criteria);
        searchResults = result.getSearchResults();
        assertEquals("Search results should have one document.", 1, searchResults.size());
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, 2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(lowerBoundValue)), Boolean.TRUE, docType));
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, -2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(searchAttributeDateTimeKey, DocSearchUtils.getDisplayValueWithDateOnly(Utilities.convertCalendar(upperBoundValue)), Boolean.FALSE, docType));
        try {
            result = docSearchService.getList(user, criteria);
            fail("Error should have been thrown for invalid range");
        } catch (WorkflowServiceErrorException e) {}
    }
}