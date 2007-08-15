/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.datadictionary.MaintainableItemDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.lookup.SelectiveReferenceRefresher;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.core.web.ui.Section;

public class MaintenanceUtils {
    /**
     * Returns the field templates defined in the maint dictionary xml files. Field templates are used in multiple value lookups.
     * When doing a MV lookup on a collection, the returned BOs are not necessarily of the same type as the elements of the
     * collection. Therefore, a means of mapping between the fields for the 2 BOs are necessary. The template attribute of
     * &lt;maintainableField&gt;s contained within &lt;maintainableCollection&gt;s tells us this mapping. Example: a
     * &lt;maintainableField name="collectionAttrib" template="lookupBOAttrib"&gt; definition means that when a list of BOs are
     * returned, the lookupBOAttrib value of the looked up BO will be placed into the collectionAttrib value of the BO added to the
     * collection
     * 
     * @param sections the sections of a document
     * @param collectionName the name of a collection. May be a nested collection with indices (e.g. collA[1].collB)
     * @return
     */
    public static Map<String, String> generateMultipleValueLookupBOTemplate(List<MaintainableSectionDefinition> sections, String collectionName) {
        MaintainableCollectionDefinition definition = findMaintainableCollectionDefinition(sections, collectionName);
        if (definition == null) {
            return null;
        }
        Map<String, String> template = null;

        String name = definition.getSourceAttributeName();
        if (name != null) {
            template = new HashMap<String, String>();
            for (MaintainableFieldDefinition maintainableField : definition.getMaintainableFields()) {
                String templateString = maintainableField.getTemplate();
                if (StringUtils.isNotBlank(templateString)) {
                    template.put(maintainableField.getName(), templateString);
                }
            }
        }
        return template;
    }

    /**
     * Finds the MaintainableCollectionDefinition corresponding to the given collection name. For example, if the collection name is
     * "A.B.C", it will attempt to find the MaintainableCollectionDefinition for C that is nested in B that is nested under A. This
     * may not work correctly if there are duplicate collection definitions within the sections
     * 
     * @param sections the sections of a maint doc
     * @param collectionName the name of a collection, relative to the root of the BO being maintained. This value may have index
     *        values (e.g. [1]), but these are ignored.
     * @return
     */
    public static MaintainableCollectionDefinition findMaintainableCollectionDefinition(List<MaintainableSectionDefinition> sections, String collectionName) {
        String[] collectionNameParts = StringUtils.split(collectionName, ".");
        for (MaintainableSectionDefinition section : sections) {
            MaintainableCollectionDefinition collDefinition = findMaintainableCollectionDefinitionHelper(section.getMaintainableItems(), collectionNameParts, 0);
            if (collDefinition != null) {
                return collDefinition;
            }
        }
        return null;
    }

    private static <E extends MaintainableItemDefinition> MaintainableCollectionDefinition findMaintainableCollectionDefinitionHelper(Collection<E> items, String[] collectionNameParts, int collectionNameIndex) {
        if (collectionNameParts.length <= collectionNameIndex) {
            // we've gone too far down the nesting without finding it
            return null;
        }

        // we only care about the coll name, and not the index, since the coll definitions do not include the indexing characters,
        // i.e. [ and ]
        String collectionToFind = StringUtils.substringBefore(collectionNameParts[collectionNameIndex], "[");
        for (MaintainableItemDefinition item : items) {
            if (item instanceof MaintainableCollectionDefinition) {
                MaintainableCollectionDefinition collection = (MaintainableCollectionDefinition) item;
                if (collection.getName().equals(collectionToFind)) {
                    // we found an appropriate coll, now we have to see if we need to recurse even more (more nested collections),
                    // or just return the one we found.
                    if (collectionNameIndex == collectionNameParts.length - 1) {
                        // we're at the last part of the name, so we return
                        return collection;
                    }
                    else {
                        // go deeper
                        return findMaintainableCollectionDefinitionHelper(collection.getMaintainableCollections(), collectionNameParts, collectionNameIndex + 1);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks to see if there has been an override lookup declared for the maintenance field. If so, the override will be used for
     * the quickfinder and lookup utils will not be called. If no override was given, LookupUtils.setFieldQuickfinder will be called
     * to set the system generated quickfinder based on the attributes relationship to the parent business object.
     * 
     * @return Field with quickfinder set if one was found
     */
    public static final Field setFieldQuickfinder(BusinessObject businessObject, String attributeName, MaintainableFieldDefinition maintainableFieldDefinition, Field field, List displayedFieldNames, SelectiveReferenceRefresher srr) {
        if (maintainableFieldDefinition.getOverrideLookupClass() != null && StringUtils.isNotBlank(maintainableFieldDefinition.getOverrideFieldConversions())) {
            field.setQuickFinderClassNameImpl(maintainableFieldDefinition.getOverrideLookupClass().getName());
            field.setFieldConversions(maintainableFieldDefinition.getOverrideFieldConversions());
            return field;
        }

        return LookupUtils.setFieldQuickfinder(businessObject, attributeName, field, displayedFieldNames, srr);
    }

    /**
     * Given a section, returns a comma delimited string of all fields, representing the error keys that exist for a section
     * 
     * @param section a section
     * @return
     */
    public static String generateErrorKeyForSection(Section section) {
        Set<String> fieldPropertyNames = new HashSet<String>();
        addRowsToErrorKeySet(section.getRows(), fieldPropertyNames);

        StringBuilder buf = new StringBuilder();
        Iterator<String> nameIter = fieldPropertyNames.iterator();
        while (nameIter.hasNext()) {
            buf.append(nameIter.next());
            if (nameIter.hasNext()) {
                buf.append(",");
            }
        }

        if (section.getContainedCollectionNames() != null && section.getContainedCollectionNames().size() > 0) {
            buf.append(",");
            
            Iterator<String> collectionIter = section.getContainedCollectionNames().iterator();
            while (collectionIter.hasNext()) {
                buf.append(RiceConstants.MAINTENANCE_NEW_MAINTAINABLE + collectionIter.next());
                if (collectionIter.hasNext()) {
                    buf.append(",");
                }
            }
        }

        return buf.toString();
    }

    /**
     * This method recurses through all the fields of the list of rows and adds each field's property name to the set if it starts
     * with Constants.MAINTENANCE_NEW_MAINTAINABLE
     * 
     * @see RiceConstants#MAINTENANCE_NEW_MAINTAINABLE
     * @param listOfRows
     * @param errorKeys
     */
    protected static void addRowsToErrorKeySet(List<Row> listOfRows, Set<String> errorKeys) {
        if (listOfRows == null) {
            return;
        }
        for (Row row : listOfRows) {
            List<Field> fields = row.getFields();
            if (fields == null) {
                continue;
            }
            for (Field field : fields) {
                String fieldPropertyName = field.getPropertyName();
                if (fieldPropertyName != null && fieldPropertyName.startsWith(RiceConstants.MAINTENANCE_NEW_MAINTAINABLE)) {
                    errorKeys.add(field.getPropertyName());
                }
                addRowsToErrorKeySet(field.getContainerRows(), errorKeys);
            }
        }
    }
}
