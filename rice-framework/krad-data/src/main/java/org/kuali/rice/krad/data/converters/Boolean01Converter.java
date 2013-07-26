package org.kuali.rice.krad.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 */
@Converter
public class Boolean01Converter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean objectValue) {
		if (objectValue == null) {
			return "0";
		}
		return objectValue ? "1" : "0";
	}

	@Override
	public Boolean convertToEntityAttribute(String dataValue) {
		if (dataValue == null) {
			return false;
		}
		return dataValue.equals("1");
	}
}