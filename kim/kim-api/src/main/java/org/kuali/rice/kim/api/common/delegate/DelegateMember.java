package org.kuali.rice.kim.api.common.delegate;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Collection;

@XmlRootElement(name = DelegateMember.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DelegateMember.Constants.TYPE_NAME, propOrder = {
        DelegateMember.Elements.MEMBER_NAME,
        DelegateMember.Elements.MEMBER_NAMESPACE_CODE,
        DelegateMember.Elements.DELEGATION_MEMBER_ID,
        DelegateMember.Elements.ROLE_ID,
        DelegateMember.Elements.DELEGATE,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DelegateMember
        implements ModelObjectComplete, DelegateMemberContract {

    @XmlElement(name = Elements.MEMBER_NAME, required = false)
    private final String memberName;
    @XmlElement(name = Elements.MEMBER_NAMESPACE_CODE, required = false)
    private final String memberNamespaceCode;
    @XmlElement(name = Elements.DELEGATION_MEMBER_ID, required = false)
    private final String delegationMemberId;
    @XmlElement(name = Elements.ROLE_ID, required = false)
    private final String roleId;
    @XmlElement(name = Elements.DELEGATE, required = false)
    private final Delegate delegate;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private DelegateMember() {
        this.memberName = null;
        this.memberNamespaceCode = null;
        this.delegationMemberId = null;
        this.roleId = null;
        this.delegate = null;
    }

    private DelegateMember(Builder builder) {
        this.memberName = builder.getMemberName();
        this.memberNamespaceCode = builder.getMemberNamespaceCode();
        this.delegationMemberId = builder.getDelegationMemberId();
        this.roleId = builder.getRoleId();
        this.delegate = builder.getDelegate().build();
    }

    @Override
    public String getMemberName() {
        return this.memberName;
    }

    @Override
    public String getMemberNamespaceCode() {
        return this.memberNamespaceCode;
    }

    @Override
    public String getDelegationMemberId() {
        return this.delegationMemberId;
    }

    @Override
    public String getRoleId() {
        return this.roleId;
    }

    @Override
    public Delegate getDelegate() {
        return this.delegate;
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
     * A builder which can be used to construct {@link DelegateMember} instances.  Enforces the constraints of the {@link DelegateMemberContract}.
     */
    public static final class Builder implements Serializable, ModelBuilder, DelegateMemberContract {

        private String memberName;
        private String memberNamespaceCode;
        private String delegationMemberId;
        private String roleId;
        private Delegate.Builder delegate;

        private Builder() {
            // TODO modify this constructor as needed to pass any required values and invoke the appropriate 'setter' methods
        }

        public static Builder create() {
            // TODO modify as needed to pass any required values and add them to the signature of the 'create' method
            return new Builder();
        }

        public static Builder create(DelegateMemberContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            // TODO if create() is modified to accept required parameters, this will need to be modified
            Builder builder = create();
            builder.setMemberName(contract.getMemberName());
            builder.setMemberNamespaceCode(contract.getMemberNamespaceCode());
            builder.setDelegationMemberId(contract.getDelegationMemberId());
            builder.setRoleId(contract.getRoleId());
            builder.setDelegate(Delegate.Builder.create(contract.getDelegate()));
            return builder;
        }

        public DelegateMember build() {
            return new DelegateMember(this);
        }

        @Override
        public String getMemberName() {
            return this.memberName;
        }

        @Override
        public String getMemberNamespaceCode() {
            return this.memberNamespaceCode;
        }

        @Override
        public String getDelegationMemberId() {
            return this.delegationMemberId;
        }

        @Override
        public String getRoleId() {
            return this.roleId;
        }

        @Override
        public Delegate.Builder getDelegate() {
            return this.delegate;
        }

        public void setMemberName(String memberName) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.memberName = memberName;
        }

        public void setMemberNamespaceCode(String memberNamespaceCode) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.memberNamespaceCode = memberNamespaceCode;
        }

        public void setDelegationMemberId(String delegationMemberId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.delegationMemberId = delegationMemberId;
        }

        public void setRoleId(String roleId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.roleId = roleId;
        }

        public void setDelegate(Delegate.Builder delegate) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.delegate = delegate;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "delegateMember";
        final static String TYPE_NAME = "DelegateMemberType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[]{CoreConstants.CommonElements.FUTURE_ELEMENTS};

    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {

        final static String MEMBER_NAME = "memberName";
        final static String MEMBER_NAMESPACE_CODE = "memberNamespaceCode";
        final static String DELEGATION_MEMBER_ID = "delegationMemberId";
        final static String ROLE_ID = "roleId";
        final static String DELEGATE = "delegate";

    }

}