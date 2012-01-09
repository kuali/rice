/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch.xml;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.docsearch.DocumentSearchInternalUtils;
import org.kuali.rice.kew.docsearch.DocumentSearchTestBase;
import org.kuali.rice.kew.docsearch.TestXMLSearchableAttributeDateTime;
import org.kuali.rice.kew.docsearch.TestXMLSearchableAttributeFloat;
import org.kuali.rice.kew.docsearch.TestXMLSearchableAttributeLong;
import org.kuali.rice.kew.docsearch.TestXMLSearchableAttributeString;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests the StandardGenericXMLSearchableAttribute.
 *
 * KULWF-654: Tests the resolution to this issue by configuring a CustomActionListAttribute as well as a
 * searchable attribute.
 */
public class StandardGenericXMLSearchableAttributeRangesTest extends DocumentSearchTestBase {
    private DocumentSearchService docSearchService;

    protected void loadTestData() throws Exception {
        loadXmlFile("XmlConfig.xml");
    }

    @Before
    public void retrieveDocSearchSvc() {
        docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
    }

    /*
     * Test method for 'org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute.getSearchingRows()'
     */
    /*
    @Ignore("See KULRICE-2988")
    @Test public void testGetSearchingRowsUsingRangeSearches() {
        StandardGenericXMLSearchableAttribute searchAttribute = getAttribute("XMLSearchableAttributeStringRange");
        String documentTypeName = "SearchDocType";
        DocumentSearchContext context = DocSearchUtils.getDocumentSearchContext("", documentTypeName, "");
        List<Row> searchRows = searchAttribute.getSearchingRows(context);
        if ((new SearchableAttributeStringValue()).allowsRangeSearches()) {
        	fail("Cannot perform range search on string field at database level");
        } else {
            assertEquals("Invalid number of search rows", 1, searchRows.size());
            Row row = searchRows.get(0);
            assertEquals("Invalid number of fields for search row", 1, row.getFields().size());
            assertFalse("Field is the member of a range when ranges are not allowed",((Field)row.getField(0)).isMemberOfRange());
        }

        searchAttribute = getAttribute("XMLSearchableAttributeStdLongRange");
        // search def :  rangeSearch=true
        // range def  :
        // upper def  :
        // lower def  :
        searchRows = searchAttribute.getSearchingRows(context);
        if ((new SearchableAttributeLongValue()).allowsRangeSearches()) {
            assertEquals("Invalid number of search rows", 2, searchRows.size());
            for (int i = 1; i <= searchRows.size(); i++) {
                Row row = searchRows.get(i - 1);
	            assertEquals("Invalid number of fields for search row " + i, 1, row.getFields().size());
	            Field field = (Field)(row.getField(0));
	            assertTrue("Field should be the member of a range",field.isMemberOfRange());
	            assertTrue("Field should not be inclusive",field.isInclusive());
	            assertFalse("Field should not be using datepicker", field.isDatePicker());
			}
        } else {
            assertEquals("Invalid number of search rows", 1, searchRows.size());
            Row row = searchRows.get(0);
            assertEquals("Invalid number of fields for search row", 1, row.getFields().size());
            Field field = (Field)(row.getField(0));
            assertFalse("Field is the member of a range when ranges are not allowed",field.isMemberOfRange());
            assertFalse("Field is inclusive when ranges are not allowed",field.isInclusive());
            assertFalse("Field should not be using datepicker", field.isDatePicker());
        }

        searchAttribute = getAttribute("XMLSearchableAttributeStdFloatRange");
        // search def :
        // range def  :  inclusive=false
        // upper def  :  label=ending
        // lower def  :  label=starting
        searchRows = searchAttribute.getSearchingRows(context);
        if ((new SearchableAttributeFloatValue()).allowsRangeSearches()) {
            assertEquals("Invalid number of search rows", 2, searchRows.size());
            for (int i = 1; i <= searchRows.size(); i++) {
                Row row = searchRows.get(i - 1);
	            assertEquals("Invalid number of fields for search row " + i, 1, row.getFields().size());
	            Field field = (Field)(row.getField(0));
	            assertTrue("Upper and Lower Fields should be members of a range",field.isMemberOfRange());
	            assertFalse("Upper and Lower Fields should not be inclusive",field.isInclusive());
	            String labelValue = null;
	            if (field.getPropertyName().startsWith(KewApiConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
	            	labelValue = "starting";
	            } else if (field.getPropertyName().startsWith(KewApiConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX)) {
	            	labelValue = "ending";
	            } else {
	            	fail("Field should have prefix consistent with upper or lower bound of a range");
	            }
	            assertEquals("Field label is incorrect.", labelValue, field.getFieldLabel());
	            assertFalse("Field should not be using datepicker", field.isDatePicker());
			}
        } else {
            assertEquals("Invalid number of search rows", 1, searchRows.size());
            Row row = searchRows.get(0);
            assertEquals("Invalid number of fields for search row", 1, row.getFields().size());
            Field field = (Field)(row.getField(0));
            assertFalse("Field is the member of a range when ranges are not allowed",field.isMemberOfRange());
            assertFalse("Field should not be using datepicker", field.isDatePicker());
        }

        searchAttribute = getAttribute("XMLSearchableAttributeStdDateTimeRange");
        // search def :  datePicker=false
        // range def  :  inclusive=false
        // upper def  :  inclusvie=true - datePicker=true
        // lower def  :
        searchRows = searchAttribute.getSearchingRows(context);
        if ((new SearchableAttributeDateTimeValue()).allowsRangeSearches()) {
            assertEquals("Invalid number of search rows", 2, searchRows.size());
            for (int i = 0; i < searchRows.size(); i++) {
                Row row = searchRows.get(i);
	            assertTrue("Invalid number of fields for search row", row.getFields().size() > 0);
	            Field field = (Field)(row.getField(0));
	            assertTrue("Field should be the member of a range search", field.isMemberOfRange());
	            if (field.getPropertyName().startsWith(KewApiConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
	            	// this is the lower bound row
	            	assertFalse("Upper Field should not be using datepicker field", field.isDatePicker());
	            	assertFalse("Upper Field should not be inclusive", field.isInclusive());
	            } else if (field.getPropertyName().startsWith(KewApiConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX)) {
	            	// this is the upper bound row
	            	assertTrue("Upper Field should be using datepicker field", field.isDatePicker());
	            	assertTrue("Upper Field should not be inclusive", field.isInclusive());
	            	assertEquals("Row should have two fields (including the datepicker field)", 2, row.getFields().size());
	            	assertEquals("Second field in row  should be of type datepicker", Field.DATEPICKER, row.getField(1).getFieldType());
	            } else {
	            	fail("Field should have prefix consistent with upper or lower bound of a range");
	            }
			}
        } else {
            assertEquals("Invalid number of search rows", 1, searchRows.size());
            Row row = searchRows.get(0);
            // check to make sure our datepicker field didn't make it to the search rows
            assertEquals("Invalid number of fields", 1, row.getFields().size());
            assertFalse("Field is the member of a range when ranges are not allowed",((Field)(row.getField(0))).isMemberOfRange());
        }
    }

    */
    /*
     * Test method for 'org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute.validateUserSearchInputs(Map)'
     */
    @Test  public void testValidateUserSearchRangeInputs() {
    	// upper bound and lower bound fields should be using same validation... we just altername which formKey we use here
        StandardGenericXMLSearchableAttribute searchAttribute = getAttribute("XMLSearchableAttributeStringRange");
        ExtensionDefinition ed = createExtensionDefinition("XMLSearchableAttributeStringRange");

        String documentTypeName = "SearchDocType";

        assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, ">= jack", false);

