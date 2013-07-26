package org.kuali.rice.krad.data.converters;

import java.math.BigDecimal;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.kuali.rice.core.api.util.type.KualiPercent;

/**
 * Converts our custom {@link KualiPercent} objects for OJB by converting them to/from {@link BigDecimal}.
 */
@Converter(
		autoApply = true)
public class KualiPercentConverter implements AttributeConverter<KualiPercent, BigDecimal> {

	@Override
	public BigDecimal convertToDatabaseColumn(KualiPercent objectValue) {
		if (objectValue == null) {
			return null;
		}
		return objectValue.bigDecimalValue();
	}

	@Override
	public KualiPercent convertToEntityAttribute(BigDecimal dataValue) {
		if (dataValue == null) {
			return null;
		}
		return new KualiPercent(dataValue);
	}
}