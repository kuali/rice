package org.kuali.rice.kim.impl.identity.residency

import org.junit.Test
import org.kuali.rice.kim.api.identity.residency.EntityResidency

import org.junit.Assert


class EntityResidencyBoTest {
  @Test
  public void testNotEqualsWithEmail() {
      EntityResidency.Builder builder = EntityResidency.Builder.create()
      builder.setId("1")
      builder.setEntityId("10101")
      builder.setInState("TX")
      builder.setDeterminationMethod("No idea what this should be")
      EntityResidency immutable = builder.build()
      EntityResidencyBo bo = EntityResidencyBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityResidencyBo.to(bo))
  }
}
