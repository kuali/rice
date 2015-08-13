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
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityNameMapper extends UaBaseMapper<EntityName> {

	private static final Logger LOG = Logger.getLogger(UaEntityAddressMapper.class);

	@Override
	EntityName mapDtoFromContext(DirContextOperations context) {
		return mapDtoFromContext(context, true);
	}

	EntityName mapDtoFromContext(DirContextOperations context, boolean isdefault) {
		EntityName.Builder builder = mapBuilderFromContext(context, isdefault);
		return builder != null ? builder.build() : null;
	}

	EntityName.Builder mapBuilderFromContext(DirContextOperations context) {
		return mapBuilderFromContext(context, true);
	}

	EntityName.Builder mapBuilderFromContext(DirContextOperations context, boolean isdefault) {
		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS eduPersonAffiliation found for context: " + context.getAttributes());
			return null;
		}

		String id = edsRecord.getUaId();
		if (id == null) {
			LOG.debug("No UaId for context: " + context.getAttributes());
			id = "";
		}
		String givenName = edsRecord.getGivenName();
		if (givenName == null) {
			LOG.debug("No given name for context: " + context.getAttributes());
			givenName = "";
		}

		String firstName = "";
		String middleName = "";
		String[] nameArray = { "" };

		if (givenName != null) {
			nameArray = givenName.split(" ");
		}
		firstName = nameArray[0];
		if (nameArray.length > 1) {
			middleName = nameArray[1];
		}

		String lastName = edsRecord.getSn();
		if (lastName == null) {
			LOG.debug("No surname for context: " + context.getAttributes());
			lastName = "";
		}

		String compositeName = edsRecord.getCn();
		;
		if (compositeName == null) {
			LOG.debug("No composite name for context: " + context.getAttributes());
			compositeName = "";
		}

		boolean active = true;
		CodedAttribute.Builder nameType = CodedAttribute.Builder.create("PRI");

		final EntityName.Builder entityName = EntityName.Builder.create();
		entityName.setId(id);
		entityName.setFirstName(firstName);
		entityName.setLastName(lastName);
		entityName.setMiddleName(middleName);
		entityName.setCompositeName(compositeName);
		entityName.setActive(active);
		entityName.setDefaultValue(isdefault);
		entityName.setNameType(nameType);
		return entityName;
	}

}