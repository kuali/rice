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

import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoDefault;
import org.kuali.rice.kim.util.Constants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityTypeContactInfoDefaultMapper extends AbstractContextMapper {
    private Constants constants;
    
    private EntityAddressMapper addressMapper;
    private EntityPhoneMapper phoneMapper;
    private EntityEmailMapper emailMapper;

    public EntityTypeContactInfoDefault.Builder mapFromContext(DirContextOperations context) {
        return (EntityTypeContactInfoDefault.Builder) doMapFromContext(context);
    }

    public Object doMapFromContext(DirContextOperations context) {
        final EntityTypeContactInfoDefault.Builder retval = EntityTypeContactInfoDefault.Builder.create(); 
        
        retval.setDefaultAddress(getAddressMapper().mapFromContext(context));
        retval.setDefaultPhoneNumber(getPhoneMapper().mapFromContext(context));
        retval.setDefaultEmailAddress(getEmailMapper().mapFromContext(context));
        retval.setEntityTypeCode(getConstants().getPersonEntityTypeCode());
        // debug("Created Entity Type with code ", retval.getEntityTypeCode());
        
        return retval;
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
    /**
     * Gets the value of addressMapper
     *
     * @return the value of addressMapper
     */
    public final EntityAddressMapper getAddressMapper() {
        return this.addressMapper;
    }

    /**
     * Sets the value of addressMapper
     *
     * @param argAddressMapper Value to assign to this.addressMapper
     */
    public final void setAddressMapper(final EntityAddressMapper argAddressMapper) {
        this.addressMapper = argAddressMapper;
    }

    /**
     * Gets the value of phoneMapper
     *
     * @return the value of phoneMapper
     */
    public final EntityPhoneMapper getPhoneMapper() {
        return this.phoneMapper;
    }

    /**
     * Sets the value of phoneMapper
     *
     * @param argPhoneMapper Value to assign to this.phoneMapper
     */
    public final void setPhoneMapper(final EntityPhoneMapper argPhoneMapper) {
        this.phoneMapper = argPhoneMapper;
    }

    /**
     * Gets the value of emailMapper
     *
     * @return the value of emailMapper
     */
    public final EntityEmailMapper getEmailMapper() {
        return this.emailMapper;
    }

    /**
     * Sets the value of emailMapper
     *
     * @param argEmailMapper Value to assign to this.emailMapper
     */
    public final void setEmailMapper(final EntityEmailMapper argEmailMapper) {
        this.emailMapper = argEmailMapper;
    }
}