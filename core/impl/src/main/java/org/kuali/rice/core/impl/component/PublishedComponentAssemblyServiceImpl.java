package org.kuali.rice.core.impl.component;

import org.kuali.rice.core.api.component.Component;

import java.util.List;

/**
 * Reference implementation of the {@code PublishedComponentAssemblyService}.  Uses a "pull" model to periodically
 * request application-specific components from client applications who have chosen to publish components to the
 * component system.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PublishedComponentAssemblyServiceImpl implements PublishedComponentAssemblyService {

    @Override
    public List<Component> getPublishedComponents() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
