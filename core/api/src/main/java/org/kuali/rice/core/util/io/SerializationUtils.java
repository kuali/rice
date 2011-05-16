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
package org.kuali.rice.core.util.io;

import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * TODO ... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class SerializationUtils {

	public static String serializeToBase64(Serializable object) {
		if (object == null) {
			throw new IllegalArgumentException("Cannot serialize a null object");
		}
		byte[] serializedBytes = org.apache.commons.lang.SerializationUtils.serialize(object);
		return new Base64().encodeAsString(serializedBytes);
    }
	
	public static Serializable deserializeFromBase64(String base64Value) {
		if (StringUtils.isBlank(base64Value)) {
			throw new IllegalArgumentException("Cannot deserialize a null or blank base64 string value.");
		}
        byte[] decoded = new Base64().decode(base64Value);
		return (Serializable)org.apache.commons.lang.SerializationUtils.deserialize(decoded);
	}
	
	private SerializationUtils() {
		throw new UnsupportedOperationException("Should never be invoked.");
	}
	
}
