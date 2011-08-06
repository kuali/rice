package org.kuali.rice.kew.framework.document.lookup;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;

import java.util.List;

/**
 * Handles defining how results from document lookup should be displayed and processed.  Implementations
 * of this class can be created by application owners and tied to Document Types as attributes in order
 * to customize the behavior of document lookup for documents of that type.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentLookupResultProcessor {

    List<RemotableAttributeField> getResultSetFields(DocumentLookupCriteria documentLookupCriteria);

    List<DocumentLookupResult> processResults(DocumentLookupCriteria documentLookupCriteria, List<DocumentLookupResult> documentResult);

}
