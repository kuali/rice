/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.workflow;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.test.document.AccountWithDDAttributesDocument;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.test.KNSTestCase;

/**
 * This class performs various DataDictionarySearchableAttribute-related tests on the doc search, including verification of proper wildcard functionality. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataDictionarySearchableAttributeTest extends KNSTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }
    
    enum DOCUMENT_FIXTURE {
    	NORMAL_DOCUMENT(new Integer(1234567890), "John Doe", new KualiDecimal(501.77), createDate(2009, Calendar.OCTOBER, 15), createTimestamp(2009, Calendar.NOVEMBER, 1, 0, 0, 0), "SecondState", true),
    	NEGATIVE_NUMBERS_DOCUMENT(new Integer(-42), "Jane Doe", new KualiDecimal(-100), createDate(2009, Calendar.OCTOBER, 16), createTimestamp(2015, Calendar.NOVEMBER, 2, 0, 0, 0), "FirstState", true),
    	FALSE_AWAKE_DOCUMENT(new Integer(987654321), "John D'oh", new KualiDecimal(0.0), createDate(2006, Calendar.OCTOBER, 17), createTimestamp(1900, Calendar.NOVEMBER, 3, 0, 0, 0), "FourthState", false),
    	ODD_NAME_DOCUMENT(new Integer(88), " ", new KualiDecimal(10000051.0), createDate(2009, Calendar.OCTOBER, 18), createTimestamp(2009, Calendar.NOVEMBER, 4, 0, 0, 0), "FourthState", true),
    	ODD_TIMESTAMP_DOCUMENT(new Integer(9000), "Shane Kloe", new KualiDecimal(4.54), createDate(2012, Calendar.OCTOBER, 19), createTimestamp(2007, Calendar.NOVEMBER, 5, 12, 4, 38), "ThirdState", false),
    	ANOTHER_ODD_NAME_DOCUMENT(new Integer(1234567889), "---", new KualiDecimal(501), createDate(2009, Calendar.APRIL, 20), createTimestamp(2009, Calendar.NOVEMBER, 6, 12, 59, 59), "ThirdState", true),
    	INVALID_STATE_DOCUMENT(new Integer(99999), "AAAAAAAAA", new KualiDecimal(2.22), createDate(2009, Calendar.OCTOBER, 21), createTimestamp(2009, Calendar.NOVEMBER, 7, 0, 0, 1), "SeventhState", true),
    	WILDCARD_NAME_DOCUMENT(new Integer(1), "Sh*ne><K!=e?", new KualiDecimal(771.05), createDate(2054, Calendar.OCTOBER, 22), createTimestamp(2008, Calendar.NOVEMBER, 8, 12, 0, 0), "FirstState", true);
    	
    	private Integer accountNumber;
    	private String accountOwner;
    	private KualiDecimal accountBalance;
    	private Date accountOpenDate;
    	private Timestamp accountUpdateDateTime;
    	private String accountState;
    	private boolean accountAwake;
    	
    	private DOCUMENT_FIXTURE(Integer accountNumber, String accountOwner, KualiDecimal accountBalance, Date accountOpenDate, Timestamp accountUpdateDateTime, String accountState, boolean accountAwake) {
    		this.accountNumber = accountNumber;
    		this.accountOwner = accountOwner;
    		this.accountBalance = accountBalance;
    		this.accountOpenDate = accountOpenDate;
    		this.accountUpdateDateTime = accountUpdateDateTime;
    		this.accountState = accountState;
    		this.accountAwake = accountAwake;
    	}
    	
    	public AccountWithDDAttributesDocument getDocument(DocumentService docService) throws WorkflowException {
    		AccountWithDDAttributesDocument acctDoc = (AccountWithDDAttributesDocument) docService.getNewDocument("AccountWithDDAttributes");
    		acctDoc.setAccountNumber(this.accountNumber);
    		acctDoc.setAccountOwner(this.accountOwner);
    		acctDoc.setAccountBalance(this.accountBalance);
    		acctDoc.setAccountOpenDate(this.accountOpenDate);
    		acctDoc.setAccountUpdateDateTime(this.accountUpdateDateTime);
    		acctDoc.setAccountState(this.accountState);
    		acctDoc.setAccountAwake(this.accountAwake);
    		
    		return acctDoc;
    	}
    }
	
	/**
	 * Tests the use of multi-select and wildcard searches to ensure that they function correctly for DD searchable attributes on the doc search.
	 */ 
    @Ignore("Requires the creation of the ACCT_DD_ATTR_DOC table beforehand, and is still incomplete.")
    @Test
	public void testWildcardsAndMultiSelectsOnDDSearchableAttributes() throws Exception {
		DocumentService docService = KNSServiceLocator.getDocumentService();
		//docSearchService = KEWServiceLocator.getDocumentSearchService();
		DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName("AccountWithDDAttributes");
        String principalName = "quickstart";
        String principalId = KIMServiceLocator.getPersonService().getPersonByPrincipalName(principalName).getPrincipalId();
		
        // Route some test documents.
		docService.routeDocument(DOCUMENT_FIXTURE.NORMAL_DOCUMENT.getDocument(docService), "Routing NORMAL_DOCUMENT", null);
		docService.routeDocument(DOCUMENT_FIXTURE.NEGATIVE_NUMBERS_DOCUMENT.getDocument(docService), "Routing NEGATIVE_NUMBERS_DOCUMENT", null);
		docService.routeDocument(DOCUMENT_FIXTURE.FALSE_AWAKE_DOCUMENT.getDocument(docService), "Routing FALSE_AWAKE_DOCUMENT", null);
		docService.routeDocument(DOCUMENT_FIXTURE.ODD_NAME_DOCUMENT.getDocument(docService), "Routing ODD_NAME_DOCUMENT", null);
		docService.routeDocument(DOCUMENT_FIXTURE.ODD_TIMESTAMP_DOCUMENT.getDocument(docService), "Routing ODD_TIMESTAMP_DOCUMENT", null);
		docService.routeDocument(DOCUMENT_FIXTURE.ANOTHER_ODD_NAME_DOCUMENT.getDocument(docService), "Routing ANOTHER_ODD_NAME_DOCUMENT", null);
		docService.routeDocument(DOCUMENT_FIXTURE.INVALID_STATE_DOCUMENT.getDocument(docService), "Routing INVALID_STATE_DOCUMENT", null);
		docService.routeDocument(DOCUMENT_FIXTURE.WILDCARD_NAME_DOCUMENT.getDocument(docService), "Routing WILDCARD_NAME_DOCUMENT", null);
		
		// Ensure that DD searchable attribute integer fields function correctly when searched on.
		// TODO: Create a new integer validation pattern that supports negative integers?
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountNumber",
				new String[] {"!1234567890", "*567*", "9???9", ">1", "987654321|1234567889", "<100", ">=99999", "<=-42", ">9000|<=1", "<0|>=1234567890",
						">1234567889&&<1234567890", ">=88&&<=99999", "-42|>-10&&<10000", "9000..1000000", "-100..100|>1234567889", "0..10000&&>50"},
				new int[]    {1            , 0/*-1*/, 0/*-1*/, 6   , 2                     , 3     , 4        , -1/*1*/, 6          , 2,
						0                         , 3              , -1/*4*/           , 2              , -1/*4*/                , 2});
		
		// Ensure that DD searchable attribute string fields function correctly when searched on.
		// TODO: Verify how whitespace field values and wildcard-filled field values in the database should be treated when searching.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountOwner",
				new String[] {"!John Doe", "!John*", "!John Doe&&!Shane Kloe", "!Jane ???", "!Jane Doe!John Doe", " ", " |---", "Sh*ne><K!=e",
						">Jane Doe", "<Shane Kloe", ">=Johnny", "<=John D'oh", ">John Doe|<---", ">=AAAAAAAAA&&<=Jane Doe", ">---&&!John D'oh",
						"<Shane Kloe&&!John*", "*oe", "???? Doe", "Jane Doe..John Doe", "AAAAAAAAA..Shane Kloe&&!John Doe", "John D'oh|---..Jane Doe"},
				new int[]    {7          , 6       , 6                       , 7          , 6                   , 8  , 1      , 8,
						4          , 7            , 2         , 5            , 3               , 2                        , 5,
						5                    , 3    , 2         , 3                   , 5                                 , 4});
		
		// Ensure that DD searchable attribute float fields function correctly when searched on. Also ensure that the CurrencyFormatter is working.
		// TODO: Verify if "?" and "*" should cause exceptions (for integers too), like with StandardGenericXMLSearchableAttributes that are numbers.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountBalance",
				new String[] {"501.??", "*.54" , "!2.22", "10000051.0|771.05", "<0.0", ">501", "<=4.54", ">=-99.99", ">4.54|<=-1", ">=0&&<501.77",
						"<=0|>=10000051", ">501&&<501.77", "-100|>771.05", "2.22..501", "-100..4.54&&<=0", "2.22|501.77..10000051.0", "Zero",
						"-$100", "<(501)&&>=($2.22)", "$4.54|<(1)", "($0.00)..$771.05", ">=$(500)", ")501(", "4.54$"},
				new int[]    {1/*-1*/ , 0/*-1*/, 1      , 2                  , 1     , 3     , 4       , 7         , 5           , 4,
						3               , 0              , 2             , 3          , 2                , 4                        , -1,
						1      , 2                  , 3           , 6                 , -1        , -1     , -1});
		
		// Ensure that DD searchable attribute date fields function correctly when searched on.
		// Test Dates: 10/15/2009, 10/16/2009, 10/17/2006, 10/18/2009, 10/19/2012, 04/20/2009, 10/21/2009, 10/22/2054
		// TODO: Determine whether our existing allowable timestamp formats need additions (for instance, the ">10/17/06" case fails). 
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountOpenDate",
				new String[] {"!10/15/2009", "Unknown", "10/15/2009|10/21/2009", "10/22/????", "*/*/05", ">10/17/06", "<=12/31/2009&&>=10/16/2009",
						">10/18/2009&&<10/20/2012", ">=10/22/2054|<10/16/2009", ">02/29/12|<=10/21/09", "<2009", ">=10/19/2012|04/20/09", ">02/29/09",
						"10/15/2009..10/21/2009", "01/01/2009..10/20/2009|10/22/2054", "<=06/32/03", ">2008&&<2011|10/17/06"},
				new int[]    {-1           , -1       , 2                      , -1          , -1      , 7          , 3,
						2                         , 3                         , 8                     , 1      , 3                      , -1,
						4                       , 5                                  , -1          , 6});
		/*
		// Ensure that DD searchable attribute multi-select fields function correctly when searched on.
		// Validation is still broken at the moment (see KULRICE-3681), but this part at least tests the multi-select SQL generation.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountStateMultiselect",
				new String[][] {{"FirstState"}, {"SecondState"}, {"ThirdState","FourthState"}, {"SecondState","ThirdState"}},
				new int[]      {0             , 1              , 0                           , 1});
		
		// Ensure that DD searchable attribute boolean fields function correctly when searched on.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountAwake",
				new String[] {"Y", "N"},
				new int[]    {1  , 0});
		
		// Ensure that DD searchable attribute timestamp fields function correctly when searched on.
		// Test Timestamps: 11/01/2009 00:00:00, 11/02/2015 00:00:00, 11/03/1900 00:00:00, 11/04/2009 00:00:00, 11/05/2007 12:04:38, 11/06/2009 12:59:59, 11/07/2009 00:00:01, 11/08/2008 12:00:00
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountUpdateDateTime",
				new String[] {"11/01/2009", "02/31/2008", "<01/01/2010"},
				new int[]    {1           , -1          , 1});*/
	}
    
    /**
     * Creates a date quickly
     * 
     * @param year the year of the date
     * @param month the month of the date
     * @param day the day of the date
     * @return a new java.sql.Date initialized to the precise date given
     */
    private static Date createDate(int year, int month, int day) {
    	Calendar date = Calendar.getInstance();
		date.set(year, month, day, 0, 0, 0);
		return new java.sql.Date(date.getTimeInMillis());
    }
    
    /**
     * Utility method to create a timestamp quickly
     * 
     * @param year the year of the timestamp
     * @param month the month of the timestamp
     * @param day the day of the timestamp
     * @param hour the hour of the timestamp
     * @param minute the minute of the timestamp
     * @param second the second of the timestamp
     * @return a new java.sql.Timestamp initialized to the precise time given
     */
    private static Timestamp createTimestamp(int year, int month, int day, int hour, int minute, int second) {
    	Calendar date = Calendar.getInstance();
    	date.set(year, month, day, hour, minute, second);
    	return new java.sql.Timestamp(date.getTimeInMillis());
    }
	
	/*
	 * A method similar to the one from DocumentSearchTestBase. The "value" parameter has to be either a String or a String[].
	 */
	private SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,Object value,Boolean isLowerBoundValue,DocumentType docType) {
		String formKey = (isLowerBoundValue == null) ? key : ((isLowerBoundValue != null && isLowerBoundValue.booleanValue()) ? SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + key : SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + key);
		String savedKey = key;
		SearchAttributeCriteriaComponent sacc = null;
		if (value instanceof String) {
			sacc = new SearchAttributeCriteriaComponent(formKey,(String)value,savedKey);
		} else {
			sacc = new SearchAttributeCriteriaComponent(formKey,null,savedKey);
			sacc.setValues(Arrays.asList((String[])value));
		}
		Field field = getFieldByFormKey(docType, formKey);
		if (field != null) {
			sacc.setSearchableAttributeValue(DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType()));
			sacc.setRangeSearch(field.isMemberOfRange());
			sacc.setCaseSensitive(!field.isUpperCase());
			sacc.setSearchInclusive(field.isInclusive());
			sacc.setSearchable(field.isIndexedForSearch());
			sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
		}
		return sacc;
	}
	
	/*
	 * A method that was copied from DocumentSearchTestBase.
	 */
	private Field getFieldByFormKey(DocumentType docType, String formKey) {
		if (docType == null) {
			return null;
		}
		for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
			for (Row row : searchableAttribute.getSearchingRows(DocSearchUtils.getDocumentSearchContext("", docType.getName(), ""))) {
				for (org.kuali.rice.kns.web.ui.Field field : row.getFields()) {
					if (field instanceof Field) {
						if (field.getPropertyName().equals(formKey)) {
							return (Field)field;
						}
					} else {
						throw new RiceRuntimeException("Fields must be of type org.kuali.rice.kns.Field");
					}
				}
			}
		}
		return null;
	}
	
    /**
     * A convenience method for testing wildcards on data dictionary searchable attributes.
     *
     * @param docType The document type containing the attributes.
     * @param principalId The ID of the user performing the search.
     * @param fieldName The name of the field on the test document.
     * @param searchValues The search expressions to test. Has to be a String array (for regular fields) or a String[] array (for multi-select fields).
     * @param resultSizes The number of expected documents to be returned by the search; use -1 to indicate that an error should have occurred.
     * @throws Exception
     */
    private void assertDDSearchableAttributeWildcardsWork(DocumentType docType, String principalId, String fieldName, Object[] searchValues,
    		int[] resultSizes) throws Exception {
    	if (!(searchValues instanceof String[]) && !(searchValues instanceof String[][])) {
    		throw new IllegalArgumentException("'searchValues' parameter has to be either a String[] or a String[][]");
    	}
    	DocSearchCriteriaDTO criteria = null;
        DocumentSearchResultComponents result = null;
        List<DocumentSearchResult> searchResults = null;
        DocumentSearchService docSearchService = KEWServiceLocator.getDocumentSearchService();
        for (int i = 0; i < resultSizes.length; i++) {
        	criteria = new DocSearchCriteriaDTO();
        	criteria.setDocTypeFullName(docType.getName());
        	criteria.addSearchableAttribute(this.createSearchAttributeCriteriaComponent(fieldName, searchValues[i], null, docType));
        	try {
        		result = docSearchService.getList(principalId, criteria);
        		searchResults = result.getSearchResults();
        		if (resultSizes[i] < 0) {
        			fail(fieldName + "'s search at loop index " + i + " should have thrown an exception");
        		}
        		if(resultSizes[i] != searchResults.size()){
        			assertEquals(fieldName + "'s search results at loop index " + i + " returned the wrong number of documents.", resultSizes[i], searchResults.size());
        		}
        	} catch (Exception ex) {
        		if (resultSizes[i] >= 0) {
        			fail(fieldName + "'s search at loop index " + i + " should not have thrown an exception");
        		}
        	}
        	GlobalVariables.clear();
        }
    }
}
