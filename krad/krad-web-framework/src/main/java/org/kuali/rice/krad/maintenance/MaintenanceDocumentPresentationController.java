package org.kuali.rice.krad.maintenance;

import org.kuali.rice.krad.document.DocumentPresentationController;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MaintenanceDocumentPresentationController extends DocumentPresentationController {

    public boolean canCreate(Class boClass);

    public boolean canMaintain(Object dataObject);
}
