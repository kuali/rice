package org.kuali.rice.kew.api.peopleflow;

import java.io.Serializable;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

@XmlRootElement(name = PeopleFlowMember.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PeopleFlowMember.Constants.TYPE_NAME, propOrder = {
        PeopleFlowMember.Elements.ID,
        PeopleFlowMember.Elements.PEOPLE_FLOW_ID,
        PeopleFlowMember.Elements.MEMBER_TYPE,
        PeopleFlowMember.Elements.MEMBER_ID,
        PeopleFlowMember.Elements.PRIORITY,
        PeopleFlowMember.Elements.DELEGATED_FROM_ID,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class PeopleFlowMember extends AbstractDataTransferObject implements PeopleFlowMemberContract {

    private static final int STARTING_PRIORITY = 1;

    @XmlElement(name = Elements.ID, required = false)
    private final String id;

    @XmlElement(name = Elements.PEOPLE_FLOW_ID, required = false)
    private final String peopleFlowId;

    @XmlElement(name = Elements.MEMBER_TYPE, required = true)
    private final MemberType memberType;

    @XmlElement(name = Elements.MEMBER_ID, required = true)
    private final String memberId;

    @XmlElement(name = Elements.PRIORITY, required = true)
    private final int priority;

    @XmlElement(name = Elements.DELEGATED_FROM_ID, required = false)
    private final String delegatedFromId;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private PeopleFlowMember() {
        this.priority = STARTING_PRIORITY;
        this.peopleFlowId = null;
        this.memberType = null;
        this.memberId = null;
        this.delegatedFromId = null;
        this.id = null;
        this.versionNumber = null;
    }

    private PeopleFlowMember(Builder builder) {
        this.priority = builder.getPriority();
        this.peopleFlowId = builder.getPeopleFlowId();
        this.memberType = builder.getMemberType();
        this.memberId = builder.getMemberId();
        this.delegatedFromId = builder.getDelegatedFromId();
        this.id = builder.getId();
        this.versionNumber = builder.getVersionNumber();
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public String getPeopleFlowId() {
        return this.peopleFlowId;
    }

    @Override
    public MemberType getMemberType() {
        return this.memberType;
    }

    @Override
    public String getMemberId() {
        return this.memberId;
    }

    @Override
    public String getDelegatedFromId() {
        return this.delegatedFromId;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    /**
     * A builder which can be used to construct {@link PeopleFlowMember} instances.  Enforces the constraints of the
     * {@link PeopleFlowMemberContract}.
     * 
     */
    public final static class Builder implements Serializable, ModelBuilder, PeopleFlowMemberContract {

        private int priority;
        private String peopleFlowId;
        private MemberType memberType;
        private String memberId;
        private String delegatedFromId;
        private String id;
        private Long versionNumber;

        private Builder(String memberId, MemberType memberType) {
            setMemberId(memberId);
            setMemberType(memberType);
            setPriority(STARTING_PRIORITY);
        }

        public static Builder create(String memberId, MemberType memberType) {
            // TODO modify as needed to pass any required values and add them to the signature of the 'create' method
            return new Builder(memberId, memberType);
        }

        public static Builder create(PeopleFlowMemberContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getMemberId(), contract.getMemberType());
            builder.setPriority(contract.getPriority());
            builder.setPeopleFlowId(contract.getPeopleFlowId());
            builder.setDelegatedFromId(contract.getDelegatedFromId());
            builder.setId(contract.getId());
            builder.setVersionNumber(contract.getVersionNumber());
            return builder;
        }

        public PeopleFlowMember build() {
            return new PeopleFlowMember(this);
        }

        @Override
        public int getPriority() {
            return this.priority;
        }

        @Override
        public String getPeopleFlowId() {
            return this.peopleFlowId;
        }

        @Override
        public MemberType getMemberType() {
            return this.memberType;
        }

        @Override
        public String getMemberId() {
            return this.memberId;
        }

        @Override
        public String getDelegatedFromId() {
            return this.delegatedFromId;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

        public void setPriority(int priority) {
            if (priority < STARTING_PRIORITY) {
                throw new IllegalArgumentException("Given priority was smaller than the minimum prior value of " + STARTING_PRIORITY);
            }
            this.priority = priority;
        }

        public void setPeopleFlowId(String peopleFlowId) {
            this.peopleFlowId = peopleFlowId;
        }

        public void setMemberType(MemberType memberType) {
            if (memberType == null) {
                throw new IllegalArgumentException("memberType was null");
            }
            this.memberType = memberType;
        }

        public void setMemberId(String memberId) {
            if (StringUtils.isBlank(memberId)) {
                throw new IllegalArgumentException("memberId was null or blank");
            }
            this.memberId = memberId;
        }

        public void setDelegatedFromId(String delegatedFromId) {
            this.delegatedFromId = delegatedFromId;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "peopleFlowMember";
        final static String TYPE_NAME = "PeopleFlowMemberType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String PRIORITY = "priority";
        final static String PEOPLE_FLOW_ID = "peopleFlowId";
        final static String MEMBER_TYPE = "memberType";
        final static String MEMBER_ID = "memberId";
        final static String DELEGATED_FROM_ID = "delegatedFromId";
        final static String ID = "id";
    }

}