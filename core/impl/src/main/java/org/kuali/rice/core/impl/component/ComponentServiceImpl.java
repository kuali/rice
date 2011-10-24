package org.kuali.rice.core.impl.component;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.component.ComponentService;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.util.ChecksumUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
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
@Transactional
public class ComponentServiceImpl implements ComponentService {



    private BusinessObjectService businessObjectService;
    private ComponentSetDao componentSetDao;

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
        ComponentBo componentBo = getBusinessObjectService().findByPrimaryKey(ComponentBo.class, primaryKeys);
        return componentBo == null ? null : ComponentBo.to(componentBo);
    }

    @Override
    public List<Component> getAllComponentsByNamespaceCode(String namespaceCode) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("namespaceCode", namespaceCode);
        Collection<ComponentBo> componentBos =
                getBusinessObjectService().findMatching(ComponentBo.class, criteria);
        if (CollectionUtils.isEmpty(componentBos)) {
            return Collections.emptyList();
        }
        List<Component> components = new ArrayList<Component>();
        for (ComponentBo componentBo : componentBos) {
            components.add(ComponentBo.to(componentBo));
        }
        return Collections.unmodifiableList(components);
    }

    @Override
    public List<Component> getActiveComponentsByNamespaceCode(String namespaceCode) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("namespaceCode", namespaceCode);
        criteria.put("active", Boolean.TRUE);
        Collection<ComponentBo> componentBos =
                getBusinessObjectService().findMatching(ComponentBo.class, criteria);
        return translateCollection(componentBos);
    }

    @Override
    public List<Component> getPublishedComponentSet(String componentSetId) {
        if (StringUtils.isBlank(componentSetId)) {
            throw new RiceIllegalArgumentException("componentSetId was a null or blank value");
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("componentSetId", componentSetId);
        criteria.put("active", Boolean.TRUE);
        Collection<ComponentBo> componentBos =
                getBusinessObjectService().findMatching(ComponentBo.class, criteria);
        return translateCollection(componentBos);
    }

    @Override
    public void publishComponents(String componentSetId, List<Component> components) {
        if (StringUtils.isBlank(componentSetId)) {
            throw new RiceIllegalArgumentException("componentSetId was a null or blank value");
        }
        components = validateAndNormalizeComponents(componentSetId, components);
        ComponentSetBo componentSet = getComponentSetDao().getComponentSet(componentSetId);
        if (componentSet == null) {
            componentSet = new ComponentSetBo();
            componentSet.setComponentSetId(componentSetId);
        }
        String checksum = calculateChecksum(components);
        if (!checksum.equals(componentSet.getChecksum())) {
            componentSet.setChecksum(checksum);
            componentSet.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
            if (getComponentSetDao().saveIgnoreLockingFailure(componentSet)) {
                updatePublishedComponents(componentSetId, components);
            }
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

    protected void updatePublishedComponents(String componentSetId, List<Component> components) {
        Map<String, Object> deleteCriteria = new HashMap<String, Object>();
        deleteCriteria.put("componentSetId", componentSetId);
        businessObjectService.deleteMatching(ComponentBo.class, deleteCriteria);
        if (CollectionUtils.isNotEmpty(components)) {
            List<ComponentBo> componentBos = new ArrayList<ComponentBo>();
            for (Component component : components) {
                componentBos.add(ComponentBo.from(component));
            }
            businessObjectService.save(componentBos);
        }
    }

    protected List<Component> translateCollection(Collection<ComponentBo> componentBos) {
        if (CollectionUtils.isEmpty(componentBos)) {
            return Collections.emptyList();
        } else {
            List<Component> components = new ArrayList<Component>();
            for (ComponentBo componentBo : componentBos) {
                components.add(ComponentBo.to(componentBo));
            }
            return Collections.unmodifiableList(components);
        }
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ComponentSetDao getComponentSetDao() {
        return componentSetDao;
    }

    public void setComponentSetDao(ComponentSetDao componentSetDao) {
        this.componentSetDao = componentSetDao;
    }
    
}
