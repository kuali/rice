package org.kuali.rice.kew.api.document.lookup;

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;

import java.util.List;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentLookupConfigurationContract {

    String getDocumentTypeName();

    List<AttributeFields> getSearchAttributeFields();

    List<AttributeFields> getResultSetAttributeFields();

}
