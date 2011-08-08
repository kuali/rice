package org.kuali.rice.kew.docsearch;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfiguration;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.doctype.bo.DocumentType;

import javax.jws.WebParam;
import java.util.List;
import java.util.Map;

/**
 * Handles communication between {@link org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomizationHandlerService}
 * endpoints in order to invoke document search customizations from various client applications.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentLookupCustomizationMediator {

    DocumentLookupConfiguration getDocumentLookupConfiguration(DocumentType documentType);

    List<RemotableAttributeError> validateSearchFieldParameters(DocumentType documentType, Map<String, List<String>> parameters);

    DocSearchCriteriaDTO customizeCriteria(DocumentType documentType, DocSearchCriteriaDTO documentLookupCriteria);

    /**
     * TODO...
     *
     * return null if criteria was not cleared using a custom clear...this indicates a standard clear should be performed
     *
     * @param documentType
     * @param documentLookupCriteria
     * @return
     */
    DocSearchCriteriaDTO customizeClearCriteria(DocumentType documentType, DocSearchCriteriaDTO documentLookupCriteria);

    // TODO - Rice 2.0 - add additional customization points here

}


