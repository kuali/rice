package org.kuali.rice.kew.api.extension;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

/**
 * A service which is used for retrieving information about extensions to various
 * pieces of Kuali Enterprise Workflow.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ExtensionRepositoryService {

    ExtensionDefinition getExtensionById(String id) throws RiceIllegalArgumentException;

    ExtensionDefinition getExtensionByName(String name) throws RiceIllegalArgumentException;

}
