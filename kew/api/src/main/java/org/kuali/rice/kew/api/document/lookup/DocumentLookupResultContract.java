package org.kuali.rice.kew.api.document.lookup;

import org.kuali.rice.kew.api.document.DocumentContract;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;

import java.util.List;

public interface DocumentLookupResultContract extends DocumentContract {

    List<? extends DocumentLookupResultDataContract<?>> getResultData();

}
