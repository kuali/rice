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

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.util.Constants;

import static org.kuali.rice.core.util.BufferedLogger.*;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityTypeContactInfoMapper extends AbstractContextMapper {
    private Constants constants;

    private ContextMapper addressMapper;
    private ContextMapper phoneMapper;
    private ContextMapper emailMapper;;

    
    public Object doMapFromContext(DirContextOperations context) {
        final EntityTypeContactInfo retval = new EntityTypeContactInfo(); 
        
        final EntityAddress address = (EntityAddress) getAddressMapper().mapFromContext(context);
        retval.setDefaultAddress(address);
        final List<EntityAddress> addresses = new ArrayList<EntityAddress>();
        addresses.add(address);
        retval.setAddresses(addresses);
        
        final List<EntityEmail> email = new ArrayList<EntityEmail>();
        email.add((EntityEmail) getEmailMapper().mapFromContext(context));
        retval.setDefaultEmailAddress(email.get(0));
        final List<EntityPhone> phone = new ArrayList<EntityPhone>();
        phone.add((EntityPhone) getPhoneMapper().mapFromContext(context));
        retval.setDefaultPhoneNumber(phone.get(0));
        retval.setEmailAddresses(email);
        retval.setPhoneNumbers(phone);
        retval.setEntityTypeCode(getConstants().getPersonEntityTypeCode());
        debug("Created Entity Type with code ", retval.getEntityTypeCode());
                
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
    public final ContextMapper getAddressMapper() {
        return this.addressMapper;
    }

    /**
     * Sets the value of addressMapper
     *
     * @param argAddressMapper Value to assign to this.addressMapper
     */
    public final void setAddressMapper(final ContextMapper argAddressMapper) {
        this.addressMapper = argAddressMapper;
    }

    /**
     * Gets the value of phoneMapper
     *
     * @return the value of phoneMapper
     */
    public final ContextMapper getPhoneMapper() {
        return this.phoneMapper;
    }

    /**
     * Sets the value of phoneMapper
     *
     * @param argPhoneMapper Value to assign to this.phoneMapper
     */
    public final void setPhoneMapper(final ContextMapper argPhoneMapper) {
        this.phoneMapper = argPhoneMapper;
    }

    /**
     * Gets the value of emailMapper
     *
     * @return the value of emailMapper
     */
    public final ContextMapper getEmailMapper() {
        return this.emailMapper;
    }

    /**
     * Sets the value of emailMapper
     *
     * @param argEmailMapper Value to assign to this.emailMapper
     */
    public final void setEmailMapper(final ContextMapper argEmailMapper) {
        this.emailMapper = argEmailMapper;
    }
}