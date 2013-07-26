package org.kuali.rice.krad.data.converters;

import java.math.BigDecimal;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Converts our custom {@link KualiDecimal} objects for OJB by converting them to/from {@link BigDecimal}.
 */
@Converter(
		autoApply = true)
public class KualiDecimalConverter implements AttributeConverter<KualiDecimal, BigDecimal> {

	@Override
	public BigDecimal convertToDatabaseColumn(KualiDecimal objectValue) {
		if (objectValue == null) {
			return null;
		}
		return objectValue.bigDecimalValue();
	}

	@Override
	public KualiDecimal convertToEntityAttribute(BigDecimal dataValue) {
		if (dataValue == null) {
			return null;
		}
		return new KualiDecimal(dataValue);
	}
}
