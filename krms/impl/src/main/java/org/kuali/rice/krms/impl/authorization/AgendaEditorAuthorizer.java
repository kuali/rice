package org.kuali.rice.krms.impl.authorization;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;

import java.util.HashSet;
import java.util.Set;

public class AgendaEditorAuthorizer extends DocumentAuthorizerBase implements MaintenanceDocumentAuthorizer {

    @Override
    public boolean canCreate(Class boClass, Person user) {
        return getAgendaAuthorizationService().hasPermission(KrmsConstants.MAINTAIN_KRMS_AGENDA);
    }

    @Override
    public boolean canMaintain(Object dataObject, Person user) {
        return getAgendaAuthorizationService().hasPermission(KrmsConstants.MAINTAIN_KRMS_AGENDA);
    }

    @Override
    public boolean canCreateOrMaintain(MaintenanceDocument maintenanceDocument, Person user) {
        return getAgendaAuthorizationService().hasPermission(KrmsConstants.MAINTAIN_KRMS_AGENDA);
    }

    @Override
    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getSecurePotentiallyHiddenSectionIds() {
        return new HashSet<String>();
    }

    private AgendaAuthorizationService getAgendaAuthorizationService() {
        return KrmsRepositoryServiceLocator.getAgendaAuthorizationService();
    }
}
