/**
 * Copyright 2005-2015 The Kuali Foundation
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
package edu.arizona.kim.ldap;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.springframework.ldap.core.DirContextOperations;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityPhoneMapper extends UaBaseMapper<EntityPhone> {

	private static final Logger LOG = Logger.getLogger(UaEntityPhoneMapper.class);

	@Override
	EntityPhone mapDtoFromContext(DirContextOperations context) {
		return mapDtoFromContext(context, true);
	}

	EntityPhone mapDtoFromContext(DirContextOperations context, boolean isdefault) {
		EntityPhone.Builder builder = mapBuilderFromContext(context, isdefault);
		return builder != null ? builder.build() : null;
	}

	EntityPhone.Builder mapBuilderFromContext(DirContextOperations context) {
		return mapBuilderFromContext(context, true);
	}

	EntityPhone.Builder mapBuilderFromContext(DirContextOperations context, boolean isdefault) {

		final String phoneNumber = context.getStringAttribute(getEdsConstants().getEmployeePhoneContextKey());
		if (phoneNumber == null) {
			LOG.debug("No phone number for context: " + context.getAttributes());
			return null;
		}
		String hyphenatedPhoneNumber = phoneNumber;
		if (phoneNumber.length() == 10) {
			hyphenatedPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
		} else if (phoneNumber.length() == 6) {
			hyphenatedPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3);
		}
		String countryCode = getConstants().getDefaultCountryCode();
		CodedAttribute.Builder phoneType = CodedAttribute.Builder.create("WORK");
		boolean active = true;
		boolean defaultValue = true;

		final EntityPhone.Builder builder = EntityPhone.Builder.create();
		builder.setPhoneNumber(hyphenatedPhoneNumber);
		builder.setCountryCode(countryCode);
		builder.setPhoneType(phoneType);
		builder.setActive(active);
		builder.setDefaultValue(defaultValue);
		return builder;

	}

}
