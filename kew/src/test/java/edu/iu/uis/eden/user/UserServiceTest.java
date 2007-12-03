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
package edu.iu.uis.eden.user;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.vo.EmplIdVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UuIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;

public class UserServiceTest extends KEWTestCase {

	private UserService userService;

	protected void loadTestData() throws Exception {
		loadXmlFile("UserConfig.xml");
	}

	protected void setUpTransaction() throws Exception {
		userService = KEWServiceLocator.getUserService();
	}

	/**
	 * Tests the retrieval of users from the service.  There are quite a few users in the default data set that
	 * we will query for.
	 */
	@Test public void testGetWorkflowUser() throws Exception {
		// should be 'ewestfal', check all of the values on the user object to assert that the XML import is functioning properly
		WorkflowUser user = userService.getWorkflowUser(new WorkflowUserId("1"));
		assertNotNull(user);
		assertEquals("1", user.getWorkflowId());
		assertEquals("1", user.getEmplId().getEmplId());
		assertEquals("1", user.getUuId().getUuId());
		assertEquals("ewestfal", user.getAuthenticationUserId().getAuthenticationId());
		assertEquals("ewestfal@indiana.edu", user.getEmailAddress());
		assertEquals("Eric Westfall", user.getDisplayName());
		assertEquals("Eric", user.getGivenName());
		assertEquals("Westfall", user.getLastName());
		
		// get 'ewestfal' by authentication id
		user = userService.getWorkflowUser(new AuthenticationUserId("ewestfal"));
		assertNotNull(user);
		assertEquals("1", user.getWorkflowId());
		assertEquals("ewestfal", user.getAuthenticationUserId().getAuthenticationId());
		
		// get 'ewestfal' by empl id
		user = userService.getWorkflowUser(new EmplId("1"));
		assertNotNull(user);
		assertEquals("1", user.getWorkflowId());
		assertEquals("ewestfal", user.getAuthenticationUserId().getAuthenticationId());
		
		// get 'ewestfal' by uuid
		user = userService.getWorkflowUser(new UuId("1"));
		assertNotNull(user);
		assertEquals("1", user.getWorkflowId());
		assertEquals("ewestfal", user.getAuthenticationUserId().getAuthenticationId());

		// check the VO version of the same method
		
		//get 'ewestfal' by workflow id VO
		user = userService.getWorkflowUser(new WorkflowIdVO("1"));
		assertNotNull(user);
		assertEquals("1", user.getWorkflowId());
		assertEquals("ewestfal", user.getAuthenticationUserId().getAuthenticationId());
		
		// get 'ewestfal' by authentication id VO
		user = userService.getWorkflowUser(new NetworkIdVO("ewestfal"));
		assertNotNull(user);
		assertEquals("1", user.getWorkflowId());
		assertEquals("ewestfal", user.getAuthenticationUserId().getAuthenticationId());
		
		// get 'ewestfal' by empl id VO
		user = userService.getWorkflowUser(new EmplIdVO("1"));
		assertNotNull(user);
		assertEquals("1", user.getWorkflowId());
		assertEquals("ewestfal", user.getAuthenticationUserId().getAuthenticationId());
		
		// get 'ewestfal' by uuid VO
		user = userService.getWorkflowUser(new UuIdVO("1"));
		assertNotNull(user);
		assertEquals("1", user.getWorkflowId());
		assertEquals("ewestfal", user.getAuthenticationUserId().getAuthenticationId());
	
		// Try fetching a different user, UserServiceTestUser
		
		user = userService.getWorkflowUser(new WorkflowUserId("123456789"));
		assertNotNull(user);
		assertEquals("123456789", user.getWorkflowId());
		assertEquals("UserServiceTestUser", user.getAuthenticationUserId().getAuthenticationId());
		
		user = userService.getWorkflowUser(new AuthenticationUserId("UserServiceTestUser"));
		assertNotNull(user);
		assertEquals("123456789", user.getWorkflowId());
		assertEquals("UserServiceTestUser", user.getAuthenticationUserId().getAuthenticationId());
		
		user = userService.getWorkflowUser(new EmplId("00abc987123"));
		assertNotNull(user);
		assertEquals("123456789", user.getWorkflowId());
		assertEquals("UserServiceTestUser", user.getAuthenticationUserId().getAuthenticationId());
		
		user = userService.getWorkflowUser(new UuId("1029384756"));
		assertNotNull(user);
		assertEquals("123456789", user.getWorkflowId());
		assertEquals("UserServiceTestUser", user.getAuthenticationUserId().getAuthenticationId());
		
		// Now try fetching some bad users and watch the EdenUserNotFoundExceptions fly...
		
		try {
			user = userService.getWorkflowUser(new AuthenticationUserId("BadIdWhichWillNeverReturnAUser,NotInAMillionYears"));
			fail("Bad id should have thrown an exception.");
		} catch (EdenUserNotFoundException e) {}
		
		try {
			user = userService.getWorkflowUser(new WorkflowUserId("-1"));
			fail("Bad id should have thrown an exception.");
		} catch (EdenUserNotFoundException e) {}
		
		// try fetching with a null id, should throw IllegalArgumentException
		try {
			user = userService.getWorkflowUser((UserId)null);
			fail("Passing null should have thrown an exception");
		} catch (IllegalArgumentException e) {}
		
		// try fetching with a null "wrapped" id, should throw EdenUserNotFoundException
		try {
			user = userService.getWorkflowUser(new AuthenticationUserId(null));
			fail("Null id should have thrown an exception");
		} catch (EdenUserNotFoundException e) {}
		
	}
	
	@Test public void testGetBlankUser() throws Exception {
		// the service implementation used by the tests is the one which comes out of the box, so we should be
		// creating instances of SimpleUsers
		WorkflowUser blankUser = userService.getBlankUser();
		assertNotNull(blankUser);
	}
	
}
