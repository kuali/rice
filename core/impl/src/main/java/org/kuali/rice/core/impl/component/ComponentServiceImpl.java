package org.kuali.rice.core.impl.component;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.component.ComponentService;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import javax.jws.WebParam;
import java.util.List;

/**
 * Reference implementation of the {@code ComponentService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentServiceImpl implements ComponentService {

    @Override
    public Component getComponentByCode(String namespaceCode, String componentCode) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        if (StringUtils.isBlank(componentCode)) {
            throw new RiceIllegalArgumentException("componentCode was a null or blank value");
        }
        
        // TODO implement the rest using BOS!
        return null;
    }

    @Override
    public List<Component> getAllComponentsByNamespaceCode(String namespaceCode) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }

        // TODO implement the rest using BOS!
        return null;
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
}
