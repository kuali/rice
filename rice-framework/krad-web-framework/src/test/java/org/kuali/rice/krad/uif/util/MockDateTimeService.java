/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

import org.kuali.rice.core.api.datetime.DateTimeService;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Mock {@link DateTimeService} implementation for supporting UIF unit tests.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockDateTimeService implements DateTimeService {
    
    @Override
    public String toDateString(Date date) {
        return date == null ? null : new SimpleDateFormat("MM/dd/yy").format(date);
    }

    @Override
    public String toTimeString(Time time) {
        return null;
    }

    @Override
    public String toDateTimeString(Date date) {
        return null;
    }

    @Override
    public String toString(Date date, String pattern) {
        return null;
    }

    @Override
    public Date getCurrentDate() {
        return null;
    }

    @Override
    public Timestamp getCurrentTimestamp() {
        return null;
    }

    @Override
    public java.sql.Date getCurrentSqlDate() {
        return null;
    }

    @Override
    public java.sql.Date getCurrentSqlDateMidnight() {
        return null;
    }

    @Override
    public Calendar getCurrentCalendar() {
        return null;
    }

    @Override
    public Calendar getCalendar(Date date) {
        return null;
    }

    @Override
    public Date convertToDate(String dateString) throws ParseException {
        return null;
    }

    @Override
    public Date convertToDateTime(String dateTimeString) throws ParseException {
        return null;
    }

    @Override
    public Timestamp convertToSqlTimestamp(String timeString) throws ParseException {
        return null;
    }

    /**
     * Converts a date using the format string MM/dd/yy.
     *
     * {@inheritDoc}
     */
    @Override
    public java.sql.Date convertToSqlDate(String dateString) throws ParseException {
        return new java.sql.Date(new SimpleDateFormat("MM/dd/yy").parse(dateString).getTime());
    }

    @Override
    public java.sql.Date convertToSqlDateUpperBound(String dateString) throws ParseException {
        return null;
    }

    @Override
    public Time convertToSqlTime(String timeString) throws ParseException {
        return null;
    }

    @Override
    public java.sql.Date convertToSqlDate(Timestamp timestamp) throws ParseException {
        return null;
    }

    @Override
    public int dateDiff(Date date1, Date date2, boolean inclusive) {
        return 0;
    }

    @Override
    public String toDateStringForFilename(Date date) {
        return null;
    }

    @Override
    public String toDateTimeStringForFilename(Date date) {
        return null;
    }
}
