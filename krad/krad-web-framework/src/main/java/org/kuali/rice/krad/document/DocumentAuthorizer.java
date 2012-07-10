/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    public boolean canEdit(Document document, Person user);

    public boolean canAnnotate(Document document, Person user);

    public boolean canReload(Document document, Person user);

    public boolean canClose(Document document, Person user);

    public boolean canSave(Document document, Person user);

    public boolean canRoute(Document document, Person user);

    public boolean canCancel(Document document, Person user);

    public boolean canCopy(Document document, Person user);

    public boolean canPerformRouteReport(Document document, Person user);

    public boolean canBlanketApprove(Document document, Person user);

    public boolean canApprove(Document document, Person user);

    public boolean canDisapprove(Document document, Person user);

    public boolean canSendNoteFyi(Document document, Person user);

    public boolean canEditDocumentOverview(Document document, Person user);

    public boolean canFyi(Document document, Person user);

    public boolean canAcknowledge(Document document, Person user);

    public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode);

    public boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user);

    public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode,
            String authorUniversalIdentifier, Person user);

    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, String authorUniversalIdentifier,
            Person user);

    public boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user);

    public boolean canSendAnyTypeAdHocRequests(Document document, Person user);

    public boolean canTakeRequestedAction(Document document, String actionRequestCode, Person user);

    /**
     * @since 2.1
     */
    public boolean canRecall(Document document, Person user);
}
