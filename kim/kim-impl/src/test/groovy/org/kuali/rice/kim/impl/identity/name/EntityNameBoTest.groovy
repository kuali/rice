package org.kuali.rice.kim.impl.identity.name

import org.junit.Test
import org.kuali.rice.kim.api.identity.name.EntityName
import org.junit.Assert

class EntityNameBoTest {
  @Test
  public void testNotEqualsWithName() {
      EntityName.Builder builder = EntityName.Builder.create();
      builder.setActive(true)
      builder.setDefaultValue(true)
      builder.setEntityId("10101")
      builder.setLastName("last")
      builder.setFirstName("first")
      EntityName immutable = builder.build()
      EntityNameBo bo = EntityNameBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityNameBo.to(bo))
  }
}
