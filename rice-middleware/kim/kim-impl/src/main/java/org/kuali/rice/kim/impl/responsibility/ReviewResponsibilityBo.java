package org.kuali.rice.kim.impl.responsibility;

import org.kuali.rice.kim.api.KimConstants;

import java.util.Map;

public class ReviewResponsibilityBo extends ResponsibilityBo {
    private static final long serialVersionUID = 1L;
    public static final String ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL_FIELD_NAME = "actionDetailsAtRoleMemberLevel";
    private String documentTypeName;
    private String routeNodeName;
    private boolean actionDetailsAtRoleMemberLevel;
    private boolean required;
    private String qualifierResolverProvidedIdentifier;

    public ReviewResponsibilityBo() { }

    public ReviewResponsibilityBo(ResponsibilityBo resp) {
        loadFromKimResponsibility(resp);
    }

    public void loadFromKimResponsibility(final ResponsibilityBo resp) {
        setId(resp.getId());
        setNamespaceCode(resp.getNamespaceCode());
        setName(resp.getName());
        setDescription(resp.getDescription());
        setActive(resp.isActive());
        setTemplateId(resp.getTemplate() != null ? resp.getTemplate().getId() : null);
        setTemplate(resp.getTemplate());
        setAttributes(resp.getAttributes());
        setVersionNumber(resp.getVersionNumber());
        setObjectId(resp.getObjectId());

        Map<String, String> respDetails = resp.getAttributes();
        documentTypeName = respDetails.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        routeNodeName = respDetails.get(KimConstants.AttributeConstants.ROUTE_NODE_NAME);
        actionDetailsAtRoleMemberLevel = Boolean.valueOf(respDetails.get(KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL));
        required = Boolean.valueOf(respDetails.get(KimConstants.AttributeConstants.REQUIRED));
        qualifierResolverProvidedIdentifier = respDetails.get(KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER);
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public String getRouteNodeName() {
        return routeNodeName;
    }

    public void setRouteNodeName(String routeNodeName) {
        this.routeNodeName = routeNodeName;
    }

    public boolean getActionDetailsAtRoleMemberLevel() {
        return actionDetailsAtRoleMemberLevel;
    }

    public boolean isActionDetailsAtRoleMemberLevel() {
        return actionDetailsAtRoleMemberLevel;
    }

    public void setActionDetailsAtRoleMemberLevel(boolean actionDetailsAtRoleMemberLevel) {
        this.actionDetailsAtRoleMemberLevel = actionDetailsAtRoleMemberLevel;
    }

    public boolean getRequired() {
        return required;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getQualifierResolverProvidedIdentifier() {
        return qualifierResolverProvidedIdentifier;
    }

    public void setQualifierResolverProvidedIdentifier(String qualifierResolverProvidedIdentifier) {
        this.qualifierResolverProvidedIdentifier = qualifierResolverProvidedIdentifier;
    }


}
