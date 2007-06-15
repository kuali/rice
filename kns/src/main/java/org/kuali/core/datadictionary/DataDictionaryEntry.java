/*
 * Copyright 2005-2006 The Kuali Foundation.
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
import java.util.Map;


/**
 * Defines methods common to all DataDictionaryDefinition types.
 * 
 * 
 */
public interface DataDictionaryEntry extends Serializable {
    /**
     * @return String used as a globally-unique key for this entry's jstl-exported version
     */
    public String getJstlKey();

    /**
     * Kicks off complete entry-wide validation which couldn't be done earlier.
     * 
     * @throws org.kuali.core.datadictionary.exception.CompletionException if a problem arises during validation-completion
     */
    public void completeValidation(ValidationCompletionUtils validationCompletionUtils);

    /**
     * @param attributeName
     * @return AttributeDefinition with the given name, or null if none with that name exists
     */
    public AttributeDefinition getAttributeDefinition(String attributeName);

    /**
     * Returns the full class name of the underlying object.
     */
    public String getFullClassName();
    
    /**
     * @return a Map containing all RelationshipDefinitions associated with this BusinessObjectEntry, indexed by relationshipName
     */
    public Map<String, RelationshipDefinition> getRelationships();
}
