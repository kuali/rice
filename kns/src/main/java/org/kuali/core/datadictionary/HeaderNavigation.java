/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary;

import java.io.Serializable;

public class HeaderNavigation extends DataDictionaryDefinitionBase implements Serializable {

    private String headerTabMethodToCall;
    private String headerTabNavigateTo;
    private String headerTabDisplayName;
    private HelpDefinition helpDefinition;
    private boolean disabled;
    
    public HeaderNavigation() {};

    public HeaderNavigation(String headerTabNavigateTo, String headerTabDisplayName) {
        this.headerTabNavigateTo = headerTabNavigateTo;
        this.headerTabDisplayName = headerTabDisplayName;
        this.disabled = false;
    }

    /**
     * Gets the navigationKey attribute.
     * 
     * @return Returns the navigationKey.
     */
    public String getHeaderTabMethodToCall() {
        return headerTabMethodToCall;
    }

    /**
     * Sets the navigationKey attribute value.
     * 
     * @param navigationKey The navigationKey to set.
     */
    public void setHeaderTabMethodToCall(String navigationKey) {
        this.headerTabMethodToCall = navigationKey;
    }

    /**
     * Gets the navigationStyle attribute.
     * 
     * @return Returns the navigationStyle.
     */
    public String getHeaderTabDisplayName() {
        return headerTabDisplayName;
    }

    /**
     * Sets the navigationStyle attribute value.
     * 
     * @param navigationStyle The navigationStyle to set.
     */
    public void setHeaderTabDisplayName(String headerTabDisplayName) {
        this.headerTabDisplayName = headerTabDisplayName;
    }

    /**
     * Gets the suffix attribute.
     * 
     * @return Returns the suffix.
     */
    public String getHeaderTabNavigateTo() {
        return headerTabNavigateTo;
    }
    
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * Sets the suffix attribute value.
     * 
     * @param suffix The suffix to set.
     */
    public void setHeaderTabNavigateTo(String suffix) {
        this.headerTabNavigateTo = suffix;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    /**
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        // No real validation to be done here other than perhaps checking to be
        // sure that the security workgroup is a valid workgroup.
    }
}
