package org.kuali.rice.core.web.format;

import org.joda.time.DateTime;

import java.sql.Date;

public class DateTimeFormatter extends DateFormatter {

    @Override
    protected Object convertToObject(String target) {
        Date date = (Date) super.convertToObject(target);
        return new DateTime(date.getTime());
    }

    @Override
    public Object format(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return value;
        } else if (value instanceof java.util.Date) {
            return getDateTimeService().toDateTimeString((java.util.Date)value);
        } else {
            return getDateTimeService().toDateTimeString(((DateTime) value).toDate());
        }
    }

}
