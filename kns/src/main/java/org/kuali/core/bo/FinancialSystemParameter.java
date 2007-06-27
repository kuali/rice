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

/**
 * This class represents a financial system parameter which has a text value associated with it. This text value can be seen as a
 * single value or can be seen as a list of values, with each value being delimited by a semi-colon. The parameter name and the
 * script name make up the primary key for this business object.
 * 
 * 
 */
public class FinancialSystemParameter extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 4874830226334867797L;
    private String moduleCode;
    private String financialSystemScriptName;
    private String financialSystemParameterName;
    private String financialSystemParameterText;
    private String financialSystemParameterDescription;
    private boolean financialSystemMultipleValueIndicator;

    private FinancialSystemParameterSecurity financialSystemParameterSecurity;

    /**
     * Default no-arg constructor.
     */
    public FinancialSystemParameter() {

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
     * Gets the financialSystemParameterName attribute.
     * 
     * @return Returns the financialSystemParameterName
     */
    public String getFinancialSystemParameterName() {
        return financialSystemParameterName;
    }

    /**
     * Sets the financialSystemParameterSecurity object.
     * 
     * @return
     */
    public FinancialSystemParameterSecurity getFinancialSystemParameterSecurity() {
        if (null == financialSystemParameterSecurity) {
            this.financialSystemParameterSecurity = new FinancialSystemParameterSecurity();
        }
        return financialSystemParameterSecurity;
    }

    /**
     * Gets the financialSystemParameterSecurity object.
     * 
     * @param financialSystemParameterSecurity
     */
    public void setFinancialSystemParameterSecurity(FinancialSystemParameterSecurity financialSystemParameterSecurity) {
        this.financialSystemParameterSecurity = financialSystemParameterSecurity;
    }

    /**
     * Sets the financialSystemParameterName attribute.
     * 
     * @param financialSystemParameterName The financialSystemParameterName to set.
     */
    public void setFinancialSystemParameterName(String financialSystemParameterName) {
        this.financialSystemParameterName = financialSystemParameterName;
    }

    /**
     * Gets the financialSystemParameterText attribute.
     * 
     * @return Returns the financialSystemParameterText
     */
    public String getFinancialSystemParameterText() {
        return financialSystemParameterText;
    }

    /**
     * Sets the financialSystemParameterText attribute.
     * 
     * @param financialSystemParameterText The financialSystemParameterText to set.
     */
    public void setFinancialSystemParameterText(String financialSystemParameterText) {
        this.financialSystemParameterText = financialSystemParameterText;
    }

    /**
     * Gets the financialSystemParameterDescription attribute.
     * 
     * @return Returns the financialSystemParameterDescription
     */
    public String getFinancialSystemParameterDescription() {
        return financialSystemParameterDescription;
    }

    /**
     * Sets the financialSystemParameterDescription attribute.
     * 
     * @param financialSystemParameterDescription The financialSystemParameterDescription to set.
     */
    public void setFinancialSystemParameterDescription(String financialSystemParameterDescription) {
        this.financialSystemParameterDescription = financialSystemParameterDescription;
    }

    /**
     * Returns whether the parameter should have a list of values or a singe value associated with it.
     * 
     * @return boolean
     */
    public boolean isFinancialSystemMultipleValueIndicator() {
        return financialSystemMultipleValueIndicator;
    }

    /**
     * Sets the financialSystemMultipleValueIndicator attribute.
     * 
     * @param financialSystemMultipleValueIndicator The financialSystemMultipleValueIndicator to set.
     */
    public void setFinancialSystemMultipleValueIndicator(boolean financialSystemMultipleValueIndicator) {
        this.financialSystemMultipleValueIndicator = financialSystemMultipleValueIndicator;
    }

    public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	/**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialSystemScriptName", this.financialSystemScriptName);
        m.put("financialSystemParameterName", this.financialSystemParameterName);
        return m;
    }
}