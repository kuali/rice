package org.kuali.rice.kim.api.common.delegate;

import org.apache.commons.collections.CollectionUtils;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = DelegateType.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DelegateType.Constants.TYPE_NAME, propOrder = {
    DelegateType.Elements.KIM_TYPE_ID,
    DelegateType.Elements.DELEGATION_TYPE_CODE,
    DelegateType.Elements.DELEGATION_ID,
    DelegateType.Elements.ROLE_ID,
    DelegateType.Elements.MEMBERS,
    DelegateType.Elements.ACTIVE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DelegateType
    implements ModelObjectComplete, DelegateTypeContract
{

    @XmlElement(name = Elements.KIM_TYPE_ID, required = false)
    private final String kimTypeId;
    @XmlElement(name = Elements.DELEGATION_TYPE_CODE, required = false)
    private final String delegationTypeCode;
    @XmlElement(name = Elements.DELEGATION_ID, required = false)
    private final String delegationId;
    @XmlElement(name = Elements.ROLE_ID, required = false)
    private final String roleId;
    @XmlElement(name = Elements.MEMBERS, required = false)
    private final List<DelegateMember> members;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private DelegateType() {
        this.kimTypeId = null;
        this.delegationTypeCode = null;
        this.delegationId = null;
        this.roleId = null;
        this.members = null;
        this.active = false;
    }

    private DelegateType(Builder builder) {
        this.kimTypeId = builder.getKimTypeId();
        this.delegationTypeCode = builder.getDelegationTypeCode();
        this.delegationId = builder.getDelegationId();
        this.roleId = builder.getRoleId();

        final List<DelegateMember> temp = new ArrayList<DelegateMember>();
        if (!CollectionUtils.isEmpty(builder.getMembers())) {
            for (DelegateMember.Builder delegate: builder.getMembers()) {
                temp.add(delegate.build());
            }
        }
        this.members = Collections.unmodifiableList(temp);
        this.active = builder.isActive();
    }

    @Override
    public String getKimTypeId() {
        return this.kimTypeId;
    }

    @Override
    public String getDelegationTypeCode() {
        return this.delegationTypeCode;
    }

    @Override
    public String getDelegationId() {
        return this.delegationId;
    }

    @Override
    public String getRoleId() {
        return this.roleId;
    }

    @Override
    public List<DelegateMember> getMembers() {
        return this.members;
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
     * A builder which can be used to construct {@link DelegateType} instances.  Enforces the constraints of the {@link DelegateTypeContract}.
     * 
     */
    public static final class Builder
        implements Serializable, ModelBuilder, DelegateTypeContract
    {

        private String kimTypeId;
        private String delegationTypeCode;
        private String delegationId;
        private String roleId;
        private List<DelegateMember.Builder> members;
        private boolean active;

        private Builder() {
            // TODO modify this constructor as needed to pass any required values and invoke the appropriate 'setter' methods
        }

        public static Builder create() {
            // TODO modify as needed to pass any required values and add them to the signature of the 'create' method
            return new Builder();
        }

        public static Builder create(DelegateTypeContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            // TODO if create() is modified to accept required parameters, this will need to be modified
            Builder builder = create();
            builder.setKimTypeId(contract.getKimTypeId());
            builder.setDelegationTypeCode(contract.getDelegationTypeCode());
            builder.setDelegationId(contract.getDelegationId());
            builder.setRoleId(contract.getRoleId());
            final List<DelegateMember.Builder> builders = new ArrayList<DelegateMember.Builder>();
            for (DelegateMemberContract c : contract.getMembers()) {
                builders.add(DelegateMember.Builder.create(c));
            }

            builder.setMembers(builders);
            builder.setActive(contract.isActive());
            return builder;
        }

        public DelegateType build() {
            return new DelegateType(this);
        }

        @Override
        public String getKimTypeId() {
            return this.kimTypeId;
        }

        @Override
        public String getDelegationTypeCode() {
            return this.delegationTypeCode;
        }

        @Override
        public String getDelegationId() {
            return this.delegationId;
        }

        @Override
        public String getRoleId() {
            return this.roleId;
        }

        @Override
        public List<DelegateMember.Builder> getMembers() {
            return this.members;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }

        public void setKimTypeId(String kimTypeId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.kimTypeId = kimTypeId;
        }

        public void setDelegationTypeCode(String delegationTypeCode) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.delegationTypeCode = delegationTypeCode;
        }

        public void setDelegationId(String delegationId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.delegationId = delegationId;
        }

        public void setRoleId(String roleId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.roleId = roleId;
        }

        public void setMembers(List<DelegateMember.Builder> members) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.members = Collections.unmodifiableList(new ArrayList<DelegateMember.Builder>(members));
        }

        public void setActive(boolean active) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.active = active;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "delegateType";
        final static String TYPE_NAME = "DelegateTypeType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String KIM_TYPE_ID = "kimTypeId";
        final static String DELEGATION_TYPE_CODE = "delegationTypeCode";
        final static String DELEGATION_ID = "delegationId";
        final static String ROLE_ID = "roleId";
        final static String MEMBERS = "members";
        final static String ACTIVE = "active";

    }

}