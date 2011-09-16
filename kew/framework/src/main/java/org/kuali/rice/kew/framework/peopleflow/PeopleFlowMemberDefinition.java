package org.kuali.rice.kew.framework.peopleflow;

import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectUtils;

/**
 * Model object for a PeopleFlow member. TODO: ...
 */
// TODO: JAX-WS annotate
public class PeopleFlowMemberDefinition extends AbstractDataTransferObject implements PeopleFlowMemberContract {

    private final String id;
    private final String peopleFlowId;
    private final MemberType memberType;
    private final String memberId;
    private final Integer priority;
    private final String delegatedFromId;
    private final Long versionNumber;

    private PeopleFlowMemberDefinition() {
        id = null;
        peopleFlowId = null;
        memberType = null;
        memberId = null;
        priority = null;
        delegatedFromId = null;
        versionNumber = null;
    }

    private PeopleFlowMemberDefinition(Builder builder) {
        id = builder.getId();
        peopleFlowId = builder.getPeopleFlowId();
        memberType = builder.getMemberType();
        memberId = builder.getMemberId();
        priority = builder.getPriority();
        delegatedFromId = builder.getDelegatedFromId();
        versionNumber = builder.getVersionNumber();
    }

    // TODO: validate constraints in builder
    public static class Builder implements ModelBuilder, PeopleFlowMemberContract {

        private String id;
        private String peopleFlowId;
        private MemberType memberType;
        private String memberId;
        private Integer priority;
        private String delegatedFromId;
        private Long versionNumber;
        
        /**
         *  a {@link org.kuali.rice.core.api.mo.ModelObjectUtils.Transformer} useful for converting a
         *  {@link org.kuali.rice.kew.framework.peopleflow.PeopleFlowMemberContract} to
         *  {@link org.kuali.rice.kew.framework.peopleflow.PeopleFlowMemberDefinition.Builder}.
         */
        public static final ModelObjectUtils.Transformer<PeopleFlowMemberContract, Builder>
                toBuilder = new ModelObjectUtils.Transformer<PeopleFlowMemberContract, Builder>() {
                public Builder transform(PeopleFlowMemberContract input) {
                    return Builder.create(input);
                }
        };

        private Builder(String peopleFlowId, MemberType memberType, String memberId) {
            this.peopleFlowId = peopleFlowId;
            this.memberType = memberType;
            this.memberId = memberId;
        }

        public static PeopleFlowMemberDefinition.Builder create(PeopleFlowMemberContract peopleFlowMember) {
            Builder result =
                    new Builder(peopleFlowMember.getPeopleFlowId(),
                            peopleFlowMember.getMemberType(),
                            peopleFlowMember.getMemberId());

            result.setId(peopleFlowMember.getId());
            result.setPriority(peopleFlowMember.getPriority());
            result.setDelegatedFromId(peopleFlowMember.getDelegatedFromId());
            result.setVersionNumber(peopleFlowMember.getVersionNumber());

            return result;
        }

        public static Builder create(String peopleFlowId, MemberType memberType, String memberId) {
            return new Builder(peopleFlowId, memberType, memberId);
        }

        @Override
        public Object build() {
            return new PeopleFlowMemberDefinition(this);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPeopleFlowId() {
            return peopleFlowId;
        }

        public void setPeopleFlowId(String peopleFlowId) {
            this.peopleFlowId = peopleFlowId;
        }

        public MemberType getMemberType() {
            return memberType;
        }

        public void setMemberType(MemberType memberType) {
            this.memberType = memberType;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public String getDelegatedFromId() {
            return delegatedFromId;
        }

        public void setDelegatedFromId(String delegatedFromId) {
            this.delegatedFromId = delegatedFromId;
        }

        public Long getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPeopleFlowId() {
        return peopleFlowId;
    }

    @Override
    public MemberType getMemberType() {
        return memberType;
    }

    @Override
    public String getMemberId() {
        return memberId;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public String getDelegatedFromId() {
        return delegatedFromId;
    }

    @Override
    public Long getVersionNumber() {
        return versionNumber;
    }
}
