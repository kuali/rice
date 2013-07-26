package org.kuali.rice.krad.data.provider.jpa.testbo;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(
		autoApply = true)
public class NonStandardDataTypeConverter implements AttributeConverter<NonStandardDataType, String> {

	@Override
	public String convertToDatabaseColumn(NonStandardDataType value) {
		if (value == null) {
			return null;
		} else {
			return value.toString();
		}
	}

	@Override
	public NonStandardDataType convertToEntityAttribute(String value) {
		if (value == null) {
			return null;
		}
		return new NonStandardDataType(value.toString());
	}

}
