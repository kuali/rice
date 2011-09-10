package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.kew.api.document.attribute.DocumentAttributeContract;

import java.util.List;
import java.util.Map;

public interface DocumentLookupResultValueContract {

    String getDocumentId();

    List<? extends DocumentAttributeContract> getDocumentAttributes();

}
