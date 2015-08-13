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

import static org.apache.commons.lang.StringUtils.contains;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType.Builder;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsAffiliation;
import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityAffiliationMapper extends UaBaseMapper<List<EntityAffiliation>> {

	private static final Logger LOG = Logger.getLogger(UaEntityAffiliationMapper.class);

	@Override
	List<EntityAffiliation> mapDtoFromContext(DirContextOperations context) {
		List<EntityAffiliation.Builder> builders = mapBuilderFromContext(context);
		List<EntityAffiliation> affiliations = new ArrayList<EntityAffiliation>();
		if (builders != null) {
			for (EntityAffiliation.Builder builder : builders) {
				affiliations.add(builder.build());
			}
		}
		return affiliations;
	}

	List<EntityAffiliation.Builder> mapBuilderFromContext(DirContextOperations context) {
		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS records found for context: " + context.getAttributes());
			return null;
		}

		List<EntityAffiliation.Builder> entityAffiliations = new ArrayList<EntityAffiliation.Builder>();

		for (UaEdsAffiliation affiliation : edsRecord.getOrderedAffiliations()) {
			Integer affiliationId = entityAffiliations.size() + 1;
			String affiliationCode = getAffiliationTypeCodeForName(affiliation.getAffiliatonString());
			if (affiliationCode != null && !hasAffiliation(entityAffiliations, affiliationCode)) {
				final EntityAffiliation.Builder aff = createEntityAffiliationFromEdsAffiliation(affiliation, affiliationId.toString());
				entityAffiliations.add(aff);

			}
		}

		// The first affiliation should be default
		if (entityAffiliations.size() > 0) {
			entityAffiliations.get(0).setDefaultValue(true);
		}
		return entityAffiliations;
	}

	EntityAffiliation.Builder createEntityAffiliationFromEdsAffiliation(UaEdsAffiliation edsAffiliation, String affiliationId) {
		String affiliationCode = edsAffiliation.getAffiliatonString();
		Builder affiliationType = EntityAffiliationType.Builder.create(affiliationCode);
		boolean active = edsAffiliation.isActive();
		String campusCode = getConstants().getDefaultCampusCode();
		boolean defaultValue = false;

		final EntityAffiliation.Builder affiliation = EntityAffiliation.Builder.create();

		affiliation.setAffiliationType(affiliationType);
		affiliation.setCampusCode(campusCode);
		affiliation.setId(affiliationId);
		affiliation.setDefaultValue(defaultValue);
		affiliation.setActive(active);

		return affiliation;
	}

	/**
	 *
	 * Returns the affiliation type code for the given affiliation name. Returns
	 * null if the affiliation is not found
	 * 
	 * @param affiliationName
	 * @return null if no matching affiliation is found
	 */
	protected String getAffiliationTypeCodeForName(String affiliationName) {
		String[] mappings = getConstants().getAffiliationMappings().split(",");
		for (String affilMap : mappings) {
			if (contains(affilMap, affiliationName)) {
				return affilMap.split("=")[1];
			}
		}
		return affiliationName;
	}

	protected boolean hasAffiliation(List<EntityAffiliation.Builder> affiliations, String affiliationCode) {
		for (EntityAffiliation.Builder affiliation : affiliations) {
			if (equalsIgnoreCase(affiliation.getAffiliationType().getCode(), affiliationCode)) {
				return true;
			}
		}
		return false;
	}

}
