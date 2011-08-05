package org.kuali.rice.kew.api.extension;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

import java.util.Map;

/**
 * Defines an extension to some component of Kuali Enterprise Workflow.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ExtensionDefinitionContract extends Identifiable, Versioned {

    String getName();

    String getApplicationId();

    String getLabel();

    String getDescription();

    String getType();

    /**
     * Retrieves the resource descriptor for this extension.  This gives the calling code the
     * information it needs to locate and execute the extension resource if it needs to.
     *
     * @return the resource descriptor for this extension, this value should never be blank or null
     */
    String getResourceDescriptor();

    Map<String, String> getConfiguration();

}
