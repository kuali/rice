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
package edu.iu.uis.eden.mail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mocks.MockEmailNotificationService;
import mocks.MockEmailNotificationServiceImpl;

import org.junit.Test;
import org.kuali.rice.core.Core;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class CustomizableActionListEmailServiceTest extends KEWTestCase {
    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    
    private static final int STANDARD_SLEEP_TIME = 5000;
    private static final String STANDARD_NO_RUN_CRON = "* * 3 * * ?";
    
    /**
     * This method used to reset email sending to false for both daily and weekly reminders 
     * 
     * @see org.kuali.rice.test.RiceTestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.DAILY_EMAIL_ACTIVE, "false");
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.WEEKLY_EMAIL_ACTIVE, "false");
        super.tearDown();
    }

    @UnitTestData(sqlFiles = {@UnitTestFile(filename = "classpath:testEmailLifecycle.sql", delimiter = ";")})
    @Test public void testEmailCreationsPerformance() throws Exception {
        resetEmailReminderLifecycle();
        int totalSleepTime = 0;

        Core.getCurrentContextConfig().overrideProperty(EdenConstants.WEEKLY_EMAIL_ACTIVE, "true");
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.WEEKLY_EMAIL_CRON_EXPRESSION, "0/2 * * * * ?");
        setupPreferences(Arrays.asList(new AuthenticationUserId[]{new AuthenticationUserId("rkirkend"),new AuthenticationUserId("jhopf")}), EdenConstants.WEEKLY);
        long totalStartTimeInMills = System.currentTimeMillis();
        long weeklyStartTimeInMills = System.currentTimeMillis();
        // let's fire up the lifecycle
        EmailReminderLifecycle emailReminderLifecycle = new EmailReminderLifecycle();
        emailReminderLifecycle.start();
        Thread.sleep(STANDARD_SLEEP_TIME);
        totalSleepTime += STANDARD_SLEEP_TIME;
        emailReminderLifecycle.stop();
        long weeklyEndTimeInMills = System.currentTimeMillis();

        // send weekly reminder should have now been called
        assertTrue("weekly reminder should have been called.", MockEmailNotificationServiceImpl.SEND_WEEKLY_REMINDER_CALLED);
        assertFalse("daily reminder should NOT have been called.", MockEmailNotificationServiceImpl.SEND_DAILY_REMINDER_CALLED);
        resetEmailReminderLifecycle();

        Core.getCurrentContextConfig().overrideProperty(EdenConstants.DAILY_EMAIL_ACTIVE, "true");
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.DAILY_EMAIL_CRON_EXPRESSION, "0/2 * * * * ?");
        setupPreferences(Arrays.asList(new AuthenticationUserId[]{new AuthenticationUserId("rkirkend"),new AuthenticationUserId("jhopf")}), EdenConstants.DAILY);
        long dailyStartTimeInMills = System.currentTimeMillis();
        // let's fire up the lifecycle
        emailReminderLifecycle = new EmailReminderLifecycle();
        emailReminderLifecycle.start();
        Thread.sleep(STANDARD_SLEEP_TIME);
        totalSleepTime += STANDARD_SLEEP_TIME;
        emailReminderLifecycle.stop();
        long dailyEndTimeInMills = System.currentTimeMillis();
        long totalEndTimeInMills = System.currentTimeMillis();

        // send daily reminder should have now been called
        assertFalse("weekly reminder should NOT have been called.", MockEmailNotificationServiceImpl.SEND_WEEKLY_REMINDER_CALLED);
        assertTrue("daily reminder should have been called.", MockEmailNotificationServiceImpl.SEND_DAILY_REMINDER_CALLED);
        resetEmailReminderLifecycle();
        
        // check performance
        LOG.info("Total time to process " + getMockEmailService().getTotalPeriodicRemindersSent() + " weekly and daily reminder messages was " + getSecondsDifferential(totalStartTimeInMills, totalEndTimeInMills) /*(totalEndTimeInMills - totalStartTimeInMills)*/ + " seconds");
        LOG.info("Total time to process " + getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_WEEK_VAL) + " weekly reminder messages was " + getSecondsDifferential(weeklyStartTimeInMills, weeklyEndTimeInMills) /*(weeklyEndTimeInMills - weeklyStartTimeInMills)*/ + " seconds");
        LOG.info("Total time to process " + getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_DAY_VAL) + " daily reminder messages was " + getSecondsDifferential(dailyStartTimeInMills, dailyEndTimeInMills) /*(dailyEndTimeInMills - dailyStartTimeInMills)*/ + " seconds");

        // each action item should take less than 1 second
        Integer totalSent = getMockEmailService().getTotalPeriodicRemindersSent();
        int expectedValue = totalSent * 1000;
        assertTrue("Total time for " + totalSent + " reminders sent must be under " + expectedValue + " ms", expectedValue > (totalEndTimeInMills - totalStartTimeInMills));
        totalSent = getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_WEEK_VAL);
        expectedValue = totalSent * 1000;
        assertTrue("Weekly Reminder time for " + totalSent + " reminders sent must be under " + expectedValue + " ms", expectedValue > (weeklyEndTimeInMills - weeklyStartTimeInMills));
        totalSent = getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_DAY_VAL);
        expectedValue = totalSent * 1000;
        assertTrue("Daily Reminder time for " + totalSent + " reminders sent must be under " + expectedValue + " ms", expectedValue > (dailyEndTimeInMills - dailyStartTimeInMills));
    }
    
    private void resetEmailReminderLifecycle() throws Exception {
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.WEEKLY_EMAIL_ACTIVE, "false");
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.DAILY_EMAIL_ACTIVE, "false");
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.WEEKLY_EMAIL_CRON_EXPRESSION, STANDARD_NO_RUN_CRON);
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.DAILY_EMAIL_CRON_EXPRESSION, STANDARD_NO_RUN_CRON);
        MockEmailNotificationServiceImpl.SEND_WEEKLY_REMINDER_CALLED = false;
        MockEmailNotificationServiceImpl.SEND_DAILY_REMINDER_CALLED = false;
        EmailReminderLifecycle emailReminderLifecycle = new EmailReminderLifecycle();
        emailReminderLifecycle.start();
        emailReminderLifecycle.stop();
    }
    
    private long getSecondsDifferential(long startTimeInMills, long endTimeInMills) {
        return (endTimeInMills - startTimeInMills) / Long.valueOf("1000");
    }
    
    private void setupPreferences(List<AuthenticationUserId> users, String emailNotificationPreference) throws Exception {
        for (Iterator iterator = users.iterator(); iterator.hasNext();) {
            AuthenticationUserId authenticationUserId = (AuthenticationUserId) iterator.next();
            WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(authenticationUserId);
            Preferences prefs = KEWServiceLocator.getPreferencesService().getPreferences(user);
            prefs.setEmailNotification(emailNotificationPreference);
            KEWServiceLocator.getPreferencesService().savePreferences(user, prefs);
        }
    }
    
    private MockEmailNotificationService getMockEmailService() {
        return (MockEmailNotificationService)KEWServiceLocator.getActionListEmailService();
    }

}
