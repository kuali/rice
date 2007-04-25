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
import java.util.List;

/**
 * This class represents a security grouping which is identified by its script name. Each security grouping has a workgroup
 * associated with it for authorization purposes.
 * 
 * 
 */
public class FinancialSystemParameterSecurity extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 2750398934571922047L;
    private String financialSystemScriptName;
    private String workgroupName;
    private String financialSystemScriptDescription;

    private List financialSystemParameters;

    /**
     * Default no-arg constructor.
     */
    public FinancialSystemParameterSecurity() {
    }

    /**
     * Gets the financialSystemScriptName attribute.
     * 
     * @return Returns the financialSystemScriptName
     */
    public String getFinancialSystemScriptName() {
        return financialSystemScriptName;
    }

    /**
     * Sets the financialSystemScriptName attribute.
     * 
     * @param financialSystemScriptName The financialSystemScriptName to set.
     */
    public void setFinancialSystemScriptName(String financialSystemScriptName) {
        this.financialSystemScriptName = financialSystemScriptName;
    }

    /**
     * Gets the workgroupName attribute.
     * 
     * @return Returns the workgroupName
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute.
     * 
     * @param workgroupName The workgroupName to set.
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }

    /**
     * Gets the financialSystemScriptDescription attribute.
     * 
     * @return Returns the financialSystemScriptDescription
     */
    public String getFinancialSystemScriptDescription() {
        return financialSystemScriptDescription;
    }

    /**
     * Sets the financialSystemScriptDescription attribute.
     * 
     * @param financialSystemScriptDescription The financialSystemScriptDescription to set.
     */
    public void setFinancialSystemScriptDescription(String financialSystemScriptDescription) {
        this.financialSystemScriptDescription = financialSystemScriptDescription;
    }

    /**
     * @return Returns the financialSystemParameters.
     */
    public List getFinancialSystemParameters() {
        return financialSystemParameters;
    }

    /**
     * @param financialSystemParameters The financialSystemParameters to set.
     */
    public void setFinancialSystemParameters(List financialSystemParameters) {
        this.financialSystemParameters = financialSystemParameters;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialSystemScriptName", this.financialSystemScriptName);
        return m;
    }
}