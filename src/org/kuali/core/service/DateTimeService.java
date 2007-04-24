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
package org.kuali.core.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;

/**
 * This interface defines methods that a DateTime service must provide
 * 
 * 
 */
public interface DateTimeService {
    /**
     * Returns the current date/time as a java.util.Date
     * 
     * @return current date/time
     */
    public java.util.Date getCurrentDate();

    /**
     * Returns the current date/time as a java.sql.Timestamp
     * 
     * @return current date/time
     */
    public Timestamp getCurrentTimestamp();

    /**
     * Returns the current date/time as a java.sql.Date
     * 
     * @return current date/time
     */
    public java.sql.Date getCurrentSqlDate();

    /**
     * Returns the current date as a java.sql.Date rounded back to midnight. This is what the JDBC driver is supposed to do with
     * dates on their way to the database, so it can be convenient for comparing to dates from the database or input from the UI.
     * 
     * @return current date at the most recent midnight in the JVM's timezone
     */
    public java.sql.Date getCurrentSqlDateMidnight();

    /**
     * Returns a Calendar initialized with the current Date
     * 
     * @return currennt Calendar
     */
    public Calendar getCurrentCalendar();

    /**
     * Returns a Calendar initialized to the given Date
     * 
     * @return date-specific Calendar
     * @throws IllegalArgumentException if the given Date is null
     */
    public Calendar getCalendar(java.util.Date date);

    /**
     * Converts the given String into a java.sql.Timestamp instance
     * 
     * @param timeString
     * @return java.sql.Timestamp
     * @throws IllegalArgumentException if the given string is null or blank
     * @throws ParseException if the string cannot be converted
     */
    public java.sql.Timestamp convertToSqlTimestamp(String timeString) throws ParseException;

    /**
     * Converts the given String into a java.sql.Date instance
     * 
     * @param dateString
     * @return java.sql.Date
     * @throws IllegalArgumentException if the given string is null or blank
     * @throws ParseException if the string cannot be converted
     */
    public java.sql.Date convertToSqlDate(String dateString) throws ParseException;

    /**
     * Converts a Timestamp into a sql Date.
     * 
     * @param timestamp
     * @return
     */
    public java.sql.Date convertToSqlDate(Timestamp timestamp) throws ParseException;



    /**
     * Returns the number of days between two days - start and end date of some arbitrary period.
     * 
     * @param date1 The first date in the period
     * @param date2 The second date in the period
     * @param inclusive Whether the result should include both the start and the end date. Otherwise it only includes one.
     * @return int The number of days in the period
     */
    public int dateDiff(java.util.Date date1, java.util.Date date2, boolean inclusive);

}