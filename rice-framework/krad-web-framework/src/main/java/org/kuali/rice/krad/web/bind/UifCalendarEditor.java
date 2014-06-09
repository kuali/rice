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
package org.kuali.rice.krad.web.bind;

import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.FormatException;

import java.io.Serializable;
import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;

/**
 * PropertyEditor converts between date display strings and <code>java.util.Calendar</code> objects using the
 * <code>org.kuali.rice.core.api.datetime.DateTimeService</code>.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifCalendarEditor extends UifDateEditor implements Serializable {
    private static final long serialVersionUID = 8123569337264797008L;

    /**
     * This overridden method uses the
     * <code>org.kuali.rice.core.api.datetime.DateTimeService</code> to convert
     * the calendar object to the display string.
     */
    @Override
    public String getAsText() {
        if (this.getValue() == null) {
            return null;
        }

        if ("".equals(this.getValue())) {
            return null;
        }

        return getDateTimeService().toDateString(new Date(((java.util.Calendar) this.getValue()).getTimeInMillis()));
    }

    /**
     * Convert display text to <code>java.util.Calendar</code> object using the
     * <code>org.kuali.rice.core.api.datetime.DateTimeService</code>.
     *
     * @param text the display text
     * @return the <code>java.util.Calendar</code> object
     * @throws IllegalArgumentException the illegal argument exception
     */
    protected Object convertToObject(String text) throws IllegalArgumentException {
        try {
            // Allow user to clear dates
            if (text == null || text.equals("")) {
                return null;
            }

            Date result = getDateTimeService().convertToSqlDate(text);
            Calendar calendar = getDateTimeService().getCalendar(result);
            calendar.setTime(result);

            if (calendar.get(Calendar.YEAR) < 1000 && verbatimYear(text).length() < 4) {
                throw new FormatException("illegal year format", RiceKeyConstants.ERROR_DATE, text);
            }

            return calendar;
        } catch (ParseException e) {
            throw new FormatException("parsing", RiceKeyConstants.ERROR_DATE, text, e);
        }
    }

}
