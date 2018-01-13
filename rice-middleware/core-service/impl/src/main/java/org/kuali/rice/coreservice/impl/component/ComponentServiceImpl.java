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
package org.kuali.rice.coreservice.impl.component;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.util.ChecksumUtils;
import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.coreservice.api.component.ComponentService;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of the {@code ComponentService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional(readOnly=true)
public class ComponentServiceImpl implements ComponentService {

    private static final Logger LOG = Logger.getLogger(ComponentServiceImpl.class);

    private ComponentSetDao componentSetDao;
    private DataObjectService dataObjectService;

    @Override
    public Component getComponentByCode(String namespaceCode, String componentCode) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        if (StringUtils.isBlank(componentCode)) {
            throw new RiceIllegalArgumentException("componentCode was a null or blank value");
        }
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("namespaceCode", namespaceCode);
        primaryKeys.put("code", componentCode);
        ComponentBo componentBo = dataObjectService.find(ComponentBo.class,new CompoundKey(primaryKeys));
        if (componentBo != null) {
            return ComponentBo.to(componentBo);
        }
        DerivedComponentBo derivedComponentBo = dataObjectService.find(
                        DerivedComponentBo.class,new CompoundKey(primaryKeys));
        return derivedComponentBo == null ? null : DerivedComponentBo.to(derivedComponentBo);
    }

    @Override
    public List<Component> getAllComponentsByNamespaceCode(String namespaceCode) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("namespaceCode", namespaceCode);
        QueryResults<ComponentBo> componentBos =
                getDataObjectService().findMatching(ComponentBo.class,
                        QueryByCriteria.Builder.andAttributes(criteria).build());

        QueryResults<DerivedComponentBo> derivedComponentBos =
                getDataObjectService().findMatching(DerivedComponentBo.class, QueryByCriteria.Builder.andAttributes(
                        criteria).build());
        return translateCollections(componentBos, derivedComponentBos);
    }

    @Override
    public List<Component> getActiveComponentsByNamespaceCode(String namespaceCode) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("namespaceCode", namespaceCode);
        criteria.put("active", Boolean.TRUE);
        QueryResults<ComponentBo> componentBos =
                getDataObjectService().findMatching(ComponentBo.class,
                        QueryByCriteria.Builder.andAttributes(criteria).build());
        criteria.remove("active");
        QueryResults<DerivedComponentBo> derivedComponentBos =
                getDataObjectService().findMatching(DerivedComponentBo.class,
                        QueryByCriteria.Builder.andAttributes(criteria).build());
        return translateCollections(componentBos, derivedComponentBos);
    }

    @Override
    public List<Component> getDerivedComponentSet(String componentSetId) {
        if (StringUtils.isBlank(componentSetId)) {
            throw new RiceIllegalArgumentException("componentSetId was a null or blank value");
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("componentSetId", componentSetId);
        QueryResults<DerivedComponentBo> derivedComponentBos =
                getDataObjectService().findMatching(DerivedComponentBo.class,
                        QueryByCriteria.Builder.andAttributes(criteria).build());
        return translateCollections(null, derivedComponentBos);
    }

    @Override
    @Transactional
    public void publishDerivedComponents(String componentSetId, List<Component> components) {
        if (StringUtils.isBlank(componentSetId)) {
            throw new RiceIllegalArgumentException("componentSetId was a null or blank value");
        }
        components = validateAndNormalizeComponents(componentSetId, components);
        LOG.info("Requesting to publish " + components.size() + " derived components for componentSetId=" + componentSetId);
        ComponentSetBo componentSet = getDataObjectService().find(ComponentSetBo.class,componentSetId);
        if (componentSet == null) {
            componentSet = new ComponentSetBo();
            componentSet.setComponentSetId(componentSetId);
        }
        String checksum = calculateChecksum(components);
        if (!checksum.equals(componentSet.getChecksum())) {
            LOG.info("Checksums were different, proceeding with update of derived components for componentSetId=" + componentSetId);
            componentSet.setChecksum(checksum);
            componentSet.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
            if (getComponentSetDao().saveIgnoreLockingFailure(componentSet)) {
                updateDerivedComponents(componentSetId, components);
            }
        } else {
            LOG.info("Checksums were the same, no derived component update needed for componentSetId=" + componentSetId);
        }
    }

    protected List<Component> validateAndNormalizeComponents(String componentSetId, List<Component> components) {
        List<Component> processedComponents = new ArrayList<Component>();

        // normalize and copy component list, we will later sort this list possibly so don't want to hold onto the original
        if (components == null) {
            components = new ArrayList<Component>();
        } else {
            components = new ArrayList<Component>(components);
        }
        // components must either have a null componentSetId or one which matches the componentSetId being published
        for (Component component : components) {
            // if componentSetId is null, recreate the component with that value
            if (component.getComponentSetId() == null) {
                Component.Builder builder = Component.Builder.create(component);
                builder.setComponentSetId(componentSetId);
                component = builder.build();
            }
            String currentComponentSetId = component.getComponentSetId();
            if (!componentSetId.equals(currentComponentSetId)) {
                throw new RiceIllegalArgumentException("Encountered a component with an invalid componentSetId of '" +
                        currentComponentSetId + "'.  Expected null or '" + componentSetId + "'.");
            }
            processedComponents.add(component);
        }
        return processedComponents;
    }

    /**
     * Calculates the checksum for the list of components.  The list of components should be sorted in a
     * consistent way prior to generation of the checksum to ensure that the checksum value comes out the same regardless
     * of the ordering of components contained therein.  The checksum allows us to easily determine if the component set
     * has been updated or not.
     */
    protected String calculateChecksum(List<Component> components) {
        Collections.sort(components, new Comparator<Component>() {
            @Override
            public int compare(Component component1, Component component2) {
                return CompareToBuilder.reflectionCompare(component1, component2);
            }
        });
        return ChecksumUtils.calculateChecksum(components);
    }

    protected void updateDerivedComponents(String componentSetId, List<Component> components) {
        Map<String, Object> deleteCriteria = new HashMap<String, Object>();
        deleteCriteria.put("componentSetId", componentSetId);
        dataObjectService.deleteMatching(DerivedComponentBo.class,
                    QueryByCriteria.Builder.andAttributes(deleteCriteria).build());
        dataObjectService.flush(DerivedComponentBo.class);
        if (CollectionUtils.isNotEmpty(components)) {
            List<DerivedComponentBo> derivedComponentBos = new ArrayList<DerivedComponentBo>();
            for (Component component : components) {
                derivedComponentBos.add(DerivedComponentBo.from(component));
            }
            for(DerivedComponentBo component : derivedComponentBos){
                dataObjectService.save(component);
            }
        }
    }

    protected List<Component> translateCollections(QueryResults<ComponentBo> componentBos,
            QueryResults<DerivedComponentBo> derivedComponentBos){
        List<Component> components = new ArrayList<Component>();
        if (componentBos != null && CollectionUtils.isNotEmpty(componentBos.getResults())) {
            for (ComponentBo componentBo : componentBos.getResults()) {
                components.add(ComponentBo.to(componentBo));
            }
        }
        if (derivedComponentBos != null && CollectionUtils.isNotEmpty(derivedComponentBos.getResults())) {
            for (DerivedComponentBo derivedComponentBo : derivedComponentBos.getResults()) {
                components.add(DerivedComponentBo.to(derivedComponentBo));
            }
        }
        return Collections.unmodifiableList(components);
    }

    public ComponentSetDao getComponentSetDao() {
        return componentSetDao;
    }

    public void setComponentSetDao(ComponentSetDao componentSetDao) {
        this.componentSetDao = componentSetDao;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
