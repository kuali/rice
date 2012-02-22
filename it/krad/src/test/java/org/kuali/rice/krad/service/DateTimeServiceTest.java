/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.test.KRADTestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * This class tests the DateTime service.
 */
public class DateTimeServiceTest extends KRADTestCase {

    @Test public void testGetCurrentDate() {
        Date beforeServiceDate = new Date();
        Date serviceDate = CoreApiServiceLocator.getDateTimeService().getCurrentDate();
        Date afterServiceDate = new Date();

        assertTrue("beforeServiceDate not <= serviceDate", beforeServiceDate.before(serviceDate) || beforeServiceDate.equals(serviceDate));
        assertTrue("afterServiceDate not >= serviceDate", afterServiceDate.after(serviceDate) || afterServiceDate.equals(serviceDate));
    }

    @Test public void testGetCurrentSqlDate() {
        java.sql.Date serviceDate = CoreApiServiceLocator.getDateTimeService().getCurrentSqlDate();

        java.sql.Date beforeServiceDate = new java.sql.Date(serviceDate.getTime() - 100);
        java.sql.Date afterServiceDate = new java.sql.Date(serviceDate.getTime() + 100);

        assertTrue("beforeServiceDate not <= serviceDate", beforeServiceDate.before(serviceDate) || beforeServiceDate.equals(serviceDate));
        assertTrue("afterServiceDate not >= serviceDate", afterServiceDate.after(serviceDate) || afterServiceDate.equals(serviceDate));
    }

    @SuppressWarnings("deprecation")
    @Test public void testGetCurrentSqlDateMidnight() throws InterruptedException {
        // this test is invalid within 1 second of midnight, so wait for it
        waitForMidnightIfWithinOneSecond();
        java.sql.Date before = CoreApiServiceLocator.getDateTimeService().getCurrentSqlDateMidnight();
        java.util.Date checkBefore = new java.util.Date();
        Thread.sleep(500); // makes sure the clock has time to tick
        java.util.Date checkAfter = new java.util.Date();
        java.sql.Date after = CoreApiServiceLocator.getDateTimeService().getCurrentSqlDateMidnight();
        assertTrue(checkBefore.before(checkAfter)); // make sure the clock did tick
        assertEquals(before.getTime(), after.getTime());
        java.util.Date afterUtil = new java.util.Date(after.getTime());
        // these methods in java.sql.Date are not just deprecated; they throw IllegalArgumentException.
        assertEquals(0, afterUtil.getHours());
        assertEquals(0, afterUtil.getMinutes());
        assertEquals(0, afterUtil.getSeconds());
    }

    @SuppressWarnings("deprecation")
    private static void waitForMidnightIfWithinOneSecond() throws InterruptedException {
        java.util.Date now = new java.util.Date();
        java.util.Date then = new java.util.Date(now.getTime() + 1000);
        if (now.getDay() != then.getDay()) {
            Thread.sleep(1000);
        }
    }

    @Test public void testGetCurrentCalendar() {
        Date beforeServiceDate = new Date();
        Calendar serviceCalendar = CoreApiServiceLocator.getDateTimeService().getCurrentCalendar();
        Date afterServiceDate = new Date();

        // extract the calendar's Date, for easier testing
        Date serviceDate = serviceCalendar.getTime();

        assertTrue("beforeServiceDate not <= serviceDate", beforeServiceDate.before(serviceDate) || beforeServiceDate.equals(serviceDate));
        assertTrue("afterServiceDate not >= serviceDate", afterServiceDate.after(serviceDate) || afterServiceDate.equals(serviceDate));
    }


    @Test public void testConvertToSqlTimestamp_blankTimeString() throws ParseException {
        assertNull(CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(null));
    }

    @Test public void testConvertToSqlTimestamp_invalidTimeString() {
        boolean failedAsExpected = false;
        try {
            CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp("foo");
        }
        catch (ParseException e) {
            failedAsExpected = true;
        }
        assertTrue("invalid timeString failed to fail", failedAsExpected);
    }

