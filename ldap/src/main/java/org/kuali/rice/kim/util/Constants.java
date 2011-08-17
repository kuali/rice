/*
 * Copyright 2009 Arizona Board of Regents
 *
 */
package org.kuali.rice.kim.util;

import java.util.Collection;

import org.kuali.rice.kim.api.identity.entity.Entity;

/**
 *
 * @author Leo Przybylski (przybyls@arizona.edu)
 */ 
public interface Constants {    
    Collection<String> getTestPrincipalNames();

    String getDefaultChartCode();

    /**
     * Gets the value of entityPrototype
     *
     * @return the value of entityPrototype
     */
    Entity getEntityPrototype();

    /**
     * Gets the value of externalIdTypeProperty
     *
     * @return the value of externalIdTypeProperty
     */
    String getExternalIdTypeProperty();

    /**
     * Gets the value of taxExternalIdTypeCode
     *
     * @return the value of taxExternalIdTypeCode
     */
    String getTaxExternalIdTypeCode();

    /**
     * Gets the value of externalIdProperty
     *
     * @return the value of externalIdProperty
     */
    String getExternalIdProperty();

    /**
     * Gets the value of employeePhoneLdapProperty
     *
     * @return the value of employeePhoneLdapProperty
     */
    String getEmployeePhoneLdapProperty();

    /**
     * Gets the value of employeeMailLdapProperty
     *
     * @return the value of employeeMailLdapProperty
     */
    String getEmployeeMailLdapProperty();

    /**
     * Gets the value of defaultCountryCode
     *
     * @return the value of defaultCountryCode
     */
    String getDefaultCountryCode();

    /**
     * Gets the value of personEntityTypeCode
     *
     * @return the value of personEntityTypeCode
     */
    String getPersonEntityTypeCode();

    /**
     * Gets the value of uaidLdapProperty
     *
     * @return the value of uaidLdapProperty
     */
    String getKimLdapIdProperty();

    /**
     * Gets the value of uidLdapProperty
     *
     * @return the value of uidLdapProperty
     */
    String getKimLdapNameProperty();

    /**
     * Gets the value of snLdapProperty
     *
     * @return the value of snLdapProperty
     */
    String getSnLdapProperty();

    /**
     * Gets the value of givenNameLdapProperty
     *
     * @return the value of givenNameLdapProperty
     */
    String getGivenNameLdapProperty();

    /**
     * Gets the value of entityIdKimProperty
     *
     * @return the value of entityIdKimProperty
     */
    String getEntityIdKimProperty();

    /**
     * Gets the value of parameterNamespaceCode
     *
     * @return the value of parameterNamespaceCode
     */
    String getParameterNamespaceCode();

    /**
     * Gets the value of parameterDetailTypeCode
     *
     * @return the value of parameterDetailTypeCode
     */
    String getParameterDetailTypeCode();

    /**
     * Gets the value of mappedParameterName
     *
     * @return the value of mappedParameterName
     */
    String getMappedParameterName();

    /**
     * Gets the value of unmappedParameterName
     *
     * @return the value of unmappedParameterName
     */
    String getUnmappedParameterName();

    /**
     * Gets the value of mappedValuesName
     *
     * @return the value of mappedValuesName
     */
    String getMappedValuesName();

    /**
     * Gets the value of employeeIdProperty
     *
     * @return the value of employeeIdProperty
     */
    String getEmployeeIdProperty();

    /**
     * Gets the value of departmentLdapProperty
     *
     * @return the value of departmentLdapProperty
     */
    String getDepartmentLdapProperty();

    /**
     * Gets the value of employeeTypeProperty
     *
     * @return the value of employeeTypeProperty
     */
    String getEmployeeTypeProperty();

    /**
     * Gets the value of employeeStatusProperty
     *
     * @return the value of employeeStatusProperty
     */
    String getEmployeeStatusProperty();

    /**
     * Gets the value of defaultCampusCode
     *
     * @return the value of defaultCampusCode
     */
    String getDefaultCampusCode();
        
    /**
     * Gets the value of the employee affiliation code
     * 
     * @return the value of employeeAffiliationCode
     */
    String getEmployeeAffiliationCodes();

    /** 
     * Gets the mappings between LDAP and KIM affiliations
     * @return mappings of the form "staff=STAFF,affiliate=AFLT"
     */
    String getAffiliationMappings();

    /**
     * Gets the mappings for the affiliation ldap property
     * @return mapping for KIM affiliation and LDAP
     */
    String getAffiliationLdapProperty();

    /**
     * Gets the mappings for the primary affiliation ldap property
     * @return mapping for KIM primary affiliation and LDAP
     */
    String getPrimaryAffiliationLdapProperty();
}
