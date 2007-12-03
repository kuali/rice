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
package edu.iu.uis.eden.mail;

import mocks.MockEmailNotificationService;
import mocks.MockEmailNotificationServiceImpl;

import org.junit.Test;
import org.kuali.rice.core.Core;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

public class EmailReminderLifecycleTest extends KEWTestCase {

	private EmailReminderLifecycle emailReminderLifecycle;

	@Override
	public void setUp() throws Exception {
		super.setUp();

	}

	@Test public void testDailyEmails() throws Exception {
		// fire daily every 2 seconds
		Core.getCurrentContextConfig().overrideProperty(EdenConstants.DAILY_EMAIL_CRON_EXPRESSION, "0/2 * * * * ?");
		// turn daily on and weekly off
		Core.getCurrentContextConfig().overrideProperty(EdenConstants.DAILY_EMAIL_ACTIVE, "true");
		Core.getCurrentContextConfig().overrideProperty(EdenConstants.WEEKLY_EMAIL_ACTIVE, "false");



		// setup ewestfal to recieve daily emails
		WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
		Preferences prefs = KEWServiceLocator.getPreferencesService().getPreferences(ewestfal);
		prefs.setEmailNotification(EdenConstants.DAILY);
		KEWServiceLocator.getPreferencesService().savePreferences(ewestfal, prefs);

		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
		document.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "", new NetworkIdVO("ewestfal"), "", Boolean.TRUE);
		document.routeDocument("");

		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
		assertTrue(document.isApprovalRequested());

		int emailsSent = getMockEmailService().emailsSent("ewestfal", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ);
		assertEquals("ewestfal should have no emails.", 0, emailsSent);
		MockEmailNotificationServiceImpl.SEND_DAILY_REMINDER_CALLED = false;
		MockEmailNotificationServiceImpl.SEND_WEEKLY_REMINDER_CALLED = false;

		// let's fire up the lifecycle
		emailReminderLifecycle = new EmailReminderLifecycle();
		emailReminderLifecycle.start();

		// sleep for 10 seconds
		Thread.sleep(10000);

		// send daily reminder should have now been called
		assertTrue("daily reminder should have been called.", MockEmailNotificationServiceImpl.SEND_DAILY_REMINDER_CALLED);
		assertFalse("weekly reminder should NOT have been called.", MockEmailNotificationServiceImpl.SEND_WEEKLY_REMINDER_CALLED);

		emailReminderLifecycle.stop();

		// try restarting to verify rescheduling of tasks
		emailReminderLifecycle.start();
		emailReminderLifecycle.stop();

	}

	@Test public void testWeeklyEmails() throws Exception {
		// fire daily every 2 seconds
		Core.getCurrentContextConfig().overrideProperty(EdenConstants.WEEKLY_EMAIL_CRON_EXPRESSION, "0/2 * * * * ?");
		// turn weekly on and daily off
		Core.getCurrentContextConfig().overrideProperty(EdenConstants.WEEKLY_EMAIL_ACTIVE, "true");
		Core.getCurrentContextConfig().overrideProperty(EdenConstants.DAILY_EMAIL_ACTIVE, "false");

		// setup ewestfal to recieve weekly emails
		WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
		Preferences prefs = KEWServiceLocator.getPreferencesService().getPreferences(ewestfal);
		prefs.setEmailNotification(EdenConstants.WEEKLY);
		KEWServiceLocator.getPreferencesService().savePreferences(ewestfal, prefs);

		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
		document.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "", new NetworkIdVO("ewestfal"), "", Boolean.TRUE);
		document.routeDocument("");

		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
		assertTrue(document.isApprovalRequested());

		int emailsSent = getMockEmailService().emailsSent("ewestfal", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ);
		assertEquals("ewestfal should have no emails.", 0, emailsSent);
		MockEmailNotificationServiceImpl.SEND_DAILY_REMINDER_CALLED = false;
		MockEmailNotificationServiceImpl.SEND_WEEKLY_REMINDER_CALLED = false;

		// let's fire up the lifecycle
		emailReminderLifecycle = new EmailReminderLifecycle();
		emailReminderLifecycle.start();

		// sleep for 10 seconds
		Thread.sleep(10000);

		// send weekly reminder should have now been called
		assertTrue("weekly reminder should have been called.", MockEmailNotificationServiceImpl.SEND_WEEKLY_REMINDER_CALLED);
		assertFalse("daily reminder should NOT have been called.", MockEmailNotificationServiceImpl.SEND_DAILY_REMINDER_CALLED);

		emailReminderLifecycle.stop();

		// try restarting to verify rescheduling of tasks
		emailReminderLifecycle.start();
	}


