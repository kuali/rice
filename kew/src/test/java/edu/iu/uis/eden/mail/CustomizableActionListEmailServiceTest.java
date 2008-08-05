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

import org.junit.Test;
import org.kuali.rice.kew.util.EdenConstants;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.workflow.test.KEWTestCase;

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
    private static final int EXPECTED_MILLISECONDS_TO_SEND_REMINDER = 100;

    @UnitTestData(sqlFiles = {@UnitTestFile(filename = "classpath:testEmailLifecycle.sql", delimiter = ";")})
    @Test public void testEmailCreationPerformance() throws Exception {
        getMockEmailService().resetReminderCounts();
        assertEquals("total number of reminders sent should be 0", Integer.valueOf(0), getMockEmailService().getTotalPeriodicRemindersSent());
        assertEquals("total number of daily reminders sent should be 0", Integer.valueOf(0), getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_DAY_VAL));
        assertEquals("total number of weekly reminders sent should be 0", Integer.valueOf(0), getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_WEEK_VAL));

        long totalStartTimeInMills = System.currentTimeMillis();
        setupPreferences(Arrays.asList(new AuthenticationUserId[]{new AuthenticationUserId("rkirkend"),new AuthenticationUserId("jhopf")}), EdenConstants.WEEKLY);
        long weeklyStartTimeInMills = System.currentTimeMillis();
        getMockEmailService().sendWeeklyReminder();
        assertTrue("Style content service should have been called but was not", getMockEmailService().wasStyleServiceAccessed());
        long weeklyEndTimeInMills = System.currentTimeMillis();
        setupPreferences(Arrays.asList(new AuthenticationUserId[]{new AuthenticationUserId("rkirkend"),new AuthenticationUserId("jhopf")}), EdenConstants.DAILY);
        long dailyStartTimeInMills = System.currentTimeMillis();
        getMockEmailService().sendDailyReminder();
        assertTrue("Style content service should have been called but was not", getMockEmailService().wasStyleServiceAccessed());
        long dailyEndTimeInMills = System.currentTimeMillis();
        long totalEndTimeInMills = System.currentTimeMillis();

        // check performance
        LOG.info("Total time to process " + getMockEmailService().getTotalPeriodicRemindersSent() + " weekly and daily reminder messages was " + (totalEndTimeInMills - totalStartTimeInMills) + " milliseconds");
        LOG.info("Total time to process " + getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_WEEK_VAL) + " weekly reminder messages was " + (weeklyEndTimeInMills - weeklyStartTimeInMills) + " milliseconds");
        LOG.info("Total time to process " + getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_DAY_VAL) + " daily reminder messages was " + (dailyEndTimeInMills - dailyStartTimeInMills) + " milliseconds");
        assertTrue("total number of daily reminders sent should be greater than 0", Integer.valueOf(0) < getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_DAY_VAL));
        assertTrue("total number of weekly reminders sent should be greater than 0", Integer.valueOf(0) < getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_WEEK_VAL));

        // each action item should take less than 1 second
        Integer totalSent = getMockEmailService().getTotalPeriodicRemindersSent();
        int expectedValue = (totalSent * EXPECTED_MILLISECONDS_TO_SEND_REMINDER);
        assertTrue("Total time for " + totalSent + " reminders sent must be under " + expectedValue + " ms", expectedValue > (totalEndTimeInMills - totalStartTimeInMills));
        totalSent = getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_WEEK_VAL);
        expectedValue = (totalSent * EXPECTED_MILLISECONDS_TO_SEND_REMINDER);
        assertTrue("Weekly Reminder time for " + totalSent + " reminders sent must be under " + expectedValue + " ms", expectedValue > (weeklyEndTimeInMills - weeklyStartTimeInMills));
        totalSent = getMockEmailService().getTotalPeriodicRemindersSent(EdenConstants.EMAIL_RMNDR_DAY_VAL);
        expectedValue = (totalSent * EXPECTED_MILLISECONDS_TO_SEND_REMINDER);
        assertTrue("Daily Reminder time for " + totalSent + " reminders sent must be under " + expectedValue + " ms", expectedValue > (dailyEndTimeInMills - dailyStartTimeInMills));
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
