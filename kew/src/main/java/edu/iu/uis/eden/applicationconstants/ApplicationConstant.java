/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.applicationconstants;

import java.io.Serializable;

import edu.iu.uis.eden.util.Utilities;

/**
 * Model object for application constants mapped ot ojb.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class ApplicationConstant implements Serializable {

	private static final long serialVersionUID = 5561795446088281002L;

	private String applicationConstantName;

    private String applicationConstantValue;

    private Integer lockVerNbr;

    public ApplicationConstant() {}

    public ApplicationConstant(String name, String value) {
        this.applicationConstantName = name;
        this.applicationConstantValue = value;
    }

    /**
     * @return Returns the applicationConstantName.
     */
    public String getApplicationConstantName() {
        return applicationConstantName;
    }

    /**
     * @param applicationConstantName
     *            The applicationConstantName to set.
     */
    public void setApplicationConstantName(String applicationConstantName) {
        this.applicationConstantName = applicationConstantName;
    }

    /**
     * @return Returns the applicationConstantValue.
     */
    public String getApplicationConstantValue() {
        return applicationConstantValue;
    }

    /**
     * @param applicationConstantValue
     *            The applicationConstantValue to set.
     */
    public void setApplicationConstantValue(String applicationConstantValue) {
        this.applicationConstantValue = applicationConstantValue;
    }

    /**
     * @return Returns the lockVerNbr.
     */
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    /**
     * @param lockVerNbr
     *            The lockVerNbr to set.
     */
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public int hashCode() {
        return (applicationConstantName + ":" + applicationConstantValue).hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ApplicationConstant)) return false;
        ApplicationConstant ac = (ApplicationConstant) o;
        return Utilities.equals(applicationConstantName, ac.applicationConstantName) &&
               Utilities.equals(applicationConstantValue, ac.applicationConstantValue);
    }

    public String toString() {
        return "[ApplicationConstant: name=" + applicationConstantName
                                 + ", value=" + applicationConstantValue
                                 + "]";
    }
}