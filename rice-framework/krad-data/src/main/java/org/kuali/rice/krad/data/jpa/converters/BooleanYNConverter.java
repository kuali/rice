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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts true/false represented by a set of yes characters and the character "N" to and from true and false.
 *
 * <p>The conversion treats the values as follows: "Y", "y", "true", and "TRUE" are all true and "N" is false.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Converter(
		autoApply = true)
public class BooleanYNConverter implements AttributeConverter<Boolean, String> {

    /**
     * Defines the set of values that all correspond to yes.
     */
	protected static final Set<String> YES_VALUES = new HashSet<String>();
	static {
		YES_VALUES.add("Y");
		YES_VALUES.add("y");
		YES_VALUES.add("true");
		YES_VALUES.add("TRUE");
	}

    /**
     * {@inheritDoc}
     *
     * This implementation will convert from a false or true value to an "N" or "Y" value.
     */
	@Override
	public String convertToDatabaseColumn(Boolean objectValue) {
		if (objectValue == null) {
			return "N";
		}
		return objectValue ? "Y" : "N";
	}

    /**
     * {@inheritDoc}
     *
     * This implementation will convert from a "F" or any of the yes values to a false or true.
     */
	@Override
	public Boolean convertToEntityAttribute(String dataValue) {
		if (dataValue == null) {
			return false;
		}
		return YES_VALUES.contains(dataValue);
	}
}