/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.api.engine;

import org.kuali.rice.core.api.exception.RiceRuntimeException;

/**
 * An exception which indicates that the type of data being evaluated in the
 * engine does not match the expected type.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class IncompatibleTypeException extends RiceRuntimeException {

	private static final long serialVersionUID = -8359509154258982033L;
	
	private final Object value;
	private final Class<?>[] validTypes;
	
	public IncompatibleTypeException(Object value, Class<?>... validTypes) {
		this(null, value, validTypes);
	}
	
	public IncompatibleTypeException(String additionalMessage, Object value, Class<?>... validTypes) {
		super(constructMessage(additionalMessage, value, validTypes));
		this.value = value;
		this.validTypes = validTypes;
	}
	
	private static String constructMessage(String additionalMessage, Object value, Class<?>... validTypes) {
		StringBuilder message = new StringBuilder();
		if (additionalMessage != null) {
			message.append(additionalMessage);
		}
		if (message.length() > 0) {
			message.append(" -> ");
		}
		if (validTypes != null && validTypes.length > 0) {	
			message.append("Type should have been one of [");
			for (Class<?> validType : validTypes) {
				message.append(validType.getName()).append(", ");
			}
			// trim off the last two character to get rid of the last ", "
			message.delete(message.length() - 2, message.length());
			message.append("] but was ").append(value == null ? "null" : value.getClass().getName());
		} else {
			message.append("Type was ").append(value == null ? "null" : value.getClass().getName());	
		}
		return message.toString();
	}
	
	public Object getValue() {
		return value;
	}
	
	public Class<?>[] getValidTypes() {
		return validTypes;
	}
	
}
