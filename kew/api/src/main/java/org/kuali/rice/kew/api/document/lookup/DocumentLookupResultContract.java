package org.kuali.rice.kew.api.document.lookup;

import org.kuali.rice.kew.api.document.DocumentContract;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeContract;

import java.util.List;

public interface DocumentLookupResultContract {

    DocumentContract getDocument();

    List<? extends DocumentAttributeContract> getDocumentAttributes();

}
