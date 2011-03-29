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
package org.kuali.rice.kew.docsearch;

import org.junit.Test;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.TestHarnessServiceLocator;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class DocumentSearchTest extends KEWTestCase {
    private static final String KREW_DOC_HDR_T = "KREW_DOC_HDR_T";
    private static final String INITIATOR_COL = "INITR_PRNCPL_ID";

    DocumentSearchService docSearchService;

    @Override
    protected void loadTestData() throws Exception {
        loadXmlFile("SearchAttributeConfig.xml");
        
    }

    @Override
    protected void setUpAfterDataLoad() throws Exception {
        docSearchService = (DocumentSearchService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
    }

    @Test public void testDocSearch() throws Exception {
        Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName("bmcgough");
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        List searchResults = null;
        DocumentSearchResultComponents result = null;
        criteria.setDocTitle("*IN");
        criteria.setNamedSearch("bytitle");
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTitle("*IN-CFSG");
        criteria.setNamedSearch("for in accounts");
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        criteria = new DocSearchCriteriaDTO();
        criteria.setFromDateApproved("09/16/2004");
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocRouteNodeId("3");
        criteria.setDocRouteNodeLogic("equal");
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        user = KIMServiceLocator.getPersonService().getPersonByPrincipalName("bmcgough");
        SavedSearchResult savedSearchResults = docSearchService.getSavedSearchResults(user.getPrincipalId(), "DocSearch.NamedSearch.bytitle");
        assertNotNull(savedSearchResults);
        assertNotNull(savedSearchResults.getSearchResult());
        savedSearchResults = docSearchService.getSavedSearchResults(user.getPrincipalId(), "DocSearch.NamedSearch.for in accounts");
        assertNotNull(savedSearchResults);
        assertNotNull(savedSearchResults.getSearchResult());
    }

    /**
     * Test for https://test.kuali.org/jira/browse/KULRICE-1968 - Document search fails when users are missing
     * Tests that we can safely search on docs whose initiator no longer exists in the identity management system
     * This test searches by doc type name criteria.
     * @throws Exception
     */
    @Test public void testDocSearch_MissingInitiator() throws Exception {
        String documentTypeName = "SearchDocType";
        DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "arh14";
        // route a document to enroute and route one to final
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), documentTypeName);
        workflowDocument.setTitle("testDocSearch_MissingInitiator");
        workflowDocument.routeDocument("routing this document.");

        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());

        // now nuke the initiator...
        new JdbcTemplate(TestHarnessServiceLocator.getDataSource()).execute("update " + KREW_DOC_HDR_T + " set " + INITIATOR_COL + " = 'bogus user' where DOC_HDR_ID = " + workflowDocument.getRouteHeaderId());


        Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName("jhopf");
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        DocumentSearchResultComponents result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 1, result.getSearchResults().size());
    }

    /**
     * Test for https://test.kuali.org/jira/browse/KULRICE-1968 - Tests that we get an error if we try and search on an initiator that doesn't exist in the IDM system
     * @throws Exception
     */
    @Test public void testDocSearch_SearchOnMissingInitiator() throws Exception {
        String documentTypeName = "SearchDocType";
        DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "arh14";
        // route a document to enroute and route one to final
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), documentTypeName);
        workflowDocument.setTitle("testDocSearch_MissingInitiator");
        workflowDocument.routeDocument("routing this document.");

        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());

        // now nuke the initiator...
        new JdbcTemplate(TestHarnessServiceLocator.getDataSource()).execute("update " + KREW_DOC_HDR_T + " set " + INITIATOR_COL + " = 'bogus user' where DOC_HDR_ID = " + workflowDocument.getRouteHeaderId());


        Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName("jhopf");
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setInitiator("bogus user");

        DocumentSearchResultComponents result = docSearchService.getList(user.getPrincipalId(), criteria);
        int size = result.getSearchResults().size();
        assertTrue("Searching by an invalid initiator should return nothing", size == 0);

    }

    @Test public void testDocSearch_RouteNodeName() throws Exception {
        loadXmlFile("DocSearchTest_RouteNode.xml");
        String documentTypeName = "SearchDocType_RouteNodeTest";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";

        // route a document to enroute and route one to final
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), documentTypeName);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());
        workflowDocument.approve("");
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsFinal());
        workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), documentTypeName);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());


        Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName(userNetworkId);
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        DocumentSearchResultComponents result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 2, result.getSearchResults().size());

        criteria.setDocRouteNodeId(getRouteNodeForSearch(documentTypeName,workflowDocument.getNodeNames()));
        criteria.setDocRouteNodeLogic("equal");
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 1, result.getSearchResults().size());

        // load the document type again to change the route node ids
        loadXmlFile("DocSearchTest_RouteNode.xml");

        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());
        criteria.setDocRouteNodeId(getRouteNodeForSearch(documentTypeName,workflowDocument.getNodeNames()));
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 1, result.getSearchResults().size());

    }

    private String getRouteNodeForSearch(String documentTypeName, String[] nodeNames) {
        assertEquals(1,	nodeNames.length);
	String expectedNodeName = nodeNames[0];
        List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName), true);
        for (Iterator iterator = routeNodes.iterator(); iterator.hasNext();) {
	    RouteNode node = (RouteNode) iterator.next();
	    if (expectedNodeName.equals(node.getRouteNodeName())) {
		return node.getRouteNodeName();
	    }
	}
        return null;
    }

    @Test public void testGetNamedDocSearches() throws Exception {
    	Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName("bmcgough");
        List namedSearches = docSearchService.getNamedSearches(user.getPrincipalId());
        assertNotNull(namedSearches);
    }

    @Test public void testDefaultCreateDateSearchCriteria() throws Exception {
        Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName("bmcgough");
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        DocumentSearchResultComponents result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull("Should have a date created value",criteria.getFromDateCreated());
        Calendar criteriaDate = Calendar.getInstance();
        criteriaDate.setTime(SQLUtils.convertStringDateToTimestamp(criteria.getFromDateCreated()));
        assertEquals("Criteria date minus today's date should equal the constant value", KEWConstants.DOCUMENT_SEARCH_NO_CRITERIA_CREATE_DATE_DAYS_AGO.doubleValue(), getDifferenceInDays(criteriaDate), 0);

        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTitle("testing");
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull("Should have a date created value",criteria.getFromDateCreated());
        criteriaDate = Calendar.getInstance();
        criteriaDate.setTime(SQLUtils.convertStringDateToTimestamp(criteria.getFromDateCreated()));
        assertEquals("Criteria date minus today's date should equal the constant value", KEWConstants.DOCUMENT_SEARCH_DOC_TITLE_CREATE_DATE_DAYS_AGO.doubleValue(), getDifferenceInDays(criteriaDate), 0);
    }

    private static double getDifferenceInDays(Calendar compareDate) {
        Calendar today = Calendar.getInstance();
        // First, get difference in whole days
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        compareDate.set(Calendar.HOUR_OF_DAY, 0);
        compareDate.set(Calendar.MINUTE, 0);
        compareDate.set(Calendar.SECOND, 0);
        compareDate.set(Calendar.MILLISECOND, 0);

        return (BigDecimal.valueOf(compareDate.getTimeInMillis()).subtract(BigDecimal.valueOf(today.getTimeInMillis()))).divide(BigDecimal.valueOf(24 * 60 * 60 * 1000.00), BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Test public void testUseWorkflowSuperUserDocHandlerPolicy() throws Exception {
        String customDocHandlerDocumentType = "SearchDocType";
        String standardDocHandlerDocumentType = "SearchDocType2";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(customDocHandlerDocumentType);
        String userNetworkId = "rkirkend";

        // route a document to enroute and route one to final
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), standardDocHandlerDocumentType);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());

        // route a document to enroute and route one to final
        workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), customDocHandlerDocumentType);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());
        workflowDocument.approve("");
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsFinal());

        workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), customDocHandlerDocumentType);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(getPrincipalId("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());

        Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName(userNetworkId);
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setSuperUserSearch("YES");
        criteria.setDocTypeFullName(customDocHandlerDocumentType);
        DocumentSearchResultComponents result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 2, result.getSearchResults().size());
        for (DocumentSearchResult resultElement : result.getSearchResults()) {
	    KeyValueSort kvs = resultElement.getResultContainer(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID);
	    assertNotNull("A valid column field value should be returned for key " + DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID, kvs);
	    assertTrue("The document handler redirect for the client should be included in the route header id url", kvs.getValue().contains(KEWConstants.DOC_HANDLER_REDIRECT_PAGE));
	    assertTrue("The document handler redirect for the client should include the command value for super user search", kvs.getValue().contains(KEWConstants.SUPERUSER_COMMAND));
	}

        user = KIMServiceLocator.getPersonService().getPersonByPrincipalName(userNetworkId);
        criteria = new DocSearchCriteriaDTO();
        criteria.setSuperUserSearch("YES");
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 3, result.getSearchResults().size());
        for (DocumentSearchResult resultElement : result.getSearchResults()) {
	    KeyValueSort routeHeaderIdValue = resultElement.getResultContainer(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID);
	    assertNotNull("A valid column field value should be returned for key " + DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID, routeHeaderIdValue);
	    KeyValueSort documentTypeValue = resultElement.getResultContainer(DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL);
	    assertNotNull("A valid column field value should be returned for key " + DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL, documentTypeValue);
	    if (customDocHandlerDocumentType.equals(documentTypeValue.getValue())) {
		assertTrue("The document handler redirect for the client should be included in the route header id url", routeHeaderIdValue.getValue().contains(KEWConstants.DOC_HANDLER_REDIRECT_PAGE));
		assertTrue("The document handler redirect for the client should include the command value for super user search", routeHeaderIdValue.getValue().contains(KEWConstants.SUPERUSER_COMMAND));
	    } else if (standardDocHandlerDocumentType.equals(documentTypeValue.getValue())) {
		assertTrue("The document handler redirect for the client should be included in the route header id url", !routeHeaderIdValue.getValue().contains(KEWConstants.DOC_HANDLER_REDIRECT_PAGE));
	    } else {
		fail("Found document search result row with document type '" + documentTypeValue.getValue() + "' that should not have existed");
	    }
	}
    }

    /**
     * Tests the usage of wildcards on the regular document search attributes.
     * @throws Exception
     */
    @Test public void testDocSearch_WildcardsOnRegularAttributes() throws Exception {
    	// TODO: Add some wildcard testing for the document type attribute once wildcards are usable with it.

    	// Route some test documents.
    	String docTypeName = "SearchDocType";
    	String[] principalNames = {"bmcgough", "quickstart", "rkirkend"};
    	String[] titles = {"The New Doc", "Document Number 2", "Some New Document"};
    	String[] docIds = new String[titles.length];
    	String[] appDocIds = {"6543", "5432", "4321"};
    	String[] approverNames = {null, "jhopf", null};
    	for (int i = 0; i < titles.length; i++) {
        	WorkflowDocument workflowDocument = new WorkflowDocument(
        			KIMServiceLocator.getPersonService().getPersonByPrincipalName(principalNames[i]).getPrincipalId(), docTypeName);
        	workflowDocument.setTitle(titles[i]);
        	workflowDocument.setAppDocId(appDocIds[i]);
        	workflowDocument.routeDocument("routing this document.");
        	docIds[i] = workflowDocument.getRouteHeaderId().toString();
        	if (approverNames[i] != null) {
        		workflowDocument.setPrincipalId(KIMServiceLocator.getPersonService().getPersonByPrincipalName(approverNames[i]).getPrincipalId());
        		workflowDocument.approve("approving this document.");
        	}
    	}
        String principalId = KIMServiceLocator.getPersonService().getPersonByPrincipalName("bmcgough").getPrincipalId();
        DocSearchCriteriaDTO criteria = null;
        List<DocumentSearchResult> searchResults = null;
        DocumentSearchResultComponents result = null;

        // Test the wildcards on the initiator attribute.
        String[] searchStrings = {"!quickstart", "!rkirkend!bmcgough", "!quickstart&&!rkirkend", "!admin", "user1", "quickstart|bmcgough",
        		"admin|rkirkend", ">bmcgough", ">=rkirkend", "<bmcgough", "<=quickstart", ">bmcgough&&<=rkirkend", "<rkirkend&&!bmcgough",
        		"?mc?oug?", "*t", "*i?k*", "*", "!quick*", "!b???????!?kirk???", "!*g*&&!*k*", ">bmc?ough", "<=quick*", "quickstart..rkirkend"};
        int[] expectedResults = {2, 1, 1, 3, 0, 2, 1, 2, 1, 0, 2, 2, 1, 1, 1, 2, 3, 2, 1, 0, 2, 1, 2/*1*/};
        for (int i = 0; i < searchStrings.length; i++) {
        	criteria = new DocSearchCriteriaDTO();
        	criteria.setInitiator(searchStrings[i]);
        	result = docSearchService.getList(principalId, criteria);
        	searchResults = result.getSearchResults();
        	assertEquals("Initiator search at index " + i + " retrieved the wrong number of documents.", expectedResults[i], searchResults.size());
        }

        // Test the wildcards on the approver attribute.
        searchStrings = new String[] {"jhopf","!jhopf", ">jhopf", "<jjopf", ">=quickstart", "<=jhopf", "jhope..jhopg", "?hopf", "*i*", "!*f", "j*"};
        expectedResults = new int[] {1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1};
        for (int i = 0; i < searchStrings.length; i++) {
        	criteria = new DocSearchCriteriaDTO();
        	criteria.setApprover(searchStrings[i]);
        	result = docSearchService.getList(principalId, criteria);
        	searchResults = result.getSearchResults();
        	assertEquals("Approver search at index " + i + " retrieved the wrong number of documents.", expectedResults[i], searchResults.size());
        }

        // Test the wildcards on the document/notification ID attribute. The string wildcards should get ignored, since the doc ID is not a string.
        searchStrings = new String[] {"!"+docIds[0], docIds[1]+"|"+docIds[2], "<="+docIds[1], ">="+docIds[2], "<"+docIds[0]+"&&>"+docIds[2],
        		">"+docIds[1], "<"+docIds[2]+"&&!"+docIds[0], docIds[0]+".."+docIds[2], "?"+docIds[1]+"*", "?9*7"};
        expectedResults = new int[] {1, 2, 2, 1, 0, 1, 1, 3/*2*/, 1, 0};
        for (int i = 0; i < searchStrings.length; i++) {
        	criteria = new DocSearchCriteriaDTO();
        	criteria.setRouteHeaderId(searchStrings[i]);
        	result = docSearchService.getList(principalId, criteria);
        	searchResults = result.getSearchResults();
        	assertEquals("Doc ID search at index " + i + " retrieved the wrong number of documents.", expectedResults[i], searchResults.size());
        }

        // Test the wildcards on the application document/notification ID attribute. The string wildcards should work, since the app doc ID is a string.
        searchStrings = new String[] {"6543", "5432|4321", ">4321", "<=5432", ">=6543", "<3210", "!3210", "!5432", "!4321!5432", ">4321&&!6543",
        		"*5?3*", "*", "?3?1", "!*43*", "!???2", ">43*1", "<=5432&&!?32?", "5432..6543"};
        expectedResults = new int[] {1, 2, 2, 2, 1, 0, 3, 2, 1, 1, 2, 3, 1, 0, 2, 3, 1, 2/*1*/};
        for (int i = 0; i < searchStrings.length; i++) {
        	criteria = new DocSearchCriteriaDTO();
        	criteria.setAppDocId(searchStrings[i]);
        	result = docSearchService.getList(principalId, criteria);
        	searchResults = result.getSearchResults();
        	if(expectedResults[i] !=  searchResults.size()){        		
        		assertEquals("App doc ID search at index " + i + " retrieved the wrong number of documents.", expectedResults[i], searchResults.size());
        	}
        }

        // Test the wildcards on the viewer attribute.
        searchStrings = new String[] {"jhopf","!jhopf", ">jhopf", "<jjopf", ">=quickstart", "<=jhopf", "jhope..jhopg", "?hopf", "*i*", "!*f", "j*"};
        expectedResults = new int[] {3, 0, 0, 3, 0, 3, 3, 3, 0, 0, 3};
        for (int i = 0; i < searchStrings.length; i++) {
        	criteria = new DocSearchCriteriaDTO();
        	criteria.setViewer(searchStrings[i]);
        	result = docSearchService.getList(principalId, criteria);
        	searchResults = result.getSearchResults();
        	if(expectedResults[i] !=  searchResults.size()){        		
        		assertEquals("Viewer search at index " + i + " retrieved the wrong number of documents.", expectedResults[i], searchResults.size());
        	}
        }

        // Test the wildcards on the title attribute.
        searchStrings = new String[] {"Some New Document", "Document Number 2|The New Doc", "!The New Doc", "!Some New Document!Document Number 2",
        		"!The New Doc&&!Some New Document", ">Document Number 2", "<=Some New Document", ">=The New Doc&&<Some New Document", ">A New Doc",
        		"<Some New Document|The New Doc", ">=Document Number 2&&!Some New Document", "*Docu??nt*", "*New*", "The ??? Doc", "*Doc*", "*Number*",
        		"Some New Document..The New Doc", "Document..The", "*New*&&!*Some*", "!The ??? Doc|!*New*"};
        expectedResults = new int[] {1, 2, 2, 1, 1, 2, 2, 0, 3, 2, 2, 2, 2, 1, 3, 1, 2/*1*/, 2, 1, 2};
        for (int i = 0; i < searchStrings.length; i++) {
        	criteria = new DocSearchCriteriaDTO();
        	criteria.setDocTitle(searchStrings[i]);
        	result = docSearchService.getList(principalId, criteria);
        	searchResults = result.getSearchResults();
        	if(expectedResults[i] !=  searchResults.size()){
        		assertEquals("Doc title search at index " + i + " retrieved the wrong number of documents.", expectedResults[i], searchResults.size());
        	}
        }


    }
    
    private String getPrincipalId(String principalName) {
    	return KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName).getPrincipalId();
    }
}
