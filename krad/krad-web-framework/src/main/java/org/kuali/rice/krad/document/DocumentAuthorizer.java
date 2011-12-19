package org.kuali.rice.krad.document;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.DataObjectAuthorizer;

/**
 * Authorizer class for {@link Document} instances
 *
 * <p>
 * Authorizer provides user based authorization
 * </p>
 *
 * <p>
 * The document authorizer is associated with a document type through its data dictionary
 * {@link org.kuali.rice.krad.datadictionary.DocumentEntry}. This is then used by the framework to authorize certain
 * actions and in addition used for view presentation logic
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentAuthorizer extends DataObjectAuthorizer {

    public boolean canInitiate(String documentTypeName, Person user);

    public boolean canOpen(Document document, Person user);

    public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode);

    public boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user);

    public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode,
            String authorUniversalIdentifier, Person user);

    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, String authorUniversalIdentifier,
            Person user);

    public boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user);

    public boolean canEditDocumentOverview(Document document, Person user);

    public boolean canSendAnyTypeAdHocRequests(Document document, Person user);

    public boolean canTakeRequestedAction(Document document, String actionRequestCode, Person user);
}
