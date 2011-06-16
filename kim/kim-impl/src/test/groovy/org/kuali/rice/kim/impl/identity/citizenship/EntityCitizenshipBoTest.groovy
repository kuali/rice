package org.kuali.rice.kim.impl.identity.citizenship

import org.junit.Test
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship
import org.junit.Assert


class EntityCitizenshipBoTest {
  @Test
  public void testNotEqualsWithAddress() {
      EntityCitizenship.Builder builder = EntityCitizenship.Builder.create();
      builder.setActive(true)
      builder.setEntityId("10101")
      EntityCitizenship immutable = builder.build()
      EntityCitizenshipBo bo = EntityCitizenshipBo.from(immutable )
      Assert.assertFalse(bo.equals(immutable))
      Assert.assertFalse(immutable.equals(bo))
      Assert.assertEquals(immutable, EntityCitizenshipBo.to(bo))
  }
}
