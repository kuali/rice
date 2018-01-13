/**
 * Copyright 2005-2018 The Kuali Foundation
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
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBinding;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBindingQueryResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findMatching;

/**
 * Implementation of the @{link ReferenceObjectBindingBoService} interface for accessing  {@link ReferenceObjectBindingBo} related business objects.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class ReferenceObjectBindingBoServiceImpl implements ReferenceObjectBindingBoService {

    private DataObjectService dataObjectService;
    private KrmsAttributeDefinitionService attributeDefinitionService;

    /**
     * Sets the value of DataObjectService to the given value.
     * 
     * @param dataObjectService the DataObjectService value to set.
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
    public ReferenceObjectBinding createReferenceObjectBinding(ReferenceObjectBinding referenceObjectBinding) {
        incomingParamCheck(referenceObjectBinding , "referenceObjectBinding");
        final String referenceObjectBindingIdKey = referenceObjectBinding.getId();
        final ReferenceObjectBinding existing = getReferenceObjectBinding(referenceObjectBindingIdKey);

        if (existing != null) {
            throw new IllegalStateException("the ReferenceObjectBinding to create already exists: " + referenceObjectBinding);
        }

        ReferenceObjectBindingBo bo = dataObjectService.save(from(referenceObjectBinding), PersistenceOption.FLUSH);

        return ReferenceObjectBindingBo.to(bo);
    }

    @Override
    public ReferenceObjectBinding getReferenceObjectBinding(String referenceObjectBindingId) {
        incomingParamCheck(referenceObjectBindingId , "referenceObjectBindingId");
        ReferenceObjectBindingBo bo = dataObjectService.find(ReferenceObjectBindingBo.class, referenceObjectBindingId);

        return ReferenceObjectBindingBo.to(bo);
    }

    @Override
    public ReferenceObjectBinding updateReferenceObjectBinding(ReferenceObjectBinding referenceObjectBinding) {
        incomingParamCheck(referenceObjectBinding , "referenceObjectBinding");
        final ReferenceObjectBinding existing = getReferenceObjectBinding(referenceObjectBinding.getId());

        if (existing == null) {
            throw new IllegalStateException("the ReferenceObjectBinding to update does not exists: " + referenceObjectBinding);
        }

        final ReferenceObjectBinding toUpdate;

        if (!existing.getId().equals(referenceObjectBinding.getId())){
            // if passed in id does not match existing id, correct it
            final ReferenceObjectBinding.Builder builder = ReferenceObjectBinding.Builder.create(referenceObjectBinding);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = referenceObjectBinding;
        }

        // copy all updateable fields to bo
        ReferenceObjectBindingBo boToUpdate = from(toUpdate);

        // update the rule and create new attributes
        ReferenceObjectBindingBo updatedData = dataObjectService.save(boToUpdate, PersistenceOption.FLUSH);

        return to(updatedData);
    }

    @Override
    public void deleteReferenceObjectBinding(String referenceObjectBindingId) {
        incomingParamCheck(referenceObjectBindingId , "referenceObjectBindingId");
        final ReferenceObjectBinding existing = getReferenceObjectBinding(referenceObjectBindingId);

        if (existing == null) {
            throw new IllegalStateException("the ReferenceObjectBinding to delete does not exists: " + referenceObjectBindingId);
        }

        dataObjectService.delete(from(existing));
    }

    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByCollectionName(String collectionName) {
        if (org.apache.commons.lang.StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("collectionName is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("collectionName", collectionName);
        List<ReferenceObjectBindingBo> bos = findMatching(dataObjectService, ReferenceObjectBindingBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByKrmsDiscriminatorType(String krmsDiscriminatorType) {
        if (org.apache.commons.lang.StringUtils.isBlank(krmsDiscriminatorType)) {
            throw new IllegalArgumentException("krmsDiscriminatorType is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("krmsDiscriminatorType", krmsDiscriminatorType);
        List<ReferenceObjectBindingBo> bos = findMatching(dataObjectService, ReferenceObjectBindingBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByKrmsObject(String krmsObjectId) {
        if (org.apache.commons.lang.StringUtils.isBlank(krmsObjectId)) {
            throw new IllegalArgumentException("krmsObjectId is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("krmsObjectId", krmsObjectId);
        List<ReferenceObjectBindingBo> bos = findMatching(dataObjectService, ReferenceObjectBindingBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByNamespace(String namespace) {
        if (org.apache.commons.lang.StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("namespace", namespace);
        List<ReferenceObjectBindingBo> bos = findMatching(dataObjectService, ReferenceObjectBindingBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByReferenceDiscriminatorType(String referenceDiscriminatorType) {
        if (org.apache.commons.lang.StringUtils.isBlank(referenceDiscriminatorType)) {
            throw new IllegalArgumentException("referenceDiscriminatorType is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("referenceDiscriminatorType", referenceDiscriminatorType);
        List<ReferenceObjectBindingBo> bos = findMatching(dataObjectService, ReferenceObjectBindingBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<ReferenceObjectBinding> findReferenceObjectBindingsByReferenceObject(String referenceObjectId) {
        if (org.apache.commons.lang.StringUtils.isBlank(referenceObjectId)) {
            throw new IllegalArgumentException("referenceObjectId is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("referenceObjectId", referenceObjectId);
        List<ReferenceObjectBindingBo> bos = findMatching(dataObjectService, ReferenceObjectBindingBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public List<String> findReferenceObjectBindingIds(final QueryByCriteria queryByCriteria) {
        incomingParamCheck(queryByCriteria, "queryByCriteria");
        ReferenceObjectBindingQueryResults results = findReferenceObjectBindings(queryByCriteria);
        List<String> result = new ArrayList<String>();

        for (ReferenceObjectBinding referenceObjectBinding: results.getResults()) {
            result.add(referenceObjectBinding.getId());
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public ReferenceObjectBindingQueryResults findReferenceObjectBindings(final QueryByCriteria queryByCriteria) {
        QueryResults<ReferenceObjectBindingBo> results =
                dataObjectService.findMatching(ReferenceObjectBindingBo.class, queryByCriteria);

        ReferenceObjectBindingQueryResults.Builder builder = ReferenceObjectBindingQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());
        final List<ReferenceObjectBinding.Builder> ims = new ArrayList<ReferenceObjectBinding.Builder>();

        for (ReferenceObjectBindingBo bo : results.getResults()) {
            ims.add(ReferenceObjectBinding.Builder.create(bo));
        }

        builder.setResults(ims);

        return builder.build();
    }

    public List<ReferenceObjectBinding> convertBosToImmutables(final Collection<ReferenceObjectBindingBo> referenceObjectBindingBos) {
        List<ReferenceObjectBinding> immutables = new LinkedList<ReferenceObjectBinding>();
        if (referenceObjectBindingBos != null) {
            ReferenceObjectBinding immutable = null;

            for (ReferenceObjectBindingBo bo : referenceObjectBindingBos ) {
                immutable = to(bo);
                immutables.add(immutable);
            }
        }

        return Collections.unmodifiableList(immutables);
    }

    @Override
    public ReferenceObjectBinding to(ReferenceObjectBindingBo referenceObjectBindingBo) {
        return ReferenceObjectBindingBo.to(referenceObjectBindingBo);
    }

    public ReferenceObjectBindingBo from(ReferenceObjectBinding referenceObjectBinding) {
        return ReferenceObjectBindingBo.from(referenceObjectBinding);
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
