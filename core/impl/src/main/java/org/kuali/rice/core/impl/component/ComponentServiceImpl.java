package org.kuali.rice.core.impl.component;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.component.ComponentService;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of the {@code ComponentService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentServiceImpl implements ComponentService {

    private BusinessObjectService businessObjectService;

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
    public List<Component> getPublishedComponentSet(String componentSetId) {
        if (StringUtils.isBlank(componentSetId)) {
            throw new RiceIllegalArgumentException("componentSetId was a null or blank value");
        }
        // TODO implement the rest using BOS!
        return null;
    }

    @Override
    public void publishComponents(String componentSetId, List<Component> components) {
        if (StringUtils.isBlank(componentSetId)) {
            throw new RiceIllegalArgumentException("componentSetId was a null or blank value");
        }
        // TODO implement this!
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
}
