package org.kuali.rice.core.framework.component;

import org.kuali.rice.core.api.component.Component;

import java.util.List;

/**
 * Classes which implement this interface can publish components to the core component system via
 * the {@link ComponentProviderService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ComponentPublisher {

    List<Component> publishComponents();

}
