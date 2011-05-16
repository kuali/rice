package org.kuali.rice.kim.api.common.delegate;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Collection;

@XmlRootElement(name = Delegate.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Delegate.Constants.TYPE_NAME, propOrder = {
        Delegate.Elements.DELEGATION_TYPE_CODE,
        Delegate.Elements.MEMBER_ID,
        Delegate.Elements.MEMBER_TYPE_CODE,
        Delegate.Elements.QUALIFIER,
        Delegate.Elements.DELEGATION_ID,
        Delegate.Elements.ROLE_MEMBER_ID,
        Delegate.Elements.ACTIVE,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class Delegate implements ModelObjectComplete, DelegateContract {

    @XmlElement(name = Elements.DELEGATION_TYPE_CODE, required = false)
    private final String delegationTypeCode;
    @XmlElement(name = Elements.MEMBER_ID, required = false)
    private final String memberId;
    @XmlElement(name = Elements.MEMBER_TYPE_CODE, required = false)
    private final String memberTypeCode;
    @XmlElement(name = Elements.QUALIFIER, required = false)
    private final Attributes qualifier;
    @XmlElement(name = Elements.DELEGATION_ID, required = false)
    private final String delegationId;
    @XmlElement(name = Elements.ROLE_MEMBER_ID, required = false)
    private final String roleMemberId;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private Delegate() {
        this.delegationTypeCode = null;
        this.memberId = null;
        this.memberTypeCode = null;
        this.qualifier = null;
        this.delegationId = null;
        this.roleMemberId = null;
        this.active = false;
    }

    private Delegate(Builder builder) {
        this.delegationTypeCode = builder.getDelegationTypeCode();
        this.memberId = builder.getMemberId();
        this.memberTypeCode = builder.getMemberTypeCode();
        this.qualifier = builder.getQualifier();
        this.delegationId = builder.getDelegationId();
        this.roleMemberId = builder.getRoleMemberId();
        this.active = builder.isActive();
    }

    @Override
    public String getDelegationTypeCode() {
        return this.delegationTypeCode;
    }

    @Override
    public String getMemberId() {
        return this.memberId;
    }

    @Override
    public String getMemberTypeCode() {
        return this.memberTypeCode;
    }

    @Override
    public Attributes getQualifier() {
        return this.qualifier;
    }

    @Override
    public String getDelegationId() {
        return this.delegationId;
    }

    @Override
    public String getRoleMemberId() {
        return this.roleMemberId;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    /**
     * A builder which can be used to construct {@link Delegate} instances.  Enforces the constraints of the {@link DelegateContract}.
     */
    public static final class Builder implements Serializable, ModelBuilder, DelegateContract {

        private String delegationTypeCode;
        private String memberId;
        private String memberTypeCode;
        private Attributes qualifier;
        private String delegationId;
        private String roleMemberId;
        private boolean active;

        private Builder() {
            // TODO modify this constructor as needed to pass any required values and invoke the appropriate 'setter' methods
        }

        public static Builder create() {
            // TODO modify as needed to pass any required values and add them to the signature of the 'create' method
            return new Builder();
        }

        public static Builder create(DelegateContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            // TODO if create() is modified to accept required parameters, this will need to be modified
            Builder builder = create();
            builder.setDelegationTypeCode(contract.getDelegationTypeCode());
            builder.setMemberId(contract.getMemberId());
            builder.setMemberTypeCode(contract.getMemberTypeCode());
            builder.setQualifier(contract.getQualifier());
            builder.setDelegationId(contract.getDelegationId());
            builder.setRoleMemberId(contract.getRoleMemberId());
            builder.setActive(contract.isActive());
            return builder;
        }

        public Delegate build() {
            return new Delegate(this);
        }

        @Override
        public String getDelegationTypeCode() {
            return this.delegationTypeCode;
        }

        @Override
        public String getMemberId() {
            return this.memberId;
        }

        @Override
        public String getMemberTypeCode() {
            return this.memberTypeCode;
        }

        @Override
        public Attributes getQualifier() {
            return this.qualifier;
        }

        @Override
        public String getDelegationId() {
            return this.delegationId;
        }

        @Override
        public String getRoleMemberId() {
            return this.roleMemberId;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }

        public void setDelegationTypeCode(String delegationTypeCode) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.delegationTypeCode = delegationTypeCode;
        }

        public void setMemberId(String memberId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.memberId = memberId;
        }

        public void setMemberTypeCode(String memberTypeCode) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.memberTypeCode = memberTypeCode;
        }

        public void setQualifier(Attributes qualifier) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.qualifier = qualifier;
        }

        public void setDelegationId(String delegationId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.delegationId = delegationId;
        }

        public void setRoleMemberId(String roleMemberId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.roleMemberId = roleMemberId;
        }

        public void setActive(boolean active) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.active = active;
        }

    }


    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "delegate";
        final static String TYPE_NAME = "DelegateType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[]{CoreConstants.CommonElements.FUTURE_ELEMENTS};

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {

        final static String DELEGATION_TYPE_CODE = "delegationTypeCode";
        final static String MEMBER_ID = "memberId";
        final static String MEMBER_TYPE_CODE = "memberTypeCode";
        final static String QUALIFIER = "qualifier";
        final static String DELEGATION_ID = "delegationId";
        final static String ROLE_MEMBER_ID = "roleMemberId";
        final static String ACTIVE = "active";

    }

}
