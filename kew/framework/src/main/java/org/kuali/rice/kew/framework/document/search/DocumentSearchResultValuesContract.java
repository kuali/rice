package org.kuali.rice.kew.framework.document.search;

import java.util.List;

/**
 * Defines the contract for an object containing result values that are used to customize document search results.
 * Defines a set of additional custom values for document search results that can be defined and returned by an
 * application which is customizing the document search for a specific document type.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentSearchResultValuesContract {

    /**
     * Returns an unmodifiable list of the result values, one for each customized document.
     *
     * @return the list of customized document search result values, will never be null but may be empty
     */
    List<? extends DocumentSearchResultValueContract> getResultValues();

}
