/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.service.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.kuali.core.service.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for a DateTime structure. This is the default, Kuali delivered implementation.
 */
@Transactional
public class DateTimeServiceImpl implements DateTimeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DateTimeServiceImpl.class);

    private static final String SQLDATE_FORMAT = "yyyy-MM-dd";
    private static final String SQLTIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //    

    /**
     * @see org.kuali.core.service.DateTimeService#getCurrentDate()
     */
    public java.util.Date getCurrentDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new java.util.Date());
        return c.getTime();
    }

    /**
     * @see org.kuali.core.service.DateTimeService#getCurrentTimestamp()
     */
    public Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(getCurrentDate().getTime());
    }

    /**
     * @see org.kuali.core.service.DateTimeService#getCurrentSqlDate()
     */
    public java.sql.Date getCurrentSqlDate() {
        return new java.sql.Date(getCurrentDate().getTime());
    }


    /**
     * @see org.kuali.core.service.DateTimeService#getCurrentSqlDateMidnight()
     */
    public java.sql.Date getCurrentSqlDateMidnight() {
        // simple and not unduely inefficient way to truncate the time component
        return java.sql.Date.valueOf(getCurrentSqlDate().toString());
    }

    /**
     * @see org.kuali.core.service.DateTimeService#getCurrentCalendar()
     */
    public Calendar getCurrentCalendar() {
        return getCalendar(getCurrentDate());
    }

    /**
     * @see org.kuali.core.service.DateTimeService#getCalendar
     */
    public Calendar getCalendar(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("invalid (null) date");
        }

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(date);

        return currentCalendar;
    }


    /**
     * @see org.kuali.core.service.DateTimeService#convertToSqlTimestamp(java.lang.String)
     */
    public java.sql.Timestamp convertToSqlTimestamp(String timeString) throws ParseException {
        if (StringUtils.isBlank(timeString)) {
            throw new IllegalArgumentException("invalid (blank) timeString");
        }

        DateFormat dateFormat = new SimpleDateFormat(SQLTIMESTAMP_FORMAT);

        java.sql.Timestamp timestamp = null;
        timestamp = new java.sql.Timestamp(dateFormat.parse(timeString).getTime());

        return timestamp;
    }

    private static String[] acceptedDateFormats = new String[] {
        SQLDATE_FORMAT,
        "MM/dd/yyyy",
        "MM/dd/yy",
        "MM-dd-yyyy",
        "MM-dd-yy"
    };
    
    /**
     * @see org.kuali.core.service.DateTimeService#convertToSqlDate(java.lang.String)
     */
    public java.sql.Date convertToSqlDate(String dateString) throws ParseException {
        if (StringUtils.isBlank(dateString)) {
            throw new IllegalArgumentException("invalid (blank) timeString");
        }
        java.sql.Date date = null;
        StringBuffer exceptionMessage = new StringBuffer("Date string '").append(dateString).append("' could not be converted using any of the accepted formats: ");
        for ( String dateFormatString : acceptedDateFormats ) {
            try {
                return new java.sql.Date(new SimpleDateFormat(dateFormatString).parse(dateString).getTime());
            } catch ( ParseException ex ) {
                exceptionMessage.append(dateFormatString).append(" (error offset=").append(ex.getErrorOffset()).append("),");
            }
        }
        if (date == null) {
			throw new ParseException(exceptionMessage.toString().substring(0, exceptionMessage.length() -1), 0);
        }
        return date;
    }

    /**
     * @throws ParseException
     * @see org.kuali.core.service.DateTimeService#convertToSqlDate(java.sql.Timestamp)
     */
    public java.sql.Date convertToSqlDate(Timestamp timestamp) throws ParseException {
        StringBuffer buf = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat(SQLDATE_FORMAT);
        formatter.setLenient(false);
        formatter.format(timestamp, buf, new FieldPosition(0));

        return convertToSqlDate(buf.toString());
    }


    public int dateDiff(java.util.Date startDate, java.util.Date endDate, boolean inclusive) {
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(startDate);

        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate);

        int startDateOffset = -(startDateCalendar.get(Calendar.ZONE_OFFSET) + startDateCalendar.get(Calendar.DST_OFFSET)) / (60 * 1000);

        int endDateOffset = -(endDateCalendar.get(Calendar.ZONE_OFFSET) + endDateCalendar.get(Calendar.DST_OFFSET)) / (60 * 1000);

        if (startDateOffset > endDateOffset) {
            startDateCalendar.add(Calendar.MINUTE, endDateOffset - startDateOffset);
        }

        if (inclusive) {
            startDateCalendar.add(Calendar.DATE, -1);
        }

        int dateDiff = Integer.parseInt(DurationFormatUtils.formatDuration(endDateCalendar.getTimeInMillis() - startDateCalendar.getTimeInMillis(), "d", true));

        return dateDiff;
    }


}