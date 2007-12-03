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
package edu.iu.uis.eden.notification;

import mocks.MockEmailNotificationService;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.preferences.PreferencesService;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

public class NotificationServiceTest extends KEWTestCase {

	protected void loadTestData() throws Exception {
        loadXmlFile("NotificationConfig.xml");
    }
	
	/**
	 * Tests that when a user is routed to twice at the same time that only email is sent to them.
	 */
	@Test public void testNoDuplicateEmails() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "NotificationTest");
		document.routeDocument("");
		
		assertEquals("rkirkend should only have one email.", 1, getMockEmailService().emailsSent("rkirkend", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		assertEquals("ewestfal should only have one email.", 1, getMockEmailService().emailsSent("ewestfal", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		assertEquals("jhopf should only have one email.", 1, getMockEmailService().emailsSent("jhopf", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		// bmcgough is doing primary delegation so he should not recieve an email notification
		assertEquals("bmcgough should have no emails.", 0, getMockEmailService().emailsSent("bmcgough", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		// jitrue should have no email because he is a secondary delegate and his default preferences should be set up to not send an email
		assertEquals("jitrue should have no emails.", 0, getMockEmailService().emailsSent("jitrue", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		// user1 took action so they should _not_ be sent any emails
		assertEquals("user1 should have no emails.", 0, getMockEmailService().emailsSent("user1", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		
	}
	
	/**
	 * Tests that the notification preferences for emails work properly.  There are four different preferences:
	 * 
	 * 1) Email notification type (none, immediate, daily, weekly) - defaults to immediate
	 * 2) Send primary delegation notifications - defaults to true
	 * 3) Send secondary delegation notifications - defaults to false
	 */
	@Test public void testEmailPreferences() throws Exception {
		WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
		WorkflowUser jitrue = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("jitrue"));
		WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
		WorkflowUser jhopf = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("jhopf"));
		WorkflowUser bmcgough = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("bmcgough"));
		
		// test that the users with secondary delegations have default preferences
		assertDefaultNotificationPreferences(ewestfal);
		assertDefaultNotificationPreferences(jitrue);
		assertDefaultNotificationPreferences(rkirkend);
		assertDefaultNotificationPreferences(jhopf);
		assertDefaultNotificationPreferences(bmcgough);
		// the rest of the default setup is actually tested by testNoDuplicateEmails
		
		// now turn on secondary notification for ewestfal and jitrue, turn off email notification for ewestfal
		Preferences prefs = getPreferencesService().getPreferences(ewestfal);
		prefs.setNotifySecondaryDelegation(EdenConstants.PREFERENCES_YES_VAL);
		prefs.setEmailNotification(EdenConstants.EMAIL_RMNDR_NO_VAL);
		getPreferencesService().savePreferences(ewestfal, prefs);
		prefs = getPreferencesService().getPreferences(jitrue);
		prefs.setNotifySecondaryDelegation(EdenConstants.PREFERENCES_YES_VAL);
		getPreferencesService().savePreferences(jitrue, prefs);
		
		// also turn off primary delegation notification for rkirkend
		prefs = getPreferencesService().getPreferences(rkirkend);
		prefs.setNotifyPrimaryDelegation(EdenConstants.PREFERENCES_NO_VAL);
		getPreferencesService().savePreferences(rkirkend, prefs);
		
		// also turn notification to daily for bmcgough
		prefs = getPreferencesService().getPreferences(bmcgough);
		prefs.setEmailNotification(EdenConstants.EMAIL_RMNDR_DAY_VAL);
		getPreferencesService().savePreferences(bmcgough, prefs);
		
		// also turn off notification for jhopf
		prefs = getPreferencesService().getPreferences(jhopf);
		prefs.setEmailNotification(EdenConstants.EMAIL_RMNDR_NO_VAL);
		getPreferencesService().savePreferences(jhopf, prefs);
		
		// route the document
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "NotificationTest");
		document.routeDocument("");
		
		// both ewestfal and jitrue should have one email
		assertEquals("ewestfal should have no emails.", 0, getMockEmailService().emailsSent("ewestfal", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		assertEquals("jitrue should have one email.", 1, getMockEmailService().emailsSent("jitrue", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		
		// rkirkend (the primary delegate) should now have no emails
		assertEquals("rkirkend should have no emails.", 0, getMockEmailService().emailsSent("rkirkend", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		
		// jhopf should now have no emails since his top-level requests are no longer notified
		assertEquals("jhopf should have no emails.", 0, getMockEmailService().emailsSent("jhopf", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		
		// bmcgough should now have no emails since his notification preference is DAILY
		assertEquals("bmcgough should have no emails.", 0, getMockEmailService().emailsSent("bmcgough", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
	}
	
	/**
	 * Tests that the fromNotificationAddress on the document type works properly.  Used to test implementation of KULWF-628.
	 */
	@Test public void testDocumentTypeNotificationFromAddress() throws Exception {
		// first test that the notification from addresses are configured correctly
		DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName("NotificationTest");
		assertNull("Wrong notification from address, should be null.", documentType.getNotificationFromAddress());
		
		// test the parent document type
		documentType = KEWServiceLocator.getDocumentTypeService().findByName("NotificationFromAddressParent");
		assertEquals("Wrong notification from address.", "fakey@mcfakey.com", documentType.getNotificationFromAddress());
		
		// test a child document type which overrides the parent's address
		documentType = KEWServiceLocator.getDocumentTypeService().findByName("NotificationFromAddressChild");
		assertEquals("Wrong notification from address.", "fakey@mcchild.com", documentType.getNotificationFromAddress());

		// test a child document type which doesn't override the parent's address
		documentType = KEWServiceLocator.getDocumentTypeService().findByName("NotificationFromAddressChildInherited");
		assertEquals("Wrong notification from address.", "fakey@mcfakey.com", documentType.getNotificationFromAddress());
		
		// Do an app specific route to a document which should send an email to fakey@mcchild.com
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "NotificationFromAddressChild");
		document.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "Initial", "", new NetworkIdVO("ewestfal"), "", true);
		document.routeDocument("");
		
		// verify that ewestfal was sent an email
		assertEquals("ewestfal should have an email.", 1, getMockEmailService().emailsSent("ewestfal", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
		
		// we currently have no way from this test to determine the email address used for notification
	}
	
	private void assertDefaultNotificationPreferences(WorkflowUser user) throws Exception {
		Preferences prefs = getPreferencesService().getPreferences(user);
		assertEquals(EdenConstants.EMAIL_RMNDR_IMMEDIATE, prefs.getEmailNotification());
		assertEquals(EdenConstants.PREFERENCES_YES_VAL, prefs.getNotifyPrimaryDelegation());
		assertEquals(EdenConstants.PREFERENCES_NO_VAL, prefs.getNotifySecondaryDelegation());
	}
	
	private PreferencesService getPreferencesService() {
		return KEWServiceLocator.getPreferencesService();
	}
		
	private MockEmailNotificationService getMockEmailService() {
		return (MockEmailNotificationService)KEWServiceLocator.getActionListEmailService();
	}
	
}
