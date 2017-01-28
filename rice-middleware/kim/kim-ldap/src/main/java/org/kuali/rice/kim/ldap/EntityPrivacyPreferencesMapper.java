/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.springframework.ldap.core.DirContextOperations;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityPrivacyPreferencesMapper extends BaseMapper<EntityPrivacyPreferences> {

    @Override
    EntityPrivacyPreferences mapDtoFromContext(DirContextOperations context) {
    	EntityPrivacyPreferences.Builder builder = mapBuilderFromContext(context);
    	return builder != null ? builder.build() : null;
    }
    
    EntityPrivacyPreferences.Builder mapBuilderFromContext(DirContextOperations context) {
        final String entityId      = context.getStringAttribute(getConstants().getKimLdapIdProperty());
        final EntityPrivacyPreferences.Builder person = EntityPrivacyPreferences.Builder.create(entityId);
        person.setSuppressName(false);
        person.setSuppressEmail(false);
        person.setSuppressPhone(false);
        person.setSuppressAddress(false);
        person.setSuppressPersonal(true);
        return person;
    }
    

}