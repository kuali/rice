package org.kuali.rice.kns.service.impl;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.SessionDocumentService;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.UserSession;

import java.sql.Timestamp;

/**
 * A {@link SessionDocumentService} implementation which does nothing.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NoOpSessionDocumentServiceImpl implements SessionDocumentService{

    @Override
    public WorkflowDocument getDocumentFromSession(UserSession userSession, String docId) {
        return null;
    }

    @Override
    public void addDocumentToUserSession(UserSession userSession, WorkflowDocument document) {

    }

    @Override
    public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress) {

    }

    @Override
    public void purgeAllSessionDocuments(Timestamp expirationDate) {

    }

    @Override
    public KualiDocumentFormBase getDocumentForm(String documentNumber, String docFormKey, UserSession userSession,
            String ipAddress) {
        return null;
    }

    @Override
    public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession, String ipAddress) {

    }
}
