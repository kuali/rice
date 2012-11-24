package org.kuali.rice.krad.uif.view;

import org.kuali.rice.krad.uif.UifConstants;

/**
 * View type for Transactional documents
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TransactionalDocumentView extends DocumentView {
    private static final long serialVersionUID = 4375336878804984171L;

    public TransactionalDocumentView() {
        super();

        setViewTypeName(UifConstants.ViewType.TRANSACTIONAL);
    }
}
