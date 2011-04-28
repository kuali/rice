/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kns.workflow;

import org.junit.Test;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.test.document.SearchAttributeIndexTestDocument;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.test.KNSTestCase;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This is a description of what this class does - jksmith don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SearchAttributeIndexRequestTest extends KNSTestCase {
	static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SearchAttributeIndexRequestTest.class);
	final static String SEARCH_ATTRIBUTE_INDEX_DOCUMENT_TEST_DOC_TYPE = "SearchAttributeIndexTestDocument";
	
	enum DOCUMENT_FIXTURE {
		NORMAL_DOCUMENT("hippo","routing");
		
		private String constantString;
		private String routingString;
		private DOCUMENT_FIXTURE(String constantString, String routingString) {
			this.constantString = constantString;
			this.routingString = routingString;
		}
		
		public SearchAttributeIndexTestDocument getDocument(DocumentService documentService) throws Exception {
			SearchAttributeIndexTestDocument doc = (SearchAttributeIndexTestDocument)documentService.getNewDocument(SearchAttributeIndexRequestTest.SEARCH_ATTRIBUTE_INDEX_DOCUMENT_TEST_DOC_TYPE);
			doc.initialize(constantString, routingString);
			return doc;
		}
	}
	
	/**
	 * Tests that a document, which goes through a regular approval process, is indexed correctly
	 */
	@Test
	public void regularApproveTest() throws Exception {
		LOG.warn("message.delivery state: "+ KNSServiceLocator.getKualiConfigurationService().getPropertyString("message.delivery"));
		
		final DocumentService documentService = KNSServiceLocatorWeb.getDocumentService();
		final String principalName = "quickstart";
        final String principalId = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName).getPrincipalId();
        GlobalVariables.setUserSession(new UserSession(principalName));
        RouteContext.clearCurrentRouteContext();

		SearchAttributeIndexTestDocument document = DOCUMENT_FIXTURE.NORMAL_DOCUMENT.getDocument(documentService);
		document.getDocumentHeader().setDocumentDescription("Routed SAIndexTestDoc");
		final String documentNumber = document.getDocumentNumber();
		final DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(SearchAttributeIndexRequestTest.SEARCH_ATTRIBUTE_INDEX_DOCUMENT_TEST_DOC_TYPE);
		
		documentService.routeDocument(document, "Routed SearchAttributeIndexTestDocument", null);
		
		document = (SearchAttributeIndexTestDocument)documentService.getByDocumentHeaderId(documentNumber);
		DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(new Long(documentNumber));
						
		assertDDSearchableAttributesWork(docType,principalId,"routeLevelCount",
				new String[] {"1","0","2","7"},
				new int[] {1, 0, 0, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"constantString",
				new String[] {"hippo","monkey"},
				new int[] {1, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"routedString",
				new String[] {"routing","","hippo"},
				new int[] {1, 1, 0}
		);
		
		GlobalVariables.setUserSession(new UserSession("user1"));
		document = (SearchAttributeIndexTestDocument)documentService.getByDocumentHeaderId(documentNumber);
		documentService.approveDocument(document, "User1 approved document", null);
		
		routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(new Long(documentNumber));
						
		assertDDSearchableAttributesWork(docType,principalId,"routeLevelCount",
				new String[] {"1","0","2","7"},
				new int[] {0, 0, 1, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"constantString",
				new String[] {"hippo","monkey"},
				new int[] {1, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"routedString",
				new String[] {"routing","","hippo"},
				new int[] {1, 1, 0}
		);
		
		LOG.info("Read Access Count not at expected value: "+document.getReadAccessCount());
		
		GlobalVariables.setUserSession(new UserSession("user2"));
		document = (SearchAttributeIndexTestDocument)documentService.getByDocumentHeaderId(documentNumber);
		documentService.approveDocument(document, "User1 approved document", null);
		
		routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(new Long(documentNumber));
				
		assertDDSearchableAttributesWork(docType,principalId,"routeLevelCount",
				new String[] {"1","0","2","3","4","7"},
				new int[] {0, 0, 0, 1, 0, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"constantString",
				new String[] {"hippo","monkey"},
				new int[] {1, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"routedString",
				new String[] {"routing","","hippo"},
				new int[] {1, 1, 0}
		);
		
		LOG.info("Read Access Count not at expected value: "+document.getReadAccessCount());
		
		GlobalVariables.setUserSession(new UserSession("user3"));
		document = (SearchAttributeIndexTestDocument)documentService.getByDocumentHeaderId(documentNumber);
		documentService.approveDocument(document, "User3 approved document", null);
		
		routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(new Long(documentNumber));
						
		assertDDSearchableAttributesWork(docType,principalId,"routeLevelCount",
				new String[] {"1","0","2","3","4","7"},
				new int[] {0, 0, 0, 1, 0, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"constantString",
				new String[] {"hippo","monkey"},
				new int[] {1, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"routedString",
				new String[] {"routing","","hippo"},
				new int[] {1, 1, 0}
		);
		
		LOG.info("Read Access Count not at expected value: "+document.getReadAccessCount());
		
		GlobalVariables.setUserSession(null);
	}
	
	/**
	 * Tests that a blanket approved document is indexed correctly
	 */
	@Test
	public void blanketApproveTest() throws Exception {
		LOG.warn("message.delivery state: "+ KNSServiceLocator.getKualiConfigurationService().getPropertyString("message.delivery"));
		
		final DocumentService documentService = KNSServiceLocatorWeb.getDocumentService();
		final String principalName = "admin";
        final String principalId = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName).getPrincipalId();
        GlobalVariables.setUserSession(new UserSession(principalName));

		SearchAttributeIndexTestDocument document = DOCUMENT_FIXTURE.NORMAL_DOCUMENT.getDocument(documentService);
		document.getDocumentHeader().setDocumentDescription("Blanket Approved SAIndexTestDoc");
		final String documentNumber = document.getDocumentNumber();
		final DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(SearchAttributeIndexRequestTest.SEARCH_ATTRIBUTE_INDEX_DOCUMENT_TEST_DOC_TYPE);
				
		documentService.blanketApproveDocument(document, "Blanket Approved SearchAttributeIndexTestDocument", null);
		
		document = (SearchAttributeIndexTestDocument)documentService.getByDocumentHeaderId(documentNumber);
		DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(new Long(documentNumber));
						
		assertDDSearchableAttributesWork(docType,principalId,"routeLevelCount",
				new String[] {"1","0","2","3","7"},
				new int[] {0, 0, 0, 1, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"constantString",
				new String[] {"hippo","monkey"},
				new int[] {1, 0}
		);
		
		assertDDSearchableAttributesWork(docType,principalId,"routedString",
				new String[] {"routing","","hippo"},
				new int[] {1, 1, 0}
		);
		
		LOG.info("Read Access Count not at expected value: "+document.getReadAccessCount());
		
		GlobalVariables.setUserSession(null);
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
    private void assertDDSearchableAttributesWork(DocumentType docType, String principalId, String fieldName, Object[] searchValues,
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
    
    /*
	 * A method similar to the one from DocumentSearchTestBase. The "value" parameter has to be either a String or a String[].
	 */
	private SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,Object value,Boolean isLowerBoundValue,DocumentType docType) {
		String formKey = (isLowerBoundValue == null) ? key : ((isLowerBoundValue != null && isLowerBoundValue.booleanValue()) ? KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX + key : KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX + key);
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
}
