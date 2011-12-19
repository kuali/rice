package org.kuali.rice.krad.maintenance;

import org.kuali.rice.krad.document.DocumentPresentationControllerBase;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceDocumentPresentationControllerBase extends DocumentPresentationControllerBase
        implements MaintenanceDocumentPresentationController {
    private static final long serialVersionUID = 2849921477944820474L;

    private transient DocumentDictionaryService documentDictionaryService;

    public boolean canCreate(Class boClass) {
        return getDocumentDictionaryService().getAllowsNewOrCopy(
                getDocumentDictionaryService().getMaintenanceDocumentTypeName(boClass));
    }

    public boolean canMaintain(Object dataObject) {
        return true;
    }

    protected DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
