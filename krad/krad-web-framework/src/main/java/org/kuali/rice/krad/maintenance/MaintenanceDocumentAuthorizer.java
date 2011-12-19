package org.kuali.rice.krad.maintenance;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.DocumentAuthorizer;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MaintenanceDocumentAuthorizer extends DocumentAuthorizer {

    public boolean canCreate(Class boClass, Person user);

    public boolean canMaintain(Object dataObject, Person user);

    public boolean canCreateOrMaintain(MaintenanceDocument maintenanceDocument, Person user);

}
