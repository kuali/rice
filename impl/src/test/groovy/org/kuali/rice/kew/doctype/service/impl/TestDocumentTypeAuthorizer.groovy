package org.kuali.rice.kew.doctype.service.impl

import org.kuali.rice.kew.doctype.bo.DocumentType
import org.kuali.rice.kew.engine.node.RouteNodeInstance
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue

/**
 * A custom document type permission service implementation that supplies additional permission details and role qualifiers
 */
class TestDocumentTypeAuthorizer extends KimDocumentTypeAuthorizer {
    static final ADDITIONAL_ENTRIES= [ ADDITIONAL_ENTRY: "ADDITIONAL_ENTRY" ];
    static final ADDITIONAL_DETAILS = ADDITIONAL_ENTRIES;
    static final ADDITIONAL_QUALIFIERS = ADDITIONAL_DETAILS;

    @Override
    def boolean useKimPermission(String namespace, String permissionTemplateName, Map<String, String> permissionDetails, boolean checkKimPriorityInd) { true }

    @Override
    protected Map<String, String> buildDocumentTypePermissionDetails(DocumentType documentType, String documentStatus, String actionRequestCode, String routeNodeName) {
        return super.buildDocumentTypePermissionDetails(documentType, documentStatus, actionRequestCode, routeNodeName) + ADDITIONAL_DETAILS
    }

    @Override
    protected Map<String, String> buildDocumentRoleQualifiers(DocumentRouteHeaderValue document, String routeNodeName) {
        return super.buildDocumentRoleQualifiers(document, routeNodeName) + ADDITIONAL_QUALIFIERS
    }
}