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

package org.kuali.core.datadictionary.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains section-related information relating to the parent MaintainableDocument.
 * 
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 * 
 * 
 */
public class MaintainableSectionDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(MaintainableSectionDefinition.class);

    private String title;

    private List<MaintainableItemDefinition> maintainableItems = new ArrayList<MaintainableItemDefinition>();
    private Map<String, MaintainableItemDefinition> maintainableItemMap = new HashMap<String, MaintainableItemDefinition>();
    
    private boolean hidden = false;

    public MaintainableSectionDefinition() {
        LOG.debug("creating new LookupDefinition");
    }


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
     * @param maintainableField
     * @throws IllegalArgumentException if the given maintainableField is null
     */
    public void addMaintainableItem(MaintainableItemDefinition maintainableItem) {
        if (maintainableItem == null) {
            throw new IllegalArgumentException("invalid (null) maintainableItem");
        }
        
        String itemName = maintainableItem.getName();

        maintainableItems.add(maintainableItem);
        maintainableItemMap.put(itemName, maintainableItem);
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
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        for ( MaintainableItemDefinition maintainableItem : maintainableItems ) {
            maintainableItem.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
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
        this.maintainableItems = maintainableItems;
    }

}