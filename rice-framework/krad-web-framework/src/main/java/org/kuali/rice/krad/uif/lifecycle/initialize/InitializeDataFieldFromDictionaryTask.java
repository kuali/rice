/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.lifecycle.initialize;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Performs initialization on data fields based on attributes found in the data dictionary.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InitializeDataFieldFromDictionaryTask extends ViewLifecycleTaskBase<DataField> {

    /**
     * Constructor.
     * 
     * @param phase The initialize phase for the data field.
     */
    public InitializeDataFieldFromDictionaryTask() {
        super(DataField.class);
    }

    /**
     * Sets properties of the <code>InputField</code> (if blank) to the corresponding attribute
     * entry in the data dictionary
     * 
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        DataField field = (DataField) getElementState().getElement();

        AttributeDefinition attributeDefinition = null;

        String dictionaryAttributeName = field.getDictionaryAttributeName();
        String dictionaryObjectEntry = field.getDictionaryObjectEntry();

        // if entry given but not attribute name, use field name as attribute name
        if (StringUtils.isNotBlank(dictionaryObjectEntry) && StringUtils.isBlank(dictionaryAttributeName)) {
            dictionaryAttributeName = field.getPropertyName();
        }

        // if dictionary entry and attribute set, attempt to find definition
        if (StringUtils.isNotBlank(dictionaryAttributeName) && StringUtils.isNotBlank(dictionaryObjectEntry)) {
            attributeDefinition = KRADServiceLocatorWeb.getDataDictionaryService()
                    .getAttributeDefinition(dictionaryObjectEntry, dictionaryAttributeName);
        }

        // if definition not found, recurse through path
        if (attributeDefinition == null) {
            BindingInfo fieldBindingInfo = field.getBindingInfo();
            String collectionPath = fieldBindingInfo.getCollectionPath();

            String propertyPath;
            if (StringUtils.isNotBlank(collectionPath)) {
                StringBuilder propertyPathBuilder = new StringBuilder();

                String bindingObjectPath = fieldBindingInfo.getBindingObjectPath();
                if (StringUtils.isNotBlank(bindingObjectPath)) {
                    propertyPathBuilder.append(bindingObjectPath).append('.');
                }

                propertyPathBuilder.append(collectionPath).append('.');

                String bindByNamePrefix = fieldBindingInfo.getBindByNamePrefix();
                if (StringUtils.isNotBlank(bindByNamePrefix)) {
                    propertyPathBuilder.append(bindByNamePrefix).append('.');
                }

                propertyPathBuilder.append(fieldBindingInfo.getBindingName());
                propertyPath = propertyPathBuilder.toString();

            } else {
                propertyPath = field.getBindingInfo().getBindingPath();
            }

            attributeDefinition = findNestedDictionaryAttribute(propertyPath);
        }

        // if a definition was found, initialize field from definition
        if (attributeDefinition != null) {
            field.copyFromAttributeDefinition(attributeDefinition);
        }

        // if control still null, assign default
        if (field instanceof InputField) {
            InputField inputField = (InputField) field;
            if (inputField.getControl() == null) {
                inputField.setControl(ComponentFactory.getTextControl());
            }
        }
    }

    /**
     * Determines the name of a data dictionary entry based on the portion of the path leading up to
     * the attribute name.
     * 
     * <p>
     * The property path passed in is checked first against
     * {@link View#getObjectPathToConcreteClassMapping()} for a full or partial match. If no match
     * is found then property type relative to the model involved in the current view lifecycle is
     * returned, where applicable.
     * </p>
     * 
     * @param dictionaryEntryPrefix Portion of a property path referring to the entry that has the
     *        attribute.
     * @return The name of the dictionary entry indicated by the property path.
     */
    private String getDictionaryEntryName(String dictionaryEntryPrefix) {
        if (StringUtils.isEmpty(dictionaryEntryPrefix)) {
            return dictionaryEntryPrefix;
        }
        
        Map<String, Class<?>> modelClasses = ViewLifecycle.getView().getObjectPathToConcreteClassMapping();
        Class<?> dictionaryModelClass = modelClasses.get(dictionaryEntryPrefix);

        // full match
        if (dictionaryModelClass != null) {
            return dictionaryModelClass.getName();
        }

        // in case of partial match, holds the class that matched and the
        // property so we can get by reflection
        Class<?> modelClass = null;
        String modelProperty = dictionaryEntryPrefix;

        int bestMatchLength = 0;
        int modelClassPathLength = dictionaryEntryPrefix.length();

        // check if property path matches one of the modelClass entries
        synchronized (modelClasses) {
            // synchronizing on modelClasses prevents ConcurrentModificationException during
            // asynchronous lifecycle processing
            for (Entry<String, Class<?>> modelClassEntry : modelClasses.entrySet()) {
                String path = modelClassEntry.getKey();
                int pathlen = path.length();

                if (dictionaryEntryPrefix.startsWith(path) && pathlen > bestMatchLength
                        && modelClassPathLength > pathlen && dictionaryEntryPrefix.charAt(pathlen) == '.') {
                    bestMatchLength = pathlen;
                    modelClass = modelClassEntry.getValue();
                    modelProperty = dictionaryEntryPrefix.substring(pathlen + 1);
                }
            }
        }

        if (modelClass != null) {
            // if a partial match was found, look up the property type based on matched model class
            dictionaryModelClass = ObjectPropertyUtils.getPropertyType(modelClass, modelProperty);
        }

        if (dictionaryModelClass == null) {
            // If no full or partial match, look up based on the model directly
            dictionaryModelClass = ObjectPropertyUtils.getPropertyType(ViewLifecycle.getModel(), dictionaryEntryPrefix);
        }

        return dictionaryModelClass == null ? null : dictionaryModelClass.getSimpleName();
    }

    /**
     * Recursively drills down the property path (if nested) to find an AttributeDefinition, the
     * first attribute definition found will be returned
     * 
     * <p>
     * e.g. suppose parentPath is 'document' and propertyPath is 'account.subAccount.name', first
     * the property type for document will be retrieved using the view metadata and used as the
     * dictionary entry, with the propertyPath as the dictionary attribute, if an attribute
     * definition exists it will be returned. Else, the first part of the property path is added to
     * the parent, making the parentPath 'document.account' and the propertyPath 'subAccount.name',
     * the method is then called again to perform the process with those parameters. The recursion
     * continues until an attribute field is found, or the propertyPath is no longer nested
     * </p>
     * 
     * @param propertyPath path of the property to use as dictionary attribute and to drill down on
     * @return AttributeDefinition if found, or Null
     */
    protected AttributeDefinition findNestedDictionaryAttribute(String propertyPath) {
        DataField field = (DataField) getElementState().getElement();

        String fieldBindingPrefix = null;
        String dictionaryAttributePath = propertyPath;

        if (field.getBindingInfo().isBindToMap()) {
            fieldBindingPrefix = "";
            if (!field.getBindingInfo().isBindToForm() && StringUtils.isNotBlank(
                    field.getBindingInfo().getBindingObjectPath())) {
                fieldBindingPrefix = field.getBindingInfo().getBindingObjectPath();
            }
            if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
                if (StringUtils.isNotBlank(fieldBindingPrefix)) {
                    fieldBindingPrefix += "." + field.getBindingInfo().getBindByNamePrefix();
                } else {
                    fieldBindingPrefix = field.getBindingInfo().getBindByNamePrefix();
                }
            }

            dictionaryAttributePath = field.getBindingInfo().getBindingName();
        }

        if (StringUtils.isEmpty(dictionaryAttributePath)) {
            return null;
        }

        if (StringUtils.startsWith(dictionaryAttributePath, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
            dictionaryAttributePath = StringUtils.substringAfter(dictionaryAttributePath, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
        }
        
        DataDictionaryService ddService = KRADServiceLocatorWeb.getDataDictionaryService();
        
        String dictionaryAttributeName = ObjectPropertyUtils.getCanonicalPath(dictionaryAttributePath);
        String dictionaryEntryPrefix = fieldBindingPrefix;
        
        AttributeDefinition attribute = null;
        String dictionaryEntryName = null;
        
        int i = dictionaryAttributeName.indexOf('.');
        while (attribute == null && i != -1) {
            
            if (dictionaryEntryPrefix != null) {
                dictionaryEntryName = getDictionaryEntryName(dictionaryEntryPrefix);
                dictionaryEntryPrefix += '.' + dictionaryAttributeName.substring(0, i);
            } else {
                dictionaryEntryName = null;
                dictionaryEntryPrefix = dictionaryAttributeName.substring(0, i);
            }

            if (dictionaryEntryName != null) {
                attribute = ddService.getAttributeDefinition(dictionaryEntryName, dictionaryAttributeName);
            }
            
            if (attribute == null) {
                dictionaryAttributeName = dictionaryAttributeName.substring(i+1);
                i = dictionaryAttributeName.indexOf('.');
            }
        }
        
        if (attribute == null && dictionaryEntryPrefix != null) {
            dictionaryEntryName = getDictionaryEntryName(dictionaryEntryPrefix);
            
            if (dictionaryEntryName != null) {
                attribute = ddService.getAttributeDefinition(dictionaryEntryName, dictionaryAttributeName);
            }
        }
        
        // if a definition was found, update the fields dictionary properties
        if (attribute != null) {
            field.setDictionaryObjectEntry(dictionaryEntryName);
            field.setDictionaryAttributeName(dictionaryAttributeName);
        }

        return attribute;
    }

}
