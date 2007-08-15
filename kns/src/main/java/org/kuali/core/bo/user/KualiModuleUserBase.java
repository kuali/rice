/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.TransientBusinessObjectBase;
import org.kuali.rice.KNSServiceLocator;

public class KualiModuleUserBase extends TransientBusinessObjectBase implements KualiModuleUser {

    private String personUniversalIdentifier;
    private UniversalUser universalUser;
    private String moduleId;

    public KualiModuleUserBase( String moduleId, UniversalUser user ) {
        this.moduleId = moduleId;
        setUniversalUser( user );
    }
    
    public boolean isModified(UniversalUser oldRecord, UniversalUser newRecord) {
        for (String propertyName : newRecord.getModuleProperties(moduleId).keySet()) {
            String newPropertyValue = newRecord.getModuleProperties(moduleId).get(propertyName);
            String oldPropertyValue = null;
            if (oldRecord != null) {
                if ((oldRecord.getModuleUser(moduleId) != null) && (oldRecord.getModuleProperties(moduleId) != null) && oldRecord.getModuleProperties(moduleId).containsKey(propertyName)) {
                    oldPropertyValue = oldRecord.getModuleProperties(moduleId).get(propertyName);
                }
            }
            if (((oldRecord == null) && !StringUtils.isBlank(newPropertyValue)) || !StringUtils.equals(oldPropertyValue, newPropertyValue)) {
                return true;
            }
        }
        return false;
    }

    public boolean isActive() {
        return getUserProperty( RicePropertyConstants.ACTIVE ).equals( "Y" );
    }
    
    protected boolean isActiveFacultyStaffAffiliate() {
        return (getUniversalUser().isFaculty() || getUniversalUser().isStaff() || getUniversalUser().isAffiliate()) && !KNSServiceLocator.getKualiConfigurationService().getApplicationParameterRule(RiceConstants.ADMIN_GROUP, RiceConstants.ALLOWED_EMPLOYEE_STATUS_RULE).failsRule(getUniversalUser().getEmployeeStatusCode());
    }

    public void setActive(boolean active) {
        if (KNSServiceLocator.getKualiModuleService().getModule(moduleId).getModuleUserService().getPropertyList().contains(RicePropertyConstants.ACTIVE)) { 
            setUserProperty( RicePropertyConstants.ACTIVE, active?"Y":"N" );
        }
    }

    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    private void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }

    /**
     * Gets the universalUser attribute.
     * 
     * @return Returns the universalUser.
     */
    public UniversalUser getUniversalUser() {
        universalUser = KNSServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(personUniversalIdentifier, universalUser);
        return universalUser;
    }

    public void setUniversalUser(UniversalUser universalUser) {
        this.universalUser = universalUser;
        if ( universalUser != null ) {
            personUniversalIdentifier = universalUser.getPersonUniversalIdentifier();
        } else {
            personUniversalIdentifier = null;
        }
    }

    public String getUserProperty(String propKey) {
        if ( propKey == null || universalUser == null ) return "";
        String value = universalUser.getModuleProperties( getModuleId() ).get( propKey );
        return value == null ? "" : value;
    }

    public void setUserProperty(String propKey, String value) {
        if ( propKey == null || universalUser == null ) return;
        if ( value == null ) value = "";
        universalUser.getModuleProperties( getModuleId() ).put( propKey, value );
    }


    public Map<String, String> getUserProperties() {
        if ( universalUser == null ) return null;
        return universalUser.getModuleProperties( getModuleId() );
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put( RicePropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, getPersonUniversalIdentifier());

        return m;
    }

    public String getModuleId() {
        return moduleId;
    }

    protected void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
    
}
