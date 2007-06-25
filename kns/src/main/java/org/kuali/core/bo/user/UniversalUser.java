/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.core.bo.user;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.KualiModule;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.EmployeeStatus;
import org.kuali.core.bo.EmployeeType;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.rice.KNSServiceLocator;

/**
 * 
 */
public class UniversalUser extends PersistableBusinessObjectBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversalUser.class);

    private transient static UniversalUserService universalUserService;
    private transient static KualiModuleService kualiModuleService;
    
    private String personUniversalIdentifier;
    private String personUserIdentifier;
    private String personPayrollIdentifier;
    private String personTaxIdentifier;
    private String personTaxIdentifierTypeCode;
    private String personFirstName;
    private String personLastName;
    private String personMiddleName;
    private String personName;
    private String personEmailAddress;
    private String campusCode;
    private String primaryDepartmentCode;
    private String personCampusAddress;
    private String personLocalPhoneNumber;
    private String employeeStatusCode;
    private String employeeTypeCode;
    private KualiDecimal personBaseSalaryAmount;
    private String financialSystemsEncryptedPasswordText;
    
    private boolean student;
    private boolean staff;
    private boolean faculty;
    private boolean affiliate;
    
    private Campus campus;
    private EmployeeStatus employeeStatus;
    private EmployeeType employeeType;

    private Map<String,KualiModuleUser> moduleUsers;
    private boolean activeForAnyModule = false;
    private List<KualiGroup> groups;
    
    // TODO this shouldn't really be here
    // need to push a list of changed module codes into workflow - doing this in the UniversalUserPreRules
    // - ideally this would go on the doc, since it relates to the old and new maintainable, but no option for extending document implementation in the maint framework
    // - so, we could put it on the maintainable, but properties of the maintainable don't get put in the main doc xml in populateXmDocumentContentsFromMaintainables
    // - so, we could derive and set after populateMaintainablesFromXmlDocumentContents, but i don't see a hook - have processAfterRetrieve called then on the new maintainable, but we need the od maintainable to derive
    // - so, i could modify the framework - e.g. implement handleRouteLevelChange on maintenance document and call a method on the maintainable, where i pass the prior row - seems like a big change for this need
    private Set<String> changedModuleCodes;
    
    /**
     * Default no-arg constructor.
     */
    public UniversalUser() {
    }


    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return - Returns the personUniversalIdentifier
     * 
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }


    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     * 
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }

    /**
     * Gets the personUserIdentifier attribute.
     * 
     * @return - Returns the personUserIdentifier
     * 
     */
    public String getPersonUserIdentifier() {
        return personUserIdentifier;
    }


    /**
     * Sets the personUserIdentifier attribute.
     * 
     * @param personUserIdentifier The personUserIdentifier to set.
     * 
     */
    public void setPersonUserIdentifier(String personUserIdentifier) {
        this.personUserIdentifier = personUserIdentifier;
    }

    /**
     * Gets the employeeStatusCode attribute.
     * 
     * @return - Returns the employeeStatusCode
     * 
     */
    public String getEmployeeStatusCode() {
        return employeeStatusCode;
    }


    /**
     * Sets the employeeStatusCode attribute.
     * 
     * @param employeeStatusCode The employeeStatusCode to set.
     * 
     */
    public void setEmployeeStatusCode(String employeeStatusCode) {
        this.employeeStatusCode = employeeStatusCode;
    }

    /**
     * Gets the personPayrollIdentifier attribute.
     * 
     * @return - Returns the personPayrollIdentifier
     * 
     */
    public String getPersonPayrollIdentifier() {
        return personPayrollIdentifier;
    }


    /**
     * Sets the personPayrollIdentifier attribute.
     * 
     * @param personPayrollIdentifier The personPayrollIdentifier to set.
     * 
     */
    public void setPersonPayrollIdentifier(String emplid) {
        this.personPayrollIdentifier = emplid;
    }

    /**
     * Gets the primaryDepartmentCode attribute.
     * 
     * @return - Returns the primaryDepartmentCode
     * 
     */
    public String getPrimaryDepartmentCode() {
        return primaryDepartmentCode;
    }


    /**
     * Sets the primaryDepartmentCode attribute.
     * 
     * @param primaryDepartmentCode The primaryDepartmentCode to set.
     * 
     */
    public void setPrimaryDepartmentCode(String deptid) {
        this.primaryDepartmentCode = deptid;
    }

    /**
     * Gets the personEmailAddress attribute.
     * 
     * @return - Returns the personEmailAddress
     * 
     */
    public String getPersonEmailAddress() {
        return personEmailAddress;
    }


    /**
     * Sets the personEmailAddress attribute.
     * 
     * @param personEmailAddress The personEmailAddress to set.
     * 
     */
    public void setPersonEmailAddress(String personEmailAddress) {
        this.personEmailAddress = personEmailAddress;
    }

    /**
     * Gets the personFirstName attribute.
     * 
     * @return - Returns the personFirstName
     * 
     */
    public String getPersonFirstName() {
        return personFirstName;
    }


    /**
     * Sets the personFirstName attribute.
     * 
     * @param personFirstName The personFirstName to set.
     * 
     */
    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    /**
     * Gets the personLastName attribute.
     * 
     * @return - Returns the personLastName
     * 
     */
    public String getPersonLastName() {
        return personLastName;
    }


    /**
     * Sets the personLastName attribute.
     * 
     * @param personLastName The personLastName to set.
     * 
     */
    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    /**
     * Gets the personCampusAddress attribute.
     * 
     * @return - Returns the personCampusAddress
     * 
     */
    public String getPersonCampusAddress() {
        return personCampusAddress;
    }


    /**
     * Sets the personCampusAddress attribute.
     * 
     * @param personCampusAddress The personCampusAddress to set.
     * 
     */
    public void setPersonCampusAddress(String personCampusAddress) {
        this.personCampusAddress = personCampusAddress;
    }

    /**
     * Gets the personLocalPhoneNumber attribute.
     * 
     * @return - Returns the personLocalPhoneNumber
     * 
     */
    public String getPersonLocalPhoneNumber() {
        return personLocalPhoneNumber;
    }


    /**
     * Sets the personLocalPhoneNumber attribute.
     * 
     * @param personLocalPhoneNumber The personLocalPhoneNumber to set.
     * 
     */
    public void setPersonLocalPhoneNumber(String personLocalPhoneNumber) {
        this.personLocalPhoneNumber = personLocalPhoneNumber;
    }

    /**
     * Gets the personBaseSalaryAmount attribute.
     * 
     * @return - Returns the personBaseSalaryAmount
     * 
     */
    public KualiDecimal getPersonBaseSalaryAmount() {
        return personBaseSalaryAmount;
    }


    /**
     * Sets the personBaseSalaryAmount attribute.
     * 
     * @param personBaseSalaryAmount The personBaseSalaryAmount to set.
     * 
     */
    public void setPersonBaseSalaryAmount(KualiDecimal personBaseSalaryAmount) {
        this.personBaseSalaryAmount = personBaseSalaryAmount;
    }

    /**
     * @return Returns the personTaxIdentifier.
     */
    public String getPersonTaxIdentifier() {
        return personTaxIdentifier;
    }

    /**
     * @param personTaxIdentifier The personTaxIdentifier to set.
     */
    public void setPersonTaxIdentifier(String personSocialSecurityNbrId) {
        this.personTaxIdentifier = personSocialSecurityNbrId;
    }

    /**
     * @return Returns the campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * @return Returns the campus.
     */
    public Campus getCampus() {
        return campus;
    }

    /**
     * @param campus The campus to set.
     * @deprecated
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }
    
    

    /**
     * Gets the personName attribute. 
     * @return Returns the personName.
     */
    public String getPersonName() {
        return personName;
    }

    /**
     * Sets the personName attribute value.
     * @param personName The personName to set.
     */
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * Gets the financialSystemsEncryptedPasswordText attribute. 
     * @return Returns the financialSystemsEncryptedPasswordText.
     */
    public String getFinancialSystemsEncryptedPasswordText() {
        return financialSystemsEncryptedPasswordText;
    }

    /**
     * Sets the financialSystemsEncryptedPasswordText attribute value.
     * @param financialSystemsEncryptedPasswordText The financialSystemsEncryptedPasswordText to set.
     */
    public void setFinancialSystemsEncryptedPasswordText(String financialSystemsEncryptedPasswordText) {
        this.financialSystemsEncryptedPasswordText = financialSystemsEncryptedPasswordText;
    }    

    private void initModuleUsers() {
        if ( universalUserService == null ) {
            universalUserService = KNSServiceLocator.getUniversalUserService();
        }
        moduleUsers = universalUserService.getModuleUsers( this );
    }
    
    
    public KualiModuleUser getModuleUser( String moduleId ) {
        if ( moduleUsers == null ) {
            initModuleUsers();
        }
        refreshModuleUsersToUniversalUserReferences();
        KualiModuleUser kualiModuleUser = moduleUsers.get( moduleId );
        return kualiModuleUser;
    }
    
    public Map<String,KualiModuleUser> getModuleUsers() {
        if ( moduleUsers == null ) {
            initModuleUsers();
        }
        refreshModuleUsersToUniversalUserReferences();
        return moduleUsers;
    }

    /**
     * sometimes there are problems w/ the serialization when coming back from struts when retrieving the actionform from struts.
     * 
     * the module user is not linked to "this" universal user, so this code fixes that
     */
    protected void refreshModuleUsersToUniversalUserReferences() {
        for (KualiModuleUser kualiModuleUser : moduleUsers.values()) {
            if (kualiModuleUser instanceof KualiModuleUserBase) {
                ((KualiModuleUserBase) kualiModuleUser).setUniversalUser(this);
            }
        }
    }

    public boolean isActiveForModule( String moduleId ) {
        KualiModuleUser user = getModuleUser( moduleId );
        if ( user == null ) return false;
        return user.isActive();
    }

    public boolean isActiveForAnyModule() {
        if ( activeForAnyModule == false ) {
            for ( KualiModuleUser user : getModuleUsers().values() ) {
                if ( user != null && user.isActive() ) {
                    activeForAnyModule = true;
                }
            }
        }
        return activeForAnyModule;
    }
    
    /**
     * Returns a comma-delimited string of the KualiModule codes for which this person is an active user. 
     * 
     * @return
     */
    public String getActiveModuleCodeString() {
        if ( kualiModuleService == null ) {
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
        }
        StringBuffer sb = new StringBuffer( 40 );
        for ( KualiModule module : kualiModuleService.getInstalledModules() ) {
            if ( isActiveForModule( module.getModuleId() ) ) {
                if ( sb.length() > 0 ) {
                    sb.append( ", " );
                }
                sb.append( module.getModuleCode() );
            }
        }
        return sb.toString();
    }
    

    private Map<String,Map<String,String>> moduleProperties;

    public Map<String,Map<String,String>> getModuleProperties() {
        if ( moduleProperties == null ) {
            loadModuleProperties();
        }
        return moduleProperties;
    }
    
    public Map<String,String> getModuleProperties( String moduleId ) {
        if ( moduleProperties == null ) {
            loadModuleProperties();
        }
        if ( moduleProperties.get( moduleId ) == null ) {
            moduleProperties.put( moduleId, new HashMap<String,String>() );
        }
        return moduleProperties.get( moduleId );
    }

    private void loadModuleProperties() {
        if ( universalUserService == null ) {
            universalUserService = KNSServiceLocator.getUniversalUserService();
        }
        moduleProperties = universalUserService.loadModuleUserProperties( this );
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        return m;
    }
    
    /**
     * @return Returns the groups.
     */
    public List<KualiGroup> getGroups() {
        if ( groups == null ) {
            refreshUserGroups();
        }
        return groups;
    }


    /**
     * @param groups The groups to set.
     */
    public void setGroups(List<KualiGroup> groups) {
        this.groups = groups;
    }

    public void refreshUserGroups() {
        if ( universalUserService == null ) {
            universalUserService = KNSServiceLocator.getUniversalUserService();
        }
        setGroups(universalUserService.getUsersGroups( this ));
    }
    
    /**
     * boolean to indicate if the user is a member of a kuali group
     * 
     * @param kualiGroup
     * @return true if the user is a member of the group passed in
     */
    public boolean isMember( String groupName ) {
        if ( universalUserService == null ) {
            universalUserService = KNSServiceLocator.getUniversalUserService();
        }
        return universalUserService.isMember( this, groupName );
    }
    
    public boolean isMember(KualiGroup kualiGroup) {
        if ( universalUserService == null ) {
            universalUserService = KNSServiceLocator.getUniversalUserService();
        }
        return universalUserService.isMember( this, kualiGroup );
    }

    /**
     * check if the user is a supervisor user (belongs to all groups)
     * 
     * @return
     */
    public boolean isSupervisorUser() {
        if ( universalUserService == null ) {
            universalUserService = KNSServiceLocator.getUniversalUserService();
        }
        return universalUserService.isSupervisorUser( this );
    }

    /**
     * check if the user is an exception user (has the Exception role, belongs to the Exception workgroup, whatever)
     * 
     * @return
     */
    public boolean isWorkflowExceptionUser() {
        if ( universalUserService == null ) {
            universalUserService = KNSServiceLocator.getUniversalUserService();
        }
        return universalUserService.isWorkflowExceptionUser( this );
    }


    public String getPersonMiddleName() {
        return personMiddleName;
    }


    public void setPersonMiddleName(String personMiddleName) {
        this.personMiddleName = personMiddleName;
    }


    public String getEmployeeTypeCode() {
        return employeeTypeCode;
    }


    public void setEmployeeTypeCode(String personTypeCode) {
        this.employeeTypeCode = personTypeCode;
    }


    public String getPersonTaxIdentifierTypeCode() {
        return personTaxIdentifierTypeCode;
    }


    public void setPersonTaxIdentifierTypeCode(String personTaxIdentifierTypeCode) {
        this.personTaxIdentifierTypeCode = personTaxIdentifierTypeCode;
    }


    public boolean isAffiliate() {
        return affiliate;
    }


    public void setAffiliate(boolean affiliate) {
        this.affiliate = affiliate;
    }


    public boolean isFaculty() {
        return faculty;
    }


    public void setFaculty(boolean faculty) {
        this.faculty = faculty;
    }


    public boolean isStaff() {
        return staff;
    }


    public void setStaff(boolean staff) {
        this.staff = staff;
    }


    public boolean isStudent() {
        return student;
    }


    public void setStudent(boolean student) {
        this.student = student;
    }

    /**
     * 
     * This method returns the Employee Status record currently associated with this Universal User
     * @return EmployeeStatus the current status of this user
     */
    public EmployeeStatus getEmployeeStatus() {
        return employeeStatus;
    }

    /**
     * 
     * This method sets the current employee status of this universal user
     * @param employeeStatus the current status this universal user should be associated with
     * @deprecated
     */
    public void setEmployeeStatus(EmployeeStatus employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    /**
     * 
     * This method returns the employee type that currently describes this universal user
     * @return EmployeeType the type of employee this universal user currently is
     */
    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    /**
     * 
     * This method sets the employee type of this universal user
     * @param employeeType the employee type to set this universal user to
     * @deprecated
     */
    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    /* added for XStream de-serialization purposes */ 
    @Deprecated
    private transient String emplid;    
    @Deprecated
    private transient String deptid;

    /**
     * Gets the changedModuleCodes attribute. 
     * @return Returns the changedModuleCodes.
     */
    public Set<String> getChangedModuleCodes() {
        return changedModuleCodes;
    }

    /**
     * Sets the changedModuleCodes attribute value.
     * @param changedModuleCodes The changedModuleCodes to set.
     */
    public void setChangedModuleCodes(Set<String> changedModuleCodes) {
        this.changedModuleCodes = changedModuleCodes;
    }
}