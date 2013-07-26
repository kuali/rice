package org.kuali.rice.krad.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * Converts our custom {@link KualiInteger} objects for OJB by converting them to/from longs.
 */
@Converter(
		autoApply = true)
public class KualiIntegerConverter implements AttributeConverter<KualiInteger, Long> {

	@Override
	public Long convertToDatabaseColumn(KualiInteger objectValue) {
		if (objectValue == null) {
			return null;
		}
		return objectValue.longValue();
	}

	@Override
	public KualiInteger convertToEntityAttribute(Long dataValue) {
		if (dataValue == null) {
			return null;
		}
		return new KualiInteger(dataValue);
	}

}
