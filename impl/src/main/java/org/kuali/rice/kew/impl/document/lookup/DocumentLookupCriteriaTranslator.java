package org.kuali.rice.kew.impl.document.lookup;

import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;

import java.util.Map;

/**
 * Handles translating between parameters submitted to the document lookup and {@link org.kuali.rice.kew.api.document.search.DocumentSearchCriteria}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentLookupCriteriaTranslator {

    /**
     * Translates the given map of fields values into a {@link org.kuali.rice.kew.api.document.search.DocumentSearchCriteria}.  The given map of
     * field values is keyed based on the name of the field being submitted and the value represents that field value,
     * which may contain wildcards and other logical operators supported by the KNS lookup framework.
     *
     * @param fieldValues the map of field names and values from which to populate the criteria
     * @return populated document lookup criteria which contains the various criteria components populated based on the
     * interpretation of the given field values
     */
    DocumentSearchCriteria translateFieldsToCriteria(Map<String, String> fieldValues);

    /**
     * Translates the given {@link org.kuali.rice.kew.api.document.search.DocumentSearchCriteria} into a map of fields values.  Reverse of
     * {@link #translateFieldsToCriteria(java.util.Map)}
     *
     * @param criteria document lookup criteria
     * @return Map populated with the various criteria components
     */
    Map<String, String[]> translateCriteriaToFields(DocumentSearchCriteria criteria);
}