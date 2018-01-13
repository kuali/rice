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
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findMatching;
import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findMatchingOrderBy;
import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findSingleMatching;

/**
 * Implementation of the interface for accessing KRMS repository Proposition related
 * business objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PropositionBoServiceImpl implements PropositionBoService {

    private DataObjectService dataObjectService;

    /**
     * This overridden method creates a Proposition if it does not
     * already exist in the repository.
     *
     * @see org.kuali.rice.krms.impl.repository.PropositionBoService#createProposition(org.kuali.rice.krms.api.repository.proposition.PropositionDefinition)
     */
    @Override
    public PropositionDefinition createProposition(PropositionDefinition prop) {
        if (prop == null) {
            throw new IllegalArgumentException("proposition is null");
        }
        if (null != prop.getId()) {
            throw new IllegalStateException("for creation, PropositionDefinition.id must be null");
        }

        PropositionBo propositionBo = PropositionBo.from(prop);
        propositionBo = dataObjectService.save(propositionBo);

        return PropositionBo.to(propositionBo);
    }

    /**
     * This overridden method updates an existing proposition
     *
     * @see org.kuali.rice.krms.impl.repository.PropositionBoService#updateProposition(org.kuali.rice.krms.api.repository.proposition.PropositionDefinition)
     */
    @Override
    public PropositionDefinition updateProposition(PropositionDefinition prop) {
        if (prop == null) {
            throw new IllegalArgumentException("proposition is null");
        }

        final String propIdKey = prop.getId();
        final PropositionDefinition existing = getPropositionById(propIdKey);

        if (existing == null) {
            throw new IllegalStateException("the proposition does not exist: " + prop);
        }

        final PropositionDefinition toUpdate;

        if (!existing.getId().equals(prop.getId())) {
            final PropositionDefinition.Builder builder = PropositionDefinition.Builder.create(prop);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = prop;
        }

        return PropositionBo.to(dataObjectService.save(PropositionBo.from(toUpdate)));
    }

    /**
     * This overridden method retrieves a proposition by the give proposition id.
     *
     * @see org.kuali.rice.krms.impl.repository.PropositionBoService#getPropositionById(java.lang.String)
     */
    @Override
    public PropositionDefinition getPropositionById(String propId) {
        if (StringUtils.isBlank(propId)) {
            throw new IllegalArgumentException("propId is null or blank");
        }

        PropositionBo bo = dataObjectService.find(PropositionBo.class, propId);

        return PropositionBo.to(bo);
    }

    @Override
    public Set<PropositionDefinition> getPropositionsByType(String typeId) {
        if (org.apache.commons.lang.StringUtils.isBlank(typeId)) {
            throw new IllegalArgumentException("typeId is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("typeId", typeId);
        Collection<PropositionBo> bos = findMatching(dataObjectService, PropositionBo.class, map);

        return convertBosToImmutables(bos);
    }

    @Override
    public Set<PropositionDefinition> getPropositionsByRule(String ruleId) {
        if (org.apache.commons.lang.StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("ruleId is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("ruleId", ruleId);
        Collection<PropositionBo> bos = findMatching(dataObjectService, PropositionBo.class, map);

        return convertBosToImmutables(bos);
    }

    public Set<PropositionDefinition> convertBosToImmutables(final Collection<PropositionBo> propositionBos) {
        Set<PropositionDefinition> immutables = new HashSet<PropositionDefinition>();

        if (propositionBos != null) {
            PropositionDefinition immutable = null;
            for (PropositionBo bo : propositionBos) {
                immutable = to(bo);
                immutables.add(immutable);
            }
        }

        return Collections.unmodifiableSet(immutables);
    }

    public PropositionDefinition to(PropositionBo propositionBo) {
        return PropositionBo.to(propositionBo);
    }

    /**
     * This overridden method creates a PropositionParameter if it does not
     * already exist in the repository.
     *
     * @see org.kuali.rice.krms.impl.repository.PropositionBoService#createParameter(org.kuali.rice.krms.api.repository.proposition.PropositionParameter)
     */
    @Override
    public void createParameter(PropositionParameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("parameter is null");
        }

        final String propIdKey = parameter.getPropId();
        final Integer seqNoKey = parameter.getSequenceNumber();
        final PropositionParameter existing = getParameterByPropIdAndSequenceNumber(propIdKey, seqNoKey);

        if (existing != null && existing.getPropId().equals(propIdKey) && existing.getSequenceNumber().equals(
                seqNoKey)) {
            throw new IllegalStateException("the parameter to create already exists: " + parameter);
        }

        dataObjectService.save(PropositionParameterBo.from(parameter));
    }

    /**
     * This overridden method updates an existing proposition parameter
     *
     * @see org.kuali.rice.krms.impl.repository.PropositionBoService#updateParameter(org.kuali.rice.krms.api.repository.proposition.PropositionParameter)
     */
    @Override
    public PropositionParameter updateParameter(PropositionParameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("parameter is null");
        }

        final String propIdKey = parameter.getPropId();
        final Integer seqNoKey = parameter.getSequenceNumber();
        final PropositionParameter existing = getParameterByPropIdAndSequenceNumber(propIdKey, seqNoKey);

        if (existing == null) {
            throw new IllegalStateException("the parameter does not exist: " + parameter);
        }

        final PropositionParameter toUpdate;

        if (!existing.getId().equals(parameter.getId())) {
            final PropositionParameter.Builder builder = PropositionParameter.Builder.create(parameter);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = parameter;
        }

        final PropositionParameterBo boToUpdate = PropositionParameterBo.from(toUpdate);
        final PropositionParameterBo updatedData = dataObjectService.save(boToUpdate);

        final PropositionParameter.Builder builder = PropositionParameter.Builder.create(updatedData);
        builder.setPropId(propIdKey);
        return builder.build();
    }

    @Override
    public void deleteProposition(String propId) {
        if (propId == null) {
            throw new IllegalArgumentException("propId is null");
        }

        final PropositionDefinition existing = getPropositionById(propId);

        if (existing == null) {
            throw new IllegalStateException("the Proposition to delete does not exists: " + propId);
        }

        dataObjectService.delete(from(existing));
    }

    /**
     * This overridden method retrieves a list of parameters for a given proposition
     *
     * @see org.kuali.rice.krms.impl.repository.PropositionBoService#getParameters(java.lang.String)
     */
    @Override
    public List<PropositionParameter> getParameters(String propId) {
        if (StringUtils.isBlank(propId)) {
            throw new IllegalArgumentException("propId is null or blank");
        }

        final Map<String, Object> criteriaMap = Collections.<String, Object>singletonMap("propId", propId);

        List<PropositionParameterBo> bos = findMatchingOrderBy(dataObjectService, PropositionParameterBo.class,
                criteriaMap, "sequenceNumber", true);

        return PropositionParameterBo.to(bos);
    }

    /**
     * This overridden method gets a parameter by the parameter id
     *
     * @see org.kuali.rice.krms.impl.repository.PropositionBoService#getParameterById(java.lang.String)
     */
    @Override
    public PropositionParameter getParameterById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id is null or blank");
        }

        PropositionParameterBo bo = dataObjectService.find(PropositionParameterBo.class, id);

        return PropositionParameterBo.to(bo);
    }

    /**
     * This overridden method gets a parameter by the Proposition Id and Sequence Number
     *
     * @see org.kuali.rice.krms.impl.repository.PropositionBoService#getParameterByPropIdAndSequenceNumber(String,
     * Integer)
     */
    @Override
    public PropositionParameter getParameterByPropIdAndSequenceNumber(String propId, Integer sequenceNumber) {
        if (StringUtils.isBlank(propId)) {
            throw new IllegalArgumentException("propId is null or blank");
        }
        if (sequenceNumber == null) {
            throw new IllegalArgumentException("sequenceNumber is null");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("propId", propId);
        map.put("sequenceNumber", sequenceNumber);
        PropositionParameterBo bo = findSingleMatching(dataObjectService, PropositionParameterBo.class, map);

        return PropositionParameterBo.to(bo);
    }

    /**
     * Converts a immutable {@link PropositionDefinition} to its mutable {@link PropositionBo} counterpart.
     *
     * @param proposition the immutable object.
     * @return a {@link PropositionBo} the mutable PropositionBo.
     */
    public PropositionBo from(PropositionDefinition proposition) {
        if (proposition == null) {
            return null;
        }

        PropositionBo propositionBo = new PropositionBo();
        propositionBo.setDescription(proposition.getDescription());
        propositionBo.setTypeId(proposition.getTypeId());
        propositionBo.setRuleId(proposition.getRuleId());
        propositionBo.setPropositionTypeCode(proposition.getPropositionTypeCode());
        propositionBo.setCompoundOpCode(proposition.getCompoundOpCode());
        propositionBo.setId(proposition.getId());
        propositionBo.setVersionNumber(proposition.getVersionNumber());

        return propositionBo;
    }

    /**
     * Sets the dataObjectService attribute value.
     *
     * @param dataObjectService The dataObjectService to set.
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
