package org.kuali.rice.core.impl.component;

import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.framework.component.ComponentService;

import javax.jws.WebParam;
import java.util.List;

/**
 * Reference implementation of the {@code ComponentService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentServiceImpl implements ComponentService {

    @Override
    public void publishComponents(String applicationId, List<Component> components) {
        // TODO
    }

    @Override
    public Component getComponentByCode(@WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "componentCode") String componentCode) throws RiceIllegalArgumentException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Component> getAllComponentsByNamespaceCode(@WebParam(name = "namespaceCode") String namespaceCode) throws RiceIllegalArgumentException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Component> getApplicationComponents(@WebParam(name = "applicationId") String applicationId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
