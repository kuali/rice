/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.ojb;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.kuali.workflow.identity.IdentityType;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class IdentityTypeConversion implements FieldConversion {

	public Object javaToSql(Object object) throws ConversionException {
		if (object instanceof IdentityType) {
			return ((IdentityType)object).getCode();
		}
		return object;
	}

	public Object sqlToJava(Object object) throws ConversionException {
		if (object == null) {
			return null;
		}
		if (object instanceof String) {
			String code = (String)object;
			if (StringUtils.isBlank(code)) {
				return null;
			}
			IdentityType type = IdentityType.fromCode(code);
			if (type == null) {
				throw new ConversionException("Could not convert from code '" + code + "' to IdentityType.");
			}
			return type;
		}
		throw new ConversionException("Invalid incoming object type for conversion to IdentityType: " + object.getClass());
	}

}
