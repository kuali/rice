/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CsrfValidator}
 *
 * @author Eric Westfall
 */
public class CsrfValidatorTest {

    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void testValidateCsrf_NonUpdateHttpMethods() throws Exception {

        String sessionToken = CsrfValidator.getSessionToken(request);
        assertNull(sessionToken);

        // check GET
        request.setMethod("GET");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        sessionToken = CsrfValidator.getSessionToken(request);
        assertNotNull(sessionToken);

        // check OPTIONS
        request.setMethod("OPTIONS");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // let's also verify that the session token doesn't change since it should be the same session
        String sessionToken2 = CsrfValidator.getSessionToken(request);
        assertEquals(sessionToken, sessionToken2);

        // check HEAD
        request.setMethod("HEAD");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // verify that if we use a new session, we get a different session token
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        request2.setMethod("GET");
        assertTrue(CsrfValidator.validateCsrf(request2, response2));

        assertNotEquals(CsrfValidator.getSessionToken(request), CsrfValidator.getSessionToken(request2));

    }

    @Test
    public void testValidateCsrf_Valid() {
        // first we run a GET to establish the CSRF token
        request.setMethod("GET");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // next let's do a POST and make sure we send the same token, and it should be valid
        String sessionToken = CsrfValidator.getSessionToken(request);
        request.setMethod("POST");
        request.setParameter(CsrfValidator.CSRF_PARAMETER, sessionToken);
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // and just verify we still have the same session token after the POST
        assertEquals(sessionToken, CsrfValidator.getSessionToken(request));
    }

    /**
     * Tests the situation where a POST is made against a session without a csrf token.
     */
    @Test
    public void testValidateCsrf_Invalid_EmptySession() {
        // do a POST with no parameter, it should fail
        request.setMethod("POST");
        assertFalse(CsrfValidator.validateCsrf(request, response));
        assertEquals(403, response.getStatus());

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // now do a POST with a parameter, still should fail because the session has no csrf token
        request.setMethod("POST");
        request.setParameter(CsrfValidator.CSRF_PARAMETER, UUID.randomUUID().toString());
        assertFalse(CsrfValidator.validateCsrf(request, response));
        assertEquals(403, response.getStatus());
    }

    /**
     * Tests the situation where a POST is made against a session that has a csrf token but the tokens don't match
     */
    @Test
    public void testValidateCsrf_Invalid_TokenMismatch() {
        // first we run a GET to establish the CSRF token
        request.setMethod("GET");
        assertTrue(CsrfValidator.validateCsrf(request, response));

        // do a POST with no parameter, it should fail
        request.setMethod("POST");
        assertFalse(CsrfValidator.validateCsrf(request, response));
        assertEquals(403, response.getStatus());

        // reset the response
        response = new MockHttpServletResponse();

        // now do the POST with a parameter, it should fail because the csrf tokens won't match
        request.setParameter(CsrfValidator.CSRF_PARAMETER, UUID.randomUUID().toString());
        assertFalse(CsrfValidator.validateCsrf(request, response));
        assertEquals(403, response.getStatus());
    }



}