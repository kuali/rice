package org.kuali.rice.krad.document;


/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentPresentationController {

    public boolean canInitiate(String documentTypeName);

    public boolean canEdit(Document document);

    public boolean canAnnotate(Document document);

    public boolean canReload(Document document);

    public boolean canClose(Document document);

    public boolean canSave(Document document);

    public boolean canRoute(Document document);

    public boolean canCancel(Document document);

    public boolean canCopy(Document document);

    public boolean canPerformRouteReport(Document document);

    public boolean canAddAdhocRequests(Document document);

    public boolean canBlanketApprove(Document document);

    public boolean canApprove(Document document);

    public boolean canDisapprove(Document document);

    public boolean canSendAdhocRequests(Document document);

    public boolean canSendNoteFyi(Document document);

    public boolean canEditDocumentOverview(Document document);

    public boolean canFyi(Document document);

    public boolean canAcknowledge(Document document);

}
