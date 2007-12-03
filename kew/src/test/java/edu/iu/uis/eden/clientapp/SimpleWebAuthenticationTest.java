/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.clientapp;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * Verify that the {@link SimpleWebAuthenticationService} can get the logged in user
 * from the request param.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SimpleWebAuthenticationTest extends KEWTestCase {

	@Test public void testGetLoggedInUserFromRequest() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(SimpleWebAuthenticationService.LOGGED_IN_USER_REQUEST_ATT_KEY, "rkirkend");
		AuthenticationUserId networkId = (AuthenticationUserId)new SimpleWebAuthenticationService().getUserId(request);
		assertEquals("NetworkId on request is rkirkend", "rkirkend", networkId.getAuthenticationId());
	}

	/**
	 * Verify that this method doesn't blow an obvious NPE at this point.
	 * @throws Exception
	 */
	@Test public void testUpdateUserSession() throws Exception {
		WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO("rkirkend"));
		UserSession userSession = new UserSession(user);
		HttpServletRequest request = new MockHttpServletRequest();
		assertNotNull("Should return same usersession that was passed in", new SimpleWebAuthenticationService().updateUserSession(userSession, request));
	}
}