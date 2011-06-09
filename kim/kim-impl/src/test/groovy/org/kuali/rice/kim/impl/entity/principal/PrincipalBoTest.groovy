package org.kuali.rice.kim.impl.entity.principal

import org.junit.Test
import org.kuali.rice.kim.api.entity.principal.Principal
import org.junit.Assert


class PrincipalBoTest {
  @Test
  public void testNotEqualsWithEmail() {
      Principal immutable = Principal.Builder.create("pName").build()
      PrincipalBo bo = PrincipalBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, PrincipalBo.to(bo))
  }
}
