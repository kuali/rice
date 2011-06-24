package org.kuali.rice.kew.api.note;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

public interface NoteContract extends Identifiable, Versioned {

    String getDocumentId();
    
    String getAuthorPrincipalId();
    
    DateTime getCreateDate();
    
    String getText();

}
