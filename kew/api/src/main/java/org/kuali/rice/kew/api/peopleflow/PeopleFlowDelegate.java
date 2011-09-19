package org.kuali.rice.kew.api.peopleflow;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.kew.api.action.DelegationType;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Collection;

@XmlRootElement(name = PeopleFlowDelegate.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PeopleFlowDelegate.Constants.TYPE_NAME, propOrder = {
    PeopleFlowDelegate.Elements.MEMBER_ID,
    PeopleFlowDelegate.Elements.MEMBER_TYPE,
    PeopleFlowDelegate.Elements.DELEGATION_TYPE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class PeopleFlowDelegate extends AbstractDataTransferObject implements PeopleFlowDelegateContract {

    @XmlElement(name = Elements.MEMBER_ID, required = true)
    private final String memberId;

    @XmlElement(name = Elements.MEMBER_TYPE, required = true)
    private final MemberType memberType;

    @XmlElement(name = Elements.DELEGATION_TYPE, required = true)
    private final DelegationType delegationType;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    @SuppressWarnings("unused")
    private PeopleFlowDelegate() {
        this.memberId = null;
        this.memberType = null;
        this.delegationType = null;
    }

    private PeopleFlowDelegate(Builder builder) {
        this.memberId = builder.getMemberId();
        this.memberType = builder.getMemberType();
        this.delegationType = builder.getDelegationType();
    }

    @Override
    public String getMemberId() {
        return this.memberId;
    }

    @Override
    public MemberType getMemberType() {
        return this.memberType;
    }

    @Override
    public DelegationType getDelegationType() {
        return this.delegationType;
    }

    /**
     * A builder which can be used to construct {@link PeopleFlowDelegate} instances.  Enforces the constraints of the
     * {@link PeopleFlowDelegateContract}.
     * 
     */
    public final static class Builder implements Serializable, ModelBuilder, PeopleFlowDelegateContract {

        private String memberId;
        private MemberType memberType;
        private DelegationType delegationType;

        private Builder(String memberId, MemberType memberType) {
            setMemberId(memberId);
            setMemberType(memberType);
            setDelegationType(DelegationType.SECONDARY);
        }

        public static Builder create(String memberId, MemberType memberType) {
            return new Builder(memberId, memberType);
        }

        public static Builder create(PeopleFlowDelegateContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getMemberId(), contract.getMemberType());
            builder.setDelegationType(contract.getDelegationType());
            return builder;
        }

        public PeopleFlowDelegate build() {
            return new PeopleFlowDelegate(this);
        }

        @Override
        public String getMemberId() {
            return this.memberId;
        }

        @Override
        public MemberType getMemberType() {
            return this.memberType;
        }

        @Override
        public DelegationType getDelegationType() {
            return this.delegationType;
        }

        public void setMemberId(String memberId) {
            if (StringUtils.isBlank(memberId)) {
                throw new IllegalArgumentException("memberId was a null or blank value");
            }
            this.memberId = memberId;
        }

        public void setMemberType(MemberType memberType) {
            if (memberType == null) {
                throw new IllegalArgumentException("memberType was null");
            }
            this.memberType = memberType;
        }

        public void setDelegationType(DelegationType delegationType) {
            if (delegationType == null) {
                throw new IllegalArgumentException("delegationType was null");
            }
            this.delegationType = delegationType;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "peopleFlowDelegate";
        final static String TYPE_NAME = "PeopleFlowDelegateType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String MEMBER_ID = "memberId";
        final static String MEMBER_TYPE = "memberType";
        final static String DELEGATION_TYPE = "delegationType";
    }

}