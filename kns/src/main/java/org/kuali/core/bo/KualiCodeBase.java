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
package org.kuali.core.bo;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;


/**
 * 
 */
public class KualiCodeBase extends PersistableBusinessObjectBase implements KualiCode {

    protected String code;
    protected String name;
    protected boolean active;

    public KualiCodeBase() {
        this.active = true;
    }

    public KualiCodeBase(String code) {
        this();
        this.code = code;
    }

    /**
     * @return Getter for the Code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code - Setter for the Code.
     */
    public void setCode(String code) {
        this.code = code;
    }


    /**
     * @return Getter for the Name.
     */
    public String getName() {
        return name;
    }


    /**
     * @param name - Setter for the name.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return Getter for the active field.
     */
    public boolean isActive() {
        return active;
    }


    /**
     * @param name - Setter for the active field.
     */
    public void setActive(boolean a) {
        this.active = a;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getCode() + " - " + getName();
        return theString;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    final protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("code", getCode());
        m.put("name", getName());

        return m;
    }

    /**
     * Implements equals comparing code to code.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof KualiCodeBase) {
            return StringUtils.equals(this.getCode(), ((KualiCodeBase) obj).getCode());
        }
        return false;
    }

    /**
     * Overriding equals requires writing a hashCode method.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hashCode = 0;

        if (getCode() != null) {
            hashCode = getCode().hashCode();
        }

        return hashCode;
    }

}