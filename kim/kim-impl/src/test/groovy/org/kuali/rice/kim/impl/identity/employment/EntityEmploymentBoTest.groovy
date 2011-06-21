package org.kuali.rice.kim.impl.identity.employment

import org.kuali.rice.kim.api.identity.employment.EntityEmployment
import org.junit.Test
import org.junit.Assert


class EntityEmploymentBoTest {
  @Test
  public void testNotEqualsWithEmail() {
      EntityEmployment.Builder builder = EntityEmployment.Builder.create();
      builder.setActive(true)
      builder.setEmployeeId("1020230")
      builder.setEntityId("10101")
      EntityEmployment immutable = builder.build()
      EntityEmploymentBo bo = EntityEmploymentBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityEmploymentBo.to(bo))
  }
}
