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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.in;
import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findMatching;

public final class KrmsAttributeDefinitionServiceImpl implements KrmsAttributeDefinitionService {
    private CriteriaLookupService criteriaLookupService;

    private DataObjectService dataObjectService;

    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#convertAttributeKeys(java.util.Map, String)
     */
    @Override
    public Map<String,String> convertAttributeKeys(Map<String,String> attributesByName, String namespace) {
        Map<String,String> attributesById = new HashMap<String,String>();

        if(attributesByName != null) {
            for(Map.Entry<String, String> attr : attributesByName.entrySet()) {
                String newKey = getKrmsAttributeId(attr.getKey(), namespace);
                if(StringUtils.isNotEmpty(newKey)) {
                    attributesById.put(newKey, attr.getValue());
                }
            }
        }

        return attributesById;
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#getKrmsAttributeId(String, String)
     */
    @Override
    public String getKrmsAttributeId( String attributeName, String namespace) {
        String returnId = null;

        KrmsAttributeDefinitionBo bo = getKrmsAttributeBo(attributeName, namespace);
        if (bo != null){
            returnId = bo.getId();
        }

        return returnId;
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#getKrmsAttributeBo(String, String)
     */
    @Override
    public KrmsAttributeDefinitionBo getKrmsAttributeBo( String attributeName, String namespace) {
        KrmsAttributeDefinitionBo result = null;
        Map<String,Object> criteria = new HashMap<String,Object>( 3 );
        criteria.put( "name", attributeName );
        criteria.put( "namespace", namespace );
        criteria.put( "active", Boolean.TRUE );
        Collection<KrmsAttributeDefinitionBo> defs =
                findMatching(getDataObjectService(), KrmsAttributeDefinitionBo.class, criteria );

        if(CollectionUtils.isNotEmpty(defs)) {
            if (defs.size() > 1){
                throw new IllegalStateException("Multiple KrmsAttributeDefinitions found with same name and namespace");
            }

            result = defs.iterator().next();
        }

        return result;
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#createAttributeDefinition(org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition)
     */
    @Override
    public KrmsAttributeDefinition createAttributeDefinition(KrmsAttributeDefinition attributeDefinition) {
        if (attributeDefinition == null){
            throw new IllegalArgumentException("attributeDefinition is null");
        }

        final String nameKey = attributeDefinition.getName();
        final String namespaceKey = attributeDefinition.getNamespace();
        final KrmsAttributeDefinition existing = getAttributeDefinitionByNameAndNamespace(nameKey, namespaceKey);

        if (existing != null && existing.getName().equals(nameKey) && existing.getNamespace().equals(namespaceKey)){
            throw new IllegalStateException("the krms attribute definition to create already exists: " + attributeDefinition);
        }

        KrmsAttributeDefinitionBo bo = KrmsAttributeDefinitionBo.from(attributeDefinition);
        bo = getDataObjectService().save(bo, PersistenceOption.FLUSH);

        return KrmsAttributeDefinitionBo.to(bo);
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#updateAttributeDefinition(org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition)
     */
    @Override
    public void updateAttributeDefinition(KrmsAttributeDefinition attributeDefinition) {
        if (attributeDefinition == null){
            throw new IllegalArgumentException("attributeDefinition is null");
        }

        final String idKey = attributeDefinition.getId();
        final KrmsAttributeDefinitionBo existing = getDataObjectService().find(KrmsAttributeDefinitionBo.class, idKey);

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

        KrmsAttributeDefinitionBo bo = KrmsAttributeDefinitionBo.from(toUpdate);

        getDataObjectService().save(bo, PersistenceOption.FLUSH);
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#getAttributeDefinitionById(String)
     */
    @Override
    public KrmsAttributeDefinition getAttributeDefinitionById(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id is blank");
        }

        KrmsAttributeDefinitionBo bo = getDataObjectService().find(KrmsAttributeDefinitionBo.class, id);

        return KrmsAttributeDefinitionBo.to(bo);
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#getAttributeDefinitionByNameAndNamespace(String, String)
     */
    @Override
    public KrmsAttributeDefinition getAttributeDefinitionByNameAndNamespace(final String name, final String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is blank");
        }

        KrmsAttributeDefinitionBo bo = getKrmsAttributeBo(name, namespace);

        if (bo == null) {
            return null;
        }

        return KrmsAttributeDefinitionBo.to(bo);
    }

    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#findAttributeDefinitionsByNamespace(String)
     */
    @Override
    public List<KrmsAttributeDefinition> findAttributeDefinitionsByNamespace(final String namespace) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("namespace", namespace);
        map.put("active", Boolean.TRUE);
        Collection<KrmsAttributeDefinitionBo> krmsAttributeDefinitionBos = findMatching(getDataObjectService(),
                KrmsAttributeDefinitionBo.class, Collections.unmodifiableMap(map));

        return convertListOfBosToImmutables(krmsAttributeDefinitionBos);
    }

    @Override
    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#findAttributeDefinitionsByType(String)
     */
    public List<KrmsAttributeDefinition> findAttributeDefinitionsByType(final String typeId) {

        List<KrmsAttributeDefinition> results = Collections.emptyList();

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("type.id", typeId);
        map.put("active", Boolean.TRUE);
        Collection<KrmsTypeAttributeBo> krmsTypeAttributeBos = findMatching(getDataObjectService(),
                KrmsTypeAttributeBo.class, Collections.unmodifiableMap(map));

        if (!CollectionUtils.isEmpty(krmsTypeAttributeBos)) {
            String [] inList = new String[krmsTypeAttributeBos.size()];
            int inListIdex = 0;
            for (KrmsTypeAttributeBo krmsTypeAttributeBo : krmsTypeAttributeBos) {
                inList[inListIdex] = krmsTypeAttributeBo.getAttributeDefinitionId();
                ++inListIdex; // don't forget to increment our index
            }

            QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
            qBuilder.setPredicates(in("id", inList));
            QueryResults<KrmsAttributeDefinitionBo> queryResults =
                    getDataObjectService().findMatching(KrmsAttributeDefinitionBo.class, qBuilder.build());
            results = convertListOfBosToImmutables(queryResults.getResults());
        }

        return results;
    }


    /**
     * @see org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService#findAllAttributeDefinitions()
     */
    @Override
    public List<KrmsAttributeDefinition> findAllAttributeDefinitions() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active", Boolean.TRUE);

        Collection<KrmsAttributeDefinitionBo> krmsAttributeDefinitionBos = findMatching(getDataObjectService(),
                KrmsAttributeDefinitionBo.class, Collections.unmodifiableMap(map));

        return convertListOfBosToImmutables(krmsAttributeDefinitionBos);
    }

    /**
     * Converts a List<KrmsAttributeDefinitionBo> to an Unmodifiable List<KrmsAttributeDefinition>
     *
     * @param krmsAttributeDefinitionBos a mutable List<KrmsAttributeDefinitionBo> to made completely immutable.
     * @return An unmodifiable List<KrmsAttributeDefinition>
     */
    public List<KrmsAttributeDefinition> convertListOfBosToImmutables(final Collection<KrmsAttributeDefinitionBo> krmsAttributeDefinitionBos) {
        ArrayList<KrmsAttributeDefinition> krmsAttributeDefinitions = new ArrayList<KrmsAttributeDefinition>();

        for (KrmsAttributeDefinitionBo bo : krmsAttributeDefinitionBos) {
            KrmsAttributeDefinition krmsAttributeDefinition = KrmsAttributeDefinitionBo.to(bo);
            krmsAttributeDefinitions.add(krmsAttributeDefinition);
        }

        return Collections.unmodifiableList(krmsAttributeDefinitions);
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
     * This method returns a reference to the dataObjectService.
     * If the dataObjectService is not set, get it from the KRADServiceLocator.
     * @return dataObjectService
     */
    protected DataObjectService getDataObjectService() {
        if ( dataObjectService == null ) {
            dataObjectService = KRADServiceLocator.getDataObjectService();
        }

        return dataObjectService;
    }

}
