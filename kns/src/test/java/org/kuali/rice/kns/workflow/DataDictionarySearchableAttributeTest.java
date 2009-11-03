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

import java.sql.Timestamp;
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
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.test.document.AccountWithDDAttributesDocument;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.test.KNSTestCase;

/**
 * This class performs various DataDictionarySearchableAttribute-related tests, including verification of proper wildcard functionality. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataDictionarySearchableAttributeTest extends KNSTestCase {

	private DocumentService docService;
	private DocumentSearchService docSearchService;
	
	/**
	 * Tests the use of multi-select and wildcard searches to ensure that they function correctly for DD searchable attributes.
	 */
	@Ignore("Needs to be reworked to rely on bootstrapped data!")
	@Test public void testWildcardsAndMultiSelectsOnDDSearchableAttributes() throws Exception {
		docService = KNSServiceLocator.getDocumentService();
		docSearchService = KEWServiceLocator.getDocumentSearchService();
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
		acctUpdateDate.set(2009, 10, 15);
		acctDoc.setAccountUpdateDateTime(new java.sql.Timestamp(acctUpdateDate.getTimeInMillis()));
		acctDoc.setAccountAwake(false);
		docService.routeDocument(acctDoc, "Routing Document #1", null);
		
		DocSearchCriteriaDTO searchCriteria = new DocSearchCriteriaDTO();
		searchCriteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("accountBalance", "501.77", false, docType));
		docSearchService.validateDocumentSearchCriteria(docSearchService.getStandardDocumentSearchGenerator(), searchCriteria);
		DocumentSearchResultComponents results = docSearchService.getList(principalId, searchCriteria);
		List<DocumentSearchResult> resultList = results.getSearchResults();
		assertEquals("Should have retrieved one document.", 1, resultList.size());
		
		searchCriteria = new DocSearchCriteriaDTO();
		searchCriteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("accountAwake", "Y", false, docType));
		docSearchService.validateDocumentSearchCriteria(docSearchService.getStandardDocumentSearchGenerator(), searchCriteria);
		results = docSearchService.getList(principalId, searchCriteria);
		resultList = results.getSearchResults();
		assertEquals("Should have retrieved one document.", 1, resultList.size());
		
		searchCriteria = new DocSearchCriteriaDTO();
		searchCriteria.addSearchableAttribute(createSearchAttributeCriteriaComponent("accountAwake", "N", false, docType));
		docSearchService.validateDocumentSearchCriteria(docSearchService.getStandardDocumentSearchGenerator(), searchCriteria);
		results = docSearchService.getList(principalId, searchCriteria);
		resultList = results.getSearchResults();
		assertEquals("Should not retrieve any documents.", 0, resultList.size());
	}
	
	/* Method copied from DocumentSearchTestBase. */
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
	
	/* Method copied from DocumentSearchTestBase. */
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
}
