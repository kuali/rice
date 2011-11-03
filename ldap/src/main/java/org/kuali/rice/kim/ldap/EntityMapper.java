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

import java.util.ArrayList;
import java.util.List;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.util.Constants;

import static org.apache.commons.lang.StringUtils.contains;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EntityMapper extends AbstractContextMapper {
    private Constants constants;

    private ContextMapper affiliationMapper;
    private ContextMapper entityTypeMapper;
    private ContextMapper defaultNameMapper;
    private ContextMapper employmentMapper;
    
    public Object doMapFromContext(DirContextOperations context) {
        
        final String entityId      = context.getStringAttribute(getConstants().getKimLdapIdProperty());
        final String principalName = context.getStringAttribute(getConstants().getKimLdapNameProperty());

        final Entity.Builder person = Entity.Builder.create();
        person.setId(entityId);
        
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
        
        person.setEntityTypes(new ArrayList<EntityTypeContactInfo.Builder>());
        person.getEntityTypeContactInfos().add((EntityTypeContactInfo.Builder) getEntityTypeMapper().mapFromContext(context));
        
        final List<EntityName.Builder> names = new ArrayList<EntityName.Builder>();
        final EntityName.Builder name = (EntityName.Builder) getDefaultNameMapper().mapFromContext(context);
        name.setDefaultValue(true);
        person.getNames().add(name);
        person.setId(entityId);
        
        final EntityEmployment.Builder employmentInfo = (EntityEmployment.Builder) getEmploymentMapper().mapFromContext(context);
        final EntityAffiliation.Builder employeeAffiliation = getAffiliation(getConstants().getEmployeeAffiliationCodes(), person);
        
        //only add employee information if we have an employee affiliation, otherwise ignore
        if (employeeAffiliation != null && employmentInfo != null) {
            employeeAffiliation.getAffiliationType().setEmploymentAffiliationType(true);
            employmentInfo.setEntityAffiliation(employeeAffiliation);
            person.getEmploymentInformation().add(employmentInfo);
        }
        
        person.setPrincipals(new ArrayList<Principal.Builder>());
        person.setActive(true);
        
        final Principal.Builder defaultPrincipal = Principal.Builder.create(principalName);
        defaultPrincipal.setPrincipalId(entityId);
        defaultPrincipal.setEntityId(entityId);
        
        person.getPrincipals().add(defaultPrincipal);
        
        return person;
    }
    
    /**
     * 
     * Finds and returns affiliation id of the persons affiliation that matches the affilication code
     * @param affiliationCode
     * @param person
     * @return
     */
    protected EntityAffiliation.Builder getAffiliation(String affiliationCodes, Entity.Builder person) {
        EntityAffiliation.Builder retval = null;
        for (EntityAffiliation.Builder affil : person.getAffiliations()) {
            if (contains(affiliationCodes, affil.getAffiliationType().getCode())) {
                return affil;
            }
        }
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
     * Gets the value of affiliationMapper
     *
     * @return the value of affiliationMapper
     */
    public final ContextMapper getAffiliationMapper() {
        return this.affiliationMapper;
    }

    /**
     * Sets the value of affiliationMapper
     *
     * @param argAffiliationMapper Value to assign to this.affiliationMapper
     */
    public final void setAffiliationMapper(final ContextMapper argAffiliationMapper) {
        this.affiliationMapper = argAffiliationMapper;
    }

    /**
     * Gets the value of entityTypeMapper
     *
     * @return the value of entityTypeMapper
     */
    public final ContextMapper getEntityTypeMapper() {
        return this.entityTypeMapper;
    }

    /**
     * Sets the value of entityTypeMapper
     *
     * @param argEntityTypeMapper Value to assign to this.entityTypeMapper
     */
    public final void setEntityTypeMapper(final ContextMapper argEntityTypeMapper) {
        this.entityTypeMapper = argEntityTypeMapper;
    }

    /**
     * Gets the value of defaultNameMapper
     *
     * @return the value of defaultNameMapper
     */
    public final ContextMapper getDefaultNameMapper() {
        return this.defaultNameMapper;
    }

    /**
     * Sets the value of defaultNameMapper
     *
     * @param argDefaultNameMapper Value to assign to this.defaultNameMapper
     */
    public final void setDefaultNameMapper(final ContextMapper argDefaultNameMapper) {
        this.defaultNameMapper = argDefaultNameMapper;
    }

    /**
     * Gets the value of employmentMapper
     *
     * @return the value of employmentMapper
     */
    public final ContextMapper getEmploymentMapper() {
        return this.employmentMapper;
    }

    /**
     * Sets the value of employmentMapper
     *
     * @param argEmploymentMapper Value to assign to this.employmentMapper
     */
    public final void setEmploymentMapper(final ContextMapper argEmploymentMapper) {
        this.employmentMapper = argEmploymentMapper;
    }
}