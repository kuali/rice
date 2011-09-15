package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.kew.api.document.attribute.DocumentAttributeContract;

import java.util.List;
import java.util.Map;

/**
 * Defines the contract for an object containing a customized result value for a specific document which is part of a
 * set of document lookup results.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentLookupResultValueContract {

    /**
     * Returns the id of the document for which this customized result value applies.
     *
     * @return the document id of the customized document lookup result
     */
    String getDocumentId();

    /**
     * Returns the customized document attribute values for this document lookup result.
     *
     * @return the customized document attribute values for this document lookup result
     */
    List<? extends DocumentAttributeContract> getDocumentAttributes();

}