    @Test public void testConvertToSqlTimestamp_validTimeString() throws ParseException {
        java.sql.Timestamp serviceTimestamp = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp("05/01/1966 02:41 PM");
        Calendar serviceCalendar = Calendar.getInstance();
        serviceCalendar.setTime(serviceTimestamp);
        assertEquals("unexpected year", 1966, serviceCalendar.get(Calendar.YEAR));
        assertEquals("unexpected month", 5, serviceCalendar.get(Calendar.MONTH) + 1);
        assertEquals("unexpected day", 1, serviceCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals("unexpected hours", 14, serviceCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals("unexpected minutes", 41, serviceCalendar.get(Calendar.MINUTE));
        assertEquals("unexpected seconds", 0, serviceCalendar.get(Calendar.SECOND));
        assertEquals("unexpected milliseconds", serviceTimestamp.getNanos(), 0);
    }

    @Test public void testConvertToSqlDate_blankDateString() throws ParseException {
        boolean failedAsExpected = false;

        try {
            CoreApiServiceLocator.getDateTimeService().convertToSqlDate("");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue("blank dateString failed to fail", failedAsExpected);
    }

    @Test public void testConvertToSqlDate_invalidDateString() {
        boolean failedAsExpected = false;

        try {
            CoreApiServiceLocator.getDateTimeService().convertToSqlDate("foo");
        }
        catch (ParseException e) {
            failedAsExpected = true;
        }

        assertTrue("invalid dateString failed to fail", failedAsExpected);
    }

    @Test public void testConvertToSqlDate_validDateString() throws ParseException {
        java.sql.Date serviceDate = CoreApiServiceLocator.getDateTimeService().convertToSqlDate("05/01/1966");

        Calendar serviceCalendar = Calendar.getInstance();
        serviceCalendar.setTime(serviceDate);

        assertEquals("unexpected year", 1966, serviceCalendar.get(Calendar.YEAR));
        assertEquals("unexpected month", 5, serviceCalendar.get(Calendar.MONTH) + 1);
        assertEquals("unexpected day", 1, serviceCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals("unexpected hours", 0, serviceCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals("unexpected minutes", 0, serviceCalendar.get(Calendar.MINUTE));
        assertEquals("unexpected seconds", 0, serviceCalendar.get(Calendar.SECOND));
    }


    @Test public void testDateDiff() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        // regular 1 year period
        Date date1 = sdf.parse("01/01/2006");
        Date date2 = sdf.parse("12/31/2006");

        assertEquals("365", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));
        assertEquals("364", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // one year period w/ leap year
        date1 = sdf.parse("01/01/2008");
        date2 = sdf.parse("12/31/2008");

        assertEquals("366", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));
        assertEquals("365", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        assertEquals("366", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));
        assertEquals("365", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // one year period w/ leap year, beginning in the middle of the year
        date1 = sdf.parse("07/01/2007");
        date2 = sdf.parse("06/30/2008");

        assertEquals("366", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));
        assertEquals("365", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // one year period, start and end in middle of the year
        date1 = sdf.parse("07/01/2006");
        date2 = sdf.parse("06/30/2007");

        assertEquals("365", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));
        assertEquals("364", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // one month period
        date1 = sdf.parse("01/01/2006");
        date2 = sdf.parse("01/31/2006");

        assertEquals("31", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));
        assertEquals("30", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // another one month period
        date1 = sdf.parse("04/14/2006");
        date2 = sdf.parse("05/13/2006");

        assertEquals("30", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));
        assertEquals("29", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // one day period
        date1 = sdf.parse("01/01/2006");
        date2 = sdf.parse("01/02/2006");

        assertEquals("2", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));
        assertEquals("1", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // arbitrary dates
        date1 = sdf.parse("01/01/2006");
        date2 = sdf.parse("06/30/2006");

        assertEquals("181", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));

        date1 = sdf.parse("07/01/2006");
        date2 = sdf.parse("12/31/2006");

        assertEquals("184", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, true)));

        // within same month
        date1 = sdf.parse("07/01/2006");
        date2 = sdf.parse("07/20/2006");

        assertEquals("19", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // same day
        date1 = sdf.parse("07/20/2006");
        date2 = sdf.parse("07/20/2006");

        assertEquals("0", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));

        // end date is prior to start date
        date1 = sdf.parse("07/25/2006");
        date2 = sdf.parse("07/20/2006");

        assertEquals("-5", Integer.toString(CoreApiServiceLocator.getDateTimeService().dateDiff(date1, date2, false)));
    }
}
