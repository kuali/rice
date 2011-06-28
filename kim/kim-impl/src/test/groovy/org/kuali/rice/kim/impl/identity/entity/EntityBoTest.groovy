package org.kuali.rice.kim.impl.identity.entity

import org.junit.Test
import org.kuali.rice.kim.api.identity.entity.Entity
import org.junit.Assert

class EntityBoTest {
  @Test
  public void testNotEqualsWithEntity() {
      Entity.Builder builder = Entity.Builder.create();
      builder.setActive(true)
      builder.setId("10101")
      Entity immutable = builder.build()
      EntityBo bo = EntityBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityBo.to(bo))
  }
}
