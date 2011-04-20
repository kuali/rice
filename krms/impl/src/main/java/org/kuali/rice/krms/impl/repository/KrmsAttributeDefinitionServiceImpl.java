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


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;

import java.util.*;

public final class KrmsAttributeDefinitionServiceImpl implements KrmsAttributeDefinitionService {

    private BusinessObjectService businessObjectService;

	@Override
	public void createAttributeDefinition(KrmsAttributeDefinition attributeDefinition) {
		if (attributeDefinition == null){
	        throw new IllegalArgumentException("attributeDefinition is null");
		}
		final String nameKey = attributeDefinition.getName();
		final String namespaceKey = attributeDefinition.getNamespace();
		final KrmsAttributeDefinition existing = getAttributeDefinitionByNameAndNamespace(nameKey, namespaceKey);
		if (existing != null && existing.getName().equals(nameKey) && existing.getNamespace().equals(namespaceKey)){
            throw new IllegalStateException("the krms attribute definition to create already exists: " + attributeDefinition);			
		}
		businessObjectService.save(KrmsAttributeDefinitionBo.from(attributeDefinition));
	}

	@Override
	public void updateAttributeDefinition(KrmsAttributeDefinition attributeDefinition) {
		if (attributeDefinition == null){
	        throw new IllegalArgumentException("attributeDefinition is null");
		}
		final String nameKey = attributeDefinition.getName();
		final String namespaceKey = attributeDefinition.getNamespace();
		final KrmsAttributeDefinition existing = getAttributeDefinitionByNameAndNamespace(nameKey, namespaceKey);
		if (existing == null){
            throw new IllegalStateException("the krms attribute definition does not exist: " + attributeDefinition);			
		}
		final KrmsAttributeDefinition toUpdate;
		if (!existing.getId().equals(attributeDefinition.getId())){
			final KrmsAttributeDefinition.Builder builder = KrmsAttributeDefinition.Builder.create(attributeDefinition);
			builder.setId(existing.getId());
			toUpdate = builder.build();
		} else {
			toUpdate = attributeDefinition;
		}
		
		businessObjectService.save(KrmsAttributeDefinitionBo.from(toUpdate));
	}

	@Override
    public KrmsAttributeDefinition getAttributeDefinitionById(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id is blank");
        }
        KrmsAttributeDefinitionBo bo = businessObjectService.findBySinglePrimaryKey(KrmsAttributeDefinitionBo.class, id);
        return KrmsAttributeDefinitionBo.to(bo);
    }

    @Override
    public KrmsAttributeDefinition getAttributeDefinitionByNameAndNamespace(final String name, final String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is blank");
        }
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);
        Collection<KrmsAttributeDefinitionBo> definitionList = businessObjectService.findMatching(KrmsAttributeDefinitionBo.class, Collections.unmodifiableMap(map));
        if (definitionList == null || definitionList.isEmpty()) {
            return null;
        } else if (definitionList.size() == 1) {
            return KrmsAttributeDefinitionBo.to(definitionList.iterator().next());
        } else throw new IllegalStateException("Multiple KkrmsAttributeDefinitions found with same name and namespace");
    }

    @Override
    public List<KrmsAttributeDefinition> findAttributeDefinitionsByNamespace(final String namespace) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("namespace", namespace);
        map.put("active", Boolean.TRUE);
        Collection<KrmsAttributeDefinitionBo> krmsAttributeDefinitionBos = businessObjectService.findMatching(KrmsAttributeDefinitionBo.class, Collections.unmodifiableMap(map));
        return convertListOfBosToImmutables(krmsAttributeDefinitionBos);
    }

    @Override
    public List<KrmsAttributeDefinition> findAllAttributeDefinitions() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active", Boolean.TRUE);
        
        Collection<KrmsAttributeDefinitionBo> krmsAttributeDefinitionBos = businessObjectService.findMatching(KrmsAttributeDefinitionBo.class, Collections.unmodifiableMap(map));
        return convertListOfBosToImmutables(krmsAttributeDefinitionBos);
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Converts a List<KrmsAttributeDefinitionBo> to an Unmodifiable List<KrmsAttributeDefinition>
     *
     * @param krmsAttributeDefinitionBos a mutable List<KrmsAttributeDefinitionBo> to made completely immutable.
     * @return An unmodifiable List<KrmsAttributeDefinition>
     */
    List<KrmsAttributeDefinition> convertListOfBosToImmutables(final Collection<KrmsAttributeDefinitionBo> krmsAttributeDefinitionBos) {
        ArrayList<KrmsAttributeDefinition> krmsAttributeDefinitions = new ArrayList<KrmsAttributeDefinition>();
        for (KrmsAttributeDefinitionBo bo : krmsAttributeDefinitionBos) {
            KrmsAttributeDefinition krmsAttributeDefinition = KrmsAttributeDefinitionBo.to(bo);
            krmsAttributeDefinitions.add(krmsAttributeDefinition);
        }
        return Collections.unmodifiableList(krmsAttributeDefinitions);
    }

}
