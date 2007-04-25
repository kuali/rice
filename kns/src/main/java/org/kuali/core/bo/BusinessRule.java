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
public class BusinessRule extends PersistableBusinessObjectBase {

    private String ruleGroupName;
    private String ruleName;
    private String ruleText;
    private String ruleDescription;
    private String ruleOperatorCode;
    private boolean financialSystemMultipleValueIndicator;
    private boolean financialSystemParameterActiveIndicator;

    BusinessRuleSecurity ruleGroup;

    /**
     * Default constructor.
     */
    public BusinessRule() {

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
     * Gets the ruleName attribute.
     * 
     * @return Returns the ruleName
     * 
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Sets the ruleName attribute.
     * 
     * @param ruleName The ruleName to set.
     * 
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }


    /**
     * Gets the ruleText attribute.
     * 
     * @return Returns the ruleText
     * 
     */
    public String getRuleText() {
        return ruleText;
    }

    /**
     * Sets the ruleText attribute.
     * 
     * @param ruleText The ruleText to set.
     * 
     */
    public void setRuleText(String ruleText) {
        this.ruleText = ruleText;
    }


    /**
     * Gets the ruleDescription attribute.
     * 
     * @return Returns the ruleDescription
     * 
     */
    public String getRuleDescription() {
        return ruleDescription;
    }

    /**
     * Sets the ruleDescription attribute.
     * 
     * @param ruleDescription The ruleDescription to set.
     * 
     */
    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }


    /**
     * Gets the ruleOperatorCode attribute.
     * 
     * @return Returns the ruleOperatorCode
     * 
     */
    public String getRuleOperatorCode() {
        return ruleOperatorCode;
    }

    /**
     * Sets the ruleOperatorCode attribute.
     * 
     * @param ruleOperatorCode The ruleOperatorCode to set.
     * 
     */
    public void setRuleOperatorCode(String ruleOperatorCode) {
        this.ruleOperatorCode = ruleOperatorCode;
    }


    /**
     * Gets the financialSystemMultipleValueIndicator attribute.
     * 
     * @return Returns the financialSystemMultipleValueIndicator
     * 
     */
    public boolean isFinancialSystemMultipleValueIndicator() {
        return financialSystemMultipleValueIndicator;
    }


    /**
     * Sets the financialSystemMultipleValueIndicator attribute.
     * 
     * @param financialSystemMultipleValueIndicator The financialSystemMultipleValueIndicator to set.
     * 
     */
    public void setFinancialSystemMultipleValueIndicator(boolean financialSystemMultipleValueIndicator) {
        this.financialSystemMultipleValueIndicator = financialSystemMultipleValueIndicator;
    }


    /**
     * Gets the financialSystemParameterActiveIndicator attribute.
     * 
     * @return Returns the financialSystemParameterActiveIndicator
     * 
     */
    public boolean isFinancialSystemParameterActiveIndicator() {
        return financialSystemParameterActiveIndicator;
    }


    /**
     * Sets the financialSystemParameterActiveIndicator attribute.
     * 
     * @param financialSystemParameterActiveIndicator The financialSystemParameterActiveIndicator to set.
     * 
     */
    public void setFinancialSystemParameterActiveIndicator(boolean financialSystemParameterActiveIndicator) {
        this.financialSystemParameterActiveIndicator = financialSystemParameterActiveIndicator;
    }


    /**
     * Gets the ruleGroup attribute.
     * 
     * @return Returns the ruleGroup.
     */
    public BusinessRuleSecurity getRuleGroup() {
        return ruleGroup;
    }

    /**
     * Sets the ruleGroup attribute value.
     * 
     * @param ruleGroup The ruleGroup to set.
     * @deprecated
     */
    public void setRuleGroup(BusinessRuleSecurity ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("ruleGroupName", this.ruleGroupName);
        m.put("ruleName", this.ruleName);
        return m;
    }


}
