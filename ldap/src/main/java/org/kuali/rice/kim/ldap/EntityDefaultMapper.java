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

import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoDefault;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.util.Constants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityDefaultMapper extends AbstractContextMapper {
    private Constants constants;
    private EntityAffiliationMapper affiliationMapper;
    private EntityTypeContactInfoMapper entityTypeMapper;
    private EntityNameMapper defaultNameMapper;
    private EntityEmploymentMapper employmentMapper;
    
    public Object doMapFromContext(DirContextOperations context) {
        
        final String entityId = context.getStringAttribute(getConstants().getKimLdapIdProperty());
        final String principalName = context.getStringAttribute(getConstants().getKimLdapNameProperty());

        final EntityDefault.Builder person = EntityDefault.Builder.create(entityId);
        
        if (entityId == null) {
            throw new InvalidLdapEntityException("LDAP Search Results yielded an invalid result with attributes " 
                                                 + context.getAttributes());
        }
        
        person.setAffiliations(new ArrayList<EntityAffiliation.Builder>());
        person.setExternalIdentifiers(new ArrayList<EntityExternalIdentifier.Builder>());
        
        final EntityExternalIdentifier.Builder externalId = EntityExternalIdentifier.Builder.create();
        externalId.setExternalIdentifierTypeCode(getConstants().getTaxExternalIdTypeCode());
        externalId.setExternalId(entityId);
        person.getExternalIdentifiers().add(externalId);
        
        person.setAffiliations((List<EntityAffiliation.Builder>) getAffiliationMapper().mapFromContext(context));
        
        person.setEntityTypeContactInfos(new ArrayList<EntityTypeContactInfoDefault.Builder>());
        person.getEntityTypeContactInfos().add((EntityTypeContactInfoDefault.Builder) getEntityTypeMapper().mapFromContext(context));
        
        person.setName(getDefaultNameMapper().mapFromContext(context));
        person.setEntityId(entityId);
        
        person.setEmployment((EntityEmployment.Builder) getEmploymentMapper().mapFromContext(context));

        person.setEntityId(entityId);
        person.setPrincipals(new ArrayList<Principal.Builder>()); 
        //inactivate unless we find a matching affiliation
        person.setActive(true);
        
        final Principal.Builder defaultPrincipal = Principal.Builder.create(principalName);
        defaultPrincipal.setPrincipalId(entityId);
        defaultPrincipal.setEntityId(entityId);

        person.getPrincipals().add(defaultPrincipal);
        
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

    
    /**
     * Gets the value of affiliationMapper
     *
     * @return the value of affiliationMapper
     */
    public final EntityAffiliationMapper getAffiliationMapper() {
        return this.affiliationMapper;
    }

    /**
     * Sets the value of affiliationMapper
     *
     * @param argAffiliationMapper Value to assign to this.affiliationMapper
     */
    public final void setAffiliationMapper(final EntityAffiliationMapper argAffiliationMapper) {
        this.affiliationMapper = argAffiliationMapper;
    }

    /**
     * Gets the value of entityTypeMapper
     *
     * @return the value of entityTypeMapper
     */
    public final EntityTypeContactInfoMapper getEntityTypeMapper() {
        return this.entityTypeMapper;
    }

    /**
     * Sets the value of entityTypeMapper
     *
     * @param argEntityTypeMapper Value to assign to this.entityTypeMapper
     */
    public final void setEntityTypeMapper(final EntityTypeContactInfoMapper argEntityTypeMapper) {
        this.entityTypeMapper = argEntityTypeMapper;
    }

    /**
     * Gets the value of defaultNameMapper
     *
     * @return the value of defaultNameMapper
     */
    public final EntityNameMapper getDefaultNameMapper() {
        return this.defaultNameMapper;
    }

    /**
     * Sets the value of defaultNameMapper
     *
     * @param argDefaultNameMapper Value to assign to this.defaultNameMapper
     */
    public final void setDefaultNameMapper(final EntityNameMapper argDefaultNameMapper) {
        this.defaultNameMapper = argDefaultNameMapper;
    }

    /**
     * Gets the value of employmentMapper
     *
     * @return the value of employmentMapper
     */
    public final EntityEmploymentMapper getEmploymentMapper() {
        return this.employmentMapper;
    }

    /**
     * Sets the value of employmentMapper
     *
     * @param argEmploymentMapper Value to assign to this.employmentMapper
     */
    public final void setEmploymentMapper(final EntityEmploymentMapper argEmploymentMapper) {
        this.employmentMapper = argEmploymentMapper;
    }

}