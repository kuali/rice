/**
 * Copyright 2005-2011 The Kuali Foundation
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

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.util.Constants;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityNameMapper extends AbstractContextMapper {
    private Constants constants;
    
    public EntityName.Builder mapFromContext(DirContextOperations context, boolean isdefault) {
        return (EntityName.Builder) doMapFromContext(context, isdefault);
    }

    public EntityName.Builder mapFromContext(DirContextOperations context) {
        return mapFromContext(context, true);
    }

    public Object doMapFromContext(DirContextOperations context) {
        return doMapFromContext(context, true);
    }

    protected Object doMapFromContext(DirContextOperations context, boolean isdefault) {        
        final EntityName.Builder person = EntityName.Builder.create();
        person.setEntityId(context.getStringAttribute(getConstants().getKimLdapIdProperty()));
        person.setId(context.getStringAttribute(getConstants().getKimLdapIdProperty()));
        
        final String fullName = (String) context.getStringAttribute(getConstants().getGivenNameLdapProperty());
        
        if (fullName != null) {
            final String[] name = fullName.split(" ");
            person.setFirstName(name[0]);
            if (name.length > 1) {
                person.setMiddleName(name[1]);
            }
        }
        else {
            person.setFirstName(fullName);
        }
        
        person.setLastName(context.getStringAttribute(getConstants().getSnLdapProperty()));
        person.setDefaultValue(isdefault);
        person.setActive(true);
        person.setNameType(CodedAttribute.Builder.create("PRI"));
        
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