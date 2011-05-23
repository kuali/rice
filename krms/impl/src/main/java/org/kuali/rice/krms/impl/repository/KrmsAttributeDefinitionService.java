/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.krms.impl.repository;

import java.util.List;
import java.util.Map;

import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;


public interface KrmsAttributeDefinitionService {

	Map<String,String> convertAttributeKeys(Map<String,String> attributesByName, String namespace);
	String getKrmsAttributeId( String attributeName, String namespace);
	void clearCache();
	
    /**
     * This will create a {@link KrmsAttributeDefinition} exactly like the parameter passed in.
     *
     * @param attributeDefinition - KrmsAttributeDefinition
     * @throws IllegalArgumentException if the attribute definition is null
     * @throws IllegalStateException if the attribute definition already exists in the system
     */
	KrmsAttributeDefinition createAttributeDefinition(KrmsAttributeDefinition attributeDefinition);

    /**
     * This will update a {@link KrmsAttributeDefinition}.
     *
     *
     * @param attributeDefinition - KrmsAttributeDefinition
     * @throws IllegalArgumentException if the attribute definition is null
     * @throws IllegalStateException if the attribute definition does not exist in the system
     */
    void updateAttributeDefinition(KrmsAttributeDefinition attributeDefinition);

    /**
     * Lookup a KrmsAttributeDefinition based on the given id.
     *
     * @param id the given KrmsAttributeDefinition id
     * @return a KrmsAttributeDefinition object with the given id.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    KrmsAttributeDefinition getAttributeDefinitionById(String id);

    /**
     * Get a KrmsAttributeDefinition object based on name and namespace
     *
     * @param name the given name
     * @param namespace the given type namespace
     * @return A KrmsAttributeDefinition object with the given namespace and name if one with that name and namespace
     *         exists.  Otherwise, null is returned.
     * @throws IllegalStateException if multiple KrmsAttributeDefinitions exist with the same name and namespace
     */
    KrmsAttributeDefinition getAttributeDefinitionByNameAndNamespace(String name, String namespace);

   /**
     * Returns all KrmsAttributeDefinition that for a given namespace.
     *
     * @return all KrmsAttributeDefinition for a namespace
     */
    List<KrmsAttributeDefinition> findAttributeDefinitionsByNamespace(String namespace);

    /**
     * Returns all KrmsAttributeDefinitions
     *
     * @return all KrmsAttributeDefinitions
     */
    List<KrmsAttributeDefinition> findAllAttributeDefinitions();
}
