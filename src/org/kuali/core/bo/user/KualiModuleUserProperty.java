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

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

public class KualiModuleUserProperty extends PersistableBusinessObjectBase {

    private String personUniversalIdentifier;
    private String moduleId;
    private String name;
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
