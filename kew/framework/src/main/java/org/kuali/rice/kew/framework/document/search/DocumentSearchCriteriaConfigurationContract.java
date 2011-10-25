package org.kuali.rice.kew.framework.document.search;

import java.util.List;

/**
 * Defines the contract for which specifies attribute fields that should be included as part of document search criteria
 * on the document search user interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentSearchCriteriaConfigurationContract {

    /**
     * Returns the additional attribute fields that should be included as part of the document search criteria on the
     * document search user interface.
     *
     * @return the search attribute fields that are part of this configuration
     */
    List<AttributeFields> getSearchAttributeFields();

}
