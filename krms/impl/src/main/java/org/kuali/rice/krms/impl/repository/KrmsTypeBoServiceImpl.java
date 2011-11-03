/**
 * Copyright 2005-2011 The Kuali Foundation
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


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;

import javax.jws.WebParam;
import java.util.*;

public final class KrmsTypeBoServiceImpl implements KrmsTypeRepositoryService {

    private BusinessObjectService businessObjectService;

	/**
	 * This overridden method creates a KrmsType if it does not 
	 * already exist in the repository.
	 * 
	 * @see org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService#createKrmsType(org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition)
	 */
	@Override
	public KrmsTypeDefinition createKrmsType(KrmsTypeDefinition krmsType) {
		if (krmsType == null){
	        throw new RiceIllegalArgumentException("krmsType is null");
		}
		final String nameKey = krmsType.getName();
		final String namespaceKey = krmsType.getNamespace();
		final KrmsTypeDefinition existing = getTypeByName(namespaceKey, nameKey);
		if (existing != null && existing.getName().equals(nameKey) && existing.getNamespace().equals(namespaceKey)){
            throw new RiceIllegalStateException("the KRMS Type to create already exists: " + krmsType);
		}
		
		KrmsTypeBo bo = (KrmsTypeBo)businessObjectService.save(KrmsTypeBo.from(krmsType));
		
		return KrmsTypeBo.to(bo);
	}

	/**
	 * This overridden method updates an existing KrmsType
	 * 
	 * @see org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService#updateKrmsType(org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition)
	 */
	@Override
	public void updateKrmsType(KrmsTypeDefinition krmsType) {
        if (krmsType == null) {
            throw new RiceIllegalArgumentException("krmsType is null");
        }
		final String idKey = krmsType.getId();
		final KrmsTypeBo existing = businessObjectService.findBySinglePrimaryKey(KrmsTypeBo.class, idKey);
        if (existing == null) {
            throw new RiceIllegalStateException("the KRMS type does not exist: " + krmsType);
        }
        final KrmsTypeDefinition toUpdate;
        if (!existing.getId().equals(krmsType.getId())){
        	final KrmsTypeDefinition.Builder builder = KrmsTypeDefinition.Builder.create(krmsType);
        	builder.setId(existing.getId());
        	toUpdate = builder.build();
        } else {
        	toUpdate = krmsType;
        }
        
        businessObjectService.save(KrmsTypeBo.from(toUpdate));
	}

    @Override
    public KrmsTypeDefinition getTypeById(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("id was a null or blank value");
        }

        KrmsTypeBo krmsTypeBo = businessObjectService.findBySinglePrimaryKey(KrmsTypeBo.class, id);

        return KrmsTypeBo.to(krmsTypeBo);
    }

    @Override
    public KrmsTypeDefinition getTypeByName(final String namespaceCode, final String name) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        if (StringUtils.isBlank(name)) {
            throw new RiceIllegalArgumentException("name was a null or blank value");
        }
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("namespace", namespaceCode);
        map.put("name", name);
        
        KrmsTypeBo myType = businessObjectService.findByPrimaryKey(KrmsTypeBo.class, Collections.unmodifiableMap(map));
        return KrmsTypeBo.to(myType);
    }

    @Override
    public List<KrmsTypeDefinition> findAllTypesByNamespace(final String namespaceCode) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("namespace", namespaceCode);
        map.put("active", Boolean.TRUE);

        Collection<KrmsTypeBo> krmsTypeBos = businessObjectService.findMatching(KrmsTypeBo.class, Collections.unmodifiableMap(map));

        return convertListOfBosToImmutables(krmsTypeBos);
    }

    @Override
    public List<KrmsTypeDefinition> findAllTypes() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active", Boolean.TRUE);

        Collection<KrmsTypeBo> krmsTypeBos = businessObjectService.findMatching(KrmsTypeBo.class, Collections.unmodifiableMap(map));
        return convertListOfBosToImmutables(krmsTypeBos);
    }

    @Override
    public KrmsAttributeDefinition getAttributeDefinitionById(String attributeDefinitionId) {
        if (StringUtils.isBlank(attributeDefinitionId)) {
            throw new RiceIllegalArgumentException("attributeDefinitionId was a null or blank value");
        }
        KrmsAttributeDefinitionBo krmsAttributeDefinitionBo = businessObjectService.findBySinglePrimaryKey(KrmsAttributeDefinitionBo.class, attributeDefinitionId);
        return KrmsAttributeDefinitionBo.to(krmsAttributeDefinitionBo);
    }

    @Override
    public KrmsAttributeDefinition getAttributeDefinitionByName(@WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "name") String name) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        if (StringUtils.isBlank(name)) {
            throw new RiceIllegalArgumentException("name was a null or blank value");
        }
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("name", name);
        criteria.put("namespace", namespaceCode);

        Collection<KrmsAttributeDefinitionBo> attributeDefinitionBos = businessObjectService.findMatching(KrmsAttributeDefinitionBo.class, criteria);
        if (CollectionUtils.isEmpty(attributeDefinitionBos)) {
            return null;
        }
        return KrmsAttributeDefinitionBo.to(attributeDefinitionBos.iterator().next());
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
     * @param krmsTypeBos a mutable List<KrmsTypeBo> to made completely immutable.
     * @return An unmodifiable List<KrmsType>
     */
    protected List<KrmsTypeDefinition> convertListOfBosToImmutables(final Collection<KrmsTypeBo> krmsTypeBos) {
        ArrayList<KrmsTypeDefinition> krmsTypes = new ArrayList<KrmsTypeDefinition>();
        for (KrmsTypeBo bo : krmsTypeBos) {
            KrmsTypeDefinition krmsType = KrmsTypeBo.to(bo);
            krmsTypes.add(krmsType);
        }
        return Collections.unmodifiableList(krmsTypes);
    }

}
