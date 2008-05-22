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

package org.kuali.core.datadictionary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Contains section-related information relating to the parent MaintainableDocument.
 * 
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 * 
 * 
 */
public class MaintainableSectionDefinition extends DataDictionaryDefinitionBase {

    private String title;

    private List<MaintainableItemDefinition> maintainableItems = new ArrayList<MaintainableItemDefinition>();
    
    private boolean hidden = false;

    public MaintainableSectionDefinition() {}

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Default the ID to the title for now.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinitionBase#getId()
     */
    @Override
    public String getId() {
        if ( super.getId() == null ) {
            return title;
        }
        return super.getId();
    }


    /**
     * Sets title to the given value.
     * 
     * @param title
     * @throws IllegalArgumentException if the given title is blank
     */
    public void setTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("invalid (blank) title");
        }

        this.title = title;
    }

    /**
     * @return Collection of all MaintainableFieldDefinitions associated with this MaintainableSection, in the order in which they
     *         were added
     */
    public List<MaintainableItemDefinition> getMaintainableItems() {
        return maintainableItems;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        for ( MaintainableItemDefinition maintainableItem : maintainableItems ) {
            maintainableItem.completeValidation(rootBusinessObjectClass, null);
        }
    }

    public String toString() {
        return "MaintainableSectionDefinition '" + getTitle() + "'";
    }


    public boolean isHidden() {
        return this.hidden;
    }


    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    public void setMaintainableItems(List<MaintainableItemDefinition> maintainableItems) {
        for ( MaintainableItemDefinition maintainableItem : maintainableItems ) {
            if (maintainableItem == null) {
                throw new IllegalArgumentException("invalid (null) maintainableItem");
            }
        }
        
        this.maintainableItems = maintainableItems;
    }
}