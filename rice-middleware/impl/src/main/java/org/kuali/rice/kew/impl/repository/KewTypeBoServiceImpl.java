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
package org.kuali.rice.kew.impl.repository;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.kew.api.repository.type.KewTypeAttribute;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService;
import org.kuali.rice.kew.impl.type.KewTypeAttributeBo;
import org.kuali.rice.kew.impl.type.KewTypeBo;
import org.kuali.rice.krad.data.DataObjectService;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

public final class KewTypeBoServiceImpl implements KewTypeRepositoryService {

    private DataObjectService dataObjectService;

    /**
     * This overridden method creates a KewType if it does not already exist in the repository.
     *
     * @see org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService#createKewType(org.kuali.rice.kew.api.repository.type.KewTypeDefinition)
     */
    @Override
    public KewTypeDefinition createKewType(KewTypeDefinition kewType) {
        if (kewType == null){
            throw new RiceIllegalArgumentException("kewType is null");
        }
        final String nameKey = kewType.getName();
        final String namespaceKey = kewType.getNamespace();
        final KewTypeDefinition existing = getTypeByNameAndNamespace(nameKey, namespaceKey);
        if (existing != null && existing.getName().equals(nameKey) && existing.getNamespace().equals(namespaceKey)){
            throw new RiceIllegalStateException("The KEW Type to create already exists: " + kewType);
        }

        KewTypeBo bo = dataObjectService.save(KewTypeBo.from(kewType));

        return KewTypeBo.to(bo);
    }

    /**
     * This overridden method updates an existing KewType
     *
     * @see org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService#updateKewType(org.kuali.rice.kew.api.repository.type.KewTypeDefinition)
     */
    @Override
    public void updateKewType(KewTypeDefinition kewType) {
        if (kewType == null) {
            throw new RiceIllegalArgumentException("kewType is null");
        }
        final String idKey = kewType.getId();
        final KewTypeBo existing = dataObjectService.find(KewTypeBo.class, idKey);
        if (existing == null) {
            throw new RiceIllegalStateException("The KEW type does not exist: " + kewType);
        }
        final KewTypeDefinition toUpdate;
        if (!existing.getId().equals(kewType.getId())){
            final KewTypeDefinition.Builder builder = KewTypeDefinition.Builder.create(kewType);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = kewType;
        }

        dataObjectService.save(KewTypeBo.from(toUpdate));
    }

    @Override
    public KewTypeDefinition getTypeById(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("id is blank");
        }

        KewTypeBo kewTypeBo = dataObjectService.find(KewTypeBo.class, id);

        return KewTypeBo.to(kewTypeBo);
    }

    @Override
    public KewTypeDefinition getTypeByNameAndNamespace(final String name, final String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new RiceIllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new RiceIllegalArgumentException("namespace is blank");
        }

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("name", name), equal("namespace", namespace));
        List<KewTypeBo> myTypes = dataObjectService.findMatching(KewTypeBo.class, criteria.build()).getResults();
        if (myTypes.isEmpty()) {
            return null;
        } else if (myTypes.size() == 1) {
            return KewTypeBo.to(myTypes.get(0));
        } else {
            throw new RiceIllegalStateException("More than one type found for the given name and namespace - (name=" +
                    name + ", namespace=" + namespace + ").");
        }

    }

    @Override
    public List<KewTypeDefinition> findAllTypesByNamespace(final String namespace) {
        if (StringUtils.isBlank(namespace)) {
            throw new RiceIllegalArgumentException("namespace is blank");
        }

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("namespace", namespace), equal("active", Boolean.TRUE));
        Collection<KewTypeBo> kewTypeBos = dataObjectService.findMatching(KewTypeBo.class,
                criteria.build()).getResults();

        return convertListOfBosToImmutables(kewTypeBos);
    }

    @Override
    public List<KewTypeDefinition> findAllTypes() {

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("active", Boolean.TRUE));
        Collection<KewTypeBo> kewTypeBos = dataObjectService.findMatching(KewTypeBo.class,
                criteria.build()).getResults();

        return convertListOfBosToImmutables(kewTypeBos);
    }

    /**
     * Sets the dataObjectService attribute value.
     *
     * @param dataObjectService The dataObjectService to set.
     */
    public void setDataObjectService(final DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * Converts a List<KewTypeBo> to an Unmodifiable List<KewType>
     *
     * @param kewTypeBos a mutable List<KewTypeBo> to made completely immutable.
     * @return An unmodifiable List<KewType>
     */
    List<KewTypeDefinition> convertListOfBosToImmutables(final Collection<KewTypeBo> kewTypeBos) {
        ArrayList<KewTypeDefinition> kewTypes = new ArrayList<KewTypeDefinition>();
        for (KewTypeBo bo : kewTypeBos) {
            KewTypeDefinition kewType = KewTypeBo.to(bo);
            kewTypes.add(kewType);
        }
        return Collections.unmodifiableList(kewTypes);
    }

    /**
     * This overridden method creates a KewTypeAttribute if it does not already exist in the repository.
     *
     * @see org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService#createKewTypeAttribute(org.kuali.rice.kew.api.repository.type.KewTypeAttribute)
     */
    @Override
    public void createKewTypeAttribute(KewTypeAttribute kewTypeAttribute) {
        if (kewTypeAttribute == null){
            throw new RiceIllegalArgumentException("kewTypeAttribute is null");
        }

        KewTypeAttributeBo existing = dataObjectService.find(KewTypeAttributeBo.class, kewTypeAttribute);

        if (null != existing && kewTypeAttribute.getTypeId().equals(existing.getTypeId()) &&
                kewTypeAttribute.getAttributeDefinitionId().equals(existing.getAttributeDefinitionId())) {

            throw new RiceIllegalStateException("The KEW Type Attribute to create already exists: " + kewTypeAttribute);
        }
        KewTypeBo kewType = null;
        if (kewTypeAttribute.getTypeId() != null) {
            kewType = dataObjectService.find(KewTypeBo.class, kewTypeAttribute.getTypeId());
        }

        dataObjectService.save(KewTypeAttributeBo.from(kewTypeAttribute, kewType));
    }

    /**
     * This overridden method updates an existing KewTypeAttribute
     *
     * @see org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService#updateKewTypeAttribute(org.kuali.rice.kew.api.repository.type.KewTypeAttribute)
     */
    @Override
    public void updateKewTypeAttribute(KewTypeAttribute kewTypeAttribute) {
        if (kewTypeAttribute == null) {
            throw new RiceIllegalArgumentException("kewTypeAttribute is null");
        }
        final KewTypeAttributeBo existing = dataObjectService.find(KewTypeAttributeBo.class, kewTypeAttribute.getId());
        if (existing == null) {
            throw new RiceIllegalStateException("The KEW type Attribute does not exist: " + kewTypeAttribute);
        }
        final KewTypeAttribute toUpdate;
        if (!existing.getId().equals(kewTypeAttribute.getId())){
            final KewTypeAttribute.Builder builder = KewTypeAttribute.Builder.create(kewTypeAttribute);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = kewTypeAttribute;
        }

        KewTypeBo kewType = existing.getType();
        if (!existing.getTypeId().equals(kewTypeAttribute.getTypeId())) {
            kewType = dataObjectService.find(KewTypeBo.class, kewTypeAttribute.getTypeId());
        }

        dataObjectService.save(KewTypeAttributeBo.from(toUpdate, kewType));
    }
}
