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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.FormatException;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;

/**
 * PropertyEditor converts between timestamp display strings and @{DateTime} objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifDateTimeEditor extends PropertyEditorSupport implements Serializable {

    private static final long serialVersionUID = -1315597474978280713L;

    private transient DateTimeService dateTimeService;

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation uses the {@link DateTimeService} to convert the {@link DateTime} object to a
     * {@link Timestamp} display string.
     * </p>
     */
    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }

        if (getValue().equals(StringUtils.EMPTY)) {
            return null;
        }

        DateTime value = (DateTime) getValue();

        return getDateTimeService().toDateTimeString(new Timestamp(value.getMillis()));
    }

    /**
     * {@inheritDoc}
     *
     * This implementation converts the {@link Date} display string to a {@link DateTime} object using the
     * {@link DateTimeService}.
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isBlank(text)) {
            setValue(null);
            return;
        }

        try {
            Date value = getDateTimeService().convertToSqlDate(text);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(value);
            if (calendar.get(Calendar.YEAR) < 1000 && verbatimYear(text).length() < 4) {
                throw new FormatException("illegal year format", RiceKeyConstants.ERROR_DATE, text);
            }

            setValue(new DateTime(value.getTime()));
        } catch (ParseException e) {
            throw new FormatException("parsing", RiceKeyConstants.ERROR_DATE, text, e);
        }
    }

    /**
     * For a given user input date, this method returns the exact string the
     * user entered after the last slash. This allows the formatter to
     * distinguish between ambiguous values such as "/06" "/6" and "/0006"
     *
     * @param date the date to process
     *
     * @return the year
     */
    protected String verbatimYear(String date) {
        String result = "";

        int pos = date.lastIndexOf("/");
        if (pos >= 0) {
            result = date.substring(pos);
        }

        return result;
    }

    /**
     * Returns the {@link DateTimeService}.
     *
     * @return the {@link DateTimeService}
     */
    protected DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = GlobalResourceLoader.getService(CoreConstants.Services.DATETIME_SERVICE);
        }

        return dateTimeService;
    }

}