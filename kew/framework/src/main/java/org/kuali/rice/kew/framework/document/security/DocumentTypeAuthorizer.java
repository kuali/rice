package org.kuali.rice.kew.framework.document.security;

import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.document.Document;

import java.util.Map;

/**
 * Framework interface used by DocumentTypePermisionServiceAuthorizerImpl to allow
 * applications to customize document routing permission checks.
 * {@link #isActionAuthorized(AuthorizableAction, String, org.kuali.rice.kew.api.doctype.DocumentType, org.kuali.rice.kew.api.document.Document, java.util.Map)}
 * is invoked for applicable actions to check authorization.  Additional action-specific parameters are enumerated by {@link ActionArgument} enum and passed in the actionParameters argument
 * @since 2.1.3
 */
public interface DocumentTypeAuthorizer {
    /**
     * Type of additional arguments for {@link #isActionAuthorized(AuthorizableAction, String, org.kuali.rice.kew.api.doctype.DocumentType, org.kuali.rice.kew.api.document.Document, java.util.Map)}
     */
    public static enum ActionArgument {
        ROUTENODE_NAMES,
        DOCSTATUS
    }

    /**
     * Check whether specified action is authorized.
     * @param action the AuthorizableAction type, either a document action, initiation, or su approve action request check
     * @param principalId the principal id associated with the action
     * @param documentType the document type
     * @param document the document, if available/applicable (may be null)
     * @param actionParameters additional actionParameters if applicable to the AuthorizableAction check
     * @return Authorization object specifying whether the action was authorized
     */
    Authorization isActionAuthorized(AuthorizableAction action,
                                     String principalId,
                                     DocumentType documentType,
                                     Document document,
                                     Map<ActionArgument, Object> actionParameters);
}