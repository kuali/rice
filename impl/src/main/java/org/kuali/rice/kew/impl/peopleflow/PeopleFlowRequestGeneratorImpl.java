package org.kuali.rice.kew.impl.peopleflow;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegate;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.peopleflow.PeopleFlowTypeService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of the {@code PeopleFlowRequestGenerator} which is responsible for generating Action
 * Requests from a {@link PeopleFlowDefinition}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowRequestGeneratorImpl implements PeopleFlowRequestGenerator {

    private KewTypeRepositoryService typeRepositoryService;
    private RoleService roleService;

    @Override
    public List<ActionRequestValue> generateRequests(RouteContext routeContext, PeopleFlowDefinition peopleFlow, ActionRequestType actionRequested) {
        Context context = new Context(routeContext, peopleFlow, actionRequested);
        for (PeopleFlowMember member : peopleFlow.getMembers()) {
            generateRequestForMember(context, member);
        }
        return context.getActionRequestFactory().getRequestGraphs();
    }

    protected void generateRequestForMember(Context context, PeopleFlowMember member) {
        String actionRequestPolicyCode = null;
        if (member.getActionRequestPolicy() != null) {
            actionRequestPolicyCode = member.getActionRequestPolicy().getCode();
        }
        if (MemberType.ROLE == member.getMemberType()) {
            generateRequestForRoleMember(context, member, actionRequestPolicyCode);
        } else {
            ActionRequestValue actionRequest = context.getActionRequestFactory().addRootActionRequest(
                    context.getActionRequested().getCode(), member.getPriority(), toRecipient(member), "",
                    member.getResponsibilityId(), Boolean.TRUE, actionRequestPolicyCode, null);
            if (CollectionUtils.isNotEmpty(member.getDelegates())) {
                for (PeopleFlowDelegate delegate : member.getDelegates()) {
                    context.getActionRequestFactory().addDelegationRequest(actionRequest, toRecipient(delegate),
                            delegate.getResponsibilityId(), Boolean.TRUE, delegate.getDelegationType(), "", null);
                }
            }
        }
    }

    protected void generateRequestForRoleMember(Context context, PeopleFlowMember member, String actionRequestPolicyCode) {
        Map<String, String> roleQualifiers = loadRoleQualifiers(context, member);
        Role role = getRoleService().getRole(member.getMemberId());
        if (role == null) {
            throw new IllegalStateException("Failed to locate a role with the given role id of '" + member.getMemberId() + "'");
        }
        List<RoleMembership> memberships = getRoleService().getRoleMembers(Collections.singletonList(
                member.getMemberId()), roleQualifiers);
        if (!CollectionUtils.isEmpty(memberships)) {
            context.getActionRequestFactory().addKimRoleRequest(context.getActionRequested().getCode(), member.getPriority(),
                    role, memberships, null, member.getResponsibilityId(), true, actionRequestPolicyCode, null);
        }
        // TODO - KULRICE-5726 - still need to implement support for ignoring built-in kim delegates whenever peopleflow delegate(s) are defined
    }

    protected Map<String, String> loadRoleQualifiers(Context context, PeopleFlowMember member) {
        PeopleFlowTypeService peopleFlowTypeService = context.getPeopleFlowTypeService();
        if (peopleFlowTypeService != null) {
            Document document = DocumentRouteHeaderValue.to(context.getRouteContext().getDocument());
            DocumentRouteHeaderValueContent content = new DocumentRouteHeaderValueContent(document.getDocumentId());
            content.setDocumentContent(context.getRouteContext().getDocumentContent().getDocContent());
            DocumentContent documentContent = DocumentRouteHeaderValueContent.to(content);
            Map<String, String> roleQualifiers = peopleFlowTypeService.resolveRoleQualifiers(
                    context.getPeopleFlow().getTypeId(), member.getMemberId(), document, documentContent);
            if (roleQualifiers != null) {
                return roleQualifiers;
            }
        }
        return Collections.emptyMap();
    }

    private Recipient toRecipient(PeopleFlowMember member) {
        Recipient recipient;
        if (MemberType.PRINCIPAL == member.getMemberType()) {
            recipient = new KimPrincipalRecipient(member.getMemberId());
        } else if (MemberType.GROUP == member.getMemberType()) {
            recipient = new KimGroupRecipient(member.getMemberId());
        } else {
            throw new IllegalStateException("encountered a member type which I did not understand: " +
                    member.getMemberType());
        }
        return recipient;
    }

    private Recipient toRecipient(PeopleFlowDelegate delegate) {
        Recipient recipient;
        if (MemberType.PRINCIPAL == delegate.getMemberType()) {
            recipient = new KimPrincipalRecipient(delegate.getMemberId());
        } else if (MemberType.GROUP == delegate.getMemberType()) {
            recipient = new KimGroupRecipient(delegate.getMemberId());
        } else {
            throw new IllegalStateException("encountered a delegate member type which I did not understand: " +
                    delegate.getMemberType());
        }
        return recipient;
    }

    public KewTypeRepositoryService getTypeRepositoryService() {
        return typeRepositoryService;
    }

    public void setTypeRepositoryService(KewTypeRepositoryService typeRepositoryService) {
        this.typeRepositoryService = typeRepositoryService;
    }

    public RoleService getRoleService() {
        return roleService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * A simple class used to hold context during the PeopleFlow action request generation process.  Construction of
     * the context will validate that the given values are valid, non-null values where appropriate.
     */
    final class Context {

        private final RouteContext routeContext;
        private final PeopleFlowDefinition peopleFlow;
        private final ActionRequestType actionRequested;
        private final ActionRequestFactory actionRequestFactory;

        // lazily loaded
        private PeopleFlowTypeService peopleFlowTypeService;
        private boolean peopleFlowTypeServiceLoaded = false;

        Context(RouteContext routeContext, PeopleFlowDefinition peopleFlow, ActionRequestType actionRequested) {
            if (routeContext == null) {
                throw new IllegalArgumentException("routeContext was null");
            }
            if (peopleFlow == null) {
                throw new IllegalArgumentException("peopleFlow was null");
            }
            if (!peopleFlow.isActive()) {
                throw new ConfigurationException("Attempted to route to a PeopleFlow that is not active! " + peopleFlow);
            }
            if (actionRequested == null) {
                actionRequested = ActionRequestType.APPROVE;
            }
            this.routeContext = routeContext;
            this.peopleFlow = peopleFlow;
            this.actionRequested = actionRequested;
            this.actionRequestFactory = new ActionRequestFactory(routeContext);
        }

        RouteContext getRouteContext() {
            return routeContext;
        }

        PeopleFlowDefinition getPeopleFlow() {
            return peopleFlow;
        }

        ActionRequestType getActionRequested() {
            return actionRequested;
        }

        ActionRequestFactory getActionRequestFactory() {
            return actionRequestFactory;
        }

        /**
         * Lazily loads and caches the {@code PeopleFlowTypeService} (if necessary) and returns it.
         */
        PeopleFlowTypeService getPeopleFlowTypeService() {
            if (peopleFlowTypeServiceLoaded) {
                return this.peopleFlowTypeService;
            }
            if (getPeopleFlow().getTypeId() != null) {
                KewTypeDefinition typeDefinition = getTypeRepositoryService().getTypeById(getPeopleFlow().getTypeId());
                if (typeDefinition == null) {
                    throw new IllegalStateException("Failed to locate a PeopleFlow type for the given type id of '" + getPeopleFlow().getTypeId() + "'");
                }
                if (StringUtils.isNotBlank(typeDefinition.getServiceName())) {
                    this.peopleFlowTypeService = GlobalResourceLoader.getService(QName.valueOf(typeDefinition.getServiceName()));
                    if (this.peopleFlowTypeService == null) {
                        throw new IllegalStateException("Failed to load the PeopleFlowTypeService with the name '" + typeDefinition.getServiceName() + "'");
                     }
                }
            }
            peopleFlowTypeServiceLoaded = true;
            return this.peopleFlowTypeService;
        }

    }
}
