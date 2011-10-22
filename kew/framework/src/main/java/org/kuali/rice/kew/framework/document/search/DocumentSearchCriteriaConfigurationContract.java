package org.kuali.rice.kew.framework.document.search;

import java.util.List;

/**
 * Defines the contract for which specifies attribute fields that should be included as part of document lookup criteria
 * on the document lookup user interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentSearchCriteriaConfigurationContract {

    /**
     * Returns the additional attribute fields that should be included as part of the document lookup criteria on the
     * document lookup user interface.
     *
     * @return the search attribute fields that are part of this configuration
     */
    List<AttributeFields> getSearchAttributeFields();

}
