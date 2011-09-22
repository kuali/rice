/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kim.ldap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.util.Constants;

import static org.kuali.rice.core.util.BufferedLogger.*;
import static org.apache.commons.lang.StringUtils.contains;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

/**
 * Maps LDAP Information to KIM Entity Affiliation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityAffiliationMapper extends AbstractContextMapper {
    private Constants constants;
    
    public Object doMapFromContext(DirContextOperations context) {
        List<EntityAffiliation.Builder> retval = new ArrayList();
        final String primaryAffiliationProperty = getConstants().getPrimaryAffiliationLdapProperty();
        final String affiliationProperty = getConstants().getAffiliationLdapProperty();
        debug("Got affiliation ", context.getStringAttribute(primaryAffiliationProperty));
        debug("Got affiliation ", context.getStringAttribute(affiliationProperty));
        
        String primaryAffiliation = context.getStringAttribute(primaryAffiliationProperty);
        
        int affiliationId = 1;
        String affiliationCode = getAffiliationTypeCodeForName(primaryAffiliation);

        final EntityAffiliation.Builder aff1 = EntityAffiliation.Builder.create();
        aff1.setAffiliationType(EntityAffiliationType.Builder.create(affiliationCode == null ? "AFLT" : affiliationCode));
        aff1.setCampusCode(getConstants().getDefaultCampusCode());
        aff1.setId("" + affiliationId++);
        aff1.setDefaultValue(true);
        aff1.setActive(true);
        retval.add(aff1);
        
        String[] affiliations = context.getStringAttributes(affiliationProperty);
        // Create an empty array to prevent NPE
        if (affiliations == null) {
            affiliations = new String[] {};
        }

        for (String affiliation : affiliations) {
            if (!StringUtils.equals(affiliation, primaryAffiliation)) {
                affiliationCode = getAffiliationTypeCodeForName(affiliation);
                if (affiliationCode != null && !hasAffiliation(retval, affiliationCode)) {
                    final EntityAffiliation.Builder aff = EntityAffiliation.Builder.create();
                    aff.setAffiliationType(EntityAffiliationType.Builder.create(affiliationCode));
                    aff.setCampusCode(getConstants().getDefaultCampusCode());
                    aff.setId("" + affiliationId++);
                    aff.setDefaultValue(false);
                    aff.setActive(true);
                    retval.add(aff);
                }
            }
        }
        
        return retval;
    }
    
    /**
     *
     * Returns the affiliation type code for the given affiliation name. Returns null if the affiliation is not found
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
        return null;
    }

    protected boolean hasAffiliation(List<EntityAffiliation.Builder> affiliations, String affiliationCode) {
        for (EntityAffiliation.Builder affiliation : affiliations) {
            if (equalsIgnoreCase(affiliation.getAffiliationType().getCode(), affiliationCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the value of constants
     *
     * @return the value of constants
     */
    public final Constants getConstants() {
        return this.constants;
    }

    /**
     * Sets the value of constants
     *
     * @param argConstants Value to assign to this.constants
     */
    public final void setConstants(final Constants argConstants) {
        this.constants = argConstants;
    }
}
