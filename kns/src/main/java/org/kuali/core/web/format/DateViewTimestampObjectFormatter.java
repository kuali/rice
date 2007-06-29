package org.kuali.core.web.format;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.rice.KNSServiceLocator;

public class DateViewTimestampObjectFormatter extends DateFormatter {
	protected Object convertToObject(String target) {
		return new Timestamp(((Date) super.convertToObject(target)).getTime());
	}

	public Object format(Object value) {
		if (value == null)
			return null;
		return KNSServiceLocator.getDateTimeService().toDateTimeString(
				(Timestamp) value);
	}
}