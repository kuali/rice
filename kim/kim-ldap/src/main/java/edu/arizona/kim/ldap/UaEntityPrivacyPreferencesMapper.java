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
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityPrivacyPreferencesMapper extends UaBaseMapper<EntityPrivacyPreferences> {

	private static final Logger LOG = Logger.getLogger(UaEntityPrivacyPreferencesMapper.class);

	@Override
	EntityPrivacyPreferences mapDtoFromContext(DirContextOperations context) {
		EntityPrivacyPreferences.Builder builder = mapBuilderFromContext(context);
		return builder != null ? builder.build() : null;
	}

	EntityPrivacyPreferences.Builder mapBuilderFromContext(DirContextOperations context) {
		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS eduPersonAffiliation found for context: " + context.getAttributes());
			return null;
		}

		String entityId = edsRecord.getUaId();
		if (entityId == null) {
			entityId = "";
		}
		EntityPrivacyPreferences.Builder person = EntityPrivacyPreferences.Builder.create(entityId);
		person.setSuppressName(false);
		person.setSuppressEmail(false);
		person.setSuppressPhone(false);
		person.setSuppressAddress(false);
		person.setSuppressPersonal(true);
		return person;
	}

}