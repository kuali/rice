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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;

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

    private Map<String, MaintainableItemDefinition> maintainableItems;

    public MaintainableSectionDefinition() {
        LOG.debug("creating new LookupDefinition");

        this.maintainableItems = new LinkedHashMap();
    }


    /**
     * @return title
     */
    public String getTitle() {
        return title;
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
        LOG.debug("calling setTitle '" + title + "'");

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
        LOG.debug("calling addMaintainableItem for item '" + maintainableItem.getName() + "'");

        String itemName = maintainableItem.getName();
        if (this.maintainableItems.containsKey(itemName)) {
            throw new DuplicateEntryException("duplicate itemName entry for item '" + itemName + "'");
        }

        this.maintainableItems.put(itemName, maintainableItem);
    }

    /**
     * @return List of attributeNames of all MaintainableFieldDefinitions associated with this MaintainableSection, in the order in
     *         which they were added
     */
    public List getMaintainableItemNames() {
        List itemNames = new ArrayList();
        itemNames.addAll(this.maintainableItems.keySet());

        return Collections.unmodifiableList(itemNames);
    }

    /**
     * @return Collection of all MaintainableFieldDefinitions associated with this MaintainableSection, in the order in which they
     *         were added
     */
    public Collection<MaintainableItemDefinition> getMaintainableItems() {
        return Collections.unmodifiableCollection(this.maintainableItems.values());
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        for (Iterator i = maintainableItems.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            MaintainableItemDefinition maintainableItem = (MaintainableItemDefinition) e.getValue();
            maintainableItem.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
        }
    }

    public String toString() {
        return "MaintainableSectionDefinition '" + getTitle() + "'";
    }
}