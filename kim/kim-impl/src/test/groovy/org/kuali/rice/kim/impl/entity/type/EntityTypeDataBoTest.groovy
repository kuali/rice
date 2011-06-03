package org.kuali.rice.kim.impl.entity.type

import org.junit.Test
import org.kuali.rice.kim.api.entity.type.EntityTypeData
import org.junit.Assert


class EntityTypeDataBoTest {
  @Test
  public void testNotEqualsWithEntityTypeData() {
    EntityTypeData immutable = EntityTypeData.Builder.create("101021", "PERSON").build()
    EntityTypeDataBo bo = EntityTypeDataBo.from(immutable )
    Assert.assertFalse(bo.equals(immutable))
    Assert.assertFalse(immutable.equals(bo))
    Assert.assertEquals(immutable, EntityTypeDataBo.to(bo))
  }
}
