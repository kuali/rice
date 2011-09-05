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

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import org.kuali.rice.kim.api.identity.Type;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.util.Constants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityAddressMapper extends AbstractContextMapper {
    private Constants constants;

    public EntityAddress.Builder mapFromContext(DirContextOperations context, boolean isdefault) {
        return (EntityAddress.Builder) doMapFromContext(context, isdefault);
    }

    public EntityAddress.Builder mapFromContext(DirContextOperations context) {
        return mapFromContext(context, false);
    }

    public Object doMapFromContext(DirContextOperations context) {
        return doMapFromContext(context, false);
    }

    protected Object doMapFromContext(DirContextOperations context, boolean isdefault) {        
        final EntityAddress.Builder builder = EntityAddress.Builder.create();
        final String line1      = context.getStringAttribute("employeePrimaryDeptName");
        final String line2      = context.getStringAttribute("employeePoBox");
        final String city   = context.getStringAttribute("employeeCity");
        final String stateCode  = context.getStringAttribute("employeeState");
        final String postalCode = context.getStringAttribute("employeeZip");
        
        builder.setAddressType(Type.Builder.create("WORK"));
        builder.setLine1(line1);
        builder.setLine2(line2);
        builder.setCity(city);
        builder.setStateCode(stateCode);
        builder.setPostalCode(postalCode);
        builder.setDefaultValue(isdefault);
        builder.setActive(true);
        return builder;
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