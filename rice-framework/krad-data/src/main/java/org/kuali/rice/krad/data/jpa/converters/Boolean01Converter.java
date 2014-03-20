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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigInteger;

/**
 * Converts values of 0 or 1 to and from false or true.
 *
 * <p>The conversion treats the values as follows: 1 is true and 0 is false.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Converter
public class Boolean01Converter implements AttributeConverter<Boolean, BigInteger> {

    /**
     * {@inheritDoc}
     *
     * This implementation will convert from a false or true value to a 0 or 1 value.
     */
	@Override
	public BigInteger convertToDatabaseColumn(Boolean objectValue) {
		if (objectValue == null) {
			return BigInteger.valueOf(0);
		}
		return objectValue ? BigInteger.valueOf(1) : BigInteger.valueOf(0);
	}

    /**
     * {@inheritDoc}
     *
     * This implementation will convert from a false or true value to a 0 or 1 value.
     */
	@Override
	public Boolean convertToEntityAttribute(BigInteger dataValue) {
		if (dataValue == null) {
			return false;
		}
		return dataValue.intValue() == 1;
	}

}