package org.kuali.rice.kew.api.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

@XmlRootElement(name = RuleResponsibility.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RuleResponsibility.Constants.TYPE_NAME, propOrder = {
    RuleResponsibility.Elements.PRIORITY,
    RuleResponsibility.Elements.RESPONSIBILITY_ID,
    RuleResponsibility.Elements.ACTION_REQUESTED_CD,
    RuleResponsibility.Elements.APPROVE_POLICY,
    RuleResponsibility.Elements.PRINCIPAL_ID,
    RuleResponsibility.Elements.GROUP_ID,
    RuleResponsibility.Elements.ROLE_NAME,
    RuleResponsibility.Elements.DELEGATION_RULES,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RuleResponsibility
    extends AbstractDataTransferObject
    implements RuleResponsibilityContract
{

    @XmlElement(name = Elements.PRIORITY, required = false)
    private final Integer priority;
    @XmlElement(name = Elements.RESPONSIBILITY_ID, required = false)
    private final String responsibilityId;
    @XmlElement(name = Elements.ACTION_REQUESTED_CD, required = false)
    private final String actionRequestedCd;
    @XmlElement(name = Elements.APPROVE_POLICY, required = false)
    private final String approvePolicy;
    @XmlElement(name = Elements.PRINCIPAL_ID, required = false)
    private final String principalId;
    @XmlElement(name = Elements.GROUP_ID, required = false)
    private final String groupId;
    @XmlElement(name = Elements.ROLE_NAME, required = false)
    private final String roleName;
    @XmlElement(name = Elements.DELEGATION_RULES, required = false)
    private final List<RuleDelegation> delegationRules;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private RuleResponsibility() {
        this.priority = null;
        this.responsibilityId = null;
        this.actionRequestedCd = null;
        this.approvePolicy = null;
        this.principalId = null;
        this.groupId = null;
        this.roleName = null;
        this.delegationRules = null;
    }

    private RuleResponsibility(Builder builder) {
        this.priority = builder.getPriority();
        this.responsibilityId = builder.getResponsibilityId();
        this.actionRequestedCd = builder.getActionRequestedCd();
        this.approvePolicy = builder.getApprovePolicy();
        this.principalId = builder.getPrincipalId();
        this.groupId = builder.getGroupId();
        this.roleName = builder.getRoleName();
        if (CollectionUtils.isNotEmpty(builder.getDelegationRules())) {
            List<RuleDelegation> delegationList = new ArrayList<RuleDelegation>();
            for (RuleDelegation.Builder b : builder.getDelegationRules()) {
                delegationList.add(b.build());
            }
            this.delegationRules = delegationList;
        } else {
            this.delegationRules = Collections.emptyList();
        }
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public String getResponsibilityId() {
        return this.responsibilityId;
    }

    @Override
    public String getActionRequestedCd() {
        return this.actionRequestedCd;
    }

    @Override
    public String getApprovePolicy() {
        return this.approvePolicy;
    }

    @Override
    public String getPrincipalId() {
        return this.principalId;
    }

    @Override
    public String getGroupId() {
        return this.groupId;
    }

    @Override
    public String getRoleName() {
        return this.roleName;
    }

    @Override
    public List<RuleDelegation> getDelegationRules() {
        return this.delegationRules;
    }


    /**
     * A builder which can be used to construct {@link RuleResponsibility} instances.  Enforces the constraints of the {@link RuleResponsibilityContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, RuleResponsibilityContract
    {

        private Integer priority;
        private String responsibilityId;
        private String actionRequestedCd;
        private String approvePolicy;
        private String principalId;
        private String groupId;
        private String roleName;
        private List<RuleDelegation.Builder> delegationRules;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(RuleResponsibilityContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setPriority(contract.getPriority());
            builder.setResponsibilityId(contract.getResponsibilityId());
            builder.setActionRequestedCd(contract.getActionRequestedCd());
            builder.setApprovePolicy(contract.getApprovePolicy());
            builder.setPrincipalId(contract.getPrincipalId());
            builder.setGroupId(contract.getGroupId());
            builder.setRoleName(contract.getRoleName());
            if (CollectionUtils.isNotEmpty(contract.getDelegationRules())) {
                List<RuleDelegation.Builder> builders = new ArrayList<RuleDelegation.Builder>();
                for (RuleDelegationContract delegationContract : contract.getDelegationRules()) {
                    builders.add(RuleDelegation.Builder.create(delegationContract));
                }
                builder.setDelegationRules(builders);
            } else {
                builder.setDelegationRules(Collections.<RuleDelegation.Builder>emptyList());
            }
            return builder;
        }

        public RuleResponsibility build() {
            return new RuleResponsibility(this);
        }

        @Override
        public Integer getPriority() {
            return this.priority;
        }

        @Override
        public String getResponsibilityId() {
            return this.responsibilityId;
        }

        @Override
        public String getActionRequestedCd() {
            return this.actionRequestedCd;
        }

        @Override
        public String getApprovePolicy() {
            return this.approvePolicy;
        }

        @Override
        public String getPrincipalId() {
            return this.principalId;
        }

        @Override
        public String getGroupId() {
            return this.groupId;
        }

        @Override
        public String getRoleName() {
            return this.roleName;
        }

        @Override
        public List<RuleDelegation.Builder> getDelegationRules() {
            return this.delegationRules;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public void setResponsibilityId(String responsibilityId) {
            this.responsibilityId = responsibilityId;
        }

        public void setActionRequestedCd(String actionRequestedCd) {
            this.actionRequestedCd = actionRequestedCd;
        }

        public void setApprovePolicy(String approvePolicy) {
            this.approvePolicy = approvePolicy;
        }

        public void setPrincipalId(String principalId) {
            this.principalId = principalId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public void setDelegationRules(List<RuleDelegation.Builder> delegationRules) {
            this.delegationRules = delegationRules;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "ruleResponsibility";
        final static String TYPE_NAME = "RuleResponsibilityType";

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String PRIORITY = "priority";
        final static String RESPONSIBILITY_ID = "responsibilityId";
        final static String ACTION_REQUESTED_CD = "actionRequestedCd";
        final static String APPROVE_POLICY = "approvePolicy";
        final static String PRINCIPAL_ID = "principalId";
        final static String GROUP_ID = "groupId";
        final static String ROLE_NAME = "roleName";
        final static String DELEGATION_RULES = "delegationRules";

    }

}