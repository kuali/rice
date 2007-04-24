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

package org.kuali.core.bo;

import java.util.LinkedHashMap;

/**
 * 
 */
public class BusinessRuleSecurity extends PersistableBusinessObjectBase {

    private String ruleGroupName;
    private String workgroupName;
    private String ruleGroupDescription;

    /**
     * Default constructor.
     */
    public BusinessRuleSecurity() {

    }

    /**
     * Gets the ruleGroupName attribute.
     * 
     * @return Returns the ruleGroupName
     * 
     */
    public String getRuleGroupName() {
        return ruleGroupName;
    }

    /**
     * Sets the ruleGroupName attribute.
     * 
     * @param ruleGroupName The ruleGroupName to set.
     * 
     */
    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }


    /**
     * Gets the workgroupName attribute.
     * 
     * @return Returns the workgroupName
     * 
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute.
     * 
     * @param workgroupName The workgroupName to set.
     * 
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }


    /**
     * Gets the ruleGroupDescription attribute.
     * 
     * @return Returns the ruleGroupDescription
     * 
     */
    public String getRuleGroupDescription() {
        return ruleGroupDescription;
    }

    /**
     * Sets the ruleGroupDescription attribute.
     * 
     * @param ruleGroupDescription The ruleGroupDescription to set.
     * 
     */
    public void setRuleGroupDescription(String ruleGroupDescription) {
        this.ruleGroupDescription = ruleGroupDescription;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("ruleGroupName", this.ruleGroupName);
        return m;
    }
}
