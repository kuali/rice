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
package org.kuali.rice.krad.data.jpa.converters;

import java.math.BigDecimal;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.kuali.rice.core.api.util.type.KualiPercent;

/**
 * Converts the custom {@link KualiPercent} objects for OJB by converting them to/from {@link BigDecimal}.
 */
@Converter(
		autoApply = true)
public class KualiPercentConverter implements AttributeConverter<KualiPercent, BigDecimal> {

    /**
     * {@inheritDoc}
     *
     * This implementation will convert from a {@link KualiPercent} to a {@link BigDecimal}.
     */
	@Override
	public BigDecimal convertToDatabaseColumn(KualiPercent objectValue) {
		if (objectValue == null) {
			return null;
		}
		return objectValue.bigDecimalValue();
	}

    /**
     * {@inheritDoc}
     *
     * This implementation will convert from a {@link BigDecimal} to a {@link KualiPercent}.
     */
	@Override
	public KualiPercent convertToEntityAttribute(BigDecimal dataValue) {
		if (dataValue == null) {
			return null;
		}
		return new KualiPercent(dataValue);
	}
}