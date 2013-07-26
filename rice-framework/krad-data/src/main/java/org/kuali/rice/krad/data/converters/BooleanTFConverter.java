package org.kuali.rice.krad.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 */
@Converter
public class BooleanTFConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean objectValue) {
		if (objectValue == null) {
			return "F";
		}
		return objectValue ? "T" : "F";
	}

	@Override
	public Boolean convertToEntityAttribute(String dataValue) {
		if (dataValue == null) {
			return false;
		}
		return dataValue.equals("T");
	}
}