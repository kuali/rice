package org.kuali.rice.kew.framework.peopleflow;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

/**
 * Interface contract for PeopleFlow members.  TODO: ...
 */
public interface PeopleFlowMemberContract extends Identifiable, Versioned {

    /**
     * @return the id for the {@link PeopleFlowContract} this member belongs to.  May be null before persistence.
     */
    String getPeopleFlowId();

    /**
     * @return the {@link MemberType} of this member.  Never null.
     */
    MemberType getMemberType();

    /**
     * @return the id of the member.  This will key in to different types depending on the {@link MemberType} of the
     * instance.
     */
    String getMemberId();

    /**
     * @return the priority of the member.  This is equivalent to the sequential stop in the PeopleFlow, which means
     * lower integer value equates to higher priority.  The minimum priority is 1.
     * May be null if {@link #getDelegatedFromId()} is non-null.
     */
    Integer getPriority();

    /**
     * @return the id of the {@link PeopleFlowMemberContract} that the instance is a delegate for.  Must be null if
     * this member is not a delegate.
     */
    String getDelegatedFromId();



    /**
     * Enumeration for member types.
     */
    public enum MemberType {

        /**
         * {@link MemberType} corresponding to KIM roles
         */
        ROLE("R"),

        /**
         * {@link MemberType} corresponding to KIM groups
         */
        GROUP("G"),

        /**
         * {@link MemberType} corresponding to KIM principals
         */
        PRINCIPAL("P");

        public final String code;

        /**
         * @param code the String representation of this type code enum
         */
        private MemberType(String code) {
            this.code = code;
        }

        /**
         * @param code the type code for which to retrieve the corresponding {@link MemberType}
         * @return the {@link MemberType} for the given code
         * @throws IllegalArgumentException if the code is null or not in the set of valid codes
         */
        static MemberType getByCode(String code) {

            if (code == null) throw new IllegalArgumentException("null is not a valid MemberType code");

            for (MemberType type : MemberType.values()) {
                if (type.code.equals(code)) return type;
            }

            throw new IllegalArgumentException("'"+ code +"' is not a valid MemberType code");
        }
    }
}
