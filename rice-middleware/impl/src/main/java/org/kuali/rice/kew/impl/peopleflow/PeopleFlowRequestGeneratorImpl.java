/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kew.impl.peopleflow;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.VersionHelper;
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
import org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.bus.Endpoint;

import javax.xml.namespace.QName;
import java.util.ArrayList;
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

        // used later for generating any delegate requests
        List<ActionRequestValue> memberRequests = new ArrayList<ActionRequestValue>();

        if (member.getActionRequestPolicy() != null) {
            actionRequestPolicyCode = member.getActionRequestPolicy().getCode();
        }

        if (MemberType.ROLE == member.getMemberType()) {
            memberRequests.addAll(generateRequestForRoleMember(context, member, actionRequestPolicyCode));
        } else {
            ActionRequestValue actionRequest = context.getActionRequestFactory().addRootActionRequest(
                    context.getActionRequested().getCode(), member.getPriority(), toRecipient(member), "",
                    member.getResponsibilityId(), Boolean.TRUE, actionRequestPolicyCode, null);

            if (actionRequest != null) {
                memberRequests.add(actionRequest);
            }
        }

        // generate delegate requests
        // NOTE: if the member type is ROLE, and the nested peopleFlowTypeService.resolveMultipleRoleQualifiers calls
        // returns N results for the member and M results for the delegate, this will generate N x M requests
        // TODO: factor out helper methods to clean this block up
        if (!CollectionUtils.isEmpty(member.getDelegates())) {
            for (PeopleFlowDelegate delegate : member.getDelegates()) {
                for (ActionRequestValue memberRequest : memberRequests) {
                    if (MemberType.ROLE == delegate.getMemberType()) {
                        generateDelegateToRoleRequests(memberRequest, context, member, delegate,
                                actionRequestPolicyCode);
                    } else {
                        Recipient recipient;

                        if (MemberType.PRINCIPAL == delegate.getMemberType()) {
                            recipient = new KimPrincipalRecipient(delegate.getMemberId());
                        } else if (MemberType.GROUP == delegate.getMemberType()) {
                            recipient = new KimGroupRecipient(delegate.getMemberId());
                        } else {
                            throw new RiceIllegalStateException("MemberType unknown: " + delegate.getMemberType());
                        }

                        // TODO: delegation annotation
                        String delegationAnnotation = "TODO: delegation annotation";

                        context.getActionRequestFactory().addDelegationRequest(memberRequest, recipient,
                                delegate.getResponsibilityId(), memberRequest.getForceAction(),
                                delegate.getDelegationType(), delegationAnnotation, null);
                    }
                }
            }
        }
    }

    protected List<ActionRequestValue> generateRequestForRoleMember(Context context, PeopleFlowMember member, String actionRequestPolicyCode) {
        List<ActionRequestValue> roleMemberRequests = new ArrayList<ActionRequestValue>(); // results

        List<Map<String, String>> roleQualifierList = loadRoleQualifiers(context, member.getMemberId());
        Role role = getRoleService().getRole(member.getMemberId());

        boolean hasPeopleFlowDelegates = !CollectionUtils.isEmpty(member.getDelegates());

        if (role == null) {
            throw new IllegalStateException("Failed to locate a role with the given role id of '" +
                    member.getMemberId() + "'");
        }

        if (CollectionUtils.isEmpty(roleQualifierList)) {
            roleMemberRequests.add(
                    addKimRoleRequest(context, role, member, Collections.<String, String>emptyMap(),
                            actionRequestPolicyCode, hasPeopleFlowDelegates )
            );
        } else {
            for (Map<String, String> roleQualifiers : roleQualifierList) {
                roleMemberRequests.add(addKimRoleRequest(context, role, member, roleQualifiers, actionRequestPolicyCode,
                        hasPeopleFlowDelegates ));
            }
        }

        return roleMemberRequests;
    }

    protected List<ActionRequestValue> generateDelegateToRoleRequests(ActionRequestValue parentRequest, Context context,
            PeopleFlowMember member, PeopleFlowDelegate delegate, String actionRequestPolicyCode) {

        List<ActionRequestValue> roleMemberRequests = new ArrayList<ActionRequestValue>(); // results

        List<Map<String, String>> roleQualifierList = loadRoleQualifiers(context, member.getMemberId());
        Role role = getRoleService().getRole(delegate.getMemberId());

        if (role == null) {
            throw new IllegalStateException("Failed to locate a role with the given role id of '" + member.getMemberId() + "'");
        }

        if (CollectionUtils.isEmpty(roleQualifierList)) {
            roleMemberRequests.add(
                    addKimRoleDelegateRequest(parentRequest, context, role, member, delegate,
                            Collections.<String, String>emptyMap(), actionRequestPolicyCode)
            );
        } else {
            for (Map<String, String> roleQualifiers : roleQualifierList) {
                roleMemberRequests.add(addKimRoleDelegateRequest(parentRequest, context, role, member, delegate,
                        roleQualifiers, actionRequestPolicyCode));
            }
        }

        return roleMemberRequests;
    }

    private ActionRequestValue addKimRoleRequest(Context context, Role role, PeopleFlowMember member, Map<String, String> roleQualifiers,
            String actionRequestPolicyCode, boolean ignoreKimDelegates) {
        ActionRequestValue roleMemberRequest = null;

        List<RoleMembership> memberships = getRoleService().getRoleMembers(Collections.singletonList(
                member.getMemberId()), roleQualifiers);

        if (!CollectionUtils.isEmpty(memberships)) {
            roleMemberRequest = context.getActionRequestFactory().addKimRoleRequest(
                    context.getActionRequested().getCode(), member.getPriority(), role, memberships, null,
                    member.getResponsibilityId(), true, actionRequestPolicyCode, null, ignoreKimDelegates);
        }

        return roleMemberRequest;
    }

    private ActionRequestValue addKimRoleDelegateRequest(ActionRequestValue parentRequest, Context context, Role role, PeopleFlowMember member,
            PeopleFlowDelegate delegate, Map<String, String> roleQualifiers, String actionRequestPolicyCode) {

        if (MemberType.ROLE != delegate.getMemberType()) {
            throw new IllegalArgumentException("delegate member must be of type ROLE");
        }

        ActionRequestValue roleMemberRequest = null;

        List<RoleMembership> memberships = getRoleService().getRoleMembers(Collections.singletonList(
                delegate.getMemberId()), roleQualifiers);

        if (!CollectionUtils.isEmpty(memberships)) {
            roleMemberRequest = context.getActionRequestFactory().addDelegateKimRoleRequest(parentRequest,
                    delegate.getDelegationType(), context.getActionRequested().getCode(), member.getPriority(), role,
                    memberships, null, delegate.getResponsibilityId(), true, actionRequestPolicyCode, null);
        }

        return roleMemberRequest;
    }

    protected List<Map<String, String>> loadRoleQualifiers(Context context, String roleId) {
        PeopleFlowTypeService peopleFlowTypeService = context.getPeopleFlowTypeService();
        List<Map<String, String>> roleQualifierList = new ArrayList<Map<String, String>>();

        if (peopleFlowTypeService != null) {
            Document document = DocumentRouteHeaderValue.to(context.getRouteContext().getDocument());
            DocumentRouteHeaderValueContent content = new DocumentRouteHeaderValueContent(document.getDocumentId());
            content.setDocumentContent(context.getRouteContext().getDocumentContent().getDocContent());
            DocumentContent documentContent = DocumentRouteHeaderValueContent.to(content);

            Map<String, String> roleQualifiers = peopleFlowTypeService.resolveRoleQualifiers(
                    context.getPeopleFlow().getTypeId(), roleId, document, documentContent
            );

            if (roleQualifiers != null) {
                roleQualifierList.add(roleQualifiers);
            }

            boolean versionOk = VersionHelper.compareVersion(context.getPeopleFlowTypeServiceVersion(), CoreConstants.Versions.VERSION_2_3_0) != -1;
            if(versionOk) {
                List<Map<String, String>> multipleRoleQualifiers = peopleFlowTypeService.resolveMultipleRoleQualifiers(
                        context.getPeopleFlow().getTypeId(), roleId, document, documentContent);

                if (multipleRoleQualifiers != null) {
                    roleQualifierList.addAll(multipleRoleQualifiers);
                }
            }

        }

        return roleQualifierList;
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
        private String peopleFlowTypeServiceVersion;

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
                    Endpoint endpoint = KsbApiServiceLocator.getServiceBus().getEndpoint(QName.valueOf(typeDefinition.getServiceName()));

                    if (endpoint == null) {
                        throw new IllegalStateException("Failed to load the PeopleFlowTypeService with the name '" + typeDefinition.getServiceName() + "'");
                    }

                    this.peopleFlowTypeService = (PeopleFlowTypeService)endpoint.getService();
                    this.peopleFlowTypeServiceVersion = endpoint.getServiceConfiguration().getServiceVersion();
                }
            }
            peopleFlowTypeServiceLoaded = true;
            return this.peopleFlowTypeService;
        }

        String getPeopleFlowTypeServiceVersion() {
            if (!this.peopleFlowTypeServiceLoaded) {
                // execute getPeopleFlowTypeService first to lazy load
                getPeopleFlowTypeService();
            }

            return this.peopleFlowTypeServiceVersion;
        }
    }
}
