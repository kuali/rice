package org.kuali.rice.kim.impl.role

import org.junit.Assert
import org.junit.Test
import org.kuali.rice.kim.api.role.Role

class RoleBoTest {

    @Test
    void test_to() {
        RoleBo bo = new RoleBo(id: "1", name: "someRoleName", description: "someRoleDescription", active: true,
                kimTypeId: "22", namespaceCode: "KUALI", versionNumber: 1L);
        Role immutable = RoleBo.to(bo)
        Assert.assertEquals(bo.id, immutable.id)
        Assert.assertEquals(bo.name, immutable.name)
        Assert.assertEquals(bo.description, immutable.description)
        Assert.assertEquals(bo.active, immutable.active)
        Assert.assertEquals(bo.kimTypeId, immutable.kimTypeId)
        Assert.assertEquals(bo.versionNumber, immutable.versionNumber)
    }

    void test_from() {
        Role immutable = Role.Builder.create("1", "someRoleName", "KUALI", "description", "2").build()
        RoleBo bo = RoleBo.from(immutable)
        Assert.assertEquals(bo.id, immutable.id)
        Assert.assertEquals(bo.name, immutable.name)
        Assert.assertEquals(bo.description, immutable.description)
        Assert.assertEquals(bo.active, immutable.active)
        Assert.assertEquals(bo.kimTypeId, immutable.kimTypeId)
        Assert.assertEquals(bo.versionNumber, immutable.versionNumber)

    }

    void test_notEqualsWithRole() {
        Role immutable = Role.Builder.create("1", "someRoleName", "KUALI", "description", "2").build()
        RoleBo bo = RoleBo.from(immutable)
        Assert.assertFalse(bo.equals(immutable))
        Assert.assertFalse(immutable.equals(bo))
    }
}
