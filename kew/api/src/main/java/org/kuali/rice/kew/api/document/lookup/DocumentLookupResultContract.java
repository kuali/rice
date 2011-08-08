package org.kuali.rice.kew.api.document.lookup;

import java.util.List;

public interface DocumentLookupResultContract {

    List<? extends DocumentLookupResultDataContract<?>> getResultData();

}
