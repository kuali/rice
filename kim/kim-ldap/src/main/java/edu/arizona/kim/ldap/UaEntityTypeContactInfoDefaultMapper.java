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

import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoDefault;
import org.springframework.ldap.core.DirContextOperations;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityTypeContactInfoDefaultMapper extends UaBaseMapper<EntityTypeContactInfoDefault> {

	private UaEntityAddressMapper addressMapper;
	private UaEntityPhoneMapper phoneMapper;
	private UaEntityEmailMapper emailMapper;

	@Override
	EntityTypeContactInfoDefault mapDtoFromContext(DirContextOperations context) {
		EntityTypeContactInfoDefault.Builder builder = mapBuilderFromContext(context);
		return builder != null ? builder.build() : null;
	}

	EntityTypeContactInfoDefault.Builder mapBuilderFromContext(DirContextOperations context) {
		String entityTypeCode = getConstants().getPersonEntityTypeCode();

		EntityAddress.Builder entityAddress = getAddressMapper().mapBuilderFromContext(context, true);
		EntityEmail.Builder entityEmail = getEmailMapper().mapBuilderFromContext(context, true);
		EntityPhone.Builder entityPhone = getPhoneMapper().mapBuilderFromContext(context, true);

		final EntityTypeContactInfoDefault.Builder builder = EntityTypeContactInfoDefault.Builder.create();
		builder.setDefaultAddress(entityAddress);
		builder.setDefaultEmailAddress(entityEmail);
		builder.setDefaultPhoneNumber(entityPhone);
		builder.setEntityTypeCode(entityTypeCode);

		return builder;
	}

	public final UaEntityAddressMapper getAddressMapper() {
		return this.addressMapper;
	}

	public final void setAddressMapper(final UaEntityAddressMapper argAddressMapper) {
		this.addressMapper = argAddressMapper;
	}

	public final UaEntityPhoneMapper getPhoneMapper() {
		return this.phoneMapper;
	}

	public final void setPhoneMapper(final UaEntityPhoneMapper argPhoneMapper) {
		this.phoneMapper = argPhoneMapper;
	}

	public final UaEntityEmailMapper getEmailMapper() {
		return this.emailMapper;
	}

	public final void setEmailMapper(final UaEntityEmailMapper argEmailMapper) {
		this.emailMapper = argEmailMapper;
	}
}