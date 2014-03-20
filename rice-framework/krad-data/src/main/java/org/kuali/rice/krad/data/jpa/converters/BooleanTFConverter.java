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

/**
 * Converts true/false represented by the characters "T" and "F" to and from true and false.
 *
 * <p>The conversion treats the values as follows: "T" is true and "F" is false.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Converter
public class BooleanTFConverter implements AttributeConverter<Boolean, String> {

    /**
     * {@inheritDoc}
     *
     * This implementation will convert from a false or true value to an "F" or "T" value.
     */
	@Override
	public String convertToDatabaseColumn(Boolean objectValue) {
		if (objectValue == null) {
			return "F";
		}
		return objectValue ? "T" : "F";
	}

    /**
     * {@inheritDoc}
     *
     * This implementation will convert from a "F" or "T" value to a false or true.
     */
	@Override
	public Boolean convertToEntityAttribute(String dataValue) {
		if (dataValue == null) {
			return false;
		}
		return dataValue.equals("T");
	}
}