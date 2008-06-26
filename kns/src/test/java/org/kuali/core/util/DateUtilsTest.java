/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.kuali.rice.testharness.KNSTestCase;

public class DateUtilsTest extends KNSTestCase {
    Calendar nowCal;
    Calendar prevCal;
    Calendar nextCal;

    Date nowDate;
    Date prevDate;
    Date nextDate;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        nowCal = GregorianCalendar.getInstance();
        nowDate = nowCal.getTime();

        prevCal = new GregorianCalendar();
        prevCal.setTime(nowDate);
        prevCal.add(Calendar.DAY_OF_YEAR, -1);
        prevDate = prevCal.getTime();

        nextCal = new GregorianCalendar();
        nextCal.setTime(nowDate);
        nextCal.add(Calendar.DAY_OF_YEAR, 1);
        nextDate = nextCal.getTime();

        assertTrue(prevCal.before(nowCal));
        assertTrue(nextCal.after(nowCal));

        assertTrue(prevDate.before(nowDate));
        assertTrue(nextDate.after(nowDate));
    }

    @Test public void testIsSameDayDateDate_prevDate() {
        assertFalse(DateUtils.isSameDay(prevDate, nowDate));
    }

    @Test public void testIsSameDayDateDate_sameDate() {
        assertTrue(DateUtils.isSameDay(nowDate, nowDate));
    }

    @Test public void testIsSameDayDateDate_nextDate() {
        assertFalse(DateUtils.isSameDay(nowDate, nextDate));
    }

    @Test public void testIsSameDayCalendarCalendar_prevCal() {
        assertFalse(DateUtils.isSameDay(nowCal, prevCal));
    }

    @Test public void testIsSameDayCalendarCalendar_sameCal() {
        assertTrue(DateUtils.isSameDay(nowCal, nowCal));
    }

    @Test public void testIsSameDayCalendarCalendar_nextCal() {
        assertFalse(DateUtils.isSameDay(nextCal, nowCal));
    }

    @Test public void testConvertToSqlDate() {
        java.sql.Date sqlDate = DateUtils.convertToSqlDate(nowDate);
        assertEquals(sqlDate.getTime(), nowDate.getTime());
    }

    @Test public void testClearTimeFieldsDate() {
        java.util.Date timeless = DateUtils.clearTimeFields(nowDate);

        assertTimeCleared(timeless.getTime());
    }

    @Test public void testClearTimeFieldsSqlDate() {
        java.sql.Date timeless = DateUtils.clearTimeFields(DateUtils.convertToSqlDate(nowDate));

        assertTimeCleared(timeless.getTime());
    }


    private void assertTimeCleared(long timeInMilliseconds) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timeInMilliseconds);

        assertFalse(0 == cal.get(Calendar.YEAR));
        // month can legitimately be 0
        assertFalse(0 == cal.get(Calendar.DATE));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
    }
}
