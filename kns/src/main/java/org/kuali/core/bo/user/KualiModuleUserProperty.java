/*
 * Copyright 2007 The Kuali Foundation.
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

import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.IdClass;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

@IdClass(org.kuali.core.bo.user.KualiModuleUserPropertyId.class)
@Entity
@Table(name="SH_USR_PROP_T")
public class KualiModuleUserProperty extends PersistableBusinessObjectBase {

    @Id
	@Column(name="PERSON_UNVL_ID")
	private String personUniversalIdentifier;
    @Id
	@Column(name="APPL_MOD_ID")
	private String moduleId;
    @Id
	@Column(name="USR_PROP_NM")
	private String name;
    @Column(name="USR_PROP_VAL")
	private String value;
   
    
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }


    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    public String getName() {
        return name;
    }


    public void setName(String propertyName) {
        this.name = propertyName;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String propertyValue) {
        this.value = propertyValue;
    }


    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put( "name", name );
        m.put( "value", value );
        return m;
    }


    public String getModuleId() {
        return moduleId;
    }


    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

}

