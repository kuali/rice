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
	
	/**
	 * Tests the use of multi-select and wildcard searches to ensure that they function correctly for DD searchable attributes on the doc search.
	 */
    @Ignore("Currently throws an exception when attempting to route the test document")
	@Test
	public void testWildcardsAndMultiSelectsOnDDSearchableAttributes() throws Exception {
		DocumentService docService = KNSServiceLocator.getDocumentService();
		//docSearchService = KEWServiceLocator.getDocumentSearchService();
		DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName("AccountWithDDAttributes");
        String principalName = "quickstart";
        String principalId = KIMServiceLocator.getPersonService().getPersonByPrincipalName(principalName).getPrincipalId();
		
		AccountWithDDAttributesDocument acctDoc = (AccountWithDDAttributesDocument) docService.getNewDocument("AccountWithDDAttributes");
		acctDoc.setAccountNumber(1234567890);
		acctDoc.setAccountOwner("John Doe");
		acctDoc.setAccountBalance(new KualiDecimal(501.77));
		Calendar acctDate = Calendar.getInstance();
		acctDate.set(2009, 10, 15);
		acctDoc.setAccountOpenDate(new java.sql.Date(acctDate.getTimeInMillis()));
		acctDoc.setAccountState("SecondState");
		Calendar acctUpdateDate = Calendar.getInstance();
		acctUpdateDate.set(2009, 11, 01);
		acctDoc.setAccountUpdateDateTime(new java.sql.Timestamp(acctUpdateDate.getTimeInMillis()));
		acctDoc.setAccountAwake(true);
		docService.routeDocument(acctDoc, "Routing Document #1", null);
		
		// Ensure that DD searchable attribute integer fields function correctly when searched on.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountNumber",
				new String[] {"1234567890"},
				new int[]    {1});
		
		// Ensure that DD searchable attribute string fields function correctly when searched on.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountOwner",
				new String[] {"John Doe"},
				new int[]    {1});
		
		// Ensure that DD searchable attribute float fields function correctly when searched on.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountBalance",
				new String[] {"501.77"},
				new int[]    {1});
		
		// Ensure that DD searchable attribute date fields function correctly when searched on.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountOpenDate",
				new String[] {"10/15/2009"},
				new int[]    {1});
		
		// Ensure that DD searchable attribute multi-select fields function correctly when searched on.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountState",
				new String[] {"FirstState", "SecondState"},
				new int[]    {0           , 1});
		
		// Ensure that DD searchable attribute boolean fields function correctly when searched on.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountAwake",
				new String[] {"Y", "N"},
				new int[]    {1  , 0});
		
		// Ensure that DD searchable attribute timestamp fields function correctly when searched on.
		assertDDSearchableAttributeWildcardsWork(docType, principalId, "accountUpdateDateTime",
				new String[] {"11/01/2009"},
				new int[]    {1});
	}
	
	/*
	 * A method that was copied from DocumentSearchTestBase.
	 */
	protected SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String value,Boolean isLowerBoundValue,DocumentType docType) {
		String formKey = (isLowerBoundValue == null) ? key : ((isLowerBoundValue != null && isLowerBoundValue.booleanValue()) ? SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + key : SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + key);
		String savedKey = key;
		SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(formKey,value,savedKey);
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
     * @param searchValues The wildcard-filled search strings to test.
     * @param resultSizes The number of expected documents to be returned by the search; use -1 to indicate that an error should have occurred.
     * @throws Exception
     */
    private void assertDDSearchableAttributeWildcardsWork(DocumentType docType, String principalId, String fieldName, String[] searchValues,
    		int[] resultSizes) throws Exception {
    	DocSearchCriteriaDTO criteria = null;
        DocumentSearchResultComponents result = null;
        List<DocumentSearchResult> searchResults = null;
        DocumentSearchService docSearchService = KEWServiceLocator.getDocumentSearchService();
        for (int i = 0; i < resultSizes.length; i++) {
        	criteria = new DocSearchCriteriaDTO();
        	criteria.setDocTypeFullName(docType.getName());
        	criteria.addSearchableAttribute(this.createSearchAttributeCriteriaComponent(fieldName, searchValues[i], null, docType));
        	try {
        		docSearchService.validateDocumentSearchCriteria(docSearchService.getStandardDocumentSearchGenerator(), criteria);
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
