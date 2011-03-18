/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.datadictionary.exporter;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSubSectionHeaderDefinition;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;

/**
 * MaintenanceDocumentEntryMapper
 * 
 * 
 */
public class MaintenanceDocumentEntryMapper extends DocumentEntryMapper {

    /**
     * Default constructor
     */
    public MaintenanceDocumentEntryMapper() {
    }


    /**
     * @param entry
     * @return Map containing a String- and Map-based representation of the given entry
     */
    public ExportMap mapEntry(MaintenanceDocumentEntry entry) {
        ExportMap entryMap = super.mapEntry(entry);

        // simple properties
        entryMap.set("maintenanceDocument", "true");
        entryMap.set("businessObjectClass", entry.getBusinessObjectClass().getName());
        entryMap.set("maintainableClass", entry.getMaintainableClass().getName());

        // complex properties
        entryMap.set(buildMaintainableSectionsMap(entry));

        return entryMap;
    }

    private ExportMap buildMaintainableSectionsMap(MaintenanceDocumentEntry entry) {
        ExportMap maintainableSectionsMap = new ExportMap("maintainableSections");

        int index = 0;
        for (Iterator i = entry.getMaintainableSections().iterator(); i.hasNext();) {
            MaintainableSectionDefinition section = (MaintainableSectionDefinition) i.next();

            maintainableSectionsMap.set(buildMaintainableSectionMap(section, index++));
        }

        return maintainableSectionsMap;
    }

    private ExportMap buildMaintainableSectionMap(MaintainableSectionDefinition section, int index) {
        ExportMap sectionMap = new ExportMap(Integer.toString(index));

        sectionMap.set("index", Integer.toString(index));
        sectionMap.set("title", section.getTitle());

        sectionMap.set(buildMaintainableItemsMap(section));

        return sectionMap;
    }

    private ExportMap buildMaintainableItemsMap(MaintainableSectionDefinition section) {
        ExportMap itemsMap = new ExportMap("maintainableItems");

        for (Iterator i = section.getMaintainableItems().iterator(); i.hasNext();) {
            MaintainableItemDefinition item = (MaintainableItemDefinition) i.next();
            itemsMap.set(buildMaintainableItemMap(item));
        }

        return itemsMap;
    }

    private ExportMap buildMaintainableItemMap(MaintainableItemDefinition item) {
        ExportMap itemMap = new ExportMap(item.getName());

        if (item instanceof MaintainableFieldDefinition) {
            MaintainableFieldDefinition field = (MaintainableFieldDefinition) item;

            itemMap.set("field", "true");
            itemMap.set("name", field.getName());
            itemMap.set("required", Boolean.toString(field.isRequired()));
			if (StringUtils.isNotBlank(field.getAlternateDisplayAttributeName())) {
				itemMap.set("alternateDisplayAttributeName", field.getAlternateDisplayAttributeName());
			}
			if (StringUtils.isNotBlank(field.getAdditionalDisplayAttributeName())) {
				itemMap.set("additionalDisplayAttributeName", field.getAdditionalDisplayAttributeName());
			}
        }
        else if (item instanceof MaintainableCollectionDefinition) {
            MaintainableCollectionDefinition collection = (MaintainableCollectionDefinition) item;

            itemMap.set("collection", "true");
            itemMap.set("name", collection.getName());
            itemMap.set("businessObjectClass", collection.getBusinessObjectClass().getName());
        }
        else if (item instanceof MaintainableSubSectionHeaderDefinition) {
            MaintainableSubSectionHeaderDefinition subSectionHeader = (MaintainableSubSectionHeaderDefinition) item;
            itemMap.set("name", subSectionHeader.getName());
        }
        else {
            throw new IllegalStateException("unable to create itemMap for unknown MaintainableItem subclass '" + item.getClass().getName() + "'");
        }

        return itemMap;
    }
}
