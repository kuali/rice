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
package org.kuali.rice.krms.impl.repository;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krms.api.repository.typerelation.RelationshipType;
import org.kuali.rice.krms.api.repository.typerelation.TypeTypeRelation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findMatching;

/**
 * Implementation of the @{link TypeTypeRelationBoService} interface for accessing  {@link TypeTypeRelationBo} related business objects.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public final class TypeTypeRelationBoServiceImpl
    implements TypeTypeRelationBoService {

    private DataObjectService dataObjectService;
    private KrmsAttributeDefinitionService attributeDefinitionService;

    /**
     * Sets the value of DataObjectService to the given value.
     * 
     * @param dataObjectService the DataObjectService value to set.
     * 
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public void setAttributeDefinitionService(KrmsAttributeDefinitionService attributeDefinitionService) {
        this.attributeDefinitionService = attributeDefinitionService;
    }

    public KrmsAttributeDefinitionService getAttributeDefinitionService() {
        if (attributeDefinitionService == null) {
            attributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
        }
        return attributeDefinitionService;
    }

    @Override
    public TypeTypeRelation createTypeTypeRelation(TypeTypeRelation typeTypeRelation) {
        incomingParamCheck(typeTypeRelation , "typeTypeRelation");
        if (StringUtils.isNotEmpty(typeTypeRelation.getId())) {
            final String typeTypeRelationIdKey = typeTypeRelation.getId();
            final TypeTypeRelation existing = getTypeTypeRelation(typeTypeRelationIdKey);

            if (existing != null) {
                throw new IllegalStateException("the TypeTypeRelation to create already exists: " + typeTypeRelation);
            }
        }

        TypeTypeRelationBo bo = dataObjectService.save(from(typeTypeRelation), PersistenceOption.FLUSH);

        return TypeTypeRelationBo.to(bo);
    }

    @Override
    public TypeTypeRelation getTypeTypeRelation(String typeTypeRelationId) {
        incomingParamCheck(typeTypeRelationId , "typeTypeRelationId");
        TypeTypeRelationBo bo = dataObjectService.find(TypeTypeRelationBo.class, typeTypeRelationId);

        return TypeTypeRelationBo.to(bo);
    }

    @Override
    public void updateTypeTypeRelation(TypeTypeRelation typeTypeRelation) {
        incomingParamCheck(typeTypeRelation , "typeTypeRelation");
        final TypeTypeRelation existing = getTypeTypeRelation(typeTypeRelation.getId());

        if (existing == null) {
            throw new IllegalStateException("the TypeTypeRelation to update does not exists: " + typeTypeRelation);
        }

        final TypeTypeRelation toUpdate;

        if (!existing.getId().equals(typeTypeRelation.getId())){
            // if passed in id does not match existing id, correct it
            final TypeTypeRelation.Builder builder = TypeTypeRelation.Builder.create(typeTypeRelation);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = typeTypeRelation;
        }

        // copy all updateable fields to bo
        TypeTypeRelationBo boToUpdate = from(toUpdate);

        // update the rule and create new attributes
         dataObjectService.save(boToUpdate, PersistenceOption.FLUSH);
    }

    @Override
    public void deleteTypeTypeRelation(String typeTypeRelationId) {
        incomingParamCheck(typeTypeRelationId , "typeTypeRelationId");
        final TypeTypeRelation existing = getTypeTypeRelation(typeTypeRelationId);

        if (existing == null) {
            throw new IllegalStateException("the TypeTypeRelation to delete does not exists: " + typeTypeRelationId);
        }

        dataObjectService.delete(from(existing));
    }

    @Override
    public List<TypeTypeRelation> findTypeTypeRelationsByFromType(String fromTypeId) {
        if (org.apache.commons.lang.StringUtils.isBlank(fromTypeId)) {
            throw new IllegalArgumentException("fromTypeId is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("fromTypeId", fromTypeId);
        List<TypeTypeRelationBo> bos = findMatching(dataObjectService, TypeTypeRelationBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<TypeTypeRelation> findTypeTypeRelationsByToType(String toTypeId) {
        if (org.apache.commons.lang.StringUtils.isBlank(toTypeId)) {
            throw new IllegalArgumentException("toTypeId is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("toTypeId", toTypeId);
        List<TypeTypeRelationBo> bos = findMatching(dataObjectService, TypeTypeRelationBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<TypeTypeRelation> findTypeTypeRelationsByRelationshipType(RelationshipType relationshipType) {
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType is null");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("relationshipType", relationshipType);
        List<TypeTypeRelationBo> bos = findMatching(dataObjectService, TypeTypeRelationBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<TypeTypeRelation> findTypeTypeRelationsBySequenceNumber(Integer sequenceNumber) {
        if (sequenceNumber == null) {
            throw new IllegalArgumentException("sequenceNumber is null");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("sequenceNumber", sequenceNumber);
        List<TypeTypeRelationBo> bos = findMatching(dataObjectService, TypeTypeRelationBo.class, map);

        return convertBosToImmutables(bos);
    }

    public List<TypeTypeRelation> convertBosToImmutables(final Collection<TypeTypeRelationBo> typeTypeRelationBos) {
        List<TypeTypeRelation> immutables = new LinkedList<TypeTypeRelation>();

        if (typeTypeRelationBos != null) {
            TypeTypeRelation immutable = null;
            for (TypeTypeRelationBo bo : typeTypeRelationBos ) {
                immutable = to(bo);
                immutables.add(immutable);
            }
        }

        return Collections.unmodifiableList(immutables);
    }

    @Override
    public TypeTypeRelation to(TypeTypeRelationBo typeTypeRelationBo) {
        return TypeTypeRelationBo.to(typeTypeRelationBo);
    }

    public TypeTypeRelationBo from(TypeTypeRelation typeTypeRelation) {
        return TypeTypeRelationBo.from(typeTypeRelation);
    }

    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " was null");
        } else if (object instanceof String
                && StringUtils.isBlank((String)object)) {
            throw new IllegalArgumentException(name + " was blank");
        }
    }

}
