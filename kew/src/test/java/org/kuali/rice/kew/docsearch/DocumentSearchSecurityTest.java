/*
 * Copyright 2007 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.session.BasicAuthentication;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * This is a test class to test the document search security and row filtering
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentSearchSecurityTest extends KEWTestCase {
    private static final Logger LOG = Logger.getLogger(DocumentSearchSecurityTest.class);

    private static final String WORKFLOW_ADMIN_USER_NETWORK_ID = "bmcgough";
    private static final String APPROVER_USER_NETWORK_ID = "user2";
    private static final String STANDARD_USER_NETWORK_ID = "user1";

    private static final String GENERIC_ROLE_STAFF = "STAFF";
    private static final String GENERIC_ROLE_STUDENT = "STUDENT";
    private static final String GENERIC_ROLE_ALUMNI = "ALUMNI";

    @Override
    protected void loadTestData() throws Exception {
        loadXmlFile("SearchSecurityConfig.xml");
    	//SQLDataLoader loader = new SQLDataLoader("org/kuali/rice/kew/test/KimTestData.sql", "/");
    	//loader.runSql();
    }

    private Person loginUser(String networkId) {
        return loginUser(networkId, new ArrayList<String>());
    }

    private Person loginUser(String networkId, List<String> dummyRoleNames) {
        AuthenticationUserId id = new AuthenticationUserId(networkId);
        LOG.debug("performing user login: " + networkId);

        Person	user = null;
        UserSession.setAuthenticatedUser(null);
        try {
            if (id != null && (!StringUtils.isBlank(id.getId()))) {
                LOG.debug("Looking up user: " + id);
                user = KIMServiceLocator.getPersonService().getPersonByPrincipalName(networkId);

                LOG.debug("ending user lookup: " + user);
                UserSession userSession = new UserSession(user.getPrincipalId());
                // set up the thread local reference to the current authenticated user
                for (String dummyRoleName : dummyRoleNames) {
                    userSession.addAuthentication(new BasicAuthentication(dummyRoleName));
                }
                UserSession.setAuthenticatedUser(userSession);
            } else {
                LOG.error("UserId passed in was invalid");
            }
        } catch (Exception e) {
            LOG.error("Error in user login: " + networkId, e);
        } finally {
            LOG.debug("...finished performing user login: " + networkId);
        }
        return user;
    }

    @Test public void testFiltering_DisallowedRoleAndInitiator() throws Exception {
        String documentType = "SecurityDoc_RoleAndInitiator";
        Person initiator = loginUser("user1", Arrays.asList(new String[]{GENERIC_ROLE_ALUMNI}));
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(initiator.getPrincipalName()), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // test that workflow admin user
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // test initator user can see result
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(initiator.getPrincipalName()).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // test non-initator but user in disallowed role
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user2", Arrays.asList(new String[]{GENERIC_ROLE_ALUMNI})).getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // test non-initator user with no roles
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3").getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }

    @Test public void testFiltering_Initiator() throws Exception {    	
        String documentType = "SecurityDoc_InitiatorOnly";
        Person initiator = loginUser(STANDARD_USER_NETWORK_ID);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(initiator.getPrincipalName()), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiator.getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3").getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }

    @Test public void testFiltering_RouteLogAuthenticated() throws Exception {
        String documentType = "SecurityDoc_RouteLogAuthOnly";
        String initiatorNetworkId = STANDARD_USER_NETWORK_ID;
        Person initiatorUser = KIMServiceLocator.getPersonService().getPersonByPrincipalName(initiatorNetworkId);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(initiatorNetworkId), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // test that initiator can see the document in search
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiatorUser.getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3").getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());
        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // approve the document
        Person approverUser = loginUser(APPROVER_USER_NETWORK_ID);
        document = new WorkflowDocument(new NetworkIdDTO(approverUser.getPrincipalName()), document.getRouteHeaderId());
        assertEquals("Document route status is wrong",KEWConstants.ROUTE_HEADER_ENROUTE_CD,document.getRouteHeader().getDocRouteStatus());
        assertTrue("Approval should be requested of " + APPROVER_USER_NETWORK_ID, document.isApprovalRequested());
        // test that the approver can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(approverUser.getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
        document.approve("");

        // test that the approver can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(approverUser.getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3").getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());
        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }

    @Test public void testFiltering_Workgroup() throws Exception {
        String documentType = "SecurityDoc_WorkgroupOnly";
        Person initiator = loginUser(STANDARD_USER_NETWORK_ID);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(initiator.getPrincipalName()), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // verify that initiator cannot see the document
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiator.getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that workgroup can see the document
        String workgroupName = "Test_Security_Group";
        Group group = KIMServiceLocator.getIdentityManagementService().getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, workgroupName);
        assertNotNull("Workgroup '" + workgroupName + "' should be valid", group);
        for (String workgroupUserId : KIMServiceLocator.getIdentityManagementService().getGroupMemberPrincipalIds(group.getGroupId())) {
            Person workgroupUser = KIMServiceLocator.getPersonService().getPerson(workgroupUserId);
            criteria = new DocSearchCriteriaDTO();
            criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
            resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(workgroupUser.getPrincipalName()).getPrincipalId(), criteria);
            assertEquals("Should retrive one record from search for user " + workgroupUser, 1, resultComponents.getSearchResults().size());
            assertEquals("No rows should have been filtered due to security for user " + workgroupUser, 0, criteria.getSecurityFilteredRows());
        }

        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3").getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }

    @Test public void testFiltering_SearchAttribute() throws Exception {
        String searchAttributeName = "UserEmployeeId";
        String searchAttributeFieldName = "employeeId";
        String documentTypeName = "SecurityDoc_SearchAttributeOnly";
        String initiatorNetworkId = STANDARD_USER_NETWORK_ID;
        Person initiator = loginUser(initiatorNetworkId);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(initiator.getPrincipalName()), documentTypeName);
        WorkflowAttributeDefinitionDTO definition = new WorkflowAttributeDefinitionDTO(searchAttributeName);
        definition.addProperty(searchAttributeFieldName, "user3");
        document.addSearchableDefinition(definition);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // verify that initiator cannot see the document
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiator.getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that user3 can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3").getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // verify that user2 cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user2").getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        Person approverUser = loginUser(APPROVER_USER_NETWORK_ID);
        document = new WorkflowDocument(new NetworkIdDTO(approverUser.getPrincipalName()), document.getRouteHeaderId());
        document.clearSearchableContent();
        definition = new WorkflowAttributeDefinitionDTO(searchAttributeName);
        definition.addProperty(searchAttributeFieldName, "user2");
        document.addSearchableDefinition(definition);
        document.saveRoutingData();

        // verify that user2 can see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user2").getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3").getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that initiator cannot see the document
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(initiatorNetworkId).getPrincipalId(), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());
    }

    private Map<String,List> constructRulesByUserNetworkId() {
        Map<String,List> rolesByUserNetworkId = new HashMap<String,List>();
        // user with no roles to test general user access
        rolesByUserNetworkId.put("user1",Arrays.asList(new String[]{}));
        // users with single role only
        rolesByUserNetworkId.put("user2",Arrays.asList(new String[]{GENERIC_ROLE_STAFF}));
        rolesByUserNetworkId.put("user3",Arrays.asList(new String[]{GENERIC_ROLE_STUDENT}));
        rolesByUserNetworkId.put("dewey",Arrays.asList(new String[]{GENERIC_ROLE_ALUMNI}));
        // user with all roles
        rolesByUserNetworkId.put("xqi",Arrays.asList(new String[]{GENERIC_ROLE_ALUMNI, GENERIC_ROLE_STAFF, GENERIC_ROLE_STUDENT}));
        return rolesByUserNetworkId;
    }

    @Test public void testFiltering_Roles() throws Exception {
        String documentType = "SecurityDoc_RoleOnly";
        /*  Document Roles:
         *     Allowed    - STAFF
         *     Disallowed - STUDENT
         *
         *  Users with no roles CANNOT see the document
         */
        List<String> allowedRoles = Arrays.asList(new String[]{GENERIC_ROLE_STAFF});
        List<String> disallowedRoles = Arrays.asList(new String[]{GENERIC_ROLE_STUDENT});
        testRoleFiltering(documentType, allowedRoles, disallowedRoles);
    }

    @Test public void testFiltering_Roles_DisallowOnly() throws Exception {
        String documentType = "SecurityDoc_RoleOnly_DisallowOnly";
        /*  Document Roles:
         *     Allowed    -
         *     Disallowed - ALUMNI
         *
         *  Users with no roles CAN see the document
         */
        List<String> allowedRoles = Arrays.asList(new String[]{});
        List<String> disallowedRoles = Arrays.asList(new String[]{GENERIC_ROLE_ALUMNI});
        testRoleFiltering(documentType, allowedRoles, disallowedRoles);
    }

    @Test public void testFiltering_Roles_Overlaps() throws Exception {
        String documentType = "SecurityDoc_RoleOnly_Overlaps";
        /*  Document Roles:
         *     Allowed    - STUDENT, ALUMNI
         *     Disallowed - STUDENT, STAFF
         *
         *  Users with no roles CANNOT see the document
         */
        List<String> allowedRoles = Arrays.asList(new String[]{GENERIC_ROLE_ALUMNI,GENERIC_ROLE_STUDENT});
        List<String> disallowedRoles = Arrays.asList(new String[]{GENERIC_ROLE_STAFF,GENERIC_ROLE_STUDENT});
        testRoleFiltering(documentType, allowedRoles, disallowedRoles);
    }

    private void testRoleFiltering(String documentType, List<String> allowedRoles, List<String> disallowedRoles) throws Exception {
        Person initiator = loginUser(STANDARD_USER_NETWORK_ID, Arrays.asList(new String[]{}));
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(initiator.getPrincipalName()), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // test that workflow admin with no roles can see document
        String searchUserNetworkId = WORKFLOW_ADMIN_USER_NETWORK_ID;
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // test that workflow admin with disallowed role can see document
        searchUserNetworkId = WORKFLOW_ADMIN_USER_NETWORK_ID;
        List<String> userRoles = Arrays.asList(new String[]{GENERIC_ROLE_STUDENT});
        criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID, userRoles).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        Map<String,List> rolesByNetworkId = constructRulesByUserNetworkId();
        for (String networkId : rolesByNetworkId.keySet()) {
            searchUserNetworkId = networkId;
            userRoles = rolesByNetworkId.get(networkId);
            criteria = new DocSearchCriteriaDTO();
            criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
            resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(searchUserNetworkId, userRoles).getPrincipalId(), criteria);
            LOG.info("****************************");
            if (isRoleAuthenticated(userRoles, allowedRoles, disallowedRoles)) {
                LOG.info("***  User " + searchUserNetworkId + " should be able to view search result");
                assertEquals("Should retrive one record from search for user " + searchUserNetworkId, 1, resultComponents.getSearchResults().size());
                assertEquals("No rows should have been filtered due to security for user " + searchUserNetworkId, 0, criteria.getSecurityFilteredRows());
            } else {
                LOG.info("***  User " + searchUserNetworkId + " should NOT be able to view search result");
                assertEquals("Should retrive no records from search for user " + searchUserNetworkId, 0, resultComponents.getSearchResults().size());
                assertEquals("One row should have been filtered due to security for user " + searchUserNetworkId, 1, criteria.getSecurityFilteredRows());
            }
            LOG.info("****************************");
        }
    }

    protected boolean isRoleAuthenticated(List<String> userRoles, List<String> allowedRoles, List<String> disallowedRoles) {
        boolean allowed = false;
        boolean disallowed = false;
        for (String userRole : userRoles) {
            if (disallowedRoles.contains(userRole)) {
                disallowed = true;
            }
            if (allowedRoles.contains(userRole)) {
                allowed = true;
            }
        }
        if (allowed) {
            // allowed should take precedence over disallowed
            return true;
        } else if (disallowed) {
            // we know that we haven't been allowed at this point, if we're disallowed than we're not authenticated
            return false;
        } else if (allowedRoles.isEmpty()) {
            // if allowedRoles is empty, that means that disallowed roles are not empty and we know because of the previous condition
            // that the user has not been disallowed, therefore the user should be allowed if they aren't in the disallow set
            return true;
        }
        return false;
    }

    @Test public void testFiltering_SecurityAttribute_ByClass() throws Exception {
        testSecurityAttributeFiltering("SecurityDoc_SecurityAttributeOnly_Class");
    }

    @Test public void testFiltering_SecurityAttribute_ByName() throws Exception {
        testSecurityAttributeFiltering("SecurityDoc_SecurityAttributeOnly_Name");
    }

    private void testSecurityAttributeFiltering(String documentType) throws Exception {
        String ackNetworkId = "xqi";
        String initiatorNetworkId = STANDARD_USER_NETWORK_ID;
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(initiatorNetworkId), documentType);
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "", getPrincipalIdForName(ackNetworkId), "", true);
        document.saveDocument("");
        assertEquals("Document should have saved", KEWConstants.ROUTE_HEADER_SAVED_CD, document.getRouteHeader().getDocRouteStatus());
        runSecurityAttributeChecks(document.getRouteHeaderId());

        document = new WorkflowDocument(new NetworkIdDTO(initiatorNetworkId), document.getRouteHeaderId());
        document.routeDocument("");
        assertEquals("Document should have routed", KEWConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());
        runSecurityAttributeChecks(document.getRouteHeaderId());

        // approve the document
        document = new WorkflowDocument(new NetworkIdDTO(APPROVER_USER_NETWORK_ID), document.getRouteHeaderId());
        assertTrue("Approval should be requested of " + APPROVER_USER_NETWORK_ID, document.isApprovalRequested());
        document.approve("");
        assertEquals("Document should have gone to processed", KEWConstants.ROUTE_HEADER_PROCESSED_CD, document.getRouteHeader().getDocRouteStatus());
        runSecurityAttributeChecks(document.getRouteHeaderId());

        // ack the document
        document = new WorkflowDocument(new NetworkIdDTO(ackNetworkId), document.getRouteHeaderId());
        assertTrue("Acknowledge should be requested of " + ackNetworkId, document.isAcknowledgeRequested());
        document.acknowledge("");
        assertEquals("Document should have gone to final", KEWConstants.ROUTE_HEADER_FINAL_CD, document.getRouteHeader().getDocRouteStatus());
        runSecurityAttributeChecks(document.getRouteHeaderId());
    }

    private void runSecurityAttributeChecks(Long routeHeaderId) throws Exception {
        Set<String> userNetworkIdList = new HashSet<String>(Arrays.asList(new String[]{"user1","user2","user3","dewey","xqi"}));
        for (String networkId : userNetworkIdList) {
            Person user = loginUser(networkId);
            WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(user.getPrincipalName()), routeHeaderId);
            DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
            criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
            DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(user.getPrincipalId(), criteria);
            String visibleNetworkId = CustomSecurityFilterAttribute.VIEWERS_BY_STATUS.get(document.getRouteHeader().getDocRouteStatus());
            if ( (!Utilities.isEmpty(visibleNetworkId)) && (visibleNetworkId.equals(networkId)) ) {
                // verify that CustomSecurityFilterAttribute says user can see this document
                assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
                assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
            } else {
                // visibleNetworkId is empty or does not match currentNetworkId so document should be hidden
                assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
                assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());
            }
        }
        // verify that WorkflowAdmin can see the document
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setRouteHeaderId(routeHeaderId.toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID).getPrincipalId(), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }

}
