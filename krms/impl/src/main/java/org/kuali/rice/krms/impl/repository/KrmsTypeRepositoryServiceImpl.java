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
import org.kuali.rice.krms.api.repository.KrmsType;
import org.kuali.rice.krms.api.repository.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.KrmsTypeRepositoryService;

import java.util.*;

public final class KrmsTypeRepositoryServiceImpl implements KrmsTypeRepositoryService {

    private BusinessObjectService businessObjectService;

	/**
	 * This overridden method creates a KrmsType if it does not 
	 * already exist in the repository.
	 * 
	 * @see org.kuali.rice.krms.api.repository.KrmsTypeRepositoryService#createKrmsType(org.kuali.rice.krms.api.repository.KrmsType)
	 */
	@Override
	public void createKrmsType(KrmsType krmsType) {
		if (krmsType == null){
	        throw new IllegalArgumentException("krmsType is null");
		}
		final String nameKey = krmsType.getName();
		final String namespaceKey = krmsType.getNamespace();
		final KrmsType existing = getTypeByNameAndNamespace(nameKey, namespaceKey);
		if (existing != null && existing.getName().equals(nameKey) && existing.getNamespace().equals(namespaceKey)){
            throw new IllegalStateException("the KRMS Type to create already exists: " + krmsType);			
		}
		
		businessObjectService.save(KrmsTypeBo.from(krmsType));
	}

	/**
	 * This overridden method updates an existing KrmsType
	 * 
	 * @see org.kuali.rice.krms.api.repository.KrmsTypeRepositoryService#updateKrmsType(org.kuali.rice.krms.api.repository.KrmsType)
	 */
	@Override
	public void updateKrmsType(KrmsType krmsType) {
        if (krmsType == null) {
            throw new IllegalArgumentException("krmsType is null");
        }
		final String nameKey = krmsType.getName();
		final String namespaceKey = krmsType.getNamespace();
		final KrmsType existing = getTypeByNameAndNamespace(nameKey, namespaceKey);
        if (existing == null) {
            throw new IllegalStateException("the KRMS type does not exist: " + krmsType);
        }
        final KrmsType toUpdate;
        if (!existing.getId().equals(krmsType.getId())){
        	final KrmsType.Builder builder = KrmsType.Builder.create(krmsType);
        	builder.setId(existing.getId());
        	toUpdate = builder.build();
        } else {
        	toUpdate = krmsType;
        }
        
        businessObjectService.save(KrmsTypeBo.from(toUpdate));
	}

    @Override
    public KrmsType getTypeById(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id is blank");
        }

        KrmsTypeBo krmsTypeBo = businessObjectService.findByPrimaryKey(KrmsTypeBo.class, Collections.singletonMap("id", id));

        return KrmsTypeBo.to(krmsTypeBo);
    }

    @Override
    public KrmsType getTypeByNameAndNamespace(final String name, final String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);

        Collection<KrmsTypeBo> typeList = businessObjectService.findMatching(KrmsTypeBo.class, Collections.unmodifiableMap(map));
        if (typeList == null || typeList.isEmpty()) {
            return null;
        } else if (typeList.size() == 1) {
            return KrmsTypeBo.to(typeList.iterator().next());
        } else throw new IllegalStateException("Multiple KRMS types found with same name and namespace");
    }

    @Override
    public List<KrmsType> findAllTypesByNamespace(final String namespace) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("namespace", namespace);
        map.put("active", Boolean.TRUE);

        Collection<KrmsTypeBo> krmsTypeBos = businessObjectService.findMatching(KrmsTypeBo.class, Collections.unmodifiableMap(map));

        return convertListOfBosToImmutables(krmsTypeBos);
    }

    @Override
    public List<KrmsType> findAllTypes() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active", Boolean.TRUE);

        Collection<KrmsTypeBo> krmsTypeBos = businessObjectService.findMatching(KrmsTypeBo.class, Collections.unmodifiableMap(map));
        return convertListOfBosToImmutables(krmsTypeBos);
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
     * Converts a List<KrmsTypeBo> to an Unmodifiable List<KrmsType>
     *
     * @param KrmsTypeBos a mutable List<KrmsTypeBo> to made completely immutable.
     * @return An unmodifiable List<KrmsType>
     */
    List<KrmsType> convertListOfBosToImmutables(final Collection<KrmsTypeBo> krmsTypeBos) {
        ArrayList<KrmsType> krmsTypes = new ArrayList<KrmsType>();
        for (KrmsTypeBo bo : krmsTypeBos) {
            KrmsType krmsType = KrmsTypeBo.to(bo);
            krmsTypes.add(krmsType);
        }
        return Collections.unmodifiableList(krmsTypes);
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.KrmsTypeRepositoryService#createKrmsTypeAttribute(org.kuali.rice.krms.api.repository.KrmsTypeAttribute)
	 */
	@Override
	public void createKrmsTypeAttribute(KrmsTypeAttribute krmsTypeAttribute) {
		// TODO dseibert - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.KrmsTypeRepositoryService#updateKrmsTypeAttribute(org.kuali.rice.krms.api.repository.KrmsTypeAttribute)
	 */
	@Override
	public void updateKrmsTypeAttribute(KrmsTypeAttribute krmsTypeAttribute) {
		// TODO dseibert - THIS METHOD NEEDS JAVADOCS
		
	}
}
