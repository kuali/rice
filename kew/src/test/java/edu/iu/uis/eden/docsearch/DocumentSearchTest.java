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

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.clientapp.IDocHandler;
import org.kuali.rice.kew.clientapp.WorkflowDocument;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaVO;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.DocumentSearchService;
import org.kuali.rice.kew.docsearch.SavedSearchResult;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.doctype.DocumentTypeService;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.lookupable.DocumentTypeLookupableImpl;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.KeyValueSort;

public class DocumentSearchTest extends KEWTestCase {

    DocumentSearchService docSearchService;
    UserService userService;

    protected void loadTestData() throws Exception {
        loadXmlFile("SearchAttributeConfig.xml");
    }

    protected void setUpTransaction() throws Exception {
        docSearchService = (DocumentSearchService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        userService = (UserService)KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    }

    @Test public void testDocSearch() throws Exception {
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("bmcgough"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        List searchResults = null;
        DocumentSearchResultComponents result = null;
        criteria.setDocTitle("*IN");
        criteria.setNamedSearch("bytitle");
        result = docSearchService.getList(user, criteria);
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTitle("*IN-CFSG");
        criteria.setNamedSearch("for in accounts");
        result = docSearchService.getList(user, criteria);
        criteria = new DocSearchCriteriaVO();
        criteria.setFromDateApproved("09/16/2004");
        result = docSearchService.getList(user, criteria);
        criteria = new DocSearchCriteriaVO();
        criteria.setDocRouteNodeId("3");
        criteria.setDocRouteNodeLogic("equal");
        result = docSearchService.getList(user, criteria);
        user = userService.getWorkflowUser(new AuthenticationUserId("bmcgough"));
        SavedSearchResult savedSearchResults = docSearchService.getSavedSearchResults(user, "DocSearch.NamedSearch.bytitle");
        assertNotNull(savedSearchResults);
        assertNotNull(savedSearchResults.getSearchResult());
        savedSearchResults = docSearchService.getSavedSearchResults(user, "DocSearch.NamedSearch.for in accounts");
        assertNotNull(savedSearchResults);
        assertNotNull(savedSearchResults.getSearchResult());
    }

    @Test public void testDocSearch_RouteNodeName() throws Exception {
        loadXmlFile("DocSearchTest_RouteNode.xml");
        String documentTypeName = "SearchDocType_RouteNodeTest";
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        String userNetworkId = "rkirkend";

        // route a document to enroute and route one to final
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdDTO(userNetworkId), documentTypeName);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(new NetworkIdDTO("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());
        workflowDocument.approve("");
        workflowDocument = new WorkflowDocument(new NetworkIdDTO("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsFinal());
        workflowDocument = new WorkflowDocument(new NetworkIdDTO(userNetworkId), documentTypeName);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(new NetworkIdDTO("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId(userNetworkId));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 2, result.getSearchResults().size());

        criteria.setDocRouteNodeId(getRouteNodeForSearch(documentTypeName,workflowDocument.getNodeNames()));
        criteria.setDocRouteNodeLogic("equal");
        result = docSearchService.getList(user, criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 1, result.getSearchResults().size());

        // load the document type again to change the route node ids
        loadXmlFile("DocSearchTest_RouteNode.xml");

        workflowDocument = new WorkflowDocument(new NetworkIdDTO("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());
        criteria.setDocRouteNodeId(getRouteNodeForSearch(documentTypeName,workflowDocument.getNodeNames()));
        result = docSearchService.getList(user, criteria);
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
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("bmcgough"));
        List namedSearches = docSearchService.getNamedSearches(user);
        assertNotNull(namedSearches);
    }

    /**
     * Test for https://test.kuali.org/jira/browse/KULWF-703
     */
    @Test public void testSearchEDENSERVICE_DOCS() throws Exception {
        loadXmlStream(new FileInputStream(getBaseDir()+"/src/main/config/xml/KEWBootstrap.xml"));

        Collection c = KEWServiceLocator.getDocumentTypeService().find(new DocumentType(), "EDENSERVICE-DOCS", true);
        assertNotNull(c);
        assertTrue(c.size() > 0);

        DocumentTypeLookupableImpl lookupable = new DocumentTypeLookupableImpl();
        Map fieldValues = new HashMap();
        fieldValues.put(DocumentTypeLookupableImpl.ACTIVE_IND_PROPERTY_NAME, "ALL");
        fieldValues.put(DocumentTypeLookupableImpl.DOC_TYP_PROPERTY_NAME, "");
        fieldValues.put(DocumentTypeLookupableImpl.DOC_TYP_FULL_NAME, "");
        fieldValues.put(DocumentTypeLookupableImpl.DOCUMENT_TYPE_ID_PROPERTY_NAME, "");
        fieldValues.put(DocumentTypeLookupableImpl.DOC_TYP_NAME_PROPERTY_NAME, "EDENSERVICE-DOCS");
        fieldValues.put(DocumentTypeLookupableImpl.BACK_LOCATION_KEY_NAME, "http://localhost:8080/en-dev/DocumentSearch.do");

        fieldValues.put(DocumentTypeLookupableImpl.DOC_FORM_KEY_NAME, "0");

        //String docTypReturn = (String) fieldConversions.get(DOC_TYP_FULL_NAME);

        List list = lookupable.getSearchResults(fieldValues, new HashMap());
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test public void testDefaultCreateDateSearchCriteria() throws Exception {
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("bmcgough"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        assertNotNull("Should have a date created value",criteria.getFromDateCreated());
        Calendar criteriaDate = Calendar.getInstance();
        criteriaDate.setTime(DocSearchUtils.convertStringDateToTimestamp(criteria.getFromDateCreated()));
        assertEquals("Criteria date minus today's date should equal the constant value", KEWConstants.DOCUMENT_SEARCH_NO_CRITERIA_CREATE_DATE_DAYS_AGO.doubleValue(), getDifferenceInDays(criteriaDate), 0);

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTitle("testing");
        result = docSearchService.getList(user, criteria);
        assertNotNull("Should have a date created value",criteria.getFromDateCreated());
        criteriaDate = Calendar.getInstance();
        criteriaDate.setTime(DocSearchUtils.convertStringDateToTimestamp(criteria.getFromDateCreated()));
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
        WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdDTO(userNetworkId), standardDocHandlerDocumentType);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(new NetworkIdDTO("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());

        // route a document to enroute and route one to final
        workflowDocument = new WorkflowDocument(new NetworkIdDTO(userNetworkId), customDocHandlerDocumentType);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(new NetworkIdDTO("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());
        workflowDocument.approve("");
        workflowDocument = new WorkflowDocument(new NetworkIdDTO("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsFinal());

        workflowDocument = new WorkflowDocument(new NetworkIdDTO(userNetworkId), customDocHandlerDocumentType);
        workflowDocument.setTitle("Routing style");
        workflowDocument.routeDocument("routing this document.");
        // verify the document is enroute for jhopf
        workflowDocument = new WorkflowDocument(new NetworkIdDTO("jhopf"),workflowDocument.getRouteHeaderId());
        assertTrue(workflowDocument.stateIsEnroute());
        assertTrue(workflowDocument.isApprovalRequested());

        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId(userNetworkId));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setSuperUserSearch("YES");
        criteria.setDocTypeFullName(customDocHandlerDocumentType);
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 2, result.getSearchResults().size());
        for (DocumentSearchResult resultElement : result.getSearchResults()) {
	    KeyValueSort kvs = resultElement.getResultContainer(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID);
	    assertNotNull("A valid column field value should be returned for key " + DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID, kvs);
	    assertTrue("The document handler redirect for the client should be included in the route header id url", kvs.getValue().indexOf(KEWConstants.DOC_HANDLER_REDIRECT_PAGE) != -1);
	    assertTrue("The document handler redirect for the client should include the command value for super user search", kvs.getValue().indexOf(IDocHandler.SUPERUSER_COMMAND) != -1);
	}

        user = userService.getWorkflowUser(new AuthenticationUserId(userNetworkId));
        criteria = new DocSearchCriteriaVO();
        criteria.setSuperUserSearch("YES");
        result = docSearchService.getList(user, criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 3, result.getSearchResults().size());
        for (DocumentSearchResult resultElement : result.getSearchResults()) {
	    KeyValueSort routeHeaderIdValue = resultElement.getResultContainer(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID);
	    assertNotNull("A valid column field value should be returned for key " + DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID, routeHeaderIdValue);
	    KeyValueSort documentTypeValue = resultElement.getResultContainer(DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL);
	    assertNotNull("A valid column field value should be returned for key " + DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL, documentTypeValue);
	    if (customDocHandlerDocumentType.equals(documentTypeValue.getValue())) {
		assertTrue("The document handler redirect for the client should be included in the route header id url", routeHeaderIdValue.getValue().indexOf(KEWConstants.DOC_HANDLER_REDIRECT_PAGE) != -1);
		assertTrue("The document handler redirect for the client should include the command value for super user search", routeHeaderIdValue.getValue().indexOf(IDocHandler.SUPERUSER_COMMAND) != -1);
	    } else if (standardDocHandlerDocumentType.equals(documentTypeValue.getValue())) {
		assertTrue("The document handler redirect for the client should be included in the route header id url", routeHeaderIdValue.getValue().indexOf(KEWConstants.DOC_HANDLER_REDIRECT_PAGE) == -1);
	    } else {
		fail("Found document search result row with document type '" + documentTypeValue.getValue() + "' that should not have existed");
	    }
	}
    }

}
