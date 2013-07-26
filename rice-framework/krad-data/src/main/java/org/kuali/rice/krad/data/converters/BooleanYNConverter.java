package org.kuali.rice.krad.data.converters;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 */
@Converter(
		autoApply = true)
public class BooleanYNConverter implements AttributeConverter<Boolean, String> {

	protected static final Set<String> YES_VALUES = new HashSet<String>();
	static {
		YES_VALUES.add("Y");
		YES_VALUES.add("y");
		YES_VALUES.add("true");
		YES_VALUES.add("TRUE");
	}

	@Override
	public String convertToDatabaseColumn(Boolean objectValue) {
		if (objectValue == null) {
			return "N";
		}
		return objectValue ? "Y" : "N";
	}

	@Override
	public Boolean convertToEntityAttribute(String dataValue) {
		if (dataValue == null) {
			return false;
		}
		return YES_VALUES.contains(dataValue);
	}
}