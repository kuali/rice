package org.kuali.rice.kim.impl.role

import java.sql.Timestamp
import java.text.SimpleDateFormat
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.kuali.rice.kim.api.role.RoleMember
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import org.kuali.rice.core.api.membership.MemberType

class RoleMemberBoTest {
    static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    static final String ACTIVE_FROM_STRING = "2011-01-01 12:00:00"
    static final String ACTIVE_TO_STRING = "2012-01-01 12:00:00"
    static final DateTime ACTIVE_FROM = FORMATTER.parseDateTime(ACTIVE_FROM_STRING)
    static final DateTime ACTIVE_TO = FORMATTER.parseDateTime(ACTIVE_TO_STRING)
    static final MemberType G = MemberType.GROUP

    @Test
    @Ignore("RoleMemberBo.getAttributes(), called by RoleMemberBo.to(), requires the GRL to use both RoleService and TypeInfoService - not setup for unit tests")
    void test_to() {
        RoleMemberBo bo = new RoleMemberBo(
                roleMemberId: "1",
                roleId: "2",
                memberTypeCode: "G",
                attributes: [],
                roleRspActions: [],
                memberId: "22",
                activeFromDate: ACTIVE_FROM,
                activeToDate: ACTIVE_TO
        )

        RoleMember immutable = RoleMemberBo.to(bo)
        Assert.assertEquals(bo.roleMemberId, immutable.roleMemberId)
        Assert.assertEquals(bo.roleId, immutable.roleId)
        Assert.assertEquals(bo.memberTypeCode, immutable.memberTypeCode)
        Assert.assertEquals(bo.roleRspActions, immutable.roleRspActions)
        Assert.assertEquals(bo.memberId, immutable.memberId)
        Assert.assertEquals(bo.activeFromDate, immutable.activeFromDate)
        Assert.assertEquals(bo.activeToDate, immutable.activeToDate)
    }

    @Test
    void test_from() {
        RoleMember immutable = RoleMember.Builder.create("23", "1", "42", G, ACTIVE_FROM, ACTIVE_TO, [:]).build()
        RoleMemberBo bo = RoleMemberBo.from(immutable)
        Assert.assertEquals(bo.roleMemberId, immutable.roleMemberId)
        Assert.assertEquals(bo.roleId, immutable.roleId)
        Assert.assertEquals(bo.memberTypeCode, immutable.memberTypeCode)
        Assert.assertEquals(bo.roleRspActions, immutable.roleRspActions)
        Assert.assertEquals(bo.memberId, immutable.memberId)
        Assert.assertEquals(bo.activeFromDate, immutable.activeFromDate)
        Assert.assertEquals(bo.activeToDate, immutable.activeToDate)
    }

    @Test
    void test_notEqualToImmutable() {
        RoleMember immutable = RoleMember.Builder.create("23", "1", "42", G, ACTIVE_FROM, ACTIVE_TO, [:]).build()
        RoleMemberBo bo = RoleMemberBo.from(immutable)
        Assert.assertFalse(bo.equals(immutable))
        Assert.assertFalse(immutable.equals(bo))
    }
}
