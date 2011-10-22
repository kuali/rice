package org.kuali.rice.core.impl.component;

import org.kuali.rice.core.api.component.Component;

import java.util.List;

/**
 * An internal service used for assembling published application-specific components from application which choose to
 * publish such components.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface PublishedComponentAssemblyService {

    /**
     * Returns components which have been published by all applications which have chosen to publish such
     * application-specific components.
     *
     * @return the list of published components
     */
    List<Component> getPublishedComponents();

}
