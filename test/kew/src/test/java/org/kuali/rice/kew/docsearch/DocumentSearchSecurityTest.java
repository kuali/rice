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
package org.kuali.rice.kew.docsearch;

import org.junit.Test;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimConstants;

import static org.junit.Assert.*;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentSearchSecurityTest extends KEWTestCase {
	private static final String WORKFLOW_ADMIN_USER_NETWORK_ID = "bmcgough";
    private static final String APPROVER_USER_NETWORK_ID = "user2";
    private static final String STANDARD_USER_NETWORK_ID = "user1";
	DocumentSearchService docSearchService;
	
	@Override
	protected void setUpAfterDataLoad() throws Exception {
        docSearchService = (DocumentSearchService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
    }
	
	@Override
	protected void loadTestData() throws Exception {
    	loadXmlFile("SearchSecurityConfig.xml");
        
    }
	
    /**
     * Test for https://test.kuali.org/jira/browse/KULRICE-1968 - Document search fails when users are missing
     * Tests that we can safely search on docs whose initiator no longer exists in the identity management system
     * This test searches by doc type name criteria.
     * @throws Exception
     */
    @Test public void testDocSearchSecurityPermissionDocType() throws Exception {
        String documentTypeName = "SecurityDoc_PermissionOnly";
        String userNetworkId = "arh14";
        // route a document to enroute and route one to final
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), documentTypeName);
        workflowDocument.setTitle("testDocSearch_PermissionSecurity");
        workflowDocument.routeDocument("routing this document.");

        Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName("edna");
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        DocumentSearchResultComponents result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 1, result.getSearchResults().size());
    }
    
    @Test public void testDocSearchBadPermission() throws Exception {
        String documentTypeName = "SecurityDoc_InvalidPermissionOnly";
        String userNetworkId = "arh14";
        // route a document to enroute and route one to final
        WorkflowDocument workflowDocument = new WorkflowDocument(getPrincipalId(userNetworkId), documentTypeName);
        workflowDocument.setTitle("testDocSearch_PermissionSecurity");
        workflowDocument.routeDocument("routing this document.");

        Person user = KIMServiceLocator.getPersonService().getPersonByPrincipalName("edna");
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        DocumentSearchResultComponents result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertNotNull(result);
        assertNotNull(result.getSearchResults());
        assertEquals("Search returned invalid number of documents", 0, result.getSearchResults().size());
    }
	
    @Test public void testFilteringInitiator() throws Exception {    	
        String documentType = "SecurityDoc_InitiatorOnly";
        String initiator = getPrincipalId(STANDARD_USER_NETWORK_ID);
        WorkflowDocument document = new WorkflowDocument(initiator, documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiator, criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId("user3"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }
    
    @Test public void testFiltering_Workgroup() throws Exception {
        String documentType = "SecurityDoc_WorkgroupOnly";
        String initiator = getPrincipalId(STANDARD_USER_NETWORK_ID);
        WorkflowDocument document = new WorkflowDocument(initiator, documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // verify that initiator cannot see the document
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiator, criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that workgroup can see the document
        String workgroupName = "Test_Security_Group";
        Group group = KIMServiceLocator.getIdentityManagementService().getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, workgroupName);
        assertNotNull("Workgroup '" + workgroupName + "' should be valid", group);
        for (String workgroupUserId : KIMServiceLocator.getIdentityManagementService().getGroupMemberPrincipalIds(group.getId())) {
            Person workgroupUser = KIMServiceLocator.getPersonService().getPerson(workgroupUserId);
            criteria = new DocSearchCriteriaDTO();
            criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
            resultComponents = KEWServiceLocator.getDocumentSearchService().getList(workgroupUser.getPrincipalId(), criteria);
            assertEquals("Should retrive one record from search for user " + workgroupUser, 1, resultComponents.getSearchResults().size());
            assertEquals("No rows should have been filtered due to security for user " + workgroupUser, 0, criteria.getSecurityFilteredRows());
        }

        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId("user3"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }

    @Test public void testFiltering_SearchAttribute() throws Exception {

        String searchAttributeName = "UserEmployeeId";
        String searchAttributeFieldName = "employeeId";
        String documentTypeName = "SecurityDoc_SearchAttributeOnly";
        String initiatorNetworkId = STANDARD_USER_NETWORK_ID;
        WorkflowDocument document = new WorkflowDocument(getPrincipalId(initiatorNetworkId), documentTypeName);
        WorkflowAttributeDefinitionDTO definition = new WorkflowAttributeDefinitionDTO(searchAttributeName);
        definition.addProperty(searchAttributeFieldName, "user3");
        document.addSearchableDefinition(definition);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // verify that initiator cannot see the document
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId(initiatorNetworkId), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that user3 can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId("user3"), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // verify that user2 cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId("user2"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        RouteContext.clearCurrentRouteContext();
        document = new WorkflowDocument(getPrincipalId(APPROVER_USER_NETWORK_ID), document.getRouteHeaderId());
        document.clearSearchableContent();
        definition = new WorkflowAttributeDefinitionDTO(searchAttributeName);
        definition.addProperty(searchAttributeFieldName, "user2");
        document.addSearchableDefinition(definition);
        document.saveRoutingData();

        // verify that user2 can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId("user2"), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId("user3"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that initiator cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(getPrincipalId(initiatorNetworkId), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());
    }

    private String getPrincipalId(String principalName) {
    	return KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName).getPrincipalId();
    }
	
}
