package org.kuali.rice.kew.docsearch;

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.doctype.bo.DocumentType;

import javax.jws.WebParam;
import java.util.List;

/**
 * Handles communication between {@link org.kuali.rice.kew.framework.docsearch.DocumentSearchCustomizationService}
 * endpoints in order to invoke document search customizations from various client applications.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentSearchCustomizationMediator {

    List<RemotableAttributeField> getSearchFields(DocumentType documentType);

}
