/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.core.util.documentserializer;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.datadictionary.WorkflowProperties;
import org.kuali.core.datadictionary.WorkflowProperty;
import org.kuali.core.datadictionary.WorkflowPropertyGroup;
import org.kuali.core.document.Document;
import org.kuali.core.util.documentserializer.PropertySerializabilityMetadata.PropertySerializability;
import org.kuali.rice.KNSServiceLocator;

/**
 * This implementation of {@link PropertySerializabilityEvaluator} uses the &lt;workflowProperties&gt; defined within the data dictionary
 * for a document.  If the property being serialized corresponds to one of the properties in the data dictionary, then it will be serialized.
 * If a property specified in the data dictionary corresponds to a business object, then all primitives will be serialized of the business object.
 * All primitives of a primitive that has already been serialized will be serialized as well.   If a property specified in the data dictionary corresponds
 * to a collection, then all primitives of all collection elements will be serialized.
 *
 */
public class BusinessObjectPropertySerializibilityEvaluator extends PropertySerializabilityEvaluatorBase implements PropertySerializabilityEvaluator {

    private PropertySerializerTrie serializableProperties;
    
    /**
     * Reads the data dictionary to determine which properties of the document should be serialized.
     * 
     * @see org.kuali.core.util.documentserializer.PropertySerializabilityEvaluator#initializeEvaluator(org.kuali.core.document.Document)
     */
    public void initializeEvaluator(Document document) {
        DataDictionary dictionary = KNSServiceLocator.getDataDictionaryService().getDataDictionary();
        DocumentEntry docEntry = dictionary.getDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
        WorkflowProperties workflowProperties = docEntry.getWorkflowProperties();
        List<WorkflowPropertyGroup> groups = workflowProperties.getWorkflowPropertyGroups();
        
        serializableProperties = new PropertySerializerTrie();
        
        for (WorkflowPropertyGroup group : groups) {
            // the basepath of each workflow property group is serializable
            if (StringUtils.isEmpty(group.getBasePath())) {
                // automatically serialize all primitives of document when the base path is null or empty string
                serializableProperties.addSerializablePropertyName(document.getBasePathToDocumentDuringSerialization());
            }
            else {
               serializableProperties.addSerializablePropertyName(group.getBasePath());
            }
            
            for (WorkflowProperty property : group.getWorkflowProperties()) {
                String fullPath;
                if (StringUtils.isEmpty(group.getBasePath())) {
                    fullPath = document.getBasePathToDocumentDuringSerialization() + "." + property.getPath();
                }
                else {
                    fullPath = group.getBasePath() + "." + property.getPath(); 
                }
                serializableProperties.addSerializablePropertyName(fullPath);
            }
        }
    }

    /**
     * Returns whether a child property of a given containing object should be serialized, based on the metadata provided in the data dictionary.
     * 
     * @see org.kuali.core.util.documentserializer.PropertySerializabilityEvaluator#isPropertySerializable(org.kuali.core.util.documentserializer.DocumentSerializationState, java.lang.Object, java.lang.String, java.lang.Object)
     */
    public boolean isPropertySerializable(DocumentSerializationState state, Object containingObject, String childPropertyName, Object childPropertyValue) {
        boolean allPropertiesMatched = true;
        
        PropertySerializabilityMetadata metadata = serializableProperties.getRootPropertySerializibilityMetadata();
        int i = 0;
        for (; i < state.numPropertyElements(); i++) {
            String nextPropertyName = state.getElementName(i);
            PropertySerializabilityMetadata nextMetadata = metadata.getSerializableChildProperty(nextPropertyName);
            
            if (nextMetadata == null) {
                allPropertiesMatched = false;
                break;
            }
            else {
                // we've found the child... continue searching deeper
                metadata = nextMetadata;
            }
        }
        
        if (allPropertiesMatched) {
            // complete match, so we determine if the child property is serializable
            return evaluateCompleteMatch(state, containingObject, metadata, childPropertyName, childPropertyValue);
        }
        else {
            // we have a partial match, so we have a different algorithm to determine serializibility
            // partial matches can occur for primitives that contains nested primitives.  For example, if we have a member field named
            // "amount" of type KualiDecimal, then the XML will have to look something like <amount><value>100.00</value></amount>.
            // It is likely that "value" isn't specified in the serializability path, so we need to make inferences about whether "value" is 
            // serializable
            return evaluatePartialMatch(state, i, containingObject, metadata, childPropertyName, childPropertyValue);
        }
    }
    
    /**
     * Evaluates whether a property is serializable when all properties in the serialization state have been matched up with the properties
     * defined in the data dictionary.
     * 
     * @param state
     * @param containingObject
     * @param metadata
     * @param childPropertyName
     * @param childPropertyValue
     * @return whether the child property is serializable
     */
    protected boolean evaluateCompleteMatch(DocumentSerializationState state, Object containingObject, PropertySerializabilityMetadata metadata, String childPropertyName, Object childPropertyValue) {
        if (metadata.getPropertySerializability().equals(PropertySerializability.SERIALIZE_OBJECT_AND_ALL_PRIMITIVES)) {
            if (isPrimitiveObject(childPropertyValue)) {
                return true;
            }
        }
        return metadata.getSerializableChildProperty(childPropertyName) != null;
    }
    
    /**
     * Evaluates whether a property is serializable when only some of the properties in the serialization state have been matched up with the 
     * serializable properties specified in the data dictionary.  This often occurs when we determine whether to serialize a primitive of a serialized primitive
     * 
     * @param state
     * @param lastMatchedStateIndex the index of the state parameter that represents the last matched property
     * @param containingObject the object containing the child property
     * @param metadata metadata of the last matched property
     * @param childPropertyName the name of the child property that we are going to determine whether it is serializable
     * @param childPropertyValue the value of the child property that we are going to determine whether it is serializable
     * @return whether the child property is serializable
     */
    protected boolean evaluatePartialMatch(DocumentSerializationState state, int lastMatchedStateIndex, Object containingObject, PropertySerializabilityMetadata metadata, String childPropertyName, Object childPropertyValue) {
        
        if (metadata.getPropertySerializability().equals(PropertySerializability.SERIALIZE_OBJECT_AND_ALL_PRIMITIVES)) {
            return isPrimitiveObject(childPropertyValue);
        }
        return false;
    }
    
    /**
     * Whether the object represents a primitive
     * 
     * @param object
     * @return
     */
    protected boolean isPrimitiveObject(Object object) {
        return PropertyType.PRIMITIVE.equals(determinePropertyType(object));
    }
}
