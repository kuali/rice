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

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.util.Constants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityNameMapper extends AbstractContextMapper {
    private Constants constants;
    
    public Object doMapFromContext(DirContextOperations context) {
        final EntityName person = new EntityName();
        person.setEntityNameId(context.getStringAttribute(getConstants().getKimLdapIdProperty()));
        
        final String fullName = (String) context.getStringAttribute(getConstants().getGivenNameLdapProperty());
        
        if (fullName != null) {
            final String[] name = fullName.split(" ");
            person.setFirstName(name[0]);
            person.setFirstNameUnmasked(name[0]);
            if (name.length > 1) {
                person.setMiddleName(name[1]);
                person.setMiddleNameUnmasked(name[1]);
            }
        }
        else {
            person.setFirstName(fullName);
        }
        
        person.setLastName(context.getStringAttribute(getConstants().getSnLdapProperty()));
        person.setLastNameUnmasked(context.getStringAttribute(getConstants().getSnLdapProperty()));
        person.setFormattedName(context.getStringAttribute("cn"));
        person.setFormattedNameUnmasked(context.getStringAttribute("cn"));
        person.setDefault(true);
        person.setActive(true);
        person.setNameTypeCode("PRI");
        
        return person;
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