//	/**
//	 * Verify that no more messages are put in the queue if there are already weekly and daily reminders in the
//	 * queue
//	 * @throws Exception
//	 */
//	@Test
//	public void testEmailMessagesInQueue() throws Exception {
//
//		setUpConfigForEmail();
//
//		PersistedMessage dailyMessage = getMockDailyMessage();
//		PersistedMessage weeklyMessage = getMockWeeklyMessage();
//		KEWServiceLocator.getRouteQueueService().save(dailyMessage);
//		KEWServiceLocator.getRouteQueueService().save(weeklyMessage);
//
//		Collection messages = KEWServiceLocator.getRouteQueueService().findAll();
//		assertEquals("Should only be 2 items present in queue", 2, messages.size());
//
//		emailReminderLifecycle.start();
//
//		messages = KEWServiceLocator.getRouteQueueService().findAll();
//		assertEquals("Should only be 2 items present in queue", 2, messages.size());
//
//		PersistedMessage fetchedDaily = null;
//		PersistedMessage fetchedWeekly = null;
//
//		for (Iterator iter = messages.iterator(); iter.hasNext();) {
//			PersistedMessage fetchedMessage = (PersistedMessage) iter.next();
//			if (fetchedMessage.getMethodName().equals("sendDailyReminder")) {
//				fetchedDaily = fetchedMessage;
//			} else {
//				fetchedWeekly = fetchedMessage;
//			}
//		}
//		assertEquals("Daily message was re-inserted or removed when it should have been allowed to stay in queue for later processing", dailyMessage.getRouteQueueId(), fetchedDaily.getRouteQueueId());
//		assertEquals("Weekly message was re-inserted or removed when it should have been allowed to stay in queue for later processing", weeklyMessage.getRouteQueueId(), fetchedWeekly.getRouteQueueId());
//		assertTrue("Lifecycle should report itself as started", emailReminderLifecycle.isStarted());
//	}
//
//	/**
//	 * If only a daily is in the queue then the other reminder should be put in the queue
//	 *
//	 * @throws Exception
//	 */
//	@Test public void testOnlyDailyReminderInQueue() throws Exception {
//
//		setUpConfigForEmail();
//
//		PersistedMessage dailyMessage = getMockDailyMessage();
//		KEWServiceLocator.getRouteQueueService().save(dailyMessage);
//
//		Collection messages = KEWServiceLocator.getRouteQueueService().findAll();
//		assertEquals("Should only be 1 items present in queue", 1, messages.size());
//
//		emailReminderLifecycle.start();
//
//		messages = KEWServiceLocator.getRouteQueueService().findAll();
//		assertEquals("Should only be 2 items present in queue", 2, messages.size());
//
//		PersistedMessage fetchedDaily = null;
//		PersistedMessage fetchedWeekly = null;
//
//		for (Iterator iter = messages.iterator(); iter.hasNext();) {
//			PersistedMessage fetchedMessage = (PersistedMessage) iter.next();
//			if (fetchedMessage.getMethodName().equals("sendDailyReminder")) {
//				fetchedDaily = fetchedMessage;
//			} else {
//				fetchedWeekly = fetchedMessage;
//			}
//		}
//		assertEquals("Daily message was re-inserted or removed when it should have been allowed to stay in queue for later processing", dailyMessage.getRouteQueueId(), fetchedDaily.getRouteQueueId());
//		assertTrue(fetchedWeekly != null);
//		assertTrue("Lifecycle should report itself as started", emailReminderLifecycle.isStarted());
//	}
//
//	/**
//	 * If only a weekly reminder is in the queue then the other reminder should be put in the queue
//	 *
//	 * @throws Exception
//	 */
//	@Test public void testOnlyWeeklyReminderInQueue() throws Exception {
//
//		setUpConfigForEmail();
//
//		PersistedMessage weeklyMessage = getMockWeeklyMessage();
//		KEWServiceLocator.getRouteQueueService().save(weeklyMessage);
//
//		Collection messages = KEWServiceLocator.getRouteQueueService().findAll();
//		assertEquals("Should only be 1 items present in queue", 1, messages.size());
//
//		emailReminderLifecycle.start();
//
//		messages = KEWServiceLocator.getRouteQueueService().findAll();
//		assertEquals("Should only be 2 items present in queue", 2, messages.size());
//
//		PersistedMessage fetchedDaily = null;
//		PersistedMessage fetchedWeekly = null;
//
//		for (Iterator iter = messages.iterator(); iter.hasNext();) {
//			PersistedMessage fetchedMessage = (PersistedMessage) iter.next();
//			if (fetchedMessage.getMethodName().equals("sendDailyReminder")) {
//				fetchedDaily = fetchedMessage;
//			} else {
//				fetchedWeekly = fetchedMessage;
//			}
//		}
//		assertEquals("Weekly message was re-inserted or removed when it should have been allowed to stay in queue for later processing", weeklyMessage.getRouteQueueId(), fetchedWeekly.getRouteQueueId());
//		assertTrue("Daily message not sent", fetchedDaily != null);
//		assertTrue("Lifecycle should report itself as started", emailReminderLifecycle.isStarted());
//
//	}
//
//	/**
//	 * Tests that email reminder calls are sent to the queue when none are present.  New messages should
//	 * be set for the proper delay.
//	 *
//	 * @throws Exception
//	 */
//	@Test public void testNoEmailRemindersInQueue() throws Exception {
//
//		setUpConfigForEmail();
//
//		emailReminderLifecycle.start();
//		Collection messages = KEWServiceLocator.getRouteQueueService().findAll();
//		assertEquals("Should only be 2 items present in queue", 2, messages.size());
//		PersistedMessage fetchedDaily = null;
//		PersistedMessage fetchedWeekly = null;
//
//		for (Iterator iter = messages.iterator(); iter.hasNext();) {
//			PersistedMessage fetchedMessage = (PersistedMessage) iter.next();
//			if (fetchedMessage.getMethodName().equals("sendDailyReminder")) {
//				fetchedDaily = fetchedMessage;
//			} else {
//				fetchedWeekly = fetchedMessage;
//			}
//		}
//		assertNotNull("No daily message sent", fetchedDaily);
//		assertNotNull("No weekly message sent", fetchedWeekly);
//
//		assertTrue("Daily message not sent", fetchedDaily != null);
//		assertTrue("Weekly message not sent", fetchedWeekly != null);
//		assertTrue("Lifecycle should report itself as started", emailReminderLifecycle.isStarted());
//
//
//		AsynchronousCall methodCall = (AsynchronousCall)KSBServiceLocator.getMessageHelper().deserializeObject(fetchedWeekly.getPayload());
//		assertEquals("Weekly email not on a weekly delay", EmailReminderLifecycle.WEEKLY_DELAY, methodCall.getRepeatCallTimeIncrement().longValue());
//
//		methodCall = (AsynchronousCall)KSBServiceLocator.getMessageHelper().deserializeObject(fetchedDaily.getPayload());
//		assertEquals("Weekly email not on a weekly delay", EmailReminderLifecycle.DAILY_DELAY, methodCall.getRepeatCallTimeIncrement().longValue());
//	}
//
//	/**
//	 * the lifecycle should not blow up if this ip is the designated emailer but no email options are sent.  it should
//	 * do nothing and report started.
//	 *
//	 * @throws Exception
//	 */
//	@Test public void testNoEmailDatesInConfig() throws Exception {
//		KEWServiceLocator.getApplicationConstantsService().save(new ApplicationConstant(EdenConstants.APP_CONST_EMAIL_FIRST_SEND_IP_KEY, Utilities.getIpNumber()));
//
//		Config config = Core.getCurrentContextConfig();
//		config.getProperties().remove(Config.FIRST_DAILY_EMAIL_DELIVERY_DATE);
//		config.getProperties().remove(Config.FIRST_WEEKLY_EMAIL_DELIVERY_DATE);
//		emailReminderLifecycle.start();
//		Collection messages = KEWServiceLocator.getRouteQueueService().findAll();
//		assertEquals("Should not be items present in queue", 0, messages.size());
//
//		assertTrue("Lifecycle should report itself as started", emailReminderLifecycle.isStarted());
//		emailReminderLifecycle.stop();
//		assertFalse("Lifecycle should not report itself as started", emailReminderLifecycle.isStarted());
//	}
//
//	/**
//	 * Keep the threadpool on and synchronous.  Start the lifecycle and verify that
//	 * the action list email service got called.
//	 * @throws Exception
//	 */
//	@Test public void testActionListEmailServiceBeingCalled() throws Exception {
//		KEWServiceLocator.getApplicationConstantsService().save(new ApplicationConstant(EdenConstants.APP_CONST_EMAIL_FIRST_SEND_IP_KEY, Utilities.getIpNumber()));
//		Config config = Core.getCurrentContextConfig();
//		config.overrideProperty(Config.FIRST_DAILY_EMAIL_DELIVERY_DATE, DAILY_REMINDER_DATE);
//		config.overrideProperty(Config.FIRST_WEEKLY_EMAIL_DELIVERY_DATE, WEEKLY_REMINDER_DATE);
//		emailReminderLifecycle.start();
//		assertTrue("Send daily not called on email notification service", MockEmailNotificationServiceImpl.SEND_DAILY_REMINDER_CALLED);
//		assertTrue("Send weekly not called on email notification service", MockEmailNotificationServiceImpl.SEND_WEEKLY_REMINDER_CALLED);
//	}
//
//	private void setUpConfigForEmail() throws Exception {
//		KEWServiceLocator.getApplicationConstantsService().save(new ApplicationConstant(EdenConstants.APP_CONST_EMAIL_FIRST_SEND_IP_KEY, Utilities.getIpNumber()));
//
//		Config config = Core.getCurrentContextConfig();
//		config.overrideProperty(Config.FIRST_DAILY_EMAIL_DELIVERY_DATE, DAILY_REMINDER_DATE);
//		config.overrideProperty(Config.FIRST_WEEKLY_EMAIL_DELIVERY_DATE, WEEKLY_REMINDER_DATE);
//
//	}
//
//
//	private PersistedMessage getMockDailyMessage() throws Exception {
//		PersistedMessage dailyMessage = new PersistedMessage();
//		dailyMessage.setServiceName(emailReminderLifecycle.getEmailServiceName().toString());
//		dailyMessage.setMethodName("sendDailyReminder");
//		dailyMessage.setQueueDate(new Timestamp(System.currentTimeMillis()));
//		dailyMessage.setQueuePriority(1);
//		dailyMessage.setQueueStatus("Q");
//		dailyMessage.setRetryCount(1);
//		dailyMessage.setIpNumber(Utilities.getIpNumber());
//		dailyMessage.setMessageEntity("KEW");
//		dailyMessage.setPayload(KSBServiceLocator.getMessageHelper().serializeObject("payload"));
//		return dailyMessage;
//	}
//
//	private PersistedMessage getMockWeeklyMessage() throws Exception {
//		PersistedMessage weeklyMessage = new PersistedMessage();
//		weeklyMessage.setServiceName(emailReminderLifecycle.getEmailServiceName().toString());
//		weeklyMessage.setQueueDate(new Timestamp(System.currentTimeMillis()));
//		weeklyMessage.setMethodName("sendWeeklyReminder");
//		weeklyMessage.setQueuePriority(1);
//		weeklyMessage.setQueueStatus("Q");
//		weeklyMessage.setRetryCount(1);
//		weeklyMessage.setIpNumber(Utilities.getIpNumber());
//		weeklyMessage.setMessageEntity("KEW");
//		weeklyMessage.setPayload(KSBServiceLocator.getMessageHelper().serializeObject("payload"));
//		return weeklyMessage;
//	}

	private MockEmailNotificationService getMockEmailService() {
        return (MockEmailNotificationService)KEWServiceLocator.getActionListEmailService();
    }

}
