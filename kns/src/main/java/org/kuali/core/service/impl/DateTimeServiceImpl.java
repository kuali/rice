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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.kuali.core.service.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for a DateTime structure. This is
 * the default, Kuali delivered implementation.
 */
@Transactional
public class DateTimeServiceImpl implements DateTimeService {
	private String[] sqlDateFormats;

	private String stringDateFormat;

	private String stringDateTimeFormat;

	/**
	 * @see org.kuali.core.service.DateTimeService#toDateString(java.util.Date)
	 */
	public String toDateString(Date date) {
		return toString(date, stringDateFormat);
	}

	/**
	 * @see org.kuali.core.service.DateTimeService#toDateTimeString(java.util.Date)
	 */
	public String toDateTimeString(Date date) {
		return toString(date, stringDateTimeFormat);
	}

	/**
	 * @see org.kuali.core.service.DateTimeService#toString(java.util.Date,
	 *      java.lang.String)
	 */
	public String toString(Date date, String pattern) {
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setLenient(false);
		return dateFormat.format(date);
	}

	/**
	 * @see org.kuali.core.service.DateTimeService#getCurrentDate()
	 */
	public Date getCurrentDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
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
	public Calendar getCalendar(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("invalid (null) date");
		}

		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTime(date);

		return currentCalendar;
	}

	/**
	 * @see org.kuali.core.service.DateTimeService#convertToDate(java.lang.String)
	 */
	public Date convertToDate(String dateString) throws ParseException {
		return parse(dateString, stringDateFormat);
	}

	/**
	 * @see org.kuali.core.service.DateTimeService#convertToDateTime(java.lang.String)
	 */
	public Date convertToDateTime(String dateTimeString) throws ParseException {
		return parse(dateTimeString, stringDateTimeFormat);
	}

	/**
	 * @see org.kuali.core.service.DateTimeService#convertToSqlTimestamp(java.lang.String)
	 */
	public java.sql.Timestamp convertToSqlTimestamp(String timeString)
			throws ParseException {
		if (!StringUtils.isBlank(timeString)) {
			return new java.sql.Timestamp(parse(timeString, stringDateTimeFormat)
                    .getTime());
		}
        return null;
	}

	/**
	 * @see org.kuali.core.service.DateTimeService#convertToSqlDate(java.lang.String)
	 */
	public java.sql.Date convertToSqlDate(String dateString)
			throws ParseException {
		if (StringUtils.isBlank(dateString)) {
			throw new IllegalArgumentException("invalid (blank) timeString");
		}
		dateString = dateString.trim();
		java.sql.Date date = null;
		StringBuffer exceptionMessage = new StringBuffer("Date string '")
				.append(dateString)
				.append(
						"' could not be converted using any of the accepted formats: ");
		for (String dateFormatString : sqlDateFormats) {
			try {
				return new java.sql.Date(parse(dateString, dateFormatString)
						.getTime());
			} catch (ParseException e) {
				exceptionMessage.append(dateFormatString).append(
						" (error offset=").append(e.getErrorOffset()).append(
						"),");
			}
		}
		if (date == null) {
			throw new ParseException(exceptionMessage.toString().substring(0,
					exceptionMessage.length() - 1), 0);
		}
		return date;
	}

	/**
	 * @throws ParseException
	 * @see org.kuali.core.service.DateTimeService#convertToSqlDate(java.sql.Timestamp)
	 */
	public java.sql.Date convertToSqlDate(Timestamp timestamp)
			throws ParseException {
		return new java.sql.Date(timestamp.getTime());
	}

	public int dateDiff(Date startDate, Date endDate, boolean inclusive) {
		Calendar startDateCalendar = Calendar.getInstance();
		startDateCalendar.setTime(startDate);

		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTime(endDate);

		int startDateOffset = -(startDateCalendar.get(Calendar.ZONE_OFFSET) + startDateCalendar
				.get(Calendar.DST_OFFSET))
				/ (60 * 1000);

		int endDateOffset = -(endDateCalendar.get(Calendar.ZONE_OFFSET) + endDateCalendar
				.get(Calendar.DST_OFFSET))
				/ (60 * 1000);

		if (startDateOffset > endDateOffset) {
			startDateCalendar.add(Calendar.MINUTE, endDateOffset
					- startDateOffset);
		}

		if (inclusive) {
			startDateCalendar.add(Calendar.DATE, -1);
		}

		int dateDiff = Integer.parseInt(DurationFormatUtils.formatDuration(
				endDateCalendar.getTimeInMillis()
						- startDateCalendar.getTimeInMillis(), "d", true));

		return dateDiff;
	}

	private Date parse(String dateString, String pattern) throws ParseException {
		if (!StringUtils.isBlank(dateString)) {
			DateFormat dateFormat = new SimpleDateFormat(pattern);
			dateFormat.setLenient(false);
			return dateFormat.parse(dateString);
		}
		return null;
	}

	/**
	 * @param sqlDateFormats
	 *            the sqlDateFormats to set
	 */
	public void setSqlDateFormats(
			String[] sqlDateFormats) {
		this.sqlDateFormats = sqlDateFormats;
	}

	/**
	 * @param stringDateFormat
	 *            the stringDateFormat to set
	 */
	public void setStringDateFormat(String stringDateFormat) {
		this.stringDateFormat = stringDateFormat;
	}

	/**
	 * @param stringDateTimeFormat
	 *            the stringDateTimeFormat to set
	 */
	public void setStringDateTimeFormat(String stringDateTimeFormat) {
		this.stringDateTimeFormat = stringDateTimeFormat;
	}
}