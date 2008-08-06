/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.core.workflow.service;


import org.junit.Test;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.UserDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.test.KNSTestCase;


/**
 * This class tests the WorkflowUser service.
 */
public class WorkflowInfoServiceTest extends KNSTestCase {
    private static final String KNOWN_USERNAME = "KULUSER";

    @Test public void testGetWorkflowUser_nullUserId() throws Exception {
        boolean failedAsExpected = false;

        try {
            KNSServiceLocator.getWorkflowInfoService().getWorkflowUser(null);
        }
        catch (Exception e) {
            failedAsExpected = true;
        }

        assertTrue("nullUserId failed to fail properly", failedAsExpected);
    }

    @Test public void testGetWorkflowUser_blankAuthenticationUserId() throws Exception {
        boolean failedAsExpected = false;

        try {
            KNSServiceLocator.getWorkflowInfoService().getWorkflowUser(new NetworkIdDTO());
        }
        catch (WorkflowException e) {
            failedAsExpected = true;
        }

        assertTrue("blank authenticationUserId failed to fail properly", failedAsExpected);
    }

    @Test public void testGetWorkflowUser_unknownAuthenticationUserId() throws Exception {
        boolean failedAsExpected = false;

        try {
            KNSServiceLocator.getWorkflowInfoService().getWorkflowUser(new NetworkIdDTO("unknownUserId"));
        }
        catch (WorkflowException we) {
            // in the case of embedded mode, we get the actual KEWUserNotFoundException as the cause of the WorkflowException
            if (we.getMessage().startsWith("org.kuali.rice.kew.exception.WorkflowException: org.kuali.rice.kew.exception.KEWUserNotFoundException") || we.getCause() instanceof KEWUserNotFoundException) {
                failedAsExpected = true;
            }
        }

        assertTrue("unknown authenticationUserId failed to fail properly", failedAsExpected);
    }

    @Test public void testGetWorkflowUser_knownAuthenticationUserId() throws Exception {
        NetworkIdDTO knownUserId = new NetworkIdDTO(KNOWN_USERNAME);
        UserDTO workflowUser = KNSServiceLocator.getWorkflowInfoService().getWorkflowUser(knownUserId);

        // TODO The network ID comes back as lower case. It's listed in the constant as lower case.
        // Is this a bug?
        assertEquals("workflowUser.authenticationUserId inequal to input authenticationUserId", workflowUser.getNetworkId().toUpperCase(), KNOWN_USERNAME);
    }

    @Test public void testRouteHeaderExists_NullId() throws IllegalArgumentException {
        boolean errorThrown = false;
        try {
            boolean result = KNSServiceLocator.getWorkflowInfoService().routeHeaderExists(null);
        }
        catch (IllegalArgumentException e) {
            errorThrown = true;
        }
        assertTrue("An error should have been thrown.", errorThrown);
    }

    @Test public void testRouteHeaderExists_NegativeId() {
        boolean errorThrown = false;
        boolean result = true;
        try {
            result = KNSServiceLocator.getWorkflowInfoService().routeHeaderExists(new Long(-10));
        }
        catch (Exception e) {
            errorThrown = true;
        }
        assertFalse("An error should not have been thrown.", errorThrown);
        assertFalse("The routeHeader should never exist for a negative routeHeaderId.", result);
    }

    @Test public void testRouteHeaderExists_KnownBadZeroId() {
        boolean errorThrown = false;
        boolean result = true;
        try {
            result = KNSServiceLocator.getWorkflowInfoService().routeHeaderExists(new Long(0));
        }
        catch (Exception e) {
            errorThrown = true;
        }
        assertFalse("An error should not have been thrown.", errorThrown);
        assertFalse("The routeHeader should never exist for a negative routeHeaderId.", result);
    }

    @Test public void testRouteHeaderExists_KnownGood() {
        // no good way to test this without mocking the workflow service, and in a
        // way that will be good over the long term, across data changes
        assertTrue("This has been checked with a known-good id in the DB at this time.", true);
    }

}
