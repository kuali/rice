/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.core.util.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kuali.rice.core.api.mo.Coded;

/**
 * An abstract base class for use when marshaling enumeration values to and from XML.
 * This allows these values to be handled as plain strings (as opposed to xs:enumeration)
 * in the generated schema definitions.  This improves compatibility by allows for new
 * enumeration values to be added without breaking the schema.
 * 
 * <p>Subclasses need to indicate the concrete type of the Enum being adapted using the
 * {@link #getEnumClass()} method.
 * 
 * <p>If the enum implements the {@link Coded} interface, then the actual string value
 * that is produced by the adapter will be the result of the {@link Coded#getCode()}
 * method.  Otherwise the {@link Enum#name()} value is used.
 * 
 * <p>In situations where a string value is being requested to be unmarshalled to an
 * enum and the enum does not understand the value, then this adapter will unmarshal
 * the value to {@code null}.  This could happen in situations where a newer version
 * of an endpoint (which has added additional items to the enumeration) sends a message
 * to an older version client of the service.  In these cases, that older client will
 * not be aware of the new enumeration values and thus cannot effectively translate them.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class EnumStringAdapter<T extends Enum<T>> extends XmlAdapter<String, Enum<?>> {	
		
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnumStringAdapter.class);
	
	@Override
	public String marshal(Enum<?> enumeration) throws Exception {
		if (enumeration == null) {
			return null;
		}
		if (enumeration instanceof Coded) {
			return ((Coded)enumeration).getCode();
		}
		return enumeration.toString();
	}

	@Override
	public Enum<?> unmarshal(String value) throws Exception {
		if (value == null) {
			return null;
		}
		Class<T> enumClass = getEnumClass();
		if (!enumClass.isEnum()) {
			throw new IllegalStateException("The enumClass configured on this adapter is not an Enum: " + enumClass);
		}
		if (Coded.class.isAssignableFrom(enumClass)) {
			Enum<T>[] enumConstants = enumClass.getEnumConstants();
			for (Enum<T> enumConstant : enumConstants) {
				Coded codedEnumConstant = (Coded)enumConstant;
				if (codedEnumConstant.getCode().equals(value)) {
					return enumConstant;
				}
			}
		} else {
			try {
				return Enum.valueOf(enumClass, value);
			} catch (IllegalArgumentException e) {
				// failed to unmarshal, will fall through to null return below...
			}
		}
		LOG.warn("Failed to unmarshal enumeration value '" + value + "' for enum type: " + enumClass);
		return null;
	}
	
	protected abstract Class<T> getEnumClass();
	
}