        RemotableAttributeError error = assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, "<= jack.jack", true);
        assertEquals("Validation error should match xml attribute message", "Invalid first name", error.getMessage());

        assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, ">= jack*jack", false); // TODO: * gets stripped from value

        searchAttribute = getAttribute("XMLSearchableAttributeStdLongRange");
        ed = createExtensionDefinition("XMLSearchableAttributeStdLongRange");
        assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "<= " + TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString(), false);
        error = assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, ">= " + TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString() + ".33", true);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        error = assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, "<= jack*jack", true);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));

        searchAttribute = getAttribute("XMLSearchableAttributeStdFloatRange");
        ed = createExtensionDefinition("XMLSearchableAttributeStdFloatRange");
        assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, ">= " + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString(), false);
        error = assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, "<= " + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString() + "a", true);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        error = assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, ">= " + TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString() + "*", true);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));

        searchAttribute = getAttribute("XMLSearchableAttributeStdDateTimeRange");
        ed = createExtensionDefinition("XMLSearchableAttributeStdDateTimeRange");
        assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "<= " + DocumentSearchInternalUtils.getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE), false);
        assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, ">= 001/5/08", false);
        error = assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, ">= 41/5/08", true);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
        error = assertDocumentSearchCriteriaValidation(searchAttribute, ed, TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, "<= 01/02/20*", true);
        assertTrue("Validation error is incorrect", error.getMessage().endsWith("does not conform to standard validation for field type."));
    }

    /**
     * Helper to assert document search criteria validation
     */
    protected RemotableAttributeError assertDocumentSearchCriteriaValidation(StandardGenericXMLSearchableAttribute attribute, ExtensionDefinition ed, String attrkey, String attrvalue, boolean expectError) {
        DocumentSearchCriteria.Builder dscb = DocumentSearchCriteria.Builder.create();
        dscb.addDocumentAttributeValue(attrkey, attrvalue);

        List<RemotableAttributeError> errors = attribute.validateDocumentAttributeCriteria(ed, dscb.build());

        if (expectError) {
            assertEquals("Validation should return a single error message.", 1, errors.size());
            return errors.get(0);
        } else {
            assertEquals("Validation should not have returned an error.", 0, errors.size());
            return null;
        }
    }
    
    /**
     * Creates an ExtensionDefinition for the specified attribute with XML config pulled from the db
     */
    protected static ExtensionDefinition createExtensionDefinition(String attrName) {
        ExtensionDefinition.Builder edb = ExtensionDefinition.Builder.create(attrName, KewApiConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE, StandardGenericXMLSearchableAttribute.class.getName());
        edb.getConfiguration().put(KewApiConstants.ATTRIBUTE_XML_CONFIG_DATA, KEWServiceLocator.getRuleAttributeService().findByName(attrName).getXmlConfigData());
        return edb.build();
    }

    /**
     * Sets up a doc for searching with ranged queries
     */
    protected WorkflowDocument setUpSearchableDoc() {
        String documentTypeName = "SearchDocTypeRangeSearchDataType";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";
        WorkflowDocument workflowDocument = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName);

        /*
         *   Below we are using the keys and values from the custom searchable attribute classes' static constants but
         *   this is only for convenience as those should always be valid values to test for.
         */
        // adding string searchable attribute
        WorkflowAttributeDefinition.Builder stringXMLDef = WorkflowAttributeDefinition.Builder.create("XMLSearchableAttributeStringRange");
        stringXMLDef.addPropertyDefinition(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workflowDocument.addSearchableDefinition(stringXMLDef.build());
        // adding long searchable attribute
        WorkflowAttributeDefinition.Builder longXMLDef = WorkflowAttributeDefinition.Builder.create("XMLSearchableAttributeStdLongRange");
        longXMLDef.addPropertyDefinition(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(longXMLDef.build());
        // adding float searchable attribute
        WorkflowAttributeDefinition.Builder floatXMLDef = WorkflowAttributeDefinition.Builder.create("XMLSearchableAttributeStdFloatRange");
        floatXMLDef.addPropertyDefinition(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(floatXMLDef.build());
        // adding string searchable attribute
        WorkflowAttributeDefinition.Builder dateXMLDef = WorkflowAttributeDefinition.Builder.create("XMLSearchableAttributeStdDateTimeRange");
        dateXMLDef.addPropertyDefinition(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE));
        workflowDocument.addSearchableDefinition(dateXMLDef.build());

        workflowDocument.setTitle("Routing style");
        workflowDocument.route("routing this document.");

        return WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(userNetworkId), workflowDocument.getDocumentId());
    }

    /**
     * Special result which indicates a range validation error is expected
     */
    private static final int EXPECT_EXCEPTION = -1;

    /**
     * Helper that asserts range search results
     */
    protected void assertRangeSearchResults(String docType, String userId, String attrKey, String lowerBound, String upperBound, boolean upperBoundInclusive, int expected) throws WorkflowServiceErrorException {
        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(docType);

        addSearchableAttributeRange(criteria, attrKey, lowerBound, upperBound, upperBoundInclusive);

        DocumentSearchResults results;
        try {
            results = docSearchService.lookupDocuments(userId, criteria.build());
            if (expected == EXPECT_EXCEPTION) fail("Error should have been thrown for invalid range");
        } catch (WorkflowServiceErrorException e) {
            if (expected == EXPECT_EXCEPTION) return;
            else throw e;
        }

        assertEquals("Search results should have " + expected + " document(s).", expected, results.getSearchResults().size());
    }

    @Test public void testStringRanges() throws Exception {
        WorkflowDocument doc = setUpSearchableDoc();
        String userId = doc.getInitiatorPrincipalId();
        String docType = doc.getDocumentTypeName();

        assertRangeSearchResults(docType, userId, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, null, false, 1);
    }

    @Test public void testLongRanges() throws Exception {
        WorkflowDocument doc = setUpSearchableDoc();
        String userId = doc.getInitiatorPrincipalId();
        String docType = doc.getDocumentTypeName();

        String searchAttributeLongKey = TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY;
        Long searchAttributeLongValue = TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.longValue();
        Long longValueToUse = null;

        // test lower bound only
        longValueToUse = searchAttributeLongValue; // lowerbound == value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey, longValueToUse.toString(), null, false, 1);

        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() - 1); // lowerbound below value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey, longValueToUse.toString(), null, false, 1);

        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() + 1); // lowerbound is above value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey, longValueToUse.toString(), null, false, 0);

        // test upper bound only
        longValueToUse = searchAttributeLongValue; // upper bound == value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey, null, longValueToUse.toString(), true, 1);

        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() - 1); // upper bound < value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey, null, longValueToUse.toString(), true, 0);

        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() + 1); // upper bound > value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey, null, longValueToUse.toString(), true, 1);

        // test both bounds
        // lowerbound == upperbound == value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey,
                                 Long.valueOf(searchAttributeLongValue.longValue()).toString(),
                                 Long.valueOf(searchAttributeLongValue.longValue()).toString(), true, 1);

        // lower and upper bound > value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey,
                                 Long.valueOf(searchAttributeLongValue.longValue() + 2).toString(),
                                 Long.valueOf(searchAttributeLongValue.longValue() + 4).toString(), true, 0);

        // lower and upper bound < value, but lower > upper
        assertRangeSearchResults(docType, userId, searchAttributeLongKey,
                                 Long.valueOf(searchAttributeLongValue.longValue() - 2).toString(),
                                 Long.valueOf(searchAttributeLongValue.longValue() - 4).toString(), true, EXPECT_EXCEPTION);
        // lower and upper bound < value, lower < upper
        assertRangeSearchResults(docType, userId, searchAttributeLongKey,
                                 Long.valueOf(searchAttributeLongValue.longValue() - 4).toString(),
                                 Long.valueOf(searchAttributeLongValue.longValue() - 2).toString(), true, 0);

        // lower bound < value, upper bound > value
        assertRangeSearchResults(docType, userId, searchAttributeLongKey,
                                 Long.valueOf(searchAttributeLongValue.longValue() - 2).toString(),
                                 Long.valueOf(searchAttributeLongValue.longValue() + 2).toString(), true, 1);

        // upper < lower
        assertRangeSearchResults(docType, userId, searchAttributeLongKey,
                                 Long.valueOf(searchAttributeLongValue.longValue() + 2).toString(),
                                 Long.valueOf(searchAttributeLongValue.longValue() - 2).toString(), true, EXPECT_EXCEPTION);
    }

    @Test public void testFloatRanges() throws Exception {
        WorkflowDocument doc = setUpSearchableDoc();
        String userId = doc.getInitiatorPrincipalId();
        String docType = doc.getDocumentTypeName();

        String searchAttributeFloatKey = TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY;
        BigDecimal searchAttributeFloatValue = TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE;

        BigDecimal floatValueToUse = null;
        // test lower bound only
        floatValueToUse = searchAttributeFloatValue; // lower bound == value
        // NOTE: original test asserted 0 results, mysql actually does match the value
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey, floatValueToUse.toString(), null, false, 1);

        floatValueToUse = searchAttributeFloatValue.subtract(BigDecimal.ONE); // lowerbound < value
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey, floatValueToUse.toString(), null, false, 1);

        floatValueToUse = searchAttributeFloatValue.add(BigDecimal.ONE); // lowerbound > value
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey, floatValueToUse.toString(), null, false, 0);

        // test upper bound only
        floatValueToUse = searchAttributeFloatValue; // upperbound == value (does not match float)
        // NOTE: another case where original test had 0 results, but in fact we see a float match
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey, null, floatValueToUse.toString(), true, 1);

        floatValueToUse = searchAttributeFloatValue.subtract(BigDecimal.ONE); // upperbound < value
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey, null, floatValueToUse.toString(), true, 0);

        floatValueToUse = searchAttributeFloatValue.add(BigDecimal.ONE); // upperbound > value
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey, null, floatValueToUse.toString(), true, 1);

        // test both bounds
        // upper == lower == value
        // NOTE: original case had 0 results, now seeing 1 result
        // search generator invokes criteria which calls addNumericRangeCriteria when produces: (EXT1.VAL BETWEEN 123456.3456 AND 123456.3456)
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey, searchAttributeFloatValue.toString(), searchAttributeFloatValue.toString(), true, 1);

        // upper and lower > value
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey,
                                 searchAttributeFloatValue.add(new BigDecimal(2)).toString(),
                                 searchAttributeFloatValue.add(new BigDecimal(4)).toString(), true, 0);

        // upper and lower < value
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey,
                                 searchAttributeFloatValue.subtract(new BigDecimal(4)).toString(),
                                 searchAttributeFloatValue.subtract(new BigDecimal(2)).toString(), true, 0);

        // lower < value, upper > value
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey,
                                 searchAttributeFloatValue.subtract(new BigDecimal(2)).toString(),
                                 searchAttributeFloatValue.add(new BigDecimal(2)).toString(), true, 1);

        // upper < lower
        assertRangeSearchResults(docType, userId, searchAttributeFloatKey,
                                 searchAttributeFloatValue.add(new BigDecimal(2)).toString(),
                                 searchAttributeFloatValue.subtract(new BigDecimal(2)).toString(), true, EXPECT_EXCEPTION);
    }

    @Test public void testDateRanges() throws Exception {
        WorkflowDocument doc = setUpSearchableDoc();
        String userId = doc.getInitiatorPrincipalId();
        String docType = doc.getDocumentTypeName();

        // begin datetime attribute value testing
        // inclusive = ?
        String searchAttributeDateTimeKey = TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY;
        Calendar searchAttributeDateTimeValue = TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE.toGregorianCalendar();

        Calendar calendarValueToUse = null;
        // test lower bound only
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone(); // lower == value
        String valueToSearch = DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse));
        // NOTE: matches now
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey, valueToSearch, null, false, 1);

        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, -1); // lower < value
        valueToSearch = DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse));
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey, valueToSearch, null, false, 1);

        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, 1); // lower > value
        valueToSearch = DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse));
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey, valueToSearch, null, false, 0);

        // test upper bound only
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone(); // upper == value (inclusivity true)
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey,
                                 null,
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse)), true, 1);

        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, -1); // upper < value
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey,
                                 null,
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse)), true, 0);

        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, 1); // upper > value
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey,
                                 null,
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse)), true, 1);

        // test both bounds
        Calendar lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        Calendar upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone(); // upper == lower == value (inclusivity true)
        // NOTE: matches now
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey,
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)),
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true, 1);

        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, 2);
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, 4);  // upper and lower > value
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey,
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)),
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true, 0);

        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, -4);
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, -2); // upper and lower < value
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey,
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)),
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true, 0);

        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, -2);
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, 2);  // lower < value, upper > value
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey,
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)),
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true, 1);

        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, 2);
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, -2); // lower > upper == error
        assertRangeSearchResults(docType, userId, searchAttributeDateTimeKey,
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)),
                                 DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true, EXPECT_EXCEPTION);
    }

    /**
     * Test searching by searchable attributes that use ranges
     */
    @Ignore // TODO: KULRICE-5630 delete this old test after search behavior in test*Ranges tests above has been verified/approved
    @Test public void testSearchableAttributeRanges() throws Exception {
        String documentTypeName = "SearchDocTypeRangeSearchDataType";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";
        WorkflowDocument workflowDocument = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName);

        /*
         *   Below we are using the keys and values from the custom searchable attribute classes' static constants but
         *   this is only for convenience as those should always be valid values to test for.
         */
        // adding string searchable attribute
        WorkflowAttributeDefinition.Builder stringXMLDef = WorkflowAttributeDefinition.Builder.create("XMLSearchableAttributeStringRange");
        stringXMLDef.addPropertyDefinition(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workflowDocument.addSearchableDefinition(stringXMLDef.build());
        // adding long searchable attribute
        WorkflowAttributeDefinition.Builder longXMLDef = WorkflowAttributeDefinition.Builder.create("XMLSearchableAttributeStdLongRange");
        longXMLDef.addPropertyDefinition(TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(longXMLDef.build());
        // adding float searchable attribute
        WorkflowAttributeDefinition.Builder floatXMLDef = WorkflowAttributeDefinition.Builder.create("XMLSearchableAttributeStdFloatRange");
        floatXMLDef.addPropertyDefinition(TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE.toString());
        workflowDocument.addSearchableDefinition(floatXMLDef.build());
        // adding string searchable attribute
        WorkflowAttributeDefinition.Builder dateXMLDef = WorkflowAttributeDefinition.Builder.create("XMLSearchableAttributeStdDateTimeRange");
        dateXMLDef.addPropertyDefinition(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY, DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE));
        workflowDocument.addSearchableDefinition(dateXMLDef.build());

        workflowDocument.setTitle("Routing style");
        workflowDocument.route("routing this document.");

        workflowDocument = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(userNetworkId), workflowDocument.getDocumentId());

        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        Person user = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(userNetworkId);

        // begin string attribute value testing
        DocumentSearchCriteria.Builder criteria = null;
        DocumentSearchResults results = null;

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        addSearchableAttributeRange(criteria, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        int size = results.getSearchResults().size();
        assertTrue("Searching for a lower bound of 'jack'. case insensitive, inclusive.  so searching for something >= 'JACK'. Should Return 1, but got" + size, 1 == size);

        // begin long attribute value testing
        // inclusive = true
        String searchAttributeLongKey = TestXMLSearchableAttributeLong.SEARCH_STORAGE_KEY;
        Long searchAttributeLongValue = TestXMLSearchableAttributeLong.SEARCH_STORAGE_VALUE.longValue();
        Long longValueToUse = null;

        // test lower bound only
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        longValueToUse = searchAttributeLongValue; // lowerbound == value
        addSearchableAttributeRange(criteria, searchAttributeLongKey, longValueToUse.toString(), null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() - 1); // lowerbound below value
        addSearchableAttributeRange(criteria, searchAttributeLongKey, longValueToUse.toString(), null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() + 1); // lowerbound is above value
        addSearchableAttributeRange(criteria, searchAttributeLongKey, longValueToUse.toString(), null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        // test upper bound only
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        longValueToUse = searchAttributeLongValue; // upper bound == value
        addSearchableAttributeRange(criteria, searchAttributeLongKey, null, longValueToUse.toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName); // upper bound < value
        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() - 1);
        addSearchableAttributeRange(criteria, searchAttributeLongKey, null, longValueToUse.toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName); // upper bound > value
        longValueToUse = Long.valueOf(searchAttributeLongValue.longValue() + 1);
        addSearchableAttributeRange(criteria, searchAttributeLongKey, null, longValueToUse.toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        // test both bounds
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // lowerbound == upperbound == value
        addSearchableAttributeRange(criteria, searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue())
                .toString(), Long.valueOf(searchAttributeLongValue.longValue()).toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // lower and upper bound > value
        addSearchableAttributeRange(criteria, searchAttributeLongKey, Long.valueOf(
                searchAttributeLongValue.longValue() + 2).toString(), Long.valueOf(
                searchAttributeLongValue.longValue() + 4).toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // lower and upper bound < value
        addSearchableAttributeRange(criteria, searchAttributeLongKey, Long.valueOf(
                searchAttributeLongValue.longValue() - 2).toString(), Long.valueOf(
                searchAttributeLongValue.longValue() - 4).toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // lower bound < value, upper bound > value
        addSearchableAttributeRange(criteria, searchAttributeLongKey, Long.valueOf(
                searchAttributeLongValue.longValue() - 2).toString(), Long.valueOf(
                searchAttributeLongValue.longValue() + 2).toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // upper < lower
        addSearchableAttributeRange(criteria, searchAttributeLongKey, Long.valueOf(searchAttributeLongValue.longValue() + 2).toString(), Long.valueOf(searchAttributeLongValue.longValue() - 2).toString(), true);
        try {
            // KULRICE-5630 fails because SGXSearchableAttribute does detect ranges correctly yet
            docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
            //fail("Error should have been thrown for invalid range");
        } catch (WorkflowServiceErrorException e) {}

        // begin float attribute value testing
        String searchAttributeFloatKey = TestXMLSearchableAttributeFloat.SEARCH_STORAGE_KEY;
        BigDecimal searchAttributeFloatValue = TestXMLSearchableAttributeFloat.SEARCH_STORAGE_VALUE;

        BigDecimal floatValueToUse = null;
        // test lower bound only
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue; // lower bound == value (does not match float)
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, floatValueToUse.toString(), null, false);
        // FIXME: KULRICE-5630 SGXSA does not interpret >= correctly, compares null lower to upper bound and fails validation of the range
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue.subtract(BigDecimal.ONE); // lowerbound < value
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, floatValueToUse.toString(), null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue.add(BigDecimal.ONE); // lowerbound > value
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, floatValueToUse.toString(), null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        // test upper bound only
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue; // upperbound == value (does not match float)
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, null, floatValueToUse.toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue.subtract(BigDecimal.ONE); // upperbound < value
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, null, floatValueToUse.toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        floatValueToUse = searchAttributeFloatValue.add(BigDecimal.ONE); // upperbound > value
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, null, floatValueToUse.toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        // test both bounds
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName); // upper == lower == value
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, searchAttributeFloatValue.toString(),
                searchAttributeFloatValue.toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // upper and lower > value
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, (searchAttributeFloatValue.add(new BigDecimal(
                2))).toString(), (searchAttributeFloatValue.add(new BigDecimal(4))).toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // upper and lower < value
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, (searchAttributeFloatValue.subtract(
                new BigDecimal(4))).toString(), (searchAttributeFloatValue.subtract(new BigDecimal(2))).toString(),
                true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // lower < value, upper > value
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, (searchAttributeFloatValue.subtract(new BigDecimal(2))).toString(), (searchAttributeFloatValue.add(new BigDecimal(2))).toString(), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        // upper < lower
        addSearchableAttributeRange(criteria, searchAttributeFloatKey, (searchAttributeFloatValue.add(new BigDecimal(
                2))).toString(), (searchAttributeFloatValue.subtract(new BigDecimal(2))).toString(), true);
        try {
            docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
            //fail("Error should have been thrown for invalid range");
        } catch (WorkflowServiceErrorException e) {}

        // begin datetime attribute value testing
        // inclusive = ?
        String searchAttributeDateTimeKey = TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_KEY;
        Calendar searchAttributeDateTimeValue = TestXMLSearchableAttributeDateTime.SEARCH_STORAGE_VALUE.toGregorianCalendar();

        Calendar calendarValueToUse = null;
        // test lower bound only
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        String valueToSearch = DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(
                calendarValueToUse));
        // lower == value (TODO: no match? because inclusivity false?)
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, valueToSearch, null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, -1); // lower < value
        valueToSearch = DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(
                calendarValueToUse));
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, valueToSearch, null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, 1); // lower > value
        valueToSearch = DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(
                calendarValueToUse));
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, valueToSearch, null, false);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        // test upper bound only
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone(); // upper == value (inclusivity true)
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, null,
                DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse)), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, -1); // upper < value
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, null, DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse)), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        calendarValueToUse = (Calendar) searchAttributeDateTimeValue.clone();
        calendarValueToUse.add(Calendar.DATE, 1); // upper > value
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, null,
                DocumentSearchInternalUtils.getDisplayValueWithDateOnly(SQLUtils.convertCalendar(calendarValueToUse)), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        // test both bounds
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        Calendar lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        Calendar upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone(); // upper == lower == value (inclusivity true)
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)), DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zer documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, 2);
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, 4);  // upper and lower > value
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)), DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, -4);
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, -2); // upper and lower < value
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)), DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have zero documents.", 0, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, -2);
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, 2);  // lower < value, upper > value
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)), DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName);
        lowerBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        lowerBoundValue.add(Calendar.DATE, 2);
        upperBoundValue = (Calendar) searchAttributeDateTimeValue.clone();
        upperBoundValue.add(Calendar.DATE, -2); // lower > upper == error
        addSearchableAttributeRange(criteria, searchAttributeDateTimeKey, DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(lowerBoundValue)), DocumentSearchInternalUtils
                .getDisplayValueWithDateOnly(SQLUtils.convertCalendar(upperBoundValue)), true);
        try {
            docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
            //fail("Error should have been thrown for invalid range");
        } catch (WorkflowServiceErrorException e) {}
    }

    /*
     * Tests the XML string attributes on range definitions, using a technique similar to that of the testSearchableAttributeRanges() unit test.
     */
    @Test public void testRangeDefinitionStringAttributes() throws Exception {
        String documentTypeName = "RangeDefinitionTestDocType";
    	DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        String principalName = "rkirkend";
        String principalId = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName).getPrincipalId();
        WorkflowDocument workflowDocument = WorkflowDocumentFactory.createDocument(principalId, documentTypeName);

        // adding inclusive-lower-bound searchable attribute
        WorkflowAttributeDefinition.Builder inclusiveLowerXMLDef = WorkflowAttributeDefinition.Builder.create("TextFieldWithInclusiveLower");
        inclusiveLowerXMLDef.addPropertyDefinition("textFieldWithInclusiveLower", "newvalue");
        workflowDocument.addSearchableDefinition(inclusiveLowerXMLDef.build());
        // adding case-sensitive searchable attribute
        WorkflowAttributeDefinition.Builder caseSensitiveXMLDef = WorkflowAttributeDefinition.Builder.create("TextFieldWithCaseSensitivity");
        caseSensitiveXMLDef.addPropertyDefinition("textFieldWithCaseSensitivity", "thevalue");
        workflowDocument.addSearchableDefinition(caseSensitiveXMLDef.build());
        // adding searchable attribute with overridden properties
        WorkflowAttributeDefinition.Builder overridesXMLDef = WorkflowAttributeDefinition.Builder.create("TextFieldWithOverrides");
        overridesXMLDef.addPropertyDefinition("textFieldWithOverrides", "SomeVal");
        workflowDocument.addSearchableDefinition(overridesXMLDef.build());

        workflowDocument.setTitle("Range Def Test");
        workflowDocument.route("routing range def doc.");

        workflowDocument = WorkflowDocumentFactory.loadDocument(principalId, workflowDocument.getDocumentId());

        // Verify that the "TextFieldWithInclusiveLower" attribute behaves as expected (lower-bound-inclusive and (by default) case-insensitive).
        /* upper bound is set to inclusive in assertSearchBehavesAsExpected, second case should match */
        assertSearchBehavesAsExpected(docType, principalId, "textFieldWithInclusiveLower",
                new String[] { "newvalue", ""        , ""        , "NEWVALUD", "newValuf", "newValuj", "newvaluf"},
                new String[] { ""        , "newvalue", "Newvaluf", "NEWVALUF", "newValud", "NEWVALUK", ""        },
                new int[]    { 1         , 1         , 1         , 1         , -1        , 0         , 0         });

        // Verify that the "TextFieldWithCaseSensitivity" attribute behaves as expected (bound-inclusive and case-sensitive).
        assertSearchBehavesAsExpected(docType, principalId, "textFieldWithCaseSensitivity",
        		new String[] { "thevalue", ""        , ""        , "THEVALUD", "thevalud", "Thevalud", "THEVALUF"},
        		new String[] { ""        , "thevalue", "Thevalue", "THEVALUF", "THEVALUF", "Thevaluf", ""        },
        		new int[]    { 1         , 1         , 0         , 0         , -1        , 0         , 1         });

        // Verify that the "TextFieldWithOverrides" attribute behaves as expected
        // FIXME: KULRICE-5630 need to update these tests to specify expressions instead of using addSearchableAttribute range, which does not
        // support exclusive lower
        assertSearchBehavesAsExpected(docType, principalId, "textFieldWithOverrides",
        		new String[] { "someval", "SomeVal", ""       , ""       , "SOMEVAK", "SomeVam", "SOMEVAM", "somevak", ""       },
        		new String[] { ""       , ""       , "SOMEVAL", "SomeVal", "SomeVam", "SOMEVAK", "SomeVak", ""       , "SomeVak"},
        		new int[]    { 0        , 0        , 0        , 1        , 1        , 0       , 0        , 1        , 0        });
    }

    /*
     * A convenience method for performing document-searching operations involving range definitions. The array parameters must all be the same length,
     * since this method will perform tests with the values given by entries located at the same indices.
     * @param docType The document type to search for.
     * @param principalId The ID of the user that will perform the search.
     * @param fieldDefKey The name of the field given by the field definition on the searchable attribute.
     * @param lowBounds The lower bounds to use in the tests; to ignore a lower bound for a test, use an empty String.
     * @param upBounds The upper bounds to use in the tests; to ignore an upper bound for a test, use an empty String.
     * @param resultSizes The expected number of documents to be returned by the search; use -1 to indicate that an exception should have occurred.
     * @throws Exception
     */
    private void assertSearchBehavesAsExpected(DocumentType docType, String principalId, String fieldDefKey, String[] lowBounds, String[] upBounds,
    		int[] resultSizes) throws Exception {
        DocumentSearchCriteria.Builder criteria = null;
        DocumentSearchResults results = null;
        DocumentSearchService docSearchService = KEWServiceLocator.getDocumentSearchService();
        for (int i = 0; i < resultSizes.length; i++) {
        	criteria = DocumentSearchCriteria.Builder.create();
        	criteria.setDocumentTypeName(docType.getName());
            addSearchableAttributeRange(criteria, fieldDefKey, lowBounds[i], upBounds[i], true);
        	try {
        		results = docSearchService.lookupDocuments(principalId, criteria.build());
        		if (resultSizes[i] < 0) {
        			fail(fieldDefKey + "'s search at loop index " + i + " should have thrown an exception");
        		}
        		assertEquals(fieldDefKey
                        + "'s search results at loop index "
                        + i
                        + " returned the wrong number of documents.", resultSizes[i], results.getSearchResults().size());
        	} catch (Exception ex) {
        		if (resultSizes[i] >= 0) {
        			fail(fieldDefKey + "'s search at loop index " + i + " should not have thrown an exception");
        		}
        	}
        }
    }
}
