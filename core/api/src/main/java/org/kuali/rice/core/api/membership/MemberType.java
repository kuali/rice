package org.kuali.rice.core.api.membership;

import org.kuali.rice.core.api.mo.common.Coded;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A MemberType is an enum that represents valid membership type for Role, Group, and Delegate members.
 * This enum is currently slightly overloaded, as only PRINCIPAL and GROUP are valid Group membership types.
 * @see org.kuali.rice.kim.impl.membership.AbstractMemberBo
 * @see org.kuali.rice.kim.impl.group.GroupMemberBo
 * @see org.kuali.rice.kim.impl.role.RoleMemberBo
 * @see org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo
 */
@XmlRootElement(name = "memberType")
@XmlType(name = "MemberTypeType")
@XmlEnum
public enum MemberType implements Coded {

    /**
     * Member type corresponding to KIM roles
     */
    @XmlEnumValue("R") ROLE("R"),

    /**
     * Member type corresponding to KIM groups
     */
    @XmlEnumValue("G") GROUP("G"),

    /**
     * Member type corresponding to KIM principals
     */
    @XmlEnumValue("P") PRINCIPAL("P");

    public final String code;

    private MemberType(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public static MemberType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (MemberType memberType : values()) {
            if (memberType.code.equals(code)) {
                return memberType;
            }
        }
        throw new IllegalArgumentException("Failed to locate the MemberType with the given code: " + code);
    }

}
