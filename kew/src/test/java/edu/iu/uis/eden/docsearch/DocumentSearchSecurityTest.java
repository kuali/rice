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
package edu.iu.uis.eden.docsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.session.BasicAuthentication;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * This is a test class to test the document search security and row filtering 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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
    }

    private WorkflowUser loginUser(String networkId) throws EdenUserNotFoundException {
        return loginUser(networkId, new ArrayList<String>());
    }

    private WorkflowUser loginUser(String networkId, List<String> dummyRoleNames) throws EdenUserNotFoundException {
        AuthenticationUserId id = new AuthenticationUserId(networkId);
        LOG.debug("performing user login: " + networkId);
        WorkflowUser workflowUser = null;
        UserSession.setAuthenticatedUser(null);
        try {
            if (id != null && (!StringUtils.isBlank(id.getId()))) {
                LOG.debug("Looking up user: " + id);
                workflowUser = ((UserService) KEWServiceLocator.getUserService()).getWorkflowUser(id);
                LOG.debug("ending user lookup: " + workflowUser);
                UserSession userSession = new UserSession(workflowUser);
                //load the users preferences.  The preferences action will update them if necessary
                userSession.setPreferences(KEWServiceLocator.getPreferencesService().getPreferences(workflowUser));
                userSession.setGroups(KEWServiceLocator.getWorkgroupService().getUsersGroupNames(workflowUser));
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
        return KEWServiceLocator.getUserService().getWorkflowUser(id);
    }

    @Test public void testFiltering_DisallowedRoleAndInitiator() throws Exception {
        String documentType = "SecurityDoc_RoleAndInitiator";
        WorkflowUser initiator = loginUser("user1", Arrays.asList(new String[]{GENERIC_ROLE_ALUMNI}));
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiator.getAuthenticationUserId().getAuthenticationId()), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // test that workflow admin user
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // test initator user can see result
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(initiator.getAuthenticationUserId().getAuthenticationId()), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // test non-initator but user in disallowed role
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user2", Arrays.asList(new String[]{GENERIC_ROLE_ALUMNI})), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // test non-initator user with no roles
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3"), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }
    
    @Test public void testFiltering_Initiator() throws Exception {
        String documentType = "SecurityDoc_InitiatorOnly";
        WorkflowUser initiator = loginUser(STANDARD_USER_NETWORK_ID);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiator.getAuthenticationUserId().getAuthenticationId()), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiator, criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }
    
    @Test public void testFiltering_RouteLogAuthenticated() throws Exception {
        String documentType = "SecurityDoc_RouteLogAuthOnly";
        String initiatorNetworkId = STANDARD_USER_NETWORK_ID;
        WorkflowUser initiatorUser = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(initiatorNetworkId));
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiatorNetworkId), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // test that initiator can see the document in search
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiatorUser, criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());
        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // approve the document
        WorkflowUser approverUser = loginUser(APPROVER_USER_NETWORK_ID);
        document = new WorkflowDocument(new NetworkIdVO(approverUser.getAuthenticationUserId().getAuthenticationId()), document.getRouteHeaderId());
        assertEquals("Document route status is wrong",EdenConstants.ROUTE_HEADER_ENROUTE_CD,document.getRouteHeader().getDocRouteStatus());
        assertTrue("Approval should be requested of " + APPROVER_USER_NETWORK_ID, document.isApprovalRequested());
        // test that the approver can see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(approverUser, criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
        document.approve("");
        
        // test that the approver can see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(approverUser, criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());
        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }
    
    @Test public void testFiltering_Workgroup() throws Exception {
        String documentType = "SecurityDoc_WorkgroupOnly";
        WorkflowUser initiator = loginUser(STANDARD_USER_NETWORK_ID);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiator.getAuthenticationUserId().getAuthenticationId()), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // verify that initiator cannot see the document
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiator, criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that workgroup can see the document
        String workgroupName = "Test_Security_Group";
        Workgroup group = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(workgroupName));
        assertNotNull("Workgroup '" + workgroupName + "' should be valid", group);
        for (WorkflowUser workgroupUser : group.getUsers()) {
            criteria = new DocSearchCriteriaVO();
            criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
            resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(workgroupUser.getAuthenticationUserId().getAuthenticationId()), criteria);
            assertEquals("Should retrive one record from search for user " + workgroupUser, 1, resultComponents.getSearchResults().size());
            assertEquals("No rows should have been filtered due to security for user " + workgroupUser, 0, criteria.getSecurityFilteredRows());
        }

        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }
    
    @Test public void testFiltering_SearchAttribute() throws Exception {
        String searchAttributeName = "UserEmployeeId";
        String searchAttributeFieldName = "employeeId";
        String documentTypeName = "SecurityDoc_SearchAttributeOnly";
        String initiatorNetworkId = STANDARD_USER_NETWORK_ID;
        WorkflowUser initiator = loginUser(initiatorNetworkId);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiator.getAuthenticationUserId().getAuthenticationId()), documentTypeName);
        WorkflowAttributeDefinitionVO definition = new WorkflowAttributeDefinitionVO(searchAttributeName);
        definition.addProperty(searchAttributeFieldName, "user3");
        document.addSearchableDefinition(definition);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // verify that initiator cannot see the document
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(initiator, criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that user3 can see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3"), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // verify that user2 cannot see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user2"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that WorkflowAdmin can see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
        
        WorkflowUser approverUser = loginUser(APPROVER_USER_NETWORK_ID);
        document = new WorkflowDocument(new NetworkIdVO(approverUser.getAuthenticationUserId().getAuthenticationId()), document.getRouteHeaderId());
        document.clearSearchableContent();
        definition = new WorkflowAttributeDefinitionVO(searchAttributeName);
        definition.addProperty(searchAttributeFieldName, "user2");
        document.addSearchableDefinition(definition);
        document.saveRoutingData();

        // verify that user2 can see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user2"), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // verify that user3 cannot see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser("user3"), criteria);
        assertEquals("Should retrive no records from search", 0, resultComponents.getSearchResults().size());
        assertEquals("One row should have been filtered due to security", 1, criteria.getSecurityFilteredRows());

        // verify that initiator cannot see the document
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(initiatorNetworkId), criteria);
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
        WorkflowUser initiator = loginUser(STANDARD_USER_NETWORK_ID, Arrays.asList(new String[]{}));
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiator.getAuthenticationUserId().getAuthenticationId()), documentType);
        document.routeDocument("");
        assertFalse("Document should not be in init status after routing", document.stateIsInitiated());

        // test that workflow admin with no roles can see document
        String searchUserNetworkId = WORKFLOW_ADMIN_USER_NETWORK_ID;
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        // test that workflow admin with disallowed role can see document
        searchUserNetworkId = WORKFLOW_ADMIN_USER_NETWORK_ID;
        List<String> userRoles = Arrays.asList(new String[]{GENERIC_ROLE_STUDENT});
        criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
        resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID, userRoles), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());

        Map<String,List> rolesByNetworkId = constructRulesByUserNetworkId();
        for (String networkId : rolesByNetworkId.keySet()) {
            searchUserNetworkId = networkId;
            userRoles = rolesByNetworkId.get(networkId);
            criteria = new DocSearchCriteriaVO();
            criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
            resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(searchUserNetworkId, userRoles), criteria);
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
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiatorNetworkId), documentType);
        document.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "", new NetworkIdVO(ackNetworkId), "", true);
        document.saveDocument("");
        assertEquals("Document should have saved", EdenConstants.ROUTE_HEADER_SAVED_CD, document.getRouteHeader().getDocRouteStatus());
        runSecurityAttributeChecks(document.getRouteHeaderId());
        
        document = new WorkflowDocument(new NetworkIdVO(initiatorNetworkId), document.getRouteHeaderId());
        document.routeDocument("");
        assertEquals("Document should have routed", EdenConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());
        runSecurityAttributeChecks(document.getRouteHeaderId());

        // approve the document
        document = new WorkflowDocument(new NetworkIdVO(APPROVER_USER_NETWORK_ID), document.getRouteHeaderId());
        assertTrue("Approval should be requested of " + APPROVER_USER_NETWORK_ID, document.isApprovalRequested());
        document.approve("");
        assertEquals("Document should have gone to processed", EdenConstants.ROUTE_HEADER_PROCESSED_CD, document.getRouteHeader().getDocRouteStatus());
        runSecurityAttributeChecks(document.getRouteHeaderId());

        // ack the document
        document = new WorkflowDocument(new NetworkIdVO(ackNetworkId), document.getRouteHeaderId());
        assertTrue("Acknowledge should be requested of " + ackNetworkId, document.isAcknowledgeRequested());
        document.acknowledge("");
        assertEquals("Document should have gone to final", EdenConstants.ROUTE_HEADER_FINAL_CD, document.getRouteHeader().getDocRouteStatus());
        runSecurityAttributeChecks(document.getRouteHeaderId());
    }

    private void runSecurityAttributeChecks(Long routeHeaderId) throws Exception {
        Set<String> userNetworkIdList = new HashSet<String>(Arrays.asList(new String[]{"user1","user2","user3","dewey","xqi"}));
        for (String networkId : userNetworkIdList) {
            WorkflowUser user = loginUser(networkId);
            WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(user.getAuthenticationUserId().getAuthenticationId()), routeHeaderId);
            DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
            criteria.setRouteHeaderId(document.getRouteHeaderId().toString());
            DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(user, criteria);
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
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setRouteHeaderId(routeHeaderId.toString());
        DocumentSearchResultComponents resultComponents = KEWServiceLocator.getDocumentSearchService().getList(loginUser(WORKFLOW_ADMIN_USER_NETWORK_ID), criteria);
        assertEquals("Should retrive one record from search", 1, resultComponents.getSearchResults().size());
        assertEquals("No rows should have been filtered due to security", 0, criteria.getSecurityFilteredRows());
    }
    
}
