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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityTypeContactInfoMapper extends UaBaseMapper<EntityTypeContactInfo> {

	private static final Logger LOG = Logger.getLogger(UaEntityTypeContactInfoMapper.class);

	private UaEntityAddressMapper addressMapper;
	private UaEntityPhoneMapper phoneMapper;
	private UaEntityEmailMapper emailMapper;;

	@Override
	EntityTypeContactInfo mapDtoFromContext(DirContextOperations context) {
		EntityTypeContactInfo.Builder builder = mapBuilderFromContext(context);
		return builder != null ? builder.build() : null;
	}

	EntityTypeContactInfo.Builder mapBuilderFromContext(DirContextOperations context) {
		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS eduPersonAffiliation found for context: " + context.getAttributes());
			return null;
		}
		String entityId = edsRecord.getUaId();
		String entityTypeCode = getConstants().getPersonEntityTypeCode();

		List<EntityAddress.Builder> addresses = new ArrayList<EntityAddress.Builder>();
		EntityAddress.Builder entityAddress = getAddressMapper().mapBuilderFromContext(context);
		if ( ObjectUtils.isNotNull(entityAddress) ) {
			addresses.add(entityAddress);
		}

		List<EntityEmail.Builder> emails = new ArrayList<EntityEmail.Builder>();
		EntityEmail.Builder entityEmail = getEmailMapper().mapBuilderFromContext(context);
		if ( ObjectUtils.isNotNull(entityEmail) ) {
			emails.add(entityEmail);
		}

		List<EntityPhone.Builder> phoneNumbers = new ArrayList<EntityPhone.Builder>();
		EntityPhone.Builder entityPhone = getPhoneMapper().mapBuilderFromContext(context);
		if ( ObjectUtils.isNotNull(entityPhone) ) {
			phoneNumbers.add(entityPhone);
		}

		final EntityTypeContactInfo.Builder builder = EntityTypeContactInfo.Builder.create(entityId, entityTypeCode);

		builder.setAddresses(addresses);
		builder.setEmailAddresses(emails);
		builder.setPhoneNumbers(phoneNumbers);

